# Semantic Search Server

Python FastAPI + multilingual-e5-small + Qdrant Cloud

## Cấu trúc

```
app/
├── core/
│   ├── config.py        # Đọc biến môi trường
│   └── security.py      # Xác thực API Key
├── models/
│   └── schemas.py       # Request/Response schema
├── routers/
│   ├── customer_router.py
│   └── product_router.py
├── services/
│   ├── embedding_service.py  # Load model, tạo vector
│   └── qdrant_service.py     # Kết nối Qdrant
└── main.py
```

## Chạy local

```bash
# 1. Tạo môi trường ảo
python -m venv venv
source venv/bin/activate   # Windows: venv\Scripts\activate

# 2. Cài thư viện
pip install -r requirements.txt

# 3. Tạo file .env từ mẫu
cp .env.example .env
# Sau đó điền QDRANT_URL, QDRANT_API_KEY, API_SECRET_KEY vào .env

# 4. Chạy server
uvicorn app.main:app --reload
```

## Deploy lên Render

1. Push code lên GitHub
2. Vào Render → New Web Service → chọn repo
3. Điền 3 biến môi trường trong phần Environment:
   - `QDRANT_URL`
   - `QDRANT_API_KEY`
   - `API_SECRET_KEY`
4. Render tự đọc `render.yaml` và deploy

> ⚠️ Lần đầu deploy chậm (~3-5 phút) vì phải tải model về.
> Dùng Render plan có ít nhất **512MB RAM**.

## API Endpoints

Tất cả endpoints yêu cầu header: `X-API-Key: <API_SECRET_KEY>`

### Customers

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| POST | `/customers/upsert` | Thêm/cập nhật khách hàng |
| POST | `/customers/search` | Tìm kiếm ngữ nghĩa |
| DELETE | `/customers/delete` | Xóa theo danh sách ID |

### Products

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| POST | `/products/upsert` | Thêm/cập nhật sản phẩm |
| POST | `/products/search` | Tìm kiếm ngữ nghĩa |
| DELETE | `/products/delete` | Xóa theo danh sách ID |

### Ví dụ request

**Upsert:**
```json
POST /products/upsert
{
  "items": [
    { "id": 1, "name": "Áo thun nam", "description": "Chất liệu cotton thoáng mát" },
    { "id": 2, "name": "Quần jean nữ", "description": "Dáng skinny, co giãn tốt" }
  ]
}
```

**Search:**
```json
POST /products/search
{
  "query": "áo mặc mùa hè",
  "top_k": 5
}
```

**Response:**
```json
{
  "results": [
    { "id": 1, "name": "Áo thun nam", "score": 0.91 },
    ...
  ]
}
```

## Java Spring Boot gọi sang

```java
// Dùng RestTemplate hoặc WebClient
HttpHeaders headers = new HttpHeaders();
headers.set("X-API-Key", "your_secret_key");
headers.setContentType(MediaType.APPLICATION_JSON);

String body = """
    { "query": "áo mùa hè", "top_k": 5 }
    """;

HttpEntity<String> entity = new HttpEntity<>(body, headers);
ResponseEntity<String> response = restTemplate.postForEntity(
    "https://your-render-url.onrender.com/products/search",
    entity,
    String.class
);
```
