from fastapi import FastAPI
from contextlib import asynccontextmanager

from app.services.qdrant_service import ensure_collections
from app.routers import customer_router, product_router


@asynccontextmanager
async def lifespan(app: FastAPI):
    # Chạy khi server khởi động: đảm bảo collection đã tồn tại trên Qdrant
    ensure_collections()
    yield


app = FastAPI(title="Semantic Search Server", lifespan=lifespan)

app.include_router(customer_router.router)
app.include_router(product_router.router)


@app.get("/health")
def health():
    return {"status": "ok"}
