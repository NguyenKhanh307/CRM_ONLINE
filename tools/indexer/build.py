#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""Danh chi muc vector cho tro ly AI — CHAY TAY tren may dev.

    python -m tools.indexer.build                    # toan bo phan he
    python -m tools.indexer.build --module ticket    # mot phan he
    python -m tools.indexer.build --module campaign --limit 5   # thu nghiem
    python -m tools.indexer.build --stats            # chi xem thong ke, khong embed

Backend tren Render KHONG chay viec nay: no chi DOC bang copilot_chunks.

Het han muc ngay (RPD) thi script in so chunk da xong roi THOAT — hom sau chay lai,
content_hash khien no bo qua phan da lam va di tiep tu cho dang do.
"""
import argparse
import hashlib
import sys

if __package__ in (None, ""):
    sys.exit("Chay bang: python -m tools.indexer.build  (tu thu muc goc repo)")

from . import chunk_text, config, sources, store
from .embed import QuotaExhausted, embed


def _hash(text):
    return hashlib.md5(text.encode("utf-8")).hexdigest()


def index_module(conn, module, limit=None):
    """@return (scanned, embedded, skipped) — nem QuotaExhausted khi het quota ngay."""
    rows = sources.fetch(conn, module, limit)
    known = store.existing_hashes(conn, module)
    pending, embedded, skipped = [], 0, 0

    for r in rows:
        title, content = chunk_text.build(module, r)
        h = _hash(content)
        if known.get(r["id"]) == h:
            skipped += 1
            continue
        try:
            vec = embed(content, "RETRIEVAL_DOCUMENT")
        except QuotaExhausted:
            store.upsert_batch(conn, pending)     # giu lai phan da lam duoc
            embedded += len(pending)
            raise QuotaExhausted(f"{module}: da embed {embedded}, con lai "
                                 f"{len(rows) - skipped - embedded}")
        pending.append((module, r["id"], r["owner"], title[:255], content, h, vec))
        if len(pending) >= config.BATCH_SIZE:
            embedded += store.upsert_batch(conn, pending)
            pending = []
            print(f"    ...{embedded} chunk")
    embedded += store.upsert_batch(conn, pending)

    if limit is None:                             # chi don rac khi quet day du
        removed = store.delete_missing(conn, module, [r["id"] for r in rows])
        if removed:
            print(f"    da xoa {removed} chunk cua ban ghi khong con ton tai")
    return len(rows), embedded, skipped


def main():
    ap = argparse.ArgumentParser(description="Danh chi muc vector cho tro ly AI CRM")
    ap.add_argument("--module", choices=sources.MODULES, help="chi index mot phan he")
    ap.add_argument("--limit", type=int, help="gioi han so ban ghi (de thu nghiem)")
    ap.add_argument("--stats", action="store_true", help="chi in thong ke, khong embed")
    args = ap.parse_args()

    config.require()
    conn = store.connect()
    try:
        if args.stats:
            total = 0
            print(f"{'Phan he':14s} {'Chunk':>7s}  Lan index gan nhat")
            for r in store.stats(conn):
                total += r["n"]
                print(f"{r['module']:14s} {r['n']:7,d}  {r['last']}")
            print(f"{'TONG':14s} {total:7,d}")
            return

        modules = [args.module] if args.module else sources.MODULES
        tot_s = tot_e = tot_k = 0
        for m in modules:
            print(f"[{m}]")
            try:
                s, e, k = index_module(conn, m, args.limit)
            except QuotaExhausted as ex:
                print(f"\n[HET HAN MUC NGAY] {ex}")
                print("  Han muc Gemini free tier reset theo ngay. Chay lai lenh nay vao ngay mai,")
                print("  script se tu bo qua phan da xong (nho content_hash) va di tiep.")
                sys.exit(2)
            tot_s, tot_e, tot_k = tot_s + s, tot_e + e, tot_k + k
            print(f"    quet {s}, embed {e}, bo qua {k}")
        print(f"\nXONG — quet {tot_s}, embed {tot_e}, bo qua {tot_k} (khong doi noi dung)")
    finally:
        conn.close()


if __name__ == "__main__":
    main()
