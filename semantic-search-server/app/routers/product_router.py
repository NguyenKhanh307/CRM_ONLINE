from fastapi import APIRouter, Depends
from qdrant_client.models import PointStruct

from app.core.security import verify_api_key
from app.core.config import settings
from app.models.schemas import UpsertRequest, SearchRequest, SearchResponse, SearchResult, DeleteRequest
from app.services.embedding_service import embed, embed_query
from app.services.qdrant_service import get_client

router = APIRouter(prefix="/products", tags=["Products"])


@router.post("/upsert", dependencies=[Depends(verify_api_key)])
def upsert_products(body: UpsertRequest):
    client = get_client()
    points = []
    for item in body.items:
        text = f"{item.name}. {item.description or ''}"
        points.append(PointStruct(
            id=item.id,
            vector=embed(text),
            payload={"id": item.id, "name": item.name},
        ))
    client.upsert(collection_name=settings.product_collection, points=points)
    return {"upserted": len(points)}


@router.post("/search", response_model=SearchResponse, dependencies=[Depends(verify_api_key)])
def search_products(body: SearchRequest):
    client = get_client()
    hits = client.search(
        collection_name=settings.product_collection,
        query_vector=embed_query(body.query),
        limit=body.top_k,
        with_payload=True,
    )
    results = [SearchResult(id=h.payload["id"], name=h.payload["name"], score=h.score) for h in hits]
    return SearchResponse(results=results)


@router.delete("/delete", dependencies=[Depends(verify_api_key)])
def delete_products(body: DeleteRequest):
    client = get_client()
    client.delete(
        collection_name=settings.product_collection,
        points_selector=body.ids,
    )
    return {"deleted": len(body.ids)}
