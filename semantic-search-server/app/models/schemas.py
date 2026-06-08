from pydantic import BaseModel

# ---------- Upsert ----------

class UpsertItem(BaseModel):
    id: int                  # ID từ MySQL
    name: str
    description: str | None = None

class UpsertRequest(BaseModel):
    items: list[UpsertItem]  # Upsert nhiều item 1 lần cho hiệu quả

# ---------- Search ----------

class SearchRequest(BaseModel):
    query: str
    top_k: int = 5           # Trả về bao nhiêu kết quả

class SearchResult(BaseModel):
    id: int
    name: str
    score: float             # Độ tương đồng (0.0 - 1.0)

class SearchResponse(BaseModel):
    results: list[SearchResult]

# ---------- Delete ----------

class DeleteRequest(BaseModel):
    ids: list[int]
