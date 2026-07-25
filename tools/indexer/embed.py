# -*- coding: utf-8 -*-
"""Goi Gemini embedContent: throttle theo RPM, retry khi 429, chuan hoa L2."""
import math
import time

import requests

from . import config


class QuotaExhausted(Exception):
    """Het han muc NGAY (RPD) — dung han vong lap, hom sau chay lai tu content_hash."""


class _Throttle:
    """Gian cach toi thieu giua hai request de khong vuot RPM."""

    def __init__(self, rpm):
        self.gap = 60.0 / max(rpm, 1)
        self.last = 0.0

    def wait(self):
        delta = time.monotonic() - self.last
        if delta < self.gap:
            time.sleep(self.gap - delta)
        self.last = time.monotonic()


_throttle = _Throttle(config.EMBED_RPM)
_session = requests.Session()


def _is_daily_quota(body):
    """Phan biet het quota NGAY (dung han) voi vuot RPM tuc thoi (cho roi thu lai)."""
    low = (body or "").lower()
    return "perday" in low or "per day" in low or "requests per day" in low


def embed(text, task_type="RETRIEVAL_DOCUMENT"):
    """Nhung mot doan van ban -> list[float] da chuan hoa L2.

    task_type: RETRIEVAL_DOCUMENT khi danh chi muc, RETRIEVAL_QUERY khi tim kiem.
    Dung sai loai lam giam ro ret chat luong truy hoi.
    """
    url = f"{config.GEMINI_BASE_URL}/v1beta/models/{config.EMBED_MODEL}:embedContent"
    payload = {
        "content": {"parts": [{"text": text}]},
        "taskType": task_type,
        "outputDimensionality": config.EMBED_DIMENSIONS,
    }
    for attempt in range(5):
        _throttle.wait()
        try:
            res = _session.post(url, params={"key": config.GEMINI_API_KEY},
                                json=payload, timeout=60)
        except requests.RequestException as e:
            if attempt == 4:
                raise
            print(f"    [mang] {e} — thu lai sau {2 ** attempt}s")
            time.sleep(2 ** attempt)
            continue
        if res.status_code == 429:
            if _is_daily_quota(res.text):
                raise QuotaExhausted(res.text[:300])
            wait = 10 * (attempt + 1)
            print(f"    [429] vuot RPM — cho {wait}s")
            time.sleep(wait)
            continue
        if res.status_code >= 400:
            raise SystemExit(f"[LOI] Gemini HTTP {res.status_code}: {res.text[:500]}")
        values = res.json()["embedding"]["values"]
        return normalize(values)
    raise SystemExit("[LOI] Gemini tra 429 lien tuc sau 5 lan thu.")


def normalize(vec):
    """Chuan hoa L2 (chieu dai = 1).

    BAT BUOC: gemini-embedding-001 CHI tu chuan hoa o 3072 chieu; dung
    outputDimensionality nho hon thi Google yeu cau client tu normalize.
    Backend (GeminiEmbeddingServiceImpl) phai chuan hoa Y HET cach nay, neu khong
    vector luc hoi va vector luc index lech nhau -> ket qua tim sai am tham.
    """
    norm = math.sqrt(sum(v * v for v in vec))
    if norm == 0:
        return vec
    return [v / norm for v in vec]
