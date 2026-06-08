from pydantic_settings import BaseSettings


class Settings(BaseSettings):
    qdrant_url: str
    qdrant_api_key: str
    api_secret_key: str
    embedding_model: str = "intfloat/multilingual-e5-small"

    # Tên collection trong Qdrant
    customer_collection: str = "customers"
    product_collection: str = "products"

    # Số chiều vector của multilingual-e5-small
    vector_size: int = 384

    class Config:
        env_file = ".env"


settings = Settings()
