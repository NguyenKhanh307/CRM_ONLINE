# Indexer — đánh chỉ mục vector cho trợ lý AI

Công cụ **chạy tay trên máy dev**, đọc bản ghi CRM từ TiDB → dựng thẻ tóm tắt tiếng Việt →
nhúng (embed) bằng Gemini → ghi vào bảng `copilot_chunks`.

**Backend trên Render KHÔNG chạy việc này** — nó chỉ *đọc* `copilot_chunks` lúc trả lời câu hỏi.
Nhờ vậy Render không phải gánh job dài hơi, không cần `@Scheduled`, không tốn thêm RAM.

```
tools/indexer/  (máy dev)                    Render
  đọc TiDB → dựng thẻ → embed → ghi ┐        ┌─ AskCopilotUseCase
                                    ▼        │   nhúng CÂU HỎI (1 request)
                       TiDB copilot_chunks ──┘   + VEC_COSINE_DISTANCE
```

---

## Cài đặt

```bash
cd E:/CRMOnline
python -m pip install -r tools/indexer/requirements.txt
cp tools/indexer/.env.example tools/indexer/.env    # rồi điền giá trị thật
```

`.env` **không commit**. Các biến bắt buộc: `TIDB_HOST`, `TIDB_USER`, `TIDB_PASSWORD`,
`GEMINI_API_KEY` (dùng chung khóa với `APP_AI_API_KEY` của backend).

> TiDB Cloud bắt buộc TLS — mặc định đọc `isrgrootx1.pem` ở gốc repo, thường không phải khai báo.

## Chuẩn bị DB

Bảng `copilot_chunks` đã nằm trong `diagrams/crm.sql` (mục 15). Nếu DB đang chạy và bạn
không muốn reseed thì chạy `diagrams/vector_migration.sql`.

## Chạy

```bash
cd E:/CRMOnline

python -m tools.indexer.build --stats                      # xem đã index được gì
python -m tools.indexer.build --module campaign --limit 5  # chạy thử 5 bản ghi
python -m tools.indexer.build --module ticket              # một phân hệ
python -m tools.indexer.build                              # toàn bộ 11 phân hệ
```

Chạy lại lần hai sẽ in `bỏ qua N` — vì `content_hash` (MD5 của thẻ tóm tắt) không đổi thì
không embed lại, không tốn quota.

## Khi hết hạn mức ngày

Gemini free tier giới hạn **~100 request/phút và ~1.000 request/ngày**, mà model chỉ nhận
**1 văn bản mỗi request** → 1 bản ghi = 1 request.

Gặp lỗi hạn mức ngày, script in số chunk đã xong rồi **thoát với mã 2**:

```
[HET HAN MUC NGAY] ticket: da embed 320, con lai 680
```

Đây là hành vi **có chủ ý**, không phải lỗi. Hôm sau chạy lại đúng lệnh đó — nhờ `content_hash`
nó bỏ qua phần đã xong và đi tiếp từ chỗ dở. Vài ngày là phủ hết.

Muốn xong trong một lần: nâng Gemini API lên Tier 1 (hạn mức ngày lên 1 triệu).

## ⚠️ Hai điều tuyệt đối không được lệch với backend

| | Indexer | Backend |
|---|---|---|
| Model | `EMBED_MODEL` trong `.env` | `app.ai.embed.model` |
| Số chiều | `EMBED_DIMENSIONS` | `app.ai.embed.dimensions` |
| Chuẩn hóa | `embed.normalize()` — L2 | `GeminiEmbeddingServiceImpl` — L2 |
| taskType | `RETRIEVAL_DOCUMENT` | `RETRIEVAL_QUERY` |

Ba dòng đầu **phải trùng nhau**. Mỗi model có hệ tọa độ riêng — trộn hai model (hoặc hai số
chiều, hoặc một bên chuẩn hóa một bên không) sẽ làm khoảng cách cosine vô nghĩa và chatbot
trả về bản ghi ngẫu nhiên **mà không báo lỗi gì**. Đổi model ⇒ phải `TRUNCATE copilot_chunks`
và build lại từ đầu.

Riêng `taskType` thì **cố ý khác nhau** — đó là cách Gemini phân biệt "đoạn văn cần lưu" với
"câu truy vấn", dùng đúng loại giúp tăng chất lượng truy hồi.

## Cấu trúc

| File | Vai trò |
|------|---------|
| `config.py` | Đọc `.env`, kiểm biến bắt buộc |
| `sources.py` | SQL lấy bản ghi 11 phân hệ (luôn kèm `deleted_at IS NULL`, resolve tên FK bằng `LEFT JOIN`) |
| `chunk_text.py` | Dựng thẻ tóm tắt tiếng Việt — **quyết định chất lượng truy hồi** |
| `embed.py` | Gọi Gemini `embedContent`, throttle theo RPM, retry 429, chuẩn hóa L2 |
| `store.py` | Kết nối TiDB, đọc hash cũ, `INSERT ... ON DUPLICATE KEY UPDATE` |
| `build.py` | Entry point CLI |

> Bình luận ticket (`ticket_comments`) được **gộp vào thẻ của ticket cha**, và bỏ hẳn
> `type='system'` (audit log tự sinh, không có giá trị ngữ nghĩa).
