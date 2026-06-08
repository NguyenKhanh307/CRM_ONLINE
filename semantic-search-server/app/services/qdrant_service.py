from qdrant_client import QdrantClient
from qdrant_client.models import Distance, VectorParams
from app.core.config import settings

_client: QdrantClient | None = None

def get_client() -> QdrantClient:
    global _client
    if _client is None:
        _client = QdrantClient(url=settings.qdrant_url, api_key=settings.qdrant_api_key)
    return _client

def ensure_collections():
    """Tạo collection nếu chưa tồn tại. Gọi khi server khởi động."""
    client = get_client()
    existing = [c.name for c in client.get_collections().collections]

    for collection_name in [settings.customer_collection, settings.product_collection]:
        if collection_name not in existing:
            client.create_collection(
                collection_name=collection_name,
                vectors_config=VectorParams(size=settings.vector_size, distance=Distance.COSINE),
            )
