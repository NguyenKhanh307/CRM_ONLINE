# CRM Nội bộ

Hệ thống CRM nội bộ gồm hai module độc lập: backend API và frontend web app.

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
| **Port** | `8080` | `5173` (dev) |

## Khởi động nhanh

```bash
# Backend
cd be-crm
./mvnw spring-boot:run

# Frontend
cd fe-crm
npm run dev
```
