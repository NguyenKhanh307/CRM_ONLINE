# CRM Nội bộ

Hệ thống CRM nội bộ gồm hai module: backend REST API và frontend web app.

## Tài liệu chi tiết

| Module | Thư mục | Tài liệu |
|--------|---------|---------|
| Backend | `be-crm/` | [be-crm/README.md](be-crm/README.md) — Kiến trúc, toàn bộ API endpoints, cấu trúc folder/file |
| Frontend | `fe-crm/` | [fe-crm/README.md](fe-crm/README.md) — Kiến trúc, component, hướng dẫn phát triển |

## Tổng quan nhanh

| | Backend | Frontend |
|---|---------|----------|
| **Stack** | Java 21, Spring Boot 4.x, Hibernate 7 | React 19, Vite 7, TypeScript, Tailwind CSS v3 |
| **Database** | TiDB Cloud (MySQL compatible) | — |
| **Auth** | JWT (JJWT 0.12, HS256, 24h) | Bearer token trong localStorage |
| **Port** | `8080` | `5173` (dev) |

## Khởi động nhanh

### 1. Cấu hình môi trường Frontend

Tạo file `fe-crm/.env` (nếu chưa có):

```env
VITE_API_BASE_URL=http://localhost:8080
```

### 2. Chạy Backend

```bash
cd be-crm
./mvnw spring-boot:run
```

### 3. Chạy Frontend

```bash
cd fe-crm
npm install   # chỉ cần lần đầu
npm run dev
```

Truy cập: http://localhost:5173

### Tài khoản test

| Email | Mật khẩu | Role |
|-------|---------|------|
| `admin@abc.vn` | `123456` | ADMIN |

## Trạng thái tích hợp

| Module | Backend | Frontend |
|--------|---------|---------|
| Auth (Login / JWT) | Hoàn thành | Hoàn thành |
| Tiềm năng (Lead) | Hoàn thành | List view |
| Liên hệ (Contact) | Hoàn thành | List view |
| Khách hàng (Customer) | Hoàn thành | List view |
| Cơ hội (Opportunity) | Hoàn thành | List view |
| Báo giá (Quotation) | Hoàn thành | List view |
| Đơn hàng (Order) | Hoàn thành | List view |
| Hoạt động (Activity) | Hoàn thành | List view |
| Sản phẩm (Product) | Hoàn thành | List view |
| Chính sách giá (Pricing) | Hoàn thành | Chưa có |
| Chấm điểm tiềm năng + Web tracking | Hoàn thành | Demo `/tracking-demo` |
| Thông báo (Notification) | Hoàn thành | Chuông trên header |
| Form tạo/sửa tất cả module | — | Chưa có |
