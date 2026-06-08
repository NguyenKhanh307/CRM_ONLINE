from sentence_transformers import SentenceTransformer
from app.core.config import settings

# Load model 1 lần khi server khởi động, dùng lại cho mọi request
_model: SentenceTransformer | None = None

def get_model() -> SentenceTransformer:
    global _model
    if _model is None:
        _model = SentenceTransformer(settings.embedding_model)
    return _model

def embed(text: str) -> list[float]:
    """
    multilingual-e5 yêu cầu prefix:
    - "query: ..." khi search
    - "passage: ..." khi upsert
    Hàm này dùng cho upsert nên prefix là 'passage'
    """
    model = get_model()
    vector = model.encode(f"passage: {text}", normalize_embeddings=True)
    return vector.tolist()

def embed_query(text: str) -> list[float]:
    """Dùng khi search"""
    model = get_model()
    vector = model.encode(f"query: {text}", normalize_embeddings=True)
    return vector.tolist()
