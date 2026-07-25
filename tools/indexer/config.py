# -*- coding: utf-8 -*-
"""Cau hinh indexer: doc tu bien moi truong (.env), khong hardcode secret."""
import os
import pathlib

from dotenv import load_dotenv

HERE = pathlib.Path(__file__).resolve().parent
REPO_ROOT = HERE.parent.parent

load_dotenv(HERE / ".env")

# --- TiDB ---------------------------------------------------------------
TIDB_HOST = os.getenv("TIDB_HOST", "")
TIDB_PORT = int(os.getenv("TIDB_PORT", "4000"))
TIDB_USER = os.getenv("TIDB_USER", "")
TIDB_PASSWORD = os.getenv("TIDB_PASSWORD", "")
TIDB_DATABASE = os.getenv("TIDB_DATABASE", "crm")
# TiDB Cloud BAT BUOC TLS. File CA da co san o goc repo.
TIDB_CA = os.getenv("TIDB_CA", str(REPO_ROOT / "isrgrootx1.pem"))

# --- Gemini embedding ---------------------------------------------------
GEMINI_API_KEY = os.getenv("GEMINI_API_KEY", "")
GEMINI_BASE_URL = os.getenv("GEMINI_BASE_URL", "https://generativelanguage.googleapis.com")
# PHAI trung app.ai.embed.model / app.ai.embed.dimensions ben backend, neu khong thi
# vector luc index va vector luc hoi nam o hai khong gian khac nhau -> ket qua tim la rac.
EMBED_MODEL = os.getenv("EMBED_MODEL", "gemini-embedding-001")
EMBED_DIMENSIONS = int(os.getenv("EMBED_DIMENSIONS", "768"))

# So request/phut toi da (free tier ~100 RPM). Dat thap hon mot chut cho an toan.
EMBED_RPM = int(os.getenv("EMBED_RPM", "90"))
# So chunk ghi xuong DB moi lo
BATCH_SIZE = int(os.getenv("BATCH_SIZE", "50"))


def require():
    """Kiem tra cau hinh bat buoc, bao loi ro rang thay vi de connect that bai kho hieu."""
    missing = [k for k, v in (("TIDB_HOST", TIDB_HOST), ("TIDB_USER", TIDB_USER),
                              ("GEMINI_API_KEY", GEMINI_API_KEY)) if not v]
    if missing:
        raise SystemExit(
            "[LOI] Thieu bien moi truong: " + ", ".join(missing)
            + f"\n       Tao file {HERE / '.env'} theo mau .env.example roi chay lai.")
