# CRM Backend — Tài liệu kỹ thuật

## Mục lục

1. [Tổng quan kiến trúc](#1-tổng-quan-kiến-trúc)
2. [Danh sách API theo module](#2-danh-sách-api-theo-module)
   - [Auth — Xác thực & Phân quyền](#21-auth--xác-thực--phân-quyền)
   - [Activity — Hoạt động](#22-activity--hoạt-động)
   - [Contact — Liên hệ](#23-contact--liên-hệ)
   - [Customer — Khách hàng](#24-customer--khách-hàng)
   - [Lead — Tiềm năng](#25-lead--tiềm-năng)
   - [Opportunity — Cơ hội bán hàng](#26-opportunity--cơ-hội-bán-hàng)
   - [Order — Đơn hàng](#27-order--đơn-hàng)
   - [Product — Sản phẩm](#28-product--sản-phẩm)
   - [Pricing — Chính sách giá](#29-pricing--chính-sách-giá)
   - [Quotation — Báo giá](#210-quotation--báo-giá)
   - [Warehouse — Kho hàng](#211-warehouse--kho-hàng)
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
{ "email": "admin@gmail.com", "password": "123456" }
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
| `GET` | `/api/users` | Danh sách người dùng (phân trang) |
| `GET` | `/api/users/{id}` | Lấy người dùng theo ID |
| `PUT` | `/api/users/{id}` | Cập nhật người dùng |
| `DELETE` | `/api/users/{id}` | Xóa mềm người dùng |
| `POST` | `/api/users/{id}/roles` | Gán role cho người dùng — body: `{ "roleId": ... }` |
| `DELETE` | `/api/users/{id}/roles/{roleId}` | Thu hồi role khỏi người dùng |

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

---

### 2.3 Contact — Liên hệ

#### Liên hệ — `/api/contacts`

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| `POST` | `/api/contacts` | Tạo liên hệ |
| `GET` | `/api/contacts` | Danh sách liên hệ (phân trang) |
| `GET` | `/api/contacts/{id}` | Lấy liên hệ theo ID |
| `PUT` | `/api/contacts/{id}` | Cập nhật liên hệ |
| `DELETE` | `/api/contacts/{id}` | Xóa mềm liên hệ |

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

#### Chia sẻ khách hàng — `/api/customers/{customerId}/shares`

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| `POST` | `/api/customers/{customerId}/shares` | Chia sẻ khách hàng cho user |
| `GET` | `/api/customers/{customerId}/shares` | Danh sách user được chia sẻ |
| `DELETE` | `/api/customers/{customerId}/shares/{userId}` | Thu hồi quyền truy cập |

#### Kiểm kê hàng tồn kho — `/api/inventory-checks`

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| `POST` | `/api/inventory-checks` | Tạo phiếu kiểm kê |
| `GET` | `/api/inventory-checks` | Danh sách phiếu kiểm kê (phân trang) |
| `GET` | `/api/inventory-checks/{id}` | Lấy phiếu kiểm kê theo ID |
| `PUT` | `/api/inventory-checks/{id}` | Cập nhật phiếu kiểm kê |
| `DELETE` | `/api/inventory-checks/{id}` | Xóa phiếu kiểm kê |

#### Chi tiết kiểm kê — `/api/inventory-checks/{checkId}/items`

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| `POST` | `/api/inventory-checks/{checkId}/items` | Thêm dòng hàng vào phiếu kiểm kê |
| `GET` | `/api/inventory-checks/{checkId}/items` | Danh sách dòng hàng |
| `PUT` | `/api/inventory-checks/{checkId}/items/{id}` | Cập nhật dòng hàng |
| `DELETE` | `/api/inventory-checks/{checkId}/items/{id}` | Xóa dòng hàng |

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

#### Hoạt động của lead — `/api/leads/{leadId}/activities`

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| `POST` | `/api/leads/{leadId}/activities` | Ghi nhận hoạt động cho lead |
| `GET` | `/api/leads/{leadId}/activities` | Lịch sử hoạt động của lead |
| `PUT` | `/api/leads/{leadId}/activities/{id}` | Cập nhật hoạt động |
| `DELETE` | `/api/leads/{leadId}/activities/{id}` | Xóa hoạt động |

#### Đính kèm lead — `/api/leads/{leadId}/attachments`

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| `POST` | `/api/leads/{leadId}/attachments` | Thêm tệp đính kèm |
| `GET` | `/api/leads/{leadId}/attachments` | Danh sách đính kèm |
| `PUT` | `/api/leads/{leadId}/attachments/{id}` | Cập nhật đính kèm |
| `DELETE` | `/api/leads/{leadId}/attachments/{id}` | Xóa đính kèm |

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
| `POST` | `/api/opportunities` | Tạo cơ hội |
| `GET` | `/api/opportunities` | Danh sách cơ hội (phân trang) |
| `GET` | `/api/opportunities/{id}` | Lấy cơ hội theo ID |
| `PUT` | `/api/opportunities/{id}` | Cập nhật cơ hội |
| `DELETE` | `/api/opportunities/{id}` | Xóa mềm cơ hội |

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

### 2.7 Order — Đơn hàng

#### Đơn hàng — `/api/orders`

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| `POST` | `/api/orders` | Tạo đơn hàng |
| `GET` | `/api/orders` | Danh sách đơn hàng (phân trang) |
| `GET` | `/api/orders/{id}` | Lấy đơn hàng theo ID |
| `PUT` | `/api/orders/{id}` | Cập nhật đơn hàng |
| `DELETE` | `/api/orders/{id}` | Xóa mềm đơn hàng |

#### Dòng hàng đơn — `/api/orders/{orderId}/items`

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| `POST` | `/api/orders/{orderId}/items` | Thêm sản phẩm vào đơn hàng |
| `GET` | `/api/orders/{orderId}/items` | Danh sách sản phẩm trong đơn |
| `PUT` | `/api/orders/{orderId}/items/{id}` | Cập nhật dòng hàng |
| `DELETE` | `/api/orders/{orderId}/items/{id}` | Xóa dòng hàng |

#### Lịch thanh toán — `/api/orders/{orderId}/payment-schedules`

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| `POST` | `/api/orders/{orderId}/payment-schedules` | Tạo lịch thanh toán |
| `GET` | `/api/orders/{orderId}/payment-schedules` | Danh sách lịch thanh toán |
| `PUT` | `/api/orders/{orderId}/payment-schedules/{id}` | Cập nhật lịch thanh toán |
| `DELETE` | `/api/orders/{orderId}/payment-schedules/{id}` | Xóa lịch thanh toán |

#### Theo dõi giao hàng — `/api/orders/{orderId}/delivery-tracking`

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| `POST` | `/api/orders/{orderId}/delivery-tracking` | Tạo bản ghi giao hàng |
| `GET` | `/api/orders/{orderId}/delivery-tracking` | Lịch sử giao hàng |
| `PUT` | `/api/orders/{orderId}/delivery-tracking/{id}` | Cập nhật bản ghi |
| `DELETE` | `/api/orders/{orderId}/delivery-tracking/{id}` | Xóa bản ghi |

#### Ghi nhận doanh thu — `/api/orders/{orderId}/revenue-records`

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| `POST` | `/api/orders/{orderId}/revenue-records` | Ghi nhận doanh thu |
| `GET` | `/api/orders/{orderId}/revenue-records` | Danh sách doanh thu |
| `PUT` | `/api/orders/{orderId}/revenue-records/{id}` | Cập nhật bản ghi doanh thu |
| `DELETE` | `/api/orders/{orderId}/revenue-records/{id}` | Xóa bản ghi doanh thu |

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
| `POST` | `/api/quotations` | Tạo báo giá |
| `GET` | `/api/quotations` | Danh sách báo giá (phân trang) |
| `GET` | `/api/quotations/{id}` | Lấy báo giá theo ID |
| `PUT` | `/api/quotations/{id}` | Cập nhật báo giá |
| `DELETE` | `/api/quotations/{id}` | Xóa mềm báo giá |

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

### 2.11 Warehouse — Kho hàng

#### Kho hàng — `/api/warehouses`

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| `POST` | `/api/warehouses` | Tạo kho hàng |
| `GET` | `/api/warehouses` | Danh sách kho (phân trang) |
| `GET` | `/api/warehouses/{id}` | Lấy kho theo ID |
| `PUT` | `/api/warehouses/{id}` | Cập nhật kho |
| `DELETE` | `/api/warehouses/{id}` | Xóa kho |
| `POST` | `/api/warehouses/{id}/permissions` | Gán quyền truy cập kho cho user |
| `DELETE` | `/api/warehouses/{id}/permissions/{userId}` | Thu hồi quyền truy cập kho |

#### Tồn kho — `/api/inventory-stocks`

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| `POST` | `/api/inventory-stocks` | Tạo/cập nhật bản ghi tồn kho |
| `GET` | `/api/inventory-stocks` | Danh sách tồn kho (phân trang) |
| `GET` | `/api/inventory-stocks/{id}` | Lấy bản ghi tồn kho theo ID |

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
| `domain/customer/entity/InventoryCheck.java` | Domain | Phiếu kiểm kê hàng tại kho của khách |
| `domain/customer/entity/InventoryCheckItem.java` | Domain | Dòng hàng trong phiếu kiểm kê |
| `domain/customer/enums/CustomerStatus.java` | Domain | Trạng thái khách hàng |
| `domain/customer/enums/CustomerType.java` | Domain | Loại khách hàng |
| `domain/customer/enums/CustomerSharePermission.java` | Domain | Mức quyền chia sẻ (view/edit) |
| `domain/customer/enums/InventoryCheckStatus.java` | Domain | Trạng thái phiếu kiểm kê |
| `domain/customer/repository/I*Repository.java` | Domain | 4 interface repository |
| `application/customer/...` | Application | Use case CRUD + query cho cả 4 entity |
| `presentation/customer/CustomerController.java` | Presentation | REST `/api/customers` |
| `presentation/customer/CustomerShareController.java` | Presentation | REST `/api/customers/{id}/shares` |
| `presentation/customer/InventoryCheckController.java` | Presentation | REST `/api/inventory-checks` |
| `presentation/customer/InventoryCheckItemController.java` | Presentation | REST `/api/inventory-checks/{id}/items` |
| `infrastructure/customer/...` | Infrastructure | Hibernate entity, mapper, repository impl |

### 3.6 Module Lead

| File / Folder | Tầng | Công dụng |
|---------------|------|-----------|
| `domain/lead/entity/Lead.java` | Domain | Entity lead bán hàng (soft delete) |
| `domain/lead/entity/LeadActivity.java` | Domain | Hoạt động gắn với lead |
| `domain/lead/entity/LeadAttachment.java` | Domain | Tệp đính kèm của lead |
| `domain/lead/entity/LeadTransfer.java` | Domain | Lịch sử chuyển giao lead giữa nhân viên |
| `domain/lead/enums/LeadStatus.java` | Domain | Trạng thái lead — `new_` thay cho `new` (Java keyword) |
| `domain/lead/enums/LeadActivityType.java` | Domain | Loại hoạt động lead |
| `domain/lead/repository/I*Repository.java` | Domain | 4 interface repository |
| `application/lead/...` | Application | Use case cho Lead và 3 sub-entity |
| `presentation/lead/LeadController.java` | Presentation | REST `/api/leads` |
| `presentation/lead/LeadActivityController.java` | Presentation | REST `/api/leads/{id}/activities` |
| `presentation/lead/LeadAttachmentController.java` | Presentation | REST `/api/leads/{id}/attachments` |
| `presentation/lead/LeadTransferController.java` | Presentation | REST `/api/leads/{id}/transfers` |
| `infrastructure/lead/converter/LeadStatusConverter.java` | Infrastructure | `AttributeConverter` map DB `"new"` ↔ Java `LeadStatus.new_` |
| `infrastructure/lead/...` | Infrastructure | Hibernate entity, mapper, repository impl |

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

### 3.12 Module Warehouse

| File / Folder | Tầng | Công dụng |
|---------------|------|-----------|
| `domain/warehouse/entity/Warehouse.java` | Domain | Entity kho hàng |
| `domain/warehouse/entity/InventoryStock.java` | Domain | Tồn kho của từng sản phẩm trong kho (dùng @Formula computed column) |
| `domain/warehouse/entity/WarehousePermission.java` | Domain | Quyền truy cập kho cho từng user |
| `domain/warehouse/enums/WarehousePermissionType.java` | Domain | Loại quyền kho (view/manage) |
| `domain/warehouse/repository/I*Repository.java` | Domain | 3 interface repository |
| `application/warehouse/...` | Application | Use case cho cả 3 entity |
| `presentation/warehouse/WarehouseController.java` | Presentation | REST `/api/warehouses` |
| `presentation/warehouse/InventoryStockController.java` | Presentation | REST `/api/inventory-stocks` |
| `infrastructure/warehouse/...` | Infrastructure | Hibernate entity, mapper, repository impl |

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

### Soft delete

Các entity hỗ trợ soft delete: User, Contact, Customer, Lead, Opportunity, Quotation, Order, Product.

- Xóa = set `deletedAt = LocalDateTime.now()`
- Truy vấn tự động thêm `WHERE deleted_at IS NULL`

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
