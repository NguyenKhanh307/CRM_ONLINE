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
| `sortBy` | `createdAt` | Trường sắp xếp (whitelist tên field — sai → fallback `createdAt`) |
| `sortDir` | `desc` | Chiều sắp xếp (`asc`/`desc`) |
| `q` | — | Tìm kiếm server-side (LIKE trên các cột chính: code/name/email/phone... tùy module) |
| `status` | — | Lọc theo tag nhanh của module (đa số = `status`; ticket → `type`; contact → `isPrimary`; product → `isActive`) |

**Lọc theo năm (`dataAccessFromYear`):** Giá trị này **không** là query param — được trích xuất từ JWT claim (`JwtAuthFilter` set vào `request.setAttribute`). Controller đọc ra và truyền vào `PageRequest`. Repository tự động thêm `AND YEAR(createdAt) >= :fromYear` vào HQL khi giá trị không null. Nhân viên mới được tự set `dataAccessFromYear` = năm kích hoạt tài khoản.

**Lọc theo người phụ trách (record-level visibility, 2026-07-12):** Nhân viên (không phải ADMIN/SALES_MANAGER) chỉ nhận bản ghi có `owner_id` (hoặc `assigned_user_id` với contact/activity/ticket) = userId của mình — suy tự động từ JWT, không phải query param. Product/notification không lọc owner.

**Phân quyền (2026-07-12):** JWT chứa claim `permissions` (`module.action`); endpoint side-effect được guard bằng `@PreAuthorize` (xóa/khôi phục/xóa vĩnh viễn → `<module>.delete`; import-bulk → `<module>.create`; duyệt báo giá/gửi email → `quotation.approve`; phát hành/hủy hóa đơn → `invoice.approve`; workflow ticket → `ticket.process`/`ticket.approve_return`; sản phẩm/danh mục → ADMIN/SALES_MANAGER). `/api/auth/register-employee` + mutating `/api/users|roles|permissions/**` chỉ ADMIN; `/api/handover/all` ADMIN/SALES_MANAGER. Thiếu quyền → **403** `{"message":"Bạn không có quyền thực hiện thao tác này","status":403}`.

---

## 2. Danh sách API theo module

### 2.1 Auth — Xác thực & Phân quyền

#### Đăng nhập / Kích hoạt tài khoản — `/api/auth`

| Method | Endpoint | Mô tả | Auth yêu cầu |
|--------|----------|-------|--------------|
| `POST` | `/api/auth/login` | Đăng nhập, trả JWT token. Email phải là @gmail.com | Không |
| `POST` | `/api/auth/google` | Đăng nhập bằng Google — xác thực ID token, chỉ cho vào nếu email có trong bảng `users` + active; tự điền avatar từ ảnh Google khi trống | Không |
| `POST` | `/api/auth/register-employee` | Admin đăng ký tài khoản nhân viên, gửi email kích hoạt | Bearer JWT (ADMIN) |
| `POST` | `/api/auth/activate` | Nhân viên kích hoạt tài khoản và đặt mật khẩu lần đầu | Không |
| `GET` | `/api/auth/me` | Lấy hồ sơ của người dùng đang đăng nhập | Bearer JWT |
| `PUT` | `/api/auth/me` | Người dùng tự sửa hồ sơ (fullName, phone, avatarUrl) | Bearer JWT |
| `POST` | `/api/auth/change-password` | Người dùng tự đổi mật khẩu (xác minh mật khẩu hiện tại) | Bearer JWT |

**POST /api/auth/login — Request:**
```json
{ "email": "admin@gmail.com", "password": "12345678" }
```

**POST /api/auth/google — Request:**
```json
{ "idToken": "<Google ID token do Google Identity Services cấp>" }
```

**POST /api/auth/register-employee — Request:**
```json
{ "email": "nhanvien@gmail.com", "fullName": "Nguyễn Văn B", "phone": "0901234567", "unitId": 1, "roleId": 3 }
```

**POST /api/auth/activate — Request:**
```json
{ "token": "550e8400-e29b-41d4-a716-446655440000", "newPassword": "MyPass@2026" }
```

**PUT /api/auth/me — Request:**
```json
{ "fullName": "Nguyễn Văn A", "phone": "0901234567", "avatarUrl": "https://res.cloudinary.com/.../avatar.jpg" }
```

**POST /api/auth/change-password — Request:**
```json
{ "currentPassword": "MyPass@2026", "newPassword": "MyNewPass@2026" }
```

> **Đăng nhập Google** cần env `APP_GOOGLE_CLIENT_ID` = OAuth Client ID (Web) tạo ở Google Cloud Console → Credentials; dùng làm audience khi xác thực ID token (`app.google.client-id`). Thiếu env → `/api/auth/google` trả lỗi cấu hình.

Tất cả các endpoint khác đều yêu cầu header: `Authorization: Bearer <token>`

> **Phân hệ Đơn vị tổ chức (`/api/org-units`) đã GỠ HẲN 2026-07-24** — CRM phục vụ công ty nhỏ, không cần cây tổ chức. Quản lý nhận thông báo nay xác định theo **vai trò** (`SALES_MANAGER`), xem `ManagerResolverImpl`.

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
| `GET` | `/api/contacts/{id}` | Lấy liên hệ theo ID (kèm tên khóa ngoại) |
| `GET` | `/api/contacts/{id}/related` | **MỚI** — bản ghi liên quan cho trang chi tiết: cơ hội / báo giá / đơn / hóa đơn / phiếu CS / hoạt động (theo `contact_id`). Kiểm quyền một lần trên liên hệ (`assignedUserId`) → 403 |
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
| `GET` | `/api/customers/{id}/related` | **MỚI** — bản ghi liên quan cho trang chi tiết 360°: liên hệ / cơ hội / báo giá / đơn / hóa đơn / phiếu CS / hoạt động. Quyền kiểm **một lần** trên khách hàng (không phải owner → 403); qua được thì thấy **đủ** bản ghi con kể cả của đồng nghiệp |

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
| `GET` | `/api/leads/{id}` | Lấy lead theo ID (kèm tên khóa ngoại) |
| `GET` | `/api/leads/{id}/related` | **MỚI** — bản ghi liên quan cho trang chi tiết: cơ hội đã convert + hoạt động. Kiểm quyền một lần trên lead (`ownerId`) → 403 |
| `PUT` | `/api/leads/{id}` | Cập nhật lead |
| `DELETE` | `/api/leads/{id}` | Xóa mềm lead |
| `GET` | `/api/leads/deleted` | Thùng rác — danh sách lead đã xóa (30 ngày) |
| `POST` | `/api/leads/{id}/restore` | Khôi phục lead từ thùng rác |
| `DELETE` | `/api/leads/{id}/purge` | Xóa vĩnh viễn khỏi thùng rác (ẩn UI, DB giữ) |
| `POST` | `/api/leads/import-bulk` | Nhập hàng loạt lead từ file Excel/CSV (hỗ trợ CREATE/UPDATE/BOTH) |
| `POST` | `/api/leads/handover-bulk` | Bàn giao nhiều lead sang người dùng khác — body: `{ ids, toUserId, reason? }` |
| `POST` | `/api/leads/{id}/qualify` | **MỚI** — đánh dấu đủ điều kiện thủ công (new/contacting → qualified), không cần đủ 50 điểm |
| `POST` | `/api/leads/{id}/convert` | Chuyển đổi (qualified → converted) → tạo KH + LH + Cơ hội. Body **tùy chọn** `{ customerId?, contactId? }` — dùng lại bản ghi có sẵn thay vì tạo trùng |
| `POST` | `/api/leads/{id}/lose` | Đánh mất (→ lost), body `{ reason? }` |

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
| `POST` | `/api/opportunities` | Tạo cơ hội — nhận `items[]` lưu kèm dòng hàng trong 1 transaction; bổ sung field V6 (opportunityType, expectedRevenue, source, winLossReason, description). ⚠️ Body **không** nhận `status` lẫn `probability` — cả hai suy ra từ giai đoạn pipeline |
| `GET` | `/api/opportunities` | Danh sách cơ hội (phân trang) |
| `GET` | `/api/opportunities/{id}` | Lấy cơ hội theo ID |
| `PUT` | `/api/opportunities/{id}` | Cập nhật cơ hội. ⚠️ Body **không** nhận `status` lẫn `probability` — cả hai suy lại từ giai đoạn sau cập nhật (kể cả khi request không đổi giai đoạn) |
| `DELETE` | `/api/opportunities/{id}` | Xóa mềm cơ hội |
| `GET` | `/api/opportunities/deleted` | Thùng rác — danh sách cơ hội đã xóa (30 ngày) |
| `POST` | `/api/opportunities/{id}/restore` | Khôi phục cơ hội từ thùng rác |
| `DELETE` | `/api/opportunities/{id}/purge` | Xóa vĩnh viễn khỏi thùng rác |
| `POST` | `/api/opportunities/import-bulk` | Nhập hàng loạt cơ hội từ file Excel/CSV |
| `POST` | `/api/opportunities/handover-bulk` | Bàn giao nhiều cơ hội sang người dùng khác — body: `{ ids, toUserId, reason? }` |
| `GET` | `/api/opportunities/board` | **MỚI** — dữ liệu bảng Kanban: cột = giai đoạn (kèm số cơ hội + tổng tiền), tối đa 50 thẻ/cột. Param `q` tìm theo mã/tên. Lọc owner như list |
| `POST` | `/api/opportunities/{id}/stage` | **MỚI** — đổi giai đoạn (kéo-thả Kanban), body `{ stageId, winLossReason? }`. Trạng thái won/lost/open **tự suy ra** từ giai đoạn (`OpportunityStatus.fromStage`) và `probability` lấy theo `opportunity_stages.probability`; stage không tồn tại → 404 |
| `GET` | `/api/opportunities/{id}/related` | **MỚI** — bản ghi liên quan cho trang chi tiết 360°: báo giá / đơn hàng / hóa đơn / hoạt động (mỗi nhóm tối đa 50 dòng + tổng số thật) |

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
| `GET` | `/api/campaigns/{id}/related` | **MỚI** — bản ghi **quy về** chiến dịch cho trang chi tiết: tiềm năng / cơ hội / đơn hàng / hóa đơn (chiều đọc ngược của attribution). Kiểm quyền một lần trên chiến dịch (`ownerId`) → 403 |
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
| `POST` · `GET` · `GET .../{id}` · `PUT .../{id}` · `DELETE .../{id}` | `/api/orders` | CRUD (nhận `items[]`, field quotationId/opportunityId/campaignId/orderDate/deliveryDate). ⚠️ `GET ?q=` chỉ tìm theo **mã đơn** (`likeClause(q, "code")`) — tên khách hàng không nằm trên bảng `orders` nên không tìm được; frontend dùng tham số này cho ô chọn đơn ở form hóa đơn |
| `GET` | `/api/orders/deleted` · `POST .../{id}/restore` · `DELETE .../{id}/purge` | Thùng rác |
| `POST` | `/api/orders/{id}/confirm\|process\|complete\|cancel` | Chuyển trạng thái (có guard) |
| `POST` | `/api/orders/{id}/create-invoice` | Xuất hóa đơn 1-1 (khóa đơn + đơn→completed) |
| `POST` | `/api/orders/import-bulk` · `/api/orders/handover-bulk` | Nhập / bàn giao hàng loạt |
| `POST/GET/PUT/DELETE` | `/api/orders/{orderId}/items[/{id}]` | Dòng hàng đơn hàng |
| `GET` | `/api/orders/{id}/related` | **MỚI** — bản ghi liên quan cho trang chi tiết: hóa đơn + hoạt động. Kiểm quyền một lần trên đơn hàng (`ownerId`) → 403 |
| `POST` | `/api/quotations/{id}/convert-to-order` | Chuyển Báo giá → Đơn hàng (đã gỡ `convert-to-invoice`; báo giá có `campaign_id` truyền attribution sang đơn) |

### 2.7 Invoice — Hóa đơn

> Sinh từ Đơn hàng (cột `order_id` + `campaign_id`). Luồng: Báo giá → Đơn hàng → Hóa đơn.

#### Hóa đơn — `/api/invoices`

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| `POST` | `/api/invoices` | Tạo hóa đơn — nhận `items[]` lưu kèm dòng hàng trong 1 transaction; field: quotationId, opportunityId, **orderId**, campaignId, invoiceDate, dueDate, currency, exchangeRate, billingAddress, taxCode |
| `GET` | `/api/invoices` | Danh sách hóa đơn (phân trang) |
| `GET` | `/api/invoices/{id}` | Lấy hóa đơn theo ID (kèm tên khóa ngoại) |
| `GET` | `/api/invoices/{id}/related` | **MỚI** — bản ghi liên quan cho trang chi tiết: phiếu chăm sóc + hoạt động. Kiểm quyền một lần trên hóa đơn (`ownerId`) → 403 |
| `PUT` | `/api/invoices/{id}` | Cập nhật hóa đơn (chặn khi đã khóa) |
| `DELETE` | `/api/invoices/{id}` | Xóa mềm hóa đơn |
| `GET` | `/api/invoices/deleted` | Thùng rác — hóa đơn đã xóa (30 ngày) |
| `POST` | `/api/invoices/{id}/restore` | Khôi phục từ thùng rác |
| `DELETE` | `/api/invoices/{id}/purge` | Xóa vĩnh viễn khỏi thùng rác |
| `POST` | `/api/invoices/{id}/issue` | Phát hành (draft → sent) + khóa dữ liệu |
| `POST` | `/api/invoices/{id}/cancel` | Hủy hóa đơn (→ cancelled) |
| `POST` | `/api/invoices/import-bulk` | Nhập hàng loạt từ file Excel/CSV |
| `POST` | `/api/invoices/handover-bulk` | Bàn giao nhiều hóa đơn — body: `{ ids, toUserId, reason? }` |

> **Tham số `customerId` cho `GET /api/contacts` (2026-07-22)**: thu hẹp danh sách liên hệ theo khách hàng. Sinh ra vì ô chọn Liên hệ trong form không thể nạp sẵn cả bảng (hàng chục nghìn dòng) — nay lọc theo khách đang chọn, và phần tự điền "liên hệ chính" cũng hỏi qua đường này. `PageRequest.customerId` hiện **chỉ `/api/contacts` dùng**.
> `INameResolver` có thêm `orderCodes` + `invoiceCodes`; `TicketResult` trả kèm `invoiceCode` để form Chăm sóc hiện đúng mã hóa đơn đang gắn.

- **`orderId` — hóa đơn này thu tiền cho đơn hàng nào** (cột `invoices.order_id`). Được gán tự động khi đi luồng chuẩn `POST /api/orders/{id}/create-invoice`, và **nay nhận được cả khi tạo/sửa trực tiếp** (`POST`/`PUT /api/invoices/{id}`). `InvoiceResult` trả kèm **`orderCode`** (enrich qua `INameResolver.orderCodes` ở cả `ListInvoiceUseCase` lẫn `GetInvoiceUseCase`) để frontend hiện mã đơn ở danh sách + trang chi tiết.
> ⚠️ Bẫy đã tái diễn **lần thứ hai**: `InvoiceController.update` dựng lại `UpdateInvoiceCommand` bằng builder, trước đây bỏ sót `campaignId` (vá 2026-07-22) rồi lại bỏ sót `orderId`. Mapper null-guard nên dữ liệu cũ không mất, nhưng field **không bao giờ set được** qua `PUT`. Thêm field vào `Update*Command` thì phải kiểm luôn chuỗi builder trong controller.

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
| `POST` | `/api/quotations` | Tạo báo giá — nhận `items[]` lưu kèm dòng hàng trong 1 transaction; bổ sung field V6 (opportunityId, currency, exchangeRate) và **`campaignId`** (attribution khi tạo trực tiếp, không clone từ cơ hội) |
| `GET` | `/api/quotations` | Danh sách báo giá (phân trang) |
| `GET` | `/api/quotations/{id}` | Lấy báo giá theo ID (kèm tên khóa ngoại) |
| `GET` | `/api/quotations/{id}/related` | **MỚI** — bản ghi liên quan cho trang chi tiết: đơn hàng / hóa đơn phát sinh + hoạt động. Kiểm quyền một lần trên báo giá (`ownerId`) → 403 |
| `PUT` | `/api/quotations/{id}` | Cập nhật báo giá (body nhận cả **`campaignId`**) |
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
| `GET` | `/api/tracking/campaigns` | **MỚI** — chiến dịch đang chạy (`running`/`scheduled`) cho landing page chọn nguồn. Chỉ trả **id + mã + tên** (không ngân sách/chi phí/người phụ trách) vì phục vụ khách ẩn danh |
| `POST` | `/api/tracking/visit` | Lượt truy cập — body `{ code?, campaignId? }`; trả lead theo mã hoặc tạo lead ẩn danh mới (mã `TNW…`, score=0). `campaignId` gắn theo **first-touch**: chỉ điền khi lead chưa có chiến dịch, khách quay lại qua link khác không ghi đè nguồn ban đầu |
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
| `POST` | `/api/notifications/delete-bulk` | Xóa mềm các thông báo được chọn — body `{ ids: [...] }`, trả số dòng đã xóa |
| `POST` | `/api/notifications/delete-all` | Xóa mềm toàn bộ thông báo của tôi (dọn sạch hộp thông báo) |

Mỗi thông báo trả về `{ id, type, title, content, leadId, targetId, isRead, createdAt }`.

- **`type`**: tiền tố trước dấu `_` trùng module key — `lead_hot`; `quotation_pending|approved|rejected|accepted|customer_response`; `ticket_assigned|resolved`; `lead|customer|opportunity|quotation|order` + `_assigned` (được giao/bàn giao bản ghi); `handover_all` (nhận bàn giao toàn bộ công việc, không gắn bản ghi cụ thể).
- **`targetId`** (cột `notifications.target_id`): ID bản ghi đích (lead/quotation/ticket) mà thông báo trỏ tới. Frontend dùng để điều hướng tới danh sách module + focus đúng dòng. Set tại `CreateNotificationUseCase.execute(recipients, type, title, content, leadId, targetId)`.
- **Xóa mềm** (cột `notifications.deleted_at`, 2026-07-22): mỗi thông báo là **một dòng riêng của một người nhận**, nên xóa chỉ ảnh hưởng hộp thông báo của chính người đó — người nhận khác của cùng sự kiện vẫn giữ nguyên tin. Dòng đã xóa bị loại khỏi `findByRecipient`, `countUnread` và cả hai query đánh dấu đã đọc. **Không** có luồng khôi phục và **không** có tab trong Thùng rác (thông báo không phải bản ghi nghiệp vụ); dữ liệu giữ lại trong DB chỉ để tra cứu. Query xóa luôn kèm `recipientUserId` nên không xóa được thông báo của người khác.

> DB đang chạy cần bổ sung cột (TiDB: mỗi lệnh ALTER chỉ thêm một cột):
> ```sql
> ALTER TABLE notifications ADD COLUMN target_id INT UNSIGNED NULL;
> ALTER TABLE notifications ADD COLUMN deleted_at DATETIME NULL;
> ```

#### Phạm vi người nhận (thu hẹp 2026-07-12)

Trước đây `lead_hot` broadcast cho **mọi** user có role ADMIN + SALES_MANAGER + SALES_STAFF, và `quotation_pending` cho mọi ADMIN + SALES_MANAGER → chuông 99+ cho mỗi sale. Nay:

| Thông báo | Người nhận |
|-----------|-----------|
| `lead_hot` | Owner của tiềm năng **+ quản lý trực tiếp** của owner |
| `quotation_pending` | **Chỉ quản lý trực tiếp** của người phụ trách báo giá |
| `quotation_approved/rejected/accepted/customer_response`, `ticket_*` | Không đổi (vốn đã chỉ gửi owner/người xử lý) |

**Bổ sung 2026-07-22 — báo cho người được giao việc.** Bản ghi **chưa có người phụ trách** (vd tiềm năng do web tracking tạo, `owner_id` NULL) chỉ quản lý nhận thông báo, và nhân viên cũng không xem được bản ghi vì list API lọc `owner_id`. Khi quản lý **gán hoặc bàn giao** người phụ trách thì chính người nhận việc được báo:

| Nơi phát | Thông báo |
|----------|-----------|
| `Update{Lead,Customer,Opportunity,Quotation,Order}UseCase` khi `ownerId` đổi | `<module>_assigned` — "Bạn được giao \<noun\> \<mã\>", `targetId` = ID bản ghi |
| `HandoverBulk{Lead,Customer,Opportunity,Quotation,Order}UseCase` | `<module>_assigned` gộp một dòng — "Bạn được bàn giao N \<noun\>" (số lấy từ `ids` gửi lên) |
| `HandoverAllUseCase` | `handover_all` — một dòng duy nhất, không gắn bản ghi |

Dùng chung `application/shared/notify/NotifyAssignmentUseCase`; **bỏ qua** khi không có người nhận hoặc người thao tác tự giao việc cho chính mình — biết "ai đang thao tác" qua port `application/shared/security/ICurrentUser` ↔ `infrastructure/shared/security/CurrentUserImpl` (đọc `CurrentUserHolder`, dùng chung ThreadLocal của cơ chế đóng dấu `created_by`/`updated_by`).

"Quản lý trực tiếp" = toàn bộ user mang vai trò **`SALES_MANAGER`** (loại chính người thao tác). Port `application/shared/notify/IManagerResolver` + impl `infrastructure/shared/notify/ManagerResolverImpl`.

> **Đổi 2026-07-24**: bản đầu suy quản lý từ cây đơn vị (`users.unit_id` → `org_units.manager_id`, leo `parent_id`), không tìm được thì mới fallback về role. Phân hệ Đơn vị đã gỡ hẳn (công ty nhỏ không cần cây tổ chức) → nhánh fallback trở thành đường duy nhất, bỏ được `MAX_DEPTH` + 2 native query + khóa ngoại vòng `users.unit_id ↔ org_units.manager_id`.

### 2.13c Audit — Ghi "ai tạo / ai sửa cuối" tự động (MỚI 2026-07-12)

Không có endpoint mới. Mọi bảng nghiệp vụ **đã có sẵn** 2 cột `created_by` / `updated_by`; nay chúng được ghi **tự động ở tầng Hibernate** và trả về trong Result DTO của **11 module** (lead, contact, customer, opportunity, quotation, order, invoice, activity, product, campaign, ticket) dưới dạng `createdBy` / `updatedBy` + `createdByName` / `updatedByName` (resolve qua `INameResolver`).

| File | Vai trò |
|------|---------|
| `infrastructure/shared/audit/CurrentUserHolder.java` | ThreadLocal giữ userId của request; set ở `JwtAuthFilter`, **clear trong `finally`** |
| `infrastructure/shared/audit/IAuditable.java` | Đánh dấu Hibernate entity có 2 cột audit |
| `infrastructure/shared/audit/AuditInterceptor.java` | `onPersist` → ghi `created_by`; `onFlushDirty` → ghi `updated_by` + khôi phục `created_by` |
| `infrastructure/shared/audit/AuditStamper.java` | Đóng dấu trước `merge` — để body JSON của `PUT` đúng |
| `infrastructure/shared/config/HibernateConfig.java` | `builder.setInterceptor(new AuditInterceptor())` |

**Bug đã vá:** trước đây `*HibernateMapper.toHibernate()` không set `createdBy`, trong khi entity **có** map cột đó → `session.merge()` ghi `NULL` đè lên `created_by` trong DB **mỗi lần sửa bản ghi**. Dữ liệu người tạo đang mất dần. Nay chặn bằng `@Column(name="created_by", updatable = false)` — cột này không bao giờ nằm trong câu `UPDATE`.

**Ngữ nghĩa NULL:** lead do `/api/tracking/*` (công khai, không JWT) tạo → `created_by IS NULL` là **đúng** (khách ẩn danh tạo, không có người dùng nào). Bản ghi seed chưa từng sửa → `updated_by IS NULL` là **đúng**.

**Bàn giao:** `handoverAll`/`handoverBulk` dùng native bulk `UPDATE` (bypass Hibernate) nên tự ghi `updated_by = :actor, updated_at = NOW()` trong câu SQL (7 repo).

> Seed: `products.created_by` NULL (khối "giữ nguyên" của generator không liệt kê cột này). `data.sql` đã kèm `UPDATE products SET created_by = 1 WHERE created_by IS NULL;` ở cuối. Với DB đang chạy, chạy tay đúng câu đó.

### 2.13b Duplicate — Cảnh báo trùng email / SĐT / MST (MỚI)

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| `GET` | `/api/duplicates/check` | Dò bản ghi trùng trong **tiềm năng / khách hàng / liên hệ**. Param: `email`, `phone`, `taxCode`, `excludeModule`, `excludeId`. Trả `[{ module, id, code, name, matchedField, matchedValue }]` (tối đa 5 dòng/phân hệ) |

Chỉ **cảnh báo**, KHÔNG chặn lưu (nhiều khách dùng chung số tổng đài / email công ty). FE hiện banner vàng ở form Thêm mới của 3 phân hệ trên; khi convert tiềm năng, nếu có khách hàng trùng thì hỏi "dùng KH hiện có hay tạo mới" và gửi `customerId` kèm `POST /api/leads/{id}/convert`.

### 2.13 Endpoint chuyển trạng thái (workflow — 2026-06-24)

Trạng thái không sửa tay qua `PUT`; đổi qua các endpoint hành động (có guard `ensureCanTransitionTo`, trả 400 nếu bước sai):

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| `POST` | `/api/leads/{id}/convert` | qualified → converted; **tách thành Khách hàng + Liên hệ + Cơ hội** rồi khóa lead |
| `POST` | `/api/leads/{id}/lose` | → lost (body `{reason}`) |
| `POST` | `/api/customers/{id}/activate` \| `/deactivate` | active ↔ inactive |
| `POST` | `/api/activities/{id}/start` \| `/complete` \| `/cancel` | planned→in_progress→done / cancelled |
| `POST` | `/api/invoices/{id}/issue` \| `/cancel` | draft→sent (khóa) / cancelled |
| `POST` | `/api/quotations/{id}/submit` \| `/approve` \| `/reject` \| `/send` \| `/accept` | draft→pending→approved/rejected→sent→accepted (approve/reject cần ADMIN/SALES_MANAGER; **send** gửi email khách kèm **PDF bảng báo giá** + 3 nút phản hồi, sinh `response_token`). **send** nhận body tùy chọn `{ to, cc, bcc, subject, body }` — người dùng sửa được **người nhận** và thêm CC/BCC (nhiều email cách nhau dấu phẩy); bỏ trống `to` thì lấy email liên hệ/khách hàng. 3 nút phản hồi luôn được BE tự chèn vào cuối |
| `POST` | `/api/quotations/{id}/mark-sent` | **MỚI** — đánh dấu đã gửi mà KHÔNG gửi email (approved → sent), dùng khi báo giá gửi qua Zalo / in giấy. Vẫn sinh `response_token` để chia sẻ link phản hồi khi cần |
| `GET`  | `/api/quotations/{id}/email-draft` | Lấy nội dung email mặc định (`{ toEmail, recipientName, subject, body }`) để FE hiển thị trong ô soạn trước khi gửi |
| `GET`  | `/api/public/quotations/{token}` | (public) Xem báo giá theo token để khách phản hồi |
| `POST` | `/api/public/quotations/{token}/respond` | (public) Khách phản hồi — body `{ action: accept\|adjust\|reject, note? }`; `accept`→accepted + thông báo người phụ trách |
| `POST` | `/api/quotations/from-opportunity/{opportunityId}` | Clone báo giá từ cơ hội (OLI→QLI, đặt primary nếu là báo giá đầu) |
| `POST` | `/api/quotations/{id}/sync-items-from-opportunity` | Cập nhật lại dòng hàng báo giá theo cơ hội nguồn (xóa + clone lại OLI→QLI, giữ `opportunityItemId`) |
| `POST` | `/api/quotations/{id}/set-primary` | Đặt báo giá đồng bộ (chỉ 1 primary/cơ hội) |
| `POST` | `/api/quotations/{id}/convert-to-order` | Chuyển Báo giá → Đơn hàng (khóa báo giá + cơ hội won); báo giá lưu `campaign_id` (attribution) chảy sang đơn hàng |
| `GET`  | `/api/pricing/resolve?pricePolicyId&productId&quantity` | Tra đơn giá/chiết khấu theo chính sách giá (pricebook) |

- **`/api/pricing/resolve`** trả `{ productId, unitPrice, discount, found, minQty }`. `discount` là **số tiền trên một đơn vị** (đã quy đổi từ `percent`/`amount`); `found = true` chỉ khi sản phẩm có trong chính sách **và** số lượng đã đạt `min_qty`. Ngưỡng `min_qty` được xét **sau** khi tìm ra dòng chính sách (không lọc trong stream) nên **`minQty`** phân biệt được hai trường hợp trượt: `minQty != null` = chưa đủ số lượng, `minQty == null` = sản phẩm ngoài chính sách — frontend dựa vào đó để giải thích cho người dùng thay vì im lặng trả về giá gốc.

> **Seed 2026-07-22**: khối `INSERT INTO products` trong `data.sql` trước đây để `base_price = 0` cho toàn bộ sản phẩm (giá thật nằm ở `cost_price`), khiến chọn hàng hóa trong form luôn ra đơn giá 0. Đã sửa thành `round(cost_price × 1.3)`. DB đang chạy không cần seed lại, chỉ chạy:
> ```sql
> UPDATE products SET base_price = ROUND(cost_price * 1.3, 0) WHERE base_price = 0;
> ```

- **Cơ hội**: status suy ra từ `stageId`; `amount` **roll-up** từ dòng hàng (cập nhật khi sửa dòng hàng hoặc khi sync từ báo giá primary).
- **Báo giá đồng bộ (primary)**: sửa dòng hàng báo giá primary tự **sync** về dòng cơ hội + roll-up lại amount.
- **Hóa đơn — paymentStatus/status**: suy ra tự động từ tổng `paidAmount` các `/payment-schedules` (không nhận tay).
- DB: toàn bộ schema (gồm enum `pending`/`accepted`, luồng Báo giá→Hóa đơn, pricebook) đã hợp nhất trong `diagrams/crm.sql` — chỉ cần chạy `crm.sql` rồi `data.sql`.

### 2.14 Copilot — Trợ lý AI hỏi đáp CRM (MỚI)

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| `POST` | `/api/copilot/ask` | Hỏi trợ lý AI. Body `{ question }` → trả `{ answer, action? }`. Cần JWT. |
| `GET` | `/api/dashboard/revenue-by-campaign?period=` | Doanh thu theo chiến dịch trong kỳ (top 8) — dữ liệu cho trang phân tích `/phan-tich`. ADMIN/manager toàn bộ, nhân viên lọc owner. |

**`action` trong response** (`CopilotAction{type, route, label}`): `type="navigate"` → FE tự điều hướng ngay;
`type="link"` → FE hiện nút (vd "Xem biểu đồ so sánh" → `/phan-tich?period=...`).

**Lệnh điều hướng (không gọi AI — `CopilotIntentDetector` dò keyword tiếng Việt, bỏ dấu khi so khớp):**
- "mở/vào/xem trang `<phân hệ>`" → mở trang danh sách (`/khach-hang`, `/bao-gia`, ...).
- "tạo/thêm `<phân hệ>` mới" → mở form thêm mới (`/{route}/them-moi`).
- "mở `<phân hệ>` `<tên hoặc mã>`" → `findRecord` tìm bản ghi theo tên/mã (**lọc owner theo quyền**) → mở trang chi tiết `/{route}/{id}`; không thấy → "Không tìm thấy ... trong phạm vi của bạn."
- Câu chứa từ nghi vấn (nào/bao nhiêu/không...) không bị nhận nhầm là lệnh → vẫn đi luồng RAG.
- Câu hỏi có ý so sánh/số liệu ("so sánh", "doanh thu", "tỷ lệ"...) → trả lời RAG như thường **+ đính** `action link` tới `/phan-tich?period=<kỳ suy từ câu hỏi>`.

- **RAG có cấu trúc (structured RAG), KHÔNG train, KHÔNG vector DB**: `CopilotContextRepositoryImpl` chạy native SQL gom ngữ cảnh thật từ DB rồi nhồi vào prompt; mô hình chỉ diễn giải. Mọi con số do SQL tính.
- **Các khối ngữ cảnh** (file SQL tách riêng — xem bảng dưới):
  1. **Số liệu tổng hợp**: doanh thu kỳ này/kỳ trước, cơ hội mở, HĐ quá hạn, tỷ lệ thắng, tỷ lệ chốt đơn, phễu giai đoạn.
  2. **Chuỗi 24 tháng**: doanh thu + số hóa đơn từng tháng → trả lời "tháng nào cao nhất", "so sánh tháng 5 với tháng 6", cộng tháng thành quý.
  3. **Khối theo KHOẢNG THỜI GIAN bất kỳ** (`CopilotRangeParser` dò trong câu hỏi): "từ 3/5/2025 tới 4/9/2025", "ngày 3/5/2025", "tháng 5/2025", "quý 2/2025", "năm 2024", "tháng trước", "7 ngày qua"… Mỗi khoảng dựng **đủ mọi phân hệ** (tiềm năng/liên hệ/khách hàng/cơ hội/báo giá/đơn hàng/hóa đơn/chăm sóc/chiến dịch/sản phẩm) kèm **số lượng + phân rã trạng thái + giá trị tiền**. Chạy SQL trực tiếp trên khoảng nên **đúng cả ngày lẻ và mốc ngoài 24 tháng**. Tối đa 2 khoảng (đủ cho câu so sánh A với B).
  4. **Xếp hạng top 8**: doanh thu theo **nhân viên** (chỉ ADMIN/SALES_MANAGER), **chiến dịch**, **khách hàng**, **sản phẩm** (doanh thu + số lượng). Bám theo khoảng được hỏi nếu câu hỏi có nêu.
  5. **Đếm theo phân hệ**: tổng + phát sinh trong kỳ cho 10 phân hệ.
  6. **Phễu bản ghi cụ thể** khi câu hỏi nhắc tên/mã khách (cơ hội→báo giá→đơn→hóa đơn→ticket).
- ⛔ **Mọi truy vấn loại bản ghi đã xóa**: `deleted_at IS NULL` (loại luôn bản ghi trong Thùng rác và đã xóa vĩnh viễn) — áp cho **cả bảng JOIN** (users/campaigns/customers/products). `invoice_items` không có `deleted_at` nên lọc qua hóa đơn cha.
- Prompt **cho phép AI so sánh/xếp hạng/cộng trừ trên số đã cấp** (trước đây cấm tính toán nên hay trả "không có thông tin"), nhưng vẫn cấm bịa số không có trong DỮ LIỆU.
- **Phân quyền dữ liệu**: ADMIN/SALES_MANAGER xem toàn bộ (`ownerId=null`); nhân viên chỉ xem bản ghi `owner_id = userId` (kỳ + phễu đều lọc theo owner).
- **Nhà cung cấp**: Google Gemini qua `GeminiAiServiceImpl` (adapter của port `IAiService`) — dùng `RestClient` của spring-web, **không thêm dependency**. Đổi nhà cung cấp chỉ cần thay implementation.

#### Nhánh NGỮ NGHĨA — vector RAG (MỚI 2026-07-24)

Sáu khối trên là nhánh **SỐ LIỆU** (SQL), chính xác tuyệt đối với con số nhưng mù với câu hỏi
định tính. Nay bổ sung nhánh **NGỮ NGHĨA** chạy song song; `AskCopilotUseCase` ghép cả hai vào prompt:

```
DỮ LIỆU:                 <- SQL, nguồn DUY NHẤT của mọi con số
TRÍCH ĐOẠN LIÊN QUAN:    <- vector, nội dung mô tả / lý do thắng-thua / nội dung phiếu chăm sóc
CÂU HỎI:
```

- **Bảng `copilot_chunks`** (`VECTOR(768)`, TiDB Cloud native) — mỗi bản ghi của 11 phân hệ được
  tóm tắt thành một "thẻ" tiếng Việt rồi nhúng. Kiểu `VECTOR` không có trong `MySQLDialect` nên
  bảng này **chỉ truy cập bằng native query**, không tạo Hibernate entity.
- **Ghi chỉ mục KHÔNG phải việc của backend**: `tools/indexer/` (Python, **chạy tay trên máy dev**)
  đọc TiDB → dựng thẻ → gọi Gemini `embedContent` → ghi `copilot_chunks`. Nhờ vậy Render không
  cần `@Scheduled`, không endpoint rebuild, không thêm RAM. Xem `tools/indexer/README.md`.
- **Backend chỉ thêm 2 class**: `GeminiEmbeddingServiceImpl` (nhúng CÂU HỎI, `taskType=RETRIEVAL_QUERY`)
  và `TiDbVectorStoreImpl` (`ORDER BY VEC_COSINE_DISTANCE(embedding, :q) LIMIT :k`), cộng
  `SemanticRetriever` + port `IEmbeddingService` / `IVectorStore` / `VectorHit`.
- 🚨 **Vì sao vẫn phải nhúng câu hỏi**: vector DB lưu *tọa độ*, không lưu chữ. Muốn xếp hạng
  "chunk nào gần câu hỏi nhất" thì phải có tọa độ của câu hỏi, mà cách duy nhất biến chữ thành
  tọa độ là chạy model. Đúng với **mọi** vector DB (Qdrant/Pinecone/pgvector), không riêng TiDB.
  Chi phí chỉ **1 request/câu hỏi** (~vài chục/ngày) nên không bao giờ chạm trần hạn mức.
- 🚨 **Ba thứ phải trùng khít với indexer**: `app.ai.embed.model` ↔ `EMBED_MODEL`,
  `app.ai.embed.dimensions` ↔ `EMBED_DIMENSIONS`, và **cách chuẩn hóa L2** (`gemini-embedding-001`
  chỉ tự chuẩn hóa ở 3072 chiều, dùng số chiều nhỏ hơn thì client phải tự normalize). Lệch một
  trong ba → khoảng cách cosine vô nghĩa, chatbot trả bản ghi ngẫu nhiên **mà không báo lỗi gì**.
  Đổi model ⇒ `TRUNCATE copilot_chunks` rồi build lại từ đầu.
- **Phân quyền**: `WHERE (:ownerId IS NULL OR owner_id IS NULL OR owner_id = :ownerId)`. Khác
  `/related` (nới owner có chủ đích trên bản ghi con), ở đây người dùng hỏi được bất cứ điều gì
  nên **bắt buộc** lọc owner.
- **Degrade an toàn**: `APP_AI_EMBED_ENABLED=false`, chưa chạy migration, chưa build chỉ mục, hết
  hạn mức hay lỗi mạng → `SemanticRetriever` trả chuỗi rỗng và Copilot chạy y như trước khi có
  vector. Không bao giờ ném exception làm chết endpoint đang hoạt động tốt.
- **Chuẩn bị DB**: bảng đã nằm trong `diagrams/crm.sql` (mục 15). DB đang chạy thì chạy
  `diagrams/vector_migration.sql`. Index HNSW là **tùy chọn** — lỗi thì bỏ qua, vài nghìn dòng
  brute-force vẫn dưới 50ms.
- **Cấu hình** (externalize như JWT/mail): `app.ai.api-key` (env `APP_AI_API_KEY` — bắt buộc, không commit), `app.ai.model` (mặc định `gemini-flash-latest` — model chạy được trên free tier; `gemini-2.0-flash` bị giới hạn quota 0 nên **không dùng free tier**), `app.ai.base-url`. Nhúng câu hỏi thêm `app.ai.embed.enabled|model|dimensions|top-k|max-distance` (env `APP_AI_EMBED_*`, đều có default). Dev đặt key trong `application-local.properties`.

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
| `infrastructure/shared/config/SecurityConfig.java` | Infrastructure | Spring Security: stateless JWT, CORS cho localhost:5173, permit /api/auth/login, /api/auth/google và /api/auth/activate; trả **401** (không phải 403) khi token hết hạn/không hợp lệ |
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
| `domain/auth/entity/User.java` | Domain | Entity người dùng hệ thống (soft delete) |
| `domain/auth/entity/Role.java` | Domain | Entity vai trò |
| `domain/auth/entity/Permission.java` | Domain | Entity quyền hạn |
| `domain/auth/entity/UserRole.java` | Domain | Bảng nối user ↔ role |
| `domain/auth/entity/RolePermission.java` | Domain | Bảng nối role ↔ permission |
| `domain/auth/enums/UserStatus.java` | Domain | Enum trạng thái user (ACTIVE, INACTIVE) |
| `domain/auth/repository/IUserRepository.java` | Domain | Interface thao tác DB cho User |
| `domain/auth/repository/IRoleRepository.java` | Domain | Interface thao tác DB cho Role |
| `domain/auth/repository/IPermissionRepository.java` | Domain | Interface thao tác DB cho Permission |
| `domain/auth/repository/IUserRoleRepository.java` | Domain | Interface thao tác DB cho UserRole |
| `domain/auth/repository/IRolePermissionRepository.java` | Domain | Interface thao tác DB cho RolePermission |
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
| `application/auth/dto/*Result.java` | Application | Output DTO cho User/Role/Permission |
| `application/auth/dto/Create|Update*Command.java` | Application | Input DTO cho command |
| `application/auth/dto/Assign*Command.java` | Application | Input DTO cho assign/revoke |
| `application/auth/mapper/*CommandMapper.java` | Application | Chuyển đổi domain entity ↔ DTO |
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
| `APP_FRONTEND_BASE_URL` | URL frontend Render (link kích hoạt + phản hồi báo giá trong email) |
| `APP_CORS_ALLOWED_ORIGINS` | URL frontend Render (cho CORS) |
| `APP_AI_API_KEY` | Khóa API Google Gemini (AI Studio) — dùng chung cho Copilot lẫn nhúng câu hỏi. `APP_AI_MODEL`/`APP_AI_BASE_URL`/`APP_AI_EMBED_*` tùy chọn (có default) |

### Các bước
1. Render → **Blueprints → New Blueprint Instance**, chọn repo → Render đọc **`render.yaml` ở gốc repo** và tạo cả `crm-backend` (Docker, Root Directory = `be-crm`) lẫn `crm-frontend` (Static Site). Không cần tạo tay từng service nữa.
2. Nhập các env var ở bảng trên (URL frontend điền **sau** khi `crm-frontend` deploy xong — xem thứ tự đầy đủ ở `fe-crm/README.md`).
2b. Chạy `diagrams/vector_migration.sql` lên TiDB để có bảng `copilot_chunks`, rồi build chỉ mục bằng `tools/indexer/` (chạy trên máy dev, xem `tools/indexer/README.md`).
3. Deploy → lấy domain `https://<ten-service>.onrender.com`.
> Free tier: service sleep sau ~15 phút không request (cold start ~30–60s). Vì secret cũ đã commit lên git, nên **rotate** mật khẩu TiDB / JWT secret / Gmail App Password rồi nhập giá trị mới vào Render.
