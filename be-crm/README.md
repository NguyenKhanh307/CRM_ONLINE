# CRM Backend — Tài liệu kỹ thuật

> 📕 Mới đọc code backend? Xem [`../CODE_GUIDE_BACKEND.md`](../CODE_GUIDE_BACKEND.md) — hướng
> dẫn đọc hiểu code kiểu sách giáo khoa (cú pháp Java, Hibernate, UseCase, Security…).

## Mục lục

1. [Tổng quan kiến trúc](#1-tổng-quan-kiến-trúc)
2. [Danh sách API theo module](#2-danh-sách-api-theo-module)
   - [Auth — Xác thực & Phân quyền](#21-auth--xác-thực--phân-quyền)
   - [Activity — Hoạt động](#22-activity--hoạt-động)
   - [Contact — Liên hệ](#23-contact--liên-hệ)
   - [Customer — Khách hàng](#24-customer--khách-hàng)
   - [Lead — Tiềm năng](#25-lead--tiềm-năng)
   - [Opportunity — Cơ hội bán hàng](#26-opportunity--cơ-hội-bán-hàng)
   - [Invoice — Hóa đơn](#27-invoice--hóa-đơn-thay-cho-đơn-hàng)
   - [Product — Sản phẩm](#28-product--sản-phẩm)
   - [Pricing — Chính sách giá](#29-pricing--chính-sách-giá)
   - [Quotation — Báo giá](#210-quotation--báo-giá)
   - [Handover — Bàn giao công việc](#211-handover--bàn-giao-công-việc)
   - [Tracking — Web tracking & Chấm điểm tiềm năng](#212-tracking--web-tracking--chấm-điểm-tiềm-năng-public)
   - [Notification — Thông báo](#213-notification--thông-báo)
3. [Cấu trúc folder/file](#3-cấu-trúc-folderfile)
4. [Quy ước chung](#4-quy-ước-chung)

---

## 1. Tổng quan kiến trúc

| Thông tin | Chi tiết |
|-----------|----------|
| **Stack** | Java 21, Spring Boot 4.x, Hibernate 7, TiDB Cloud (MySQL compatible) |
| **Port mặc định** | `8080` |
| **Base URL** | `/api` |
| **Kiến trúc** | Clean Architecture 4 tầng |

### Luồng phụ thuộc

```
Presentation  →  Application  →  Domain  ←  Infrastructure
(Controller)     (UseCase)      (Entity)     (Repository Impl)
```

- **Domain**: Logic nghiệp vụ thuần — không phụ thuộc Spring hay Hibernate
- **Application**: Orchestrate các use case (CQRS: command/query tách biệt)
- **Presentation**: Nhận HTTP request, validate input, trả response
- **Infrastructure**: Hibernate ORM, kết nối DB, implement repository interface

### Response format chuẩn

```json
// Đơn lẻ
{ "data": { ... }, "message": "...", "success": true }

// Phân trang
{ "data": [...], "page": 0, "size": 10, "total": 100, "totalPages": 10 }
```

### Query params phân trang (dùng chung cho tất cả List API)

| Param | Mặc định | Mô tả |
|-------|----------|-------|
| `page` | `0` | Số trang (bắt đầu từ 0) |
| `size` | `10` | Số bản ghi mỗi trang |
| `sortBy` | `id` | Trường sắp xếp |
| `sortDir` | `asc` | Chiều sắp xếp (`asc`/`desc`) |

**Lọc theo năm (`dataAccessFromYear`):** Giá trị này **không** là query param — được trích xuất từ JWT claim (`JwtAuthFilter` set vào `request.setAttribute`). Controller đọc ra và truyền vào `PageRequest`. Repository tự động thêm `AND YEAR(createdAt) >= :fromYear` vào HQL khi giá trị không null. Nhân viên mới được tự set `dataAccessFromYear` = năm kích hoạt tài khoản.

---

## 2. Danh sách API theo module

### 2.1 Auth — Xác thực & Phân quyền

#### Đăng nhập / Kích hoạt tài khoản — `/api/auth`

| Method | Endpoint | Mô tả | Auth yêu cầu |
|--------|----------|-------|--------------|
| `POST` | `/api/auth/login` | Đăng nhập, trả JWT token. Email phải là @gmail.com | Không |
| `POST` | `/api/auth/register-employee` | Admin đăng ký tài khoản nhân viên, gửi email kích hoạt | Bearer JWT |
| `POST` | `/api/auth/activate` | Nhân viên kích hoạt tài khoản và đặt mật khẩu lần đầu | Không |

**POST /api/auth/login — Request:**
```json
{ "email": "admin@gmail.com", "password": "12345678" }
```

**POST /api/auth/register-employee — Request:**
```json
{ "email": "nhanvien@gmail.com", "fullName": "Nguyễn Văn B", "phone": "0901234567", "unitId": 1, "roleId": 3 }
```

**POST /api/auth/activate — Request:**
```json
{ "token": "550e8400-e29b-41d4-a716-446655440000", "newPassword": "MyPass@2026" }
```

Tất cả các endpoint khác đều yêu cầu header: `Authorization: Bearer <token>`

#### Đơn vị tổ chức — `/api/org-units`

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| `POST` | `/api/org-units` | Tạo đơn vị tổ chức |
| `GET` | `/api/org-units` | Danh sách đơn vị (phân trang) |
| `GET` | `/api/org-units/{id}` | Lấy đơn vị theo ID |
| `PUT` | `/api/org-units/{id}` | Cập nhật đơn vị |
| `DELETE` | `/api/org-units/{id}` | Xóa đơn vị |

#### Người dùng — `/api/users`

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| `POST` | `/api/users` | Tạo người dùng |
| `GET` | `/api/users` | Danh sách người dùng (phân trang) — hỗ trợ `?status=active` để lọc chỉ user đang hoạt động |
| `GET` | `/api/users/{id}` | Lấy người dùng theo ID |
| `PUT` | `/api/users/{id}` | Cập nhật người dùng (bao gồm `dataAccessFromYear`) |
| `DELETE` | `/api/users/{id}` | Xóa mềm người dùng |
| `POST` | `/api/users/{id}/roles` | Gán role cho người dùng — body: `{ "roleId": ... }` |
| `DELETE` | `/api/users/{id}/roles/{roleId}` | Thu hồi role khỏi người dùng |
| `PUT` | `/api/users/{id}/revoke` | Khóa tài khoản (chuyển status → `locked`) |
| `PUT` | `/api/users/{id}/reactivate` | Mở khóa tài khoản (chuyển status → `active`) |

#### Vai trò — `/api/roles`

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| `POST` | `/api/roles` | Tạo vai trò |
| `GET` | `/api/roles` | Danh sách vai trò (phân trang) |
| `GET` | `/api/roles/{id}` | Lấy vai trò theo ID |
| `PUT` | `/api/roles/{id}` | Cập nhật vai trò |
| `DELETE` | `/api/roles/{id}` | Xóa vai trò (không xóa role hệ thống) |
| `POST` | `/api/roles/{id}/permissions` | Gán quyền cho vai trò — body: `{ "permissionId": ... }` |
| `DELETE` | `/api/roles/{id}/permissions/{permissionId}` | Thu hồi quyền khỏi vai trò |
| `GET` | `/api/roles/{id}/permissions` | Danh sách quyền đã gán cho vai trò |
| `GET` | `/api/roles/{id}/members` | Danh sách thành viên (user) thuộc vai trò |

#### Quyền hạn — `/api/permissions`

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| `POST` | `/api/permissions` | Tạo quyền hạn |
| `GET` | `/api/permissions` | Danh sách quyền (phân trang) |
| `GET` | `/api/permissions/{id}` | Lấy quyền theo ID |
| `PUT` | `/api/permissions/{id}` | Cập nhật quyền |
| `DELETE` | `/api/permissions/{id}` | Xóa quyền |

---

### 2.2 Activity — Hoạt động

#### `/api/activities`

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| `POST` | `/api/activities` | Tạo hoạt động |
| `GET` | `/api/activities` | Danh sách hoạt động (phân trang) |
| `GET` | `/api/activities/{id}` | Lấy hoạt động theo ID |
| `PUT` | `/api/activities/{id}` | Cập nhật hoạt động |
| `DELETE` | `/api/activities/{id}` | Xóa hoạt động |
| `POST` | `/api/activities/import-bulk` | Nhập hàng loạt hoạt động từ file Excel/CSV |

---

### 2.3 Contact — Liên hệ

#### Liên hệ — `/api/contacts`

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| `POST` | `/api/contacts` | Tạo liên hệ — nhận `phones[]` để lưu kèm SĐT trong 1 transaction; bổ sung field V6 (salutation, title, department, workEmail, personalEmail, zalo, source, doNotCall, doNotEmail) |
| `GET` | `/api/contacts` | Danh sách liên hệ (phân trang) |
| `GET` | `/api/contacts/{id}` | Lấy liên hệ theo ID |
| `PUT` | `/api/contacts/{id}` | Cập nhật liên hệ |
| `DELETE` | `/api/contacts/{id}` | Xóa mềm liên hệ |
| `GET` | `/api/contacts/deleted` | Thùng rác — danh sách liên hệ đã xóa (30 ngày) |
| `POST` | `/api/contacts/{id}/restore` | Khôi phục liên hệ từ thùng rác |
| `DELETE` | `/api/contacts/{id}/purge` | Xóa vĩnh viễn khỏi thùng rác |
| `POST` | `/api/contacts/import-bulk` | Nhập hàng loạt liên hệ từ file Excel/CSV |

#### Số điện thoại — `/api/contacts/{contactId}/phones`

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| `POST` | `/api/contacts/{contactId}/phones` | Thêm số điện thoại cho liên hệ |
| `GET` | `/api/contacts/{contactId}/phones` | Danh sách SĐT của liên hệ |
| `PUT` | `/api/contacts/{contactId}/phones/{id}` | Cập nhật số điện thoại |
| `DELETE` | `/api/contacts/{contactId}/phones/{id}` | Xóa số điện thoại |

---

### 2.4 Customer — Khách hàng

#### Khách hàng — `/api/customers`

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| `POST` | `/api/customers` | Tạo khách hàng |
| `GET` | `/api/customers` | Danh sách khách hàng (phân trang) |
| `GET` | `/api/customers/{id}` | Lấy khách hàng theo ID |
| `PUT` | `/api/customers/{id}` | Cập nhật khách hàng |
| `DELETE` | `/api/customers/{id}` | Xóa mềm khách hàng |
| `GET` | `/api/customers/deleted` | Thùng rác — danh sách khách hàng đã xóa (30 ngày) |
| `POST` | `/api/customers/{id}/restore` | Khôi phục khách hàng từ thùng rác |
| `DELETE` | `/api/customers/{id}/purge` | Xóa vĩnh viễn khỏi thùng rác |
| `POST` | `/api/customers/import-bulk` | Nhập hàng loạt khách hàng từ file Excel/CSV |
| `POST` | `/api/customers/handover-bulk` | Bàn giao nhiều khách hàng sang người dùng khác — body: `{ ids, toUserId, reason? }` |

#### Chia sẻ khách hàng — `/api/customers/{customerId}/shares`

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| `POST` | `/api/customers/{customerId}/shares` | Chia sẻ khách hàng cho user |
| `GET` | `/api/customers/{customerId}/shares` | Danh sách user được chia sẻ |
| `DELETE` | `/api/customers/{customerId}/shares/{userId}` | Thu hồi quyền truy cập |

---

### 2.5 Lead — Tiềm năng

#### Lead — `/api/leads`

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| `POST` | `/api/leads` | Tạo lead |
| `GET` | `/api/leads` | Danh sách lead (phân trang) |
| `GET` | `/api/leads/{id}` | Lấy lead theo ID |
| `PUT` | `/api/leads/{id}` | Cập nhật lead |
| `DELETE` | `/api/leads/{id}` | Xóa mềm lead |
| `GET` | `/api/leads/deleted` | Thùng rác — danh sách lead đã xóa (30 ngày) |
| `POST` | `/api/leads/{id}/restore` | Khôi phục lead từ thùng rác |
| `DELETE` | `/api/leads/{id}/purge` | Xóa vĩnh viễn khỏi thùng rác (ẩn UI, DB giữ) |
| `POST` | `/api/leads/import-bulk` | Nhập hàng loạt lead từ file Excel/CSV (hỗ trợ CREATE/UPDATE/BOTH) |
| `POST` | `/api/leads/handover-bulk` | Bàn giao nhiều lead sang người dùng khác — body: `{ ids, toUserId, reason? }` |

> **Hoạt động của lead** không còn endpoint riêng `/api/leads/{leadId}/activities` (đã gỡ `LeadActivityController`). Ghi nhận hoạt động cho lead qua phân hệ Hoạt động dùng chung: `POST /api/activities` với `targetType=lead`, `targetId={leadId}` — và sẽ tự cộng điểm `leads.score`. Cộng điểm/web tracking xem [Tracking](#212-tracking--web-tracking--chấm-điểm-tiềm-năng-public).

#### Chuyển giao lead — `/api/leads/{leadId}/transfers`

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| `POST` | `/api/leads/{leadId}/transfers` | Tạo lệnh chuyển giao lead |
| `GET` | `/api/leads/{leadId}/transfers` | Lịch sử chuyển giao |
| `PUT` | `/api/leads/{leadId}/transfers/{id}` | Cập nhật chuyển giao |
| `DELETE` | `/api/leads/{leadId}/transfers/{id}` | Xóa bản ghi chuyển giao |

---

### 2.6 Opportunity — Cơ hội bán hàng

#### Cơ hội — `/api/opportunities`

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| `POST` | `/api/opportunities` | Tạo cơ hội — nhận `items[]` lưu kèm dòng hàng trong 1 transaction; bổ sung field V6 (opportunityType, expectedRevenue, source, winLossReason, description) |
| `GET` | `/api/opportunities` | Danh sách cơ hội (phân trang) |
| `GET` | `/api/opportunities/{id}` | Lấy cơ hội theo ID |
| `PUT` | `/api/opportunities/{id}` | Cập nhật cơ hội |
| `DELETE` | `/api/opportunities/{id}` | Xóa mềm cơ hội |
| `GET` | `/api/opportunities/deleted` | Thùng rác — danh sách cơ hội đã xóa (30 ngày) |
| `POST` | `/api/opportunities/{id}/restore` | Khôi phục cơ hội từ thùng rác |
| `DELETE` | `/api/opportunities/{id}/purge` | Xóa vĩnh viễn khỏi thùng rác |
| `POST` | `/api/opportunities/import-bulk` | Nhập hàng loạt cơ hội từ file Excel/CSV |
| `POST` | `/api/opportunities/handover-bulk` | Bàn giao nhiều cơ hội sang người dùng khác — body: `{ ids, toUserId, reason? }` |

#### Giai đoạn cơ hội — `/api/opportunity-stages`

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| `POST` | `/api/opportunity-stages` | Tạo giai đoạn |
| `GET` | `/api/opportunity-stages` | Danh sách giai đoạn (phân trang) |
| `GET` | `/api/opportunity-stages/{id}` | Lấy giai đoạn theo ID |
| `PUT` | `/api/opportunity-stages/{id}` | Cập nhật giai đoạn |
| `DELETE` | `/api/opportunity-stages/{id}` | Xóa giai đoạn |

#### Sản phẩm trong cơ hội — `/api/opportunities/{opportunityId}/items`

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| `POST` | `/api/opportunities/{opportunityId}/items` | Thêm sản phẩm vào cơ hội |
| `GET` | `/api/opportunities/{opportunityId}/items` | Danh sách sản phẩm |
| `PUT` | `/api/opportunities/{opportunityId}/items/{id}` | Cập nhật dòng sản phẩm |
| `DELETE` | `/api/opportunities/{opportunityId}/items/{id}` | Xóa dòng sản phẩm |

---

### 2.6b Campaign — Chiến dịch Marketing (MỚI 2026-07-02)

> Đầu phễu: quản lý chiến dịch + thành viên + gửi email hàng loạt + ROI. `campaign_id` gắn cho lead → chảy xuống cơ hội/đơn hàng/hóa đơn.

#### Chiến dịch — `/api/campaigns`

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| `POST` | `/api/campaigns` | Tạo chiến dịch (type, channel, budget, targetSize, expectedRevenue, ownerId) |
| `GET` | `/api/campaigns` | Danh sách chiến dịch (phân trang) |
| `GET` | `/api/campaigns/{id}` | Lấy chiến dịch theo ID |
| `PUT` | `/api/campaigns/{id}` | Cập nhật chiến dịch |
| `DELETE` | `/api/campaigns/{id}` | Xóa mềm |
| `GET` | `/api/campaigns/deleted` · `POST .../{id}/restore` · `DELETE .../{id}/purge` | Thùng rác |
| `POST` | `/api/campaigns/{id}/schedule\|start\|pause\|complete\|cancel` | Chuyển trạng thái (có guard) |
| `POST` | `/api/campaigns/{id}/send-email` | Gửi email hàng loạt — body `{ subject, body }`, trả số email đã gửi |
| `GET` | `/api/campaigns/{id}/stats` | Thống kê ROI (#member/#lead/#cơ hội thắng/#đơn/doanh thu) |
| `POST` | `/api/campaigns/import-bulk` · `/api/campaigns/handover-bulk` | Nhập / bàn giao hàng loạt |

#### Thành viên — `/api/campaigns/{campaignId}/members`

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| `POST` · `GET` · `PUT .../{id}` · `DELETE .../{id}` | `/api/campaigns/{campaignId}/members` | CRUD thành viên (lead/contact/nhập tay) |

#### Dashboard — `/api/dashboard` (MỚI 2026-07-04)

> Thống kê tổng hợp cho "Bàn làm việc" phân theo vai trò (native COUNT/SUM, không @Scheduled). Tham số `period` = `month\|quarter\|year` (mặc định `year`); KPI kèm % tăng trưởng so kỳ trước; biểu đồ theo tháng dùng cửa sổ 12 tháng gần nhất.

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| `GET` | `/api/dashboard/admin?period=` | **Chỉ ADMIN** (403 nếu khác). Tổng/cơ cấu tài khoản (trạng thái, vai trò, đơn vị), tài khoản mới + chuỗi tháng, số vai trò/quyền, tổng quan bản ghi toàn hệ thống |
| `GET` | `/api/dashboard/manager?period=` | **ADMIN/SALES_MANAGER**. Doanh thu/chi phí(≈giá vốn)/lợi nhuận (KPI + theo tháng), KPI cơ hội, tỷ lệ thắng, phễu chuyển đổi, cơ hội giá trị lớn, trạng thái các phân hệ, việc gấp, **thống kê theo nhân viên** |
| `GET` | `/api/dashboard/sale?period=` | Mọi user đăng nhập — dữ liệu **cá nhân** (`owner_id = userId`), không có phần theo nhân viên |

### 2.6c Order — Đơn hàng (TÁI LẬP 2026-07-02)

> Chèn giữa Báo giá & Hóa đơn: **Báo giá → Đơn hàng → Hóa đơn** (Đơn ↔ Hóa đơn = 1-1). Mirror module Invoice.

#### Đơn hàng — `/api/orders`

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| `POST` · `GET` · `GET .../{id}` · `PUT .../{id}` · `DELETE .../{id}` | `/api/orders` | CRUD (nhận `items[]`, field quotationId/opportunityId/campaignId/orderDate/deliveryDate) |
| `GET` | `/api/orders/deleted` · `POST .../{id}/restore` · `DELETE .../{id}/purge` | Thùng rác |
| `POST` | `/api/orders/{id}/confirm\|process\|complete\|cancel` | Chuyển trạng thái (có guard) |
| `POST` | `/api/orders/{id}/create-invoice` | Xuất hóa đơn 1-1 (khóa đơn + đơn→completed) |
| `POST` | `/api/orders/import-bulk` · `/api/orders/handover-bulk` | Nhập / bàn giao hàng loạt |
| `POST/GET/PUT/DELETE` | `/api/orders/{orderId}/items[/{id}]` | Dòng hàng đơn hàng |
| `POST` | `/api/quotations/{id}/convert-to-order` | Chuyển Báo giá → Đơn hàng (đã gỡ `convert-to-invoice`; báo giá có `campaign_id` truyền attribution sang đơn) |

### 2.7 Invoice — Hóa đơn

> Sinh từ Đơn hàng (cột `order_id` + `campaign_id`). Luồng: Báo giá → Đơn hàng → Hóa đơn.

#### Hóa đơn — `/api/invoices`

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| `POST` | `/api/invoices` | Tạo hóa đơn — nhận `items[]` lưu kèm dòng hàng trong 1 transaction; field: quotationId, opportunityId, invoiceDate, dueDate, currency, exchangeRate, billingAddress, taxCode |
| `GET` | `/api/invoices` | Danh sách hóa đơn (phân trang) |
| `GET` | `/api/invoices/{id}` | Lấy hóa đơn theo ID |
| `PUT` | `/api/invoices/{id}` | Cập nhật hóa đơn (chặn khi đã khóa) |
| `DELETE` | `/api/invoices/{id}` | Xóa mềm hóa đơn |
| `GET` | `/api/invoices/deleted` | Thùng rác — hóa đơn đã xóa (30 ngày) |
| `POST` | `/api/invoices/{id}/restore` | Khôi phục từ thùng rác |
| `DELETE` | `/api/invoices/{id}/purge` | Xóa vĩnh viễn khỏi thùng rác |
| `POST` | `/api/invoices/{id}/issue` | Phát hành (draft → sent) + khóa dữ liệu |
| `POST` | `/api/invoices/{id}/cancel` | Hủy hóa đơn (→ cancelled) |
| `POST` | `/api/invoices/import-bulk` | Nhập hàng loạt từ file Excel/CSV |
| `POST` | `/api/invoices/handover-bulk` | Bàn giao nhiều hóa đơn — body: `{ ids, toUserId, reason? }` |

#### Dòng hàng — `/api/invoices/{invoiceId}/items`

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| `POST` | `/api/invoices/{invoiceId}/items` | Thêm sản phẩm vào hóa đơn |
| `GET` | `/api/invoices/{invoiceId}/items` | Danh sách sản phẩm |
| `PUT` | `/api/invoices/{invoiceId}/items/{id}` | Cập nhật dòng hàng |
| `DELETE` | `/api/invoices/{invoiceId}/items/{id}` | Xóa dòng hàng |

#### Lịch thanh toán — `/api/invoices/{invoiceId}/payment-schedules`

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| `POST` | `/api/invoices/{invoiceId}/payment-schedules` | Tạo đợt thanh toán (tự suy lại paymentStatus + status) |
| `GET` | `/api/invoices/{invoiceId}/payment-schedules` | Danh sách đợt thanh toán |
| `PUT` | `/api/invoices/{invoiceId}/payment-schedules/{id}` | Cập nhật (tự suy lại) |
| `DELETE` | `/api/invoices/{invoiceId}/payment-schedules/{id}` | Xóa (tự suy lại) |

#### Ghi nhận doanh thu — `/api/invoices/{invoiceId}/revenue-records`

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| `POST` | `/api/invoices/{invoiceId}/revenue-records` | Ghi nhận doanh thu |
| `GET` | `/api/invoices/{invoiceId}/revenue-records` | Danh sách doanh thu |
| `PUT` | `/api/invoices/{invoiceId}/revenue-records/{id}` | Cập nhật |
| `DELETE` | `/api/invoices/{invoiceId}/revenue-records/{id}` | Xóa |

---

### 2.7b Service — Dịch vụ sau bán (phiếu hỗ trợ / trả / đổi / khiếu nại)

#### Phiếu hỗ trợ — `/api/tickets`

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| `POST` | `/api/tickets` | Tạo phiếu — tự tra SLA theo priority (set `slaDueAt`), ghi audit log; nhận `returnItems[]` cho trả/đổi. Field: code, type (support/return/exchange/complaint), subject, channel, priority, reason, customerId/contactId/invoiceId/productId, assignedUserId |
| `GET` | `/api/tickets` | Danh sách phiếu (phân trang); `TicketResult.isOverdue` suy ra quá hạn SLA on-read |
| `GET` | `/api/tickets/{id}` | Lấy phiếu theo ID |
| `PUT` | `/api/tickets/{id}` | Cập nhật thông tin (KHÔNG nhận `status`) |
| `DELETE` | `/api/tickets/{id}` | Xóa mềm |
| `GET` | `/api/tickets/deleted` | Thùng rác (30 ngày) |
| `POST` | `/api/tickets/{id}/restore` · `DELETE .../purge` | Khôi phục / xóa vĩnh viễn |
| `POST` | `/api/tickets/handover-bulk` | Bàn giao nhiều phiếu — body: `{ ids, toUserId, reason? }` |
| `POST` | `/api/tickets/{id}/assign` | Giao xử lý (new → assigned) — body: `{ toUserId }`; tạo notification |
| `POST` | `/api/tickets/{id}/start` | Bắt đầu (assigned/reopened → in_progress), set `firstResponseAt` |
| `POST` | `/api/tickets/{id}/resolve` | Giải quyết (support/complaint) — body: `{ resolutionType?, note? }` |
| `POST` | `/api/tickets/{id}/approve` · `/reject` | Duyệt / từ chối trả-đổi (return/exchange); reject body `{ reason }` |
| `POST` | `/api/tickets/{id}/receive` · `/inspect` · `/complete` | Luồng nhận → kiểm → hoàn tất (return/exchange) |
| `POST` | `/api/tickets/{id}/close` · `/reopen` | Đóng / mở lại |
| `POST` | `/api/tickets/{id}/csat` | Ghi CSAT (resolved/closed) — body: `{ score, comment? }` |

#### Dòng hàng trả/đổi — `/api/tickets/{ticketId}/return-items` · Ghi chú — `/api/tickets/{ticketId}/comments`

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| `GET`/`POST`/`PUT`/`DELETE` | `/api/tickets/{ticketId}/return-items[/{id}]` | CRUD dòng hàng trả/đổi |
| `GET` | `/api/tickets/{ticketId}/comments` | Lịch sử (system audit + note người dùng) |
| `POST` | `/api/tickets/{ticketId}/comments` | Thêm ghi chú (type=note) — body: `{ content, isInternal? }` |

#### Chính sách SLA — `/api/sla-policies`

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| `GET` | `/api/sla-policies` | Danh sách chính sách SLA theo priority |

---

### 2.8 Product — Sản phẩm

#### Sản phẩm — `/api/products`

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| `POST` | `/api/products` | Tạo sản phẩm |
| `GET` | `/api/products` | Danh sách sản phẩm (phân trang) |
| `GET` | `/api/products/{id}` | Lấy sản phẩm theo ID |
| `PUT` | `/api/products/{id}` | Cập nhật sản phẩm |
| `DELETE` | `/api/products/{id}` | Xóa mềm sản phẩm |
| `GET` | `/api/products/deleted` | Thùng rác — danh sách sản phẩm đã xóa (30 ngày) |
| `POST` | `/api/products/{id}/restore` | Khôi phục sản phẩm từ thùng rác |
| `DELETE` | `/api/products/{id}/purge` | Xóa vĩnh viễn khỏi thùng rác |
| `POST` | `/api/products/import-bulk` | Nhập hàng loạt sản phẩm từ file Excel/CSV |

#### Danh mục sản phẩm — `/api/product-categories`

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| `POST` | `/api/product-categories` | Tạo danh mục |
| `GET` | `/api/product-categories` | Danh sách danh mục (phân trang) |
| `GET` | `/api/product-categories/{id}` | Lấy danh mục theo ID |
| `PUT` | `/api/product-categories/{id}` | Cập nhật danh mục |
| `DELETE` | `/api/product-categories/{id}` | Xóa danh mục |

---

### 2.9 Pricing — Chính sách giá

#### Chính sách giá — `/api/price-policies`

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| `POST` | `/api/price-policies` | Tạo chính sách giá |
| `GET` | `/api/price-policies` | Danh sách chính sách (phân trang) |
| `GET` | `/api/price-policies/{id}` | Lấy chính sách theo ID |
| `PUT` | `/api/price-policies/{id}` | Cập nhật chính sách |
| `DELETE` | `/api/price-policies/{id}` | Xóa chính sách |

#### Giá theo sản phẩm — `/api/price-policies/{policyId}/products`

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| `POST` | `/api/price-policies/{policyId}/products` | Thêm giá riêng cho sản phẩm |
| `GET` | `/api/price-policies/{policyId}/products` | Danh sách giá sản phẩm |
| `PUT` | `/api/price-policies/{policyId}/products/{id}` | Cập nhật giá sản phẩm |
| `DELETE` | `/api/price-policies/{policyId}/products/{id}` | Xóa giá sản phẩm |

#### Giá theo loại sản phẩm — `/api/price-policies/{policyId}/product-types`

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| `POST` | `/api/price-policies/{policyId}/product-types` | Thêm giá theo loại SP |
| `GET` | `/api/price-policies/{policyId}/product-types` | Danh sách giá loại SP |
| `DELETE` | `/api/price-policies/{policyId}/product-types/{id}` | Xóa giá loại SP |

#### Giá theo khách hàng — `/api/price-policies/{policyId}/customers`

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| `POST` | `/api/price-policies/{policyId}/customers` | Thêm giá riêng cho khách hàng |
| `GET` | `/api/price-policies/{policyId}/customers` | Danh sách giá khách hàng |
| `DELETE` | `/api/price-policies/{policyId}/customers/{id}` | Xóa giá khách hàng |

#### Giá theo nhóm khách hàng — `/api/price-policies/{policyId}/customer-categories`

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| `POST` | `/api/price-policies/{policyId}/customer-categories` | Thêm giá theo nhóm KH |
| `GET` | `/api/price-policies/{policyId}/customer-categories` | Danh sách giá nhóm KH |
| `DELETE` | `/api/price-policies/{policyId}/customer-categories/{id}` | Xóa giá nhóm KH |

#### Giá theo nhân viên — `/api/price-policies/{policyId}/employees`

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| `POST` | `/api/price-policies/{policyId}/employees` | Thêm giá riêng cho nhân viên |
| `GET` | `/api/price-policies/{policyId}/employees` | Danh sách giá nhân viên |
| `DELETE` | `/api/price-policies/{policyId}/employees/{id}` | Xóa giá nhân viên |

---

### 2.10 Quotation — Báo giá

#### Báo giá — `/api/quotations`

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| `POST` | `/api/quotations` | Tạo báo giá — nhận `items[]` lưu kèm dòng hàng trong 1 transaction; bổ sung field V6 (opportunityId, currency, exchangeRate) |
| `GET` | `/api/quotations` | Danh sách báo giá (phân trang) |
| `GET` | `/api/quotations/{id}` | Lấy báo giá theo ID |
| `PUT` | `/api/quotations/{id}` | Cập nhật báo giá |
| `DELETE` | `/api/quotations/{id}` | Xóa mềm báo giá |
| `GET` | `/api/quotations/deleted` | Thùng rác — danh sách báo giá đã xóa (30 ngày) |
| `POST` | `/api/quotations/{id}/restore` | Khôi phục báo giá từ thùng rác |
| `DELETE` | `/api/quotations/{id}/purge` | Xóa vĩnh viễn khỏi thùng rác |
| `POST` | `/api/quotations/import-bulk` | Nhập hàng loạt báo giá từ file Excel/CSV |
| `POST` | `/api/quotations/handover-bulk` | Bàn giao nhiều báo giá sang người dùng khác — body: `{ ids, toUserId, reason? }` |

#### Dòng sản phẩm báo giá — `/api/quotations/{quotationId}/items`

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| `POST` | `/api/quotations/{quotationId}/items` | Thêm sản phẩm vào báo giá |
| `GET` | `/api/quotations/{quotationId}/items` | Danh sách sản phẩm báo giá |
| `PUT` | `/api/quotations/{quotationId}/items/{id}` | Cập nhật dòng sản phẩm |
| `DELETE` | `/api/quotations/{quotationId}/items/{id}` | Xóa dòng sản phẩm |

#### Phê duyệt báo giá — `/api/quotations/{quotationId}/approvals`

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| `POST` | `/api/quotations/{quotationId}/approvals` | Tạo yêu cầu phê duyệt |
| `GET` | `/api/quotations/{quotationId}/approvals` | Lịch sử phê duyệt |
| `PUT` | `/api/quotations/{quotationId}/approvals/{id}` | Cập nhật trạng thái phê duyệt |
| `DELETE` | `/api/quotations/{quotationId}/approvals/{id}` | Xóa bản ghi phê duyệt |

---

### 2.11 Handover — Bàn giao công việc

#### Bàn giao toàn bộ — `/api/handover`

| Method | Endpoint | Mô tả | Auth yêu cầu |
|--------|----------|-------|--------------|
| `POST` | `/api/handover/all` | Bàn giao toàn bộ bản ghi của fromUser sang toUser trên 5 module (leads, customers, opportunities, quotations, orders) | Chỉ ADMIN / SALES_MANAGER |

**Request body:**
```json
{ "fromUserId": 2, "toUserId": 3, "reason": "Lý do bàn giao (tùy chọn)" }
```

**Quyền:** `SecurityUtils.isAdminOrManager()` kiểm tra trong Spring SecurityContext — trả 403 nếu không đủ quyền.

---

### 2.12 Tracking — Web tracking & Chấm điểm tiềm năng (public)

#### `/api/tracking` — không yêu cầu đăng nhập

Phục vụ trang landing demo: tạo tiềm năng ẩn danh, ghi sự kiện & cộng điểm `leads.score`.

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| `POST` | `/api/tracking/visit` | Lượt truy cập — body `{ code? }`; trả lead theo mã hoặc tạo lead ẩn danh mới (mã `TNW…`, score=0) |
| `POST` | `/api/tracking/score` | Ghi sự kiện (`lead_tracking_events`) + cộng điểm — body `{ code, action, label?, points? }` |
| `POST` | `/api/tracking/submit` | Nộp form liên hệ + cộng điểm — body `{ code, name, companyName?, email?, phone?, note?, points? }` |

> Khi tổng điểm vượt 50 (lần đầu): lead tự chuyển `qualified` + sinh thông báo cho owner và các user ADMIN/SALES_MANAGER/SALES_STAFF (`AddLeadScoreUseCase`). Tạo Activity gắn lead (`targetType=lead`) cũng cộng điểm (call=10, meeting=20, email=5, task=5, note=2).

---

### 2.13 Notification — Thông báo

#### `/api/notifications` — theo `userId` trong JWT

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| `GET` | `/api/notifications` | Danh sách thông báo của người dùng hiện tại |
| `GET` | `/api/notifications/unread-count` | Đếm số thông báo chưa đọc |
| `POST` | `/api/notifications/{id}/read` | Đánh dấu một thông báo đã đọc |
| `POST` | `/api/notifications/read-all` | Đánh dấu tất cả đã đọc |

Mỗi thông báo trả về `{ id, type, title, content, leadId, targetId, isRead, createdAt }`.

- **`type`**: tiền tố trước dấu `_` trùng module key — `lead_hot`; `quotation_pending|approved|rejected|accepted|customer_response`; `ticket_assigned|resolved`.
- **`targetId`** (cột `notifications.target_id`): ID bản ghi đích (lead/quotation/ticket) mà thông báo trỏ tới. Frontend dùng để điều hướng tới danh sách module + focus đúng dòng. Set tại `CreateNotificationUseCase.execute(recipients, type, title, content, leadId, targetId)`.

> DB đang chạy cần bổ sung cột: `ALTER TABLE notifications ADD COLUMN target_id INT UNSIGNED NULL;` (TiDB: mỗi lệnh ALTER chỉ thêm một cột).

### 2.13 Endpoint chuyển trạng thái (workflow — 2026-06-24)

Trạng thái không sửa tay qua `PUT`; đổi qua các endpoint hành động (có guard `ensureCanTransitionTo`, trả 400 nếu bước sai):

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| `POST` | `/api/leads/{id}/convert` | qualified → converted; **tách thành Khách hàng + Liên hệ + Cơ hội** rồi khóa lead |
| `POST` | `/api/leads/{id}/lose` | → lost (body `{reason}`) |
| `POST` | `/api/customers/{id}/activate` \| `/deactivate` | active ↔ inactive |
| `POST` | `/api/activities/{id}/start` \| `/complete` \| `/cancel` | planned→in_progress→done / cancelled |
| `POST` | `/api/invoices/{id}/issue` \| `/cancel` | draft→sent (khóa) / cancelled |
| `POST` | `/api/quotations/{id}/submit` \| `/approve` \| `/reject` \| `/send` \| `/accept` | draft→pending→approved/rejected→sent→accepted (approve/reject cần ADMIN/SALES_MANAGER; **send** gửi email khách kèm **PDF bảng báo giá** + 3 nút phản hồi, sinh `response_token`). **send** nhận body tùy chọn `{ subject, body }` — người dùng tự soạn tiêu đề/nội dung; bỏ trống thì dùng mặc định. 3 nút phản hồi luôn được BE tự chèn vào cuối |
| `GET`  | `/api/quotations/{id}/email-draft` | Lấy nội dung email mặc định (`{ toEmail, recipientName, subject, body }`) để FE hiển thị trong ô soạn trước khi gửi |
| `GET`  | `/api/public/quotations/{token}` | (public) Xem báo giá theo token để khách phản hồi |
| `POST` | `/api/public/quotations/{token}/respond` | (public) Khách phản hồi — body `{ action: accept\|adjust\|reject, note? }`; `accept`→accepted + thông báo người phụ trách |
| `POST` | `/api/quotations/from-opportunity/{opportunityId}` | Clone báo giá từ cơ hội (OLI→QLI, đặt primary nếu là báo giá đầu) |
| `POST` | `/api/quotations/{id}/sync-items-from-opportunity` | Cập nhật lại dòng hàng báo giá theo cơ hội nguồn (xóa + clone lại OLI→QLI, giữ `opportunityItemId`) |
| `POST` | `/api/quotations/{id}/set-primary` | Đặt báo giá đồng bộ (chỉ 1 primary/cơ hội) |
| `POST` | `/api/quotations/{id}/convert-to-order` | Chuyển Báo giá → Đơn hàng (khóa báo giá + cơ hội won); báo giá lưu `campaign_id` (attribution) chảy sang đơn hàng |
| `GET`  | `/api/pricing/resolve?pricePolicyId&productId&quantity` | Tra đơn giá/chiết khấu theo chính sách giá (pricebook) |

- **Cơ hội**: status suy ra từ `stageId`; `amount` **roll-up** từ dòng hàng (cập nhật khi sửa dòng hàng hoặc khi sync từ báo giá primary).
- **Báo giá đồng bộ (primary)**: sửa dòng hàng báo giá primary tự **sync** về dòng cơ hội + roll-up lại amount.
- **Hóa đơn — paymentStatus/status**: suy ra tự động từ tổng `paidAmount` các `/payment-schedules` (không nhận tay).
- DB: toàn bộ schema (gồm enum `pending`/`accepted`, luồng Báo giá→Hóa đơn, pricebook) đã hợp nhất trong `diagrams/crm.sql` — chỉ cần chạy `crm.sql` rồi `data.sql`.

### 2.14 Copilot — Trợ lý AI hỏi đáp CRM (MỚI)

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| `POST` | `/api/copilot/ask` | Hỏi trợ lý AI. Body `{ question }` → trả `{ answer }` (văn bản tiếng Việt). Cần JWT. |

- **RAG có cấu trúc (structured RAG), KHÔNG train, KHÔNG vector DB**: `CopilotContextRepositoryImpl` chạy native SQL gom ngữ cảnh thật từ DB rồi nhồi vào prompt; mô hình chỉ diễn giải. Mọi con số do SQL tính.
- Ngữ cảnh gồm 2 loại: **(A) số liệu tổng hợp** (doanh thu kỳ này/kỳ trước, đếm KH/SP/cơ hội mở/HĐ quá hạn, tỷ lệ thắng, tỷ lệ chốt đơn, phễu — dùng lại `PeriodRanges` như Dashboard) và **(B) phễu bản ghi cụ thể** khi câu hỏi nhắc tên/mã khách (cơ hội→báo giá→đơn→hóa đơn→ticket).
- **Phân quyền dữ liệu**: ADMIN/SALES_MANAGER xem toàn bộ (`ownerId=null`); nhân viên chỉ xem bản ghi `owner_id = userId` (kỳ + phễu đều lọc theo owner).
- **Nhà cung cấp**: Google Gemini qua `GeminiAiServiceImpl` (adapter của port `IAiService`) — dùng `RestClient` của spring-web, **không thêm dependency**. Đổi nhà cung cấp chỉ cần thay implementation.
- **Cấu hình** (externalize như JWT/mail): `app.ai.api-key` (env `APP_AI_API_KEY` — bắt buộc, không commit), `app.ai.model` (mặc định `gemini-flash-latest` — model chạy được trên free tier; `gemini-2.0-flash` bị giới hạn quota 0 nên **không dùng free tier**), `app.ai.base-url`. Dev đặt key trong `application-local.properties`.

---

## 3. Cấu trúc folder/file

Tất cả source code nằm trong:
```
be-crm/src/main/java/vn/com/be_crm/
```

### 3.1 Root & Shared

| File / Folder | Tầng | Công dụng |
|---------------|------|-----------|
| `BeCrmApplication.java` | — | Điểm khởi động Spring Boot |
| `domain/shared/exception/DomainException.java` | Domain | Base exception cho lỗi nghiệp vụ |
| `domain/shared/exception/NotFoundException.java` | Domain | Exception khi không tìm thấy entity |
| `domain/shared/model/AuditInfo.java` | Domain | Embedded class chứa `createdAt`, `updatedAt`, `deletedAt` |
| `application/shared/usecase/IUseCase.java` | Application | Interface generic `IUseCase<Input, Output>` — marker cho tất cả use case |
| `application/shared/dto/PageRequest.java` | Application | DTO input cho query phân trang (page, size, sortBy, sortDir) |
| `application/shared/dto/PageResult.java` | Application | DTO output trả danh sách phân trang |
| `application/shared/dto/ImportBulkResult.java` | Application | Output DTO nhập hàng loạt: `record(int successCount, int failedCount, List<ImportRowError> errors)` |
| `application/shared/dto/ImportRowError.java` | Application | Lỗi từng dòng: `record(int row, String message)` — row bắt đầu từ 2 (dòng 1 là header) |
| `presentation/shared/ApiResponse.java` | Presentation | Wrapper chuẩn cho mọi HTTP response đơn lẻ |
| `presentation/shared/PageResponse.java` | Presentation | Wrapper chuẩn cho HTTP response phân trang |
| `presentation/shared/GlobalExceptionHandler.java` | Presentation | Bắt exception toàn cục, trả lỗi dạng chuẩn |
| `infrastructure/shared/config/SecurityConfig.java` | Infrastructure | Spring Security: stateless JWT, CORS cho localhost:5173, permit /api/auth/login và /api/auth/activate; trả **401** (không phải 403) khi token hết hạn/không hợp lệ |
| `infrastructure/shared/security/JwtAuthFilter.java` | Infrastructure | OncePerRequestFilter — extract Bearer token, set SecurityContext |
| `infrastructure/shared/security/JwtProvider.java` | Infrastructure | Generate + validate JWT (JJWT 0.12.x, HS256, 24h) |
| `infrastructure/shared/security/BcryptPasswordEncoderImpl.java` | Infrastructure | BCrypt password verification |
| `application/shared/security/ITokenProvider.java` | Application | Interface generate token |
| `application/shared/security/IPasswordEncoder.java` | Application | Interface verify password |
| `infrastructure/shared/config/HibernateConfig.java` | Infrastructure | Cấu hình Hibernate SessionFactory (truyền thủ công properties) |
| `infrastructure/shared/config/BeanConfig.java` | Infrastructure | Wire tất cả UseCase — DI configuration trung tâm |

### 3.2 Module Auth

| File / Folder | Tầng | Công dụng |
|---------------|------|-----------|
| `domain/auth/entity/OrgUnit.java` | Domain | Entity đơn vị tổ chức (phòng ban, chi nhánh) |
| `domain/auth/entity/User.java` | Domain | Entity người dùng hệ thống (soft delete) |
| `domain/auth/entity/Role.java` | Domain | Entity vai trò |
| `domain/auth/entity/Permission.java` | Domain | Entity quyền hạn |
| `domain/auth/entity/UserRole.java` | Domain | Bảng nối user ↔ role |
| `domain/auth/entity/RolePermission.java` | Domain | Bảng nối role ↔ permission |
| `domain/auth/enums/UserStatus.java` | Domain | Enum trạng thái user (ACTIVE, INACTIVE) |
| `domain/auth/repository/IOrgUnitRepository.java` | Domain | Interface thao tác DB cho OrgUnit |
| `domain/auth/repository/IUserRepository.java` | Domain | Interface thao tác DB cho User |
| `domain/auth/repository/IRoleRepository.java` | Domain | Interface thao tác DB cho Role |
| `domain/auth/repository/IPermissionRepository.java` | Domain | Interface thao tác DB cho Permission |
| `domain/auth/repository/IUserRoleRepository.java` | Domain | Interface thao tác DB cho UserRole |
| `domain/auth/repository/IRolePermissionRepository.java` | Domain | Interface thao tác DB cho RolePermission |
| `application/auth/command/Create|Update|DeleteOrgUnitUseCase.java` | Application | Use case CRUD cho OrgUnit |
| `application/auth/query/Get|ListOrgUnitUseCase.java` | Application | Use case truy vấn OrgUnit |
| `application/auth/command/Create|Update|DeleteUserUseCase.java` | Application | Use case CRUD cho User |
| `application/auth/query/Get|ListUserUseCase.java` | Application | Use case truy vấn User |
| `application/auth/command/Create|Update|DeleteRoleUseCase.java` | Application | Use case CRUD cho Role |
| `application/auth/query/Get|ListRoleUseCase.java` | Application | Use case truy vấn Role |
| `application/auth/command/Create|Update|DeletePermissionUseCase.java` | Application | Use case CRUD cho Permission |
| `application/auth/query/Get|ListPermissionUseCase.java` | Application | Use case truy vấn Permission |
| `application/auth/command/AssignUserRoleUseCase.java` | Application | Gán role cho user |
| `application/auth/command/RevokeUserRoleUseCase.java` | Application | Thu hồi role khỏi user |
| `application/auth/command/AssignRolePermissionUseCase.java` | Application | Gán permission cho role |
| `application/auth/command/RevokeRolePermissionUseCase.java` | Application | Thu hồi permission khỏi role |
| `application/auth/dto/*Result.java` | Application | Output DTO cho OrgUnit/User/Role/Permission |
| `application/auth/dto/Create|Update*Command.java` | Application | Input DTO cho command |
| `application/auth/dto/Assign*Command.java` | Application | Input DTO cho assign/revoke |
| `application/auth/mapper/*CommandMapper.java` | Application | Chuyển đổi domain entity ↔ DTO |
| `presentation/auth/OrgUnitController.java` | Presentation | REST endpoint `/api/org-units` |
| `presentation/auth/UserController.java` | Presentation | REST endpoint `/api/users` |
| `presentation/auth/RoleController.java` | Presentation | REST endpoint `/api/roles` |
| `presentation/auth/PermissionController.java` | Presentation | REST endpoint `/api/permissions` |
| `presentation/auth/request/*.java` | Presentation | Request DTO đầu vào (validate input HTTP) |
| `infrastructure/auth/entity/*Hibernate.java` | Infrastructure | Hibernate entity mapping bảng DB |
| `infrastructure/auth/mapper/*HibernateMapper.java` | Infrastructure | Chuyển đổi Hibernate entity ↔ domain entity |
| `infrastructure/auth/repository/*RepositoryImpl.java` | Infrastructure | Triển khai repository dùng Hibernate Session |

### 3.3 Module Activity

| File / Folder | Tầng | Công dụng |
|---------------|------|-----------|
| `domain/activity/entity/Activity.java` | Domain | Entity hoạt động (gắn với bất kỳ đối tượng nào qua polymorphic targetType/targetId) |
| `domain/activity/enums/ActivityType.java` | Domain | Loại hoạt động (call, meeting, email...) |
| `domain/activity/enums/ActivityStatus.java` | Domain | Trạng thái hoạt động |
| `domain/activity/repository/IActivityRepository.java` | Domain | Interface thao tác DB |
| `application/activity/command/Create|Update|DeleteActivityUseCase.java` | Application | Use case CRUD |
| `application/activity/query/Get|ListActivityUseCase.java` | Application | Use case truy vấn |
| `application/activity/dto/ActivityResult.java` | Application | Output DTO |
| `application/activity/dto/Create|UpdateActivityCommand.java` | Application | Input DTO command |
| `application/activity/mapper/ActivityCommandMapper.java` | Application | Mapper DTO ↔ domain |
| `presentation/activity/ActivityController.java` | Presentation | REST endpoint `/api/activities` |
| `infrastructure/activity/entity/ActivityHibernate.java` | Infrastructure | Hibernate entity |
| `infrastructure/activity/mapper/ActivityHibernateMapper.java` | Infrastructure | Hibernate ↔ domain mapper |
| `infrastructure/activity/repository/ActivityRepositoryImpl.java` | Infrastructure | Triển khai repository |

### 3.4 Module Contact

| File / Folder | Tầng | Công dụng |
|---------------|------|-----------|
| `domain/contact/entity/Contact.java` | Domain | Entity liên hệ (soft delete) |
| `domain/contact/entity/ContactPhone.java` | Domain | Số điện thoại của liên hệ |
| `domain/contact/enums/ContactGender.java` | Domain | Giới tính |
| `domain/contact/enums/PhoneType.java` | Domain | Loại số điện thoại (mobile, work...) |
| `domain/contact/repository/IContactRepository.java` | Domain | Interface thao tác DB cho Contact |
| `domain/contact/repository/IContactPhoneRepository.java` | Domain | Interface thao tác DB cho ContactPhone |
| `application/contact/command|query/...UseCase.java` | Application | Use case CRUD + query cho Contact & Phone |
| `application/contact/dto/*.java` | Application | Input/Output DTO |
| `application/contact/mapper/*.java` | Application | Mapper |
| `presentation/contact/ContactController.java` | Presentation | REST endpoint `/api/contacts` |
| `presentation/contact/ContactPhoneController.java` | Presentation | REST endpoint `/api/contacts/{contactId}/phones` |
| `infrastructure/contact/...` | Infrastructure | Hibernate entity, mapper, repository impl |

### 3.5 Module Customer

| File / Folder | Tầng | Công dụng |
|---------------|------|-----------|
| `domain/customer/entity/Customer.java` | Domain | Entity khách hàng (soft delete) |
| `domain/customer/entity/CustomerShare.java` | Domain | Bản ghi chia sẻ khách hàng cho user |
| `domain/customer/enums/CustomerStatus.java` | Domain | Trạng thái khách hàng |
| `domain/customer/enums/CustomerType.java` | Domain | Loại khách hàng |
| `domain/customer/enums/CustomerSharePermission.java` | Domain | Mức quyền chia sẻ (view/edit) |
| `domain/customer/repository/I*Repository.java` | Domain | 2 interface repository |
| `application/customer/...` | Application | Use case CRUD + query cho Customer & CustomerShare |
| `presentation/customer/CustomerController.java` | Presentation | REST `/api/customers` |
| `presentation/customer/CustomerShareController.java` | Presentation | REST `/api/customers/{id}/shares` |
| `infrastructure/customer/...` | Infrastructure | Hibernate entity, mapper, repository impl |

### 3.6 Module Lead

| File / Folder | Tầng | Công dụng |
|---------------|------|-----------|
| `domain/lead/entity/Lead.java` | Domain | Entity lead bán hàng (soft delete; có `score`) |
| `domain/lead/entity/LeadTrackingEvent.java` | Domain | Sự kiện web tracking (action, label, points) |
| `domain/lead/entity/LeadTransfer.java` | Domain | Lịch sử chuyển giao lead giữa nhân viên |
| `domain/lead/enums/LeadStatus.java` | Domain | Trạng thái lead — `new_` thay cho `new` (Java keyword) |
| `domain/lead/repository/I*Repository.java` | Domain | interface repository (Lead, LeadTrackingEvent, LeadTransfer) |
| `application/lead/command/AddLeadScoreUseCase.java` | Application | Cộng điểm dùng chung; ngưỡng 50 → `qualified` + thông báo |
| `application/lead/command/{TrackVisit,RecordTrackingEvent,SubmitTrackingForm}UseCase.java` | Application | Web tracking (visit/score/submit) |
| `application/lead/...` | Application | Use case CRUD cho Lead và LeadTransfer |
| `presentation/lead/LeadController.java` | Presentation | REST `/api/leads` |
| `presentation/lead/LeadTransferController.java` | Presentation | REST `/api/leads/{id}/transfers` |
| `presentation/tracking/TrackingController.java` | Presentation | REST `/api/tracking/*` (public) |
| `infrastructure/lead/converter/LeadStatusConverter.java` | Infrastructure | `AttributeConverter` map DB `"new"` ↔ Java `LeadStatus.new_` |
| `infrastructure/lead/...` | Infrastructure | Hibernate entity, mapper, repository impl |

> Hoạt động của lead đã gộp vào module Activity (`targetType=lead`) — không còn `LeadActivity`/`LeadActivityController`/`LeadActivityType`.

### 3.7 Module Opportunity

| File / Folder | Tầng | Công dụng |
|---------------|------|-----------|
| `domain/opportunity/entity/Opportunity.java` | Domain | Entity cơ hội bán hàng (soft delete) |
| `domain/opportunity/entity/OpportunityStage.java` | Domain | Giai đoạn pipeline (flags isWon, isLost) |
| `domain/opportunity/entity/OpportunityItem.java` | Domain | Sản phẩm trong cơ hội |
| `domain/opportunity/enums/OpportunityStatus.java` | Domain | Trạng thái cơ hội |
| `domain/opportunity/repository/I*Repository.java` | Domain | 3 interface repository |
| `application/opportunity/...` | Application | Use case cho cả 3 entity |
| `presentation/opportunity/OpportunityController.java` | Presentation | REST `/api/opportunities` |
| `presentation/opportunity/OpportunityStageController.java` | Presentation | REST `/api/opportunity-stages` |
| `presentation/opportunity/OpportunityItemController.java` | Presentation | REST `/api/opportunities/{id}/items` |
| `infrastructure/opportunity/...` | Infrastructure | Hibernate entity, mapper, repository impl |

### 3.8 Module Order

| File / Folder | Tầng | Công dụng |
|---------------|------|-----------|
| `domain/order/entity/Order.java` | Domain | Entity đơn hàng (soft delete, self-FK parentOrderId cho đơn phụ) |
| `domain/order/entity/OrderItem.java` | Domain | Dòng sản phẩm trong đơn |
| `domain/order/entity/OrderPaymentSchedule.java` | Domain | Lịch thanh toán định kỳ |
| `domain/order/entity/OrderDeliveryTracking.java` | Domain | Theo dõi trạng thái giao hàng |
| `domain/order/entity/OrderRevenueRecord.java` | Domain | Ghi nhận doanh thu khi thanh toán |
| `domain/order/enums/OrderStatus.java` | Domain | Trạng thái đơn hàng |
| `domain/order/enums/OrderType.java` | Domain | Loại đơn hàng |
| `domain/order/enums/PaymentStatus.java` | Domain | Trạng thái thanh toán |
| `domain/order/enums/DeliveryStatus.java` | Domain | Trạng thái giao hàng |
| `domain/order/enums/PaymentScheduleStatus.java` | Domain | Trạng thái lịch thanh toán |
| `domain/order/repository/I*Repository.java` | Domain | 5 interface repository |
| `application/order/...` | Application | Use case cho cả 5 entity |
| `presentation/order/OrderController.java` | Presentation | REST `/api/orders` |
| `presentation/order/OrderItemController.java` | Presentation | REST `/api/orders/{id}/items` |
| `presentation/order/OrderPaymentScheduleController.java` | Presentation | REST `/api/orders/{id}/payment-schedules` |
| `presentation/order/OrderDeliveryTrackingController.java` | Presentation | REST `/api/orders/{id}/delivery-tracking` |
| `presentation/order/OrderRevenueRecordController.java` | Presentation | REST `/api/orders/{id}/revenue-records` |
| `infrastructure/order/...` | Infrastructure | Hibernate entity, mapper, repository impl |

### 3.9 Module Product

| File / Folder | Tầng | Công dụng |
|---------------|------|-----------|
| `domain/product/entity/Product.java` | Domain | Entity sản phẩm (soft delete) |
| `domain/product/entity/ProductCategory.java` | Domain | Danh mục sản phẩm (self-referencing — danh mục cha/con) |
| `domain/product/enums/ProductType.java` | Domain | Loại sản phẩm |
| `domain/product/repository/I*Repository.java` | Domain | 2 interface repository |
| `application/product/...` | Application | Use case CRUD + query cho Product & Category |
| `presentation/product/ProductController.java` | Presentation | REST `/api/products` |
| `presentation/product/ProductCategoryController.java` | Presentation | REST `/api/product-categories` |
| `infrastructure/product/...` | Infrastructure | Hibernate entity, mapper, repository impl |

### 3.10 Module Pricing

| File / Folder | Tầng | Công dụng |
|---------------|------|-----------|
| `domain/pricing/entity/PricePolicy.java` | Domain | Entity chính sách giá |
| `domain/pricing/entity/PricePolicyProduct.java` | Domain | Giá riêng theo từng sản phẩm |
| `domain/pricing/entity/PricePolicyProductType.java` | Domain | Giá riêng theo loại sản phẩm |
| `domain/pricing/entity/PricePolicyCustomer.java` | Domain | Giá riêng theo từng khách hàng |
| `domain/pricing/entity/PricePolicyCustomerCategory.java` | Domain | Giá riêng theo nhóm khách hàng |
| `domain/pricing/entity/PricePolicyEmployee.java` | Domain | Giá riêng theo nhân viên bán hàng |
| `domain/pricing/enums/PricePolicyStatus.java` | Domain | Trạng thái chính sách |
| `domain/pricing/enums/DiscountType.java` | Domain | Loại chiết khấu (%, số tiền cố định) |
| `domain/pricing/repository/I*Repository.java` | Domain | 6 interface repository |
| `application/pricing/...` | Application | Use case cho PricePolicy và 5 sub-entity |
| `presentation/pricing/PricePolicyController.java` | Presentation | REST `/api/price-policies` |
| `presentation/pricing/PricePolicyProductController.java` | Presentation | REST `/api/price-policies/{id}/products` |
| `presentation/pricing/PricePolicyProductTypeController.java` | Presentation | REST `/api/price-policies/{id}/product-types` |
| `presentation/pricing/PricePolicyCustomerController.java` | Presentation | REST `/api/price-policies/{id}/customers` |
| `presentation/pricing/PricePolicyCustomerCategoryController.java` | Presentation | REST `/api/price-policies/{id}/customer-categories` |
| `presentation/pricing/PricePolicyEmployeeController.java` | Presentation | REST `/api/price-policies/{id}/employees` |
| `infrastructure/pricing/...` | Infrastructure | Hibernate entity, mapper, repository impl |

### 3.11 Module Quotation

| File / Folder | Tầng | Công dụng |
|---------------|------|-----------|
| `domain/quotation/entity/Quotation.java` | Domain | Entity báo giá (soft delete) |
| `domain/quotation/entity/QuotationItem.java` | Domain | Dòng sản phẩm trong báo giá |
| `domain/quotation/entity/QuotationApproval.java` | Domain | Bản ghi phê duyệt báo giá |
| `domain/quotation/enums/QuotationStatus.java` | Domain | Trạng thái báo giá |
| `domain/quotation/enums/QuotationApprovalStatus.java` | Domain | Trạng thái phê duyệt (pending/approved/rejected) |
| `domain/quotation/repository/I*Repository.java` | Domain | 3 interface repository |
| `application/quotation/...` | Application | Use case cho cả 3 entity |
| `presentation/quotation/QuotationController.java` | Presentation | REST `/api/quotations` |
| `presentation/quotation/QuotationItemController.java` | Presentation | REST `/api/quotations/{id}/items` |
| `presentation/quotation/QuotationApprovalController.java` | Presentation | REST `/api/quotations/{id}/approvals` |
| `infrastructure/quotation/...` | Infrastructure | Hibernate entity, mapper, repository impl |

### 3.12 Module Notification

| File / Folder | Tầng | Công dụng |
|---------------|------|-----------|
| `domain/notification/entity/Notification.java` | Domain | Entity thông báo (recipientUserId, type, title, content, leadId, targetId, isRead) |
| `domain/notification/repository/INotificationRepository.java` | Domain | Interface repository |
| `application/notification/command/{CreateNotification,MarkNotificationRead}UseCase.java` | Application | Tạo / đánh dấu đã đọc |
| `application/notification/query/{ListMyNotifications,CountUnreadNotifications}UseCase.java` | Application | Danh sách / đếm chưa đọc |
| `presentation/notification/NotificationController.java` | Presentation | REST `/api/notifications` |
| `infrastructure/notification/...` | Infrastructure | Hibernate entity, mapper, repository impl |
| `infrastructure/shared/config/beans/NotificationBeanConfig.java` | Infrastructure | Wire các use case Notification |

> **Module Warehouse (Kho hàng) đã được gỡ hoàn toàn** — không còn `domain/application/infrastructure/presentation/warehouse`.

---

## 4. Quy ước chung

### Đặt tên

| Loại | Quy tắc | Ví dụ |
|------|---------|-------|
| Interface | Tiền tố `I` | `IUserRepository` |
| Implementation | Hậu tố `Impl` | `UserRepositoryImpl` |
| Use case command | Suffix `UseCase` | `CreateUserUseCase` |
| Hibernate entity | Suffix `Hibernate` | `UserHibernate` |
| Request DTO | Suffix `Request` | `CreateUserRequest` |
| Output DTO | Suffix `Result` | `UserResult` |
| Command DTO | Suffix `Command` | `CreateUserCommand` |

### Resolve tên khóa ngoại trong DTO danh sách (INameResolver)

Các DTO danh sách (`LeadResult`, `CustomerResult`, `OpportunityResult`, `QuotationResult`,
`OrderResult`, `InvoiceResult`, `ContactResult`, `ActivityResult`, `ProductResult`,
`CampaignResult`, `TicketResult`, `TicketCommentResult`) trả kèm **tên khóa ngoại** (`ownerName`,
`customerName`, `contactName`, `quotationCode`…) để FE hiển thị trực tiếp — không còn resolve `#id`
phía client.

- Port `application/shared/lookup/INameResolver` + impl `infrastructure/shared/lookup/NameResolverImpl`:
  native `SELECT id, <col> FROM <table> WHERE id IN (:ids)`, **không lọc `deleted_at`/`is_purged`**
  (tên vẫn resolve cho bản ghi đã xóa mềm/vĩnh viễn). `ids` rỗng → trả map rỗng (không chạy query).
- Helper `application/shared/lookup/NameEnricher.apply(items, Result::getFkId, names::table, Result::setFkName)`
  gom ID → tra một lần → set tên. Gọi trong `List<Module>UseCase`; wire thêm tham số `INameResolver`
  vào `@Bean list*UseCase(...)`.

### Soft delete

Các entity hỗ trợ soft delete: User, Contact, Customer, Lead, Opportunity, Quotation, Order, Product.

- Xóa = set `deletedAt = LocalDateTime.now()`
- Truy vấn tự động thêm `WHERE deleted_at IS NULL`

### UserStatus enum

| Giá trị | Ý nghĩa |
|---------|---------|
| `active` | Tài khoản đang hoạt động |
| `inactive` | Chưa kích hoạt (mới đăng ký, chưa qua link email) |
| `locked` | Đã bị admin thu hồi — không thể đăng nhập |

### dataAccessFromYear — lọc data theo năm

- Field `data_access_from_year` trên bảng `users` (kiểu `SMALLINT UNSIGNED NULL`)
- Tự set = năm hiện tại khi nhân viên kích hoạt tài khoản (`ActivateAccountUseCase`)
- Nhúng vào JWT claim, `JwtAuthFilter` trích xuất và set `request.setAttribute("dataAccessFromYear", ...)`
- Tất cả 9 controller list đọc attribute này và truyền vào `PageRequest.dataAccessFromYear`
- Repository thêm `AND YEAR(createdAt) >= :fromYear` (hoặc `WHERE YEAR(...)` cho entity không soft delete) khi `dataAccessFromYear != null`
- Admin có thể sửa qua `PUT /api/users/{id}` với body `{ "dataAccessFromYear": 2025 }`

### Hibernate 7 API

```java
// Đúng (Hibernate 7 / Spring Boot 4)
session.merge(entity);      // thay saveOrUpdate — dùng giá trị trả về
session.remove(entity);     // thay delete
session.find(Type.class, id); // thay get

// Luôn dùng try-with-resources
try (Session session = sessionFactory.openSession()) { ... }
```

### Lưu ý LeadStatus

DB lưu chuỗi `"new"` nhưng `new` là keyword Java. Giải pháp:
- Enum dùng `new_` với `@JsonValue`/`@JsonCreator` trả `"new"` trong JSON
- `LeadStatusConverter` (`AttributeConverter`) map giữa DB và Java enum

### Import UPDATE/BOTH (2026-06-13)

`importType` của `POST /api/{module}/import-bulk` hỗ trợ `CREATE` / `UPDATE` / `BOTH` cho **7 module**: lead (phone/email), product (sku), contact (email), customer (taxCode), opportunity / order / quotation (code). UPDATE dò bản ghi tồn tại qua repo `findBy*` (HQL `WHERE {key} = :v AND deletedAt IS NULL`); thấy → merge giữ id/code/FK/createdAt + cập nhật field từ row; không thấy & isCreate → tạo mới. Row DTO của opportunity/order/quotation có thêm field `code` để dò. **Activity chỉ CREATE** (không có khóa duy nhất).

---

## Triển khai production — Render (Docker)

Backend deploy lên **Render** bằng **Docker** (`be-crm/Dockerfile`, multi-stage: build fat jar bằng Maven wrapper → chạy trên `eclipse-temurin:21-jre`, cài sẵn `fonts-dejavu` để PDF báo giá in được tiếng Việt trên Linux).

### Cấu hình đã externalize
`application.properties` (commit lên git) **không chứa secret** — các giá trị nhạy cảm để `${ENV_VAR:}` (default rỗng). Khi chạy **local**, profile mặc định `local` nạp `application-local.properties` (gitignore, chứa secret thật). Khi chạy **production**, Render cấp secret qua biến môi trường. Ngoài ra:
- `server.port=${PORT:8080}` — bind theo biến `PORT` mà Render inject.
- `app.pdf.font-path` default = `/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf` (Linux). Chạy local Windows đặt env `APP_PDF_FONT_PATH=C:/Windows/Fonts/arial.ttf`.
- `app.cors.allowed-origins` (mới) — danh sách origin cho phép, phân tách bằng dấu phẩy; `SecurityConfig` đọc qua `@Value` thay cho hardcode `localhost:5173`. **Không** wildcard `*` vì `allowCredentials=true`.

### Biến môi trường cần đặt trên Render
| Env var | Ý nghĩa |
|---------|---------|
| `SPRING_DATASOURCE_URL` / `SPRING_DATASOURCE_USERNAME` / `SPRING_DATASOURCE_PASSWORD` | Kết nối TiDB |
| `APP_JWT_SECRET` | Khóa ký JWT (>= 32 ký tự) |
| `SPRING_MAIL_USERNAME` / `SPRING_MAIL_PASSWORD` | Gmail SMTP + App Password |
| `APP_FRONTEND_BASE_URL` | URL frontend Netlify (link kích hoạt + phản hồi báo giá trong email) |
| `APP_CORS_ALLOWED_ORIGINS` | URL frontend Netlify (cho CORS) |
| `APP_AI_API_KEY` | Khóa API Google Gemini (AI Studio) cho trợ lý Copilot. `APP_AI_MODEL`/`APP_AI_BASE_URL` tùy chọn (có default) |

### Các bước
1. Render → **New → Web Service**, connect repo, **Root Directory = `be-crm`**, Runtime = **Docker**.
2. Nhập các env var ở bảng trên (URL Netlify điền sau khi có domain frontend).
3. Deploy → lấy domain `https://<ten-service>.onrender.com`.
> Free tier: service sleep sau ~15 phút không request (cold start ~30–60s). Vì secret cũ đã commit lên git, nên **rotate** mật khẩu TiDB / JWT secret / Gmail App Password rồi nhập giá trị mới vào Render.
