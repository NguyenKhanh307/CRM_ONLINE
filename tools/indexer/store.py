# -*- coding: utf-8 -*-
"""Ket noi TiDB + doc/ghi bang copilot_chunks."""
import pymysql
from pymysql.cursors import DictCursor

from . import config

UPSERT = """
INSERT INTO copilot_chunks (module, record_id, owner_id, title, content, content_hash, embedding)
VALUES (%s, %s, %s, %s, %s, %s, %s)
ON DUPLICATE KEY UPDATE
    owner_id = VALUES(owner_id), title = VALUES(title), content = VALUES(content),
    content_hash = VALUES(content_hash), embedding = VALUES(embedding),
    indexed_at = CURRENT_TIMESTAMP
"""


def connect():
    """Mo ket noi TiDB Cloud (BAT BUOC TLS — dung isrgrootx1.pem o goc repo)."""
    return pymysql.connect(
        host=config.TIDB_HOST, port=config.TIDB_PORT, user=config.TIDB_USER,
        password=config.TIDB_PASSWORD, database=config.TIDB_DATABASE,
        charset="utf8mb4", cursorclass=DictCursor, autocommit=False,
        ssl={"ca": config.TIDB_CA},
    )


def existing_hashes(conn, module):
    """{record_id: content_hash} cua cac chunk da co — dung de BO QUA ban ghi khong doi."""
    with conn.cursor() as cur:
        cur.execute("SELECT record_id, content_hash FROM copilot_chunks WHERE module = %s",
                    (module,))
        return {r["record_id"]: r["content_hash"] for r in cur.fetchall()}


def upsert_batch(conn, rows):
    """Ghi mot lo chunk. rows = list[(module, record_id, owner_id, title, content, hash, vec)].

    Vector duoc bind duoi dang chuoi '[0.1,0.2,...]' — TiDB tu cast sang kieu VECTOR.
    """
    if not rows:
        return 0
    payload = [(m, rid, own, title, content, h, "[" + ",".join(f"{v:.6f}" for v in vec) + "]")
               for (m, rid, own, title, content, h, vec) in rows]
    with conn.cursor() as cur:
        cur.executemany(UPSERT, payload)
    conn.commit()
    return len(payload)


def delete_missing(conn, module, valid_ids):
    """Xoa chunk cua ban ghi khong con ton tai (da xoa mem / xoa vinh vien)."""
    with conn.cursor() as cur:
        if not valid_ids:
            cur.execute("DELETE FROM copilot_chunks WHERE module = %s", (module,))
        else:
            marks = ",".join(["%s"] * len(valid_ids))
            cur.execute(
                f"DELETE FROM copilot_chunks WHERE module = %s AND record_id NOT IN ({marks})",
                [module] + list(valid_ids))
        n = cur.rowcount
    conn.commit()
    return n


def stats(conn):
    """[(module, so_chunk, lan_index_gan_nhat)] — dung de kiem tra sau khi chay."""
    with conn.cursor() as cur:
        cur.execute("SELECT module, COUNT(*) AS n, MAX(indexed_at) AS last "
                    "FROM copilot_chunks GROUP BY module ORDER BY module")
        return cur.fetchall()
