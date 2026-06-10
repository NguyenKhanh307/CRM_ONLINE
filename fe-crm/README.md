# CRM Frontend — Tài liệu kỹ thuật

## Mục lục

1. [Tổng quan](#1-tổng-quan)
2. [Cài đặt & Chạy](#2-cài-đặt--chạy)
3. [Kiến trúc & Cấu trúc thư mục](#3-kiến-trúc--cấu-trúc-thư-mục)
4. [Các module đã có](#4-các-module-đã-có)
5. [Quy tắc phát triển](#5-quy-tắc-phát-triển)
6. [Design tokens](#6-design-tokens)

---

## 1. Tổng quan

| | Chi tiết |
|---|---------|
| **Stack** | React 19, Vite 7, TypeScript, Tailwind CSS v3 |
| **State** | TanStack Query (React Query v5) |
| **Table** | TanStack Table v8 |
| **Router** | React Router v7 |
| **HTTP** | Axios |
| **Icons** | react-icons/fi (Feather Icons) — **chỉ dùng bộ này** |
| **Port dev** | `5173` |
| **Backend** | `http://localhost:8080` |

---

## 2. Cài đặt & Chạy

### Yêu cầu
- Node.js 20+
- Backend đang chạy trên port 8080

### Bước 1 — Tạo file môi trường

```bash
# fe-crm/.env  (bắt buộc — không có file này thì API calls sẽ thất bại)
VITE_API_BASE_URL=http://localhost:8080
```

### Bước 2 — Cài dependencies

```bash
cd fe-crm
npm install
```

### Bước 3 — Chạy dev server

```bash
npm run dev
```

### Build production

```bash
npx vite build
```

### Tài khoản test

| Email | Mật khẩu |
|-------|---------|
| `admin@abc.vn` | `123456` |

---

## 3. Kiến trúc & Cấu trúc thư mục

### Dependency flow (bắt buộc)

```
pages  →  hooks  →  services  →  API (axiosInstance)
```

Không được bỏ qua bất kỳ tầng nào.

### Cấu trúc thư mục

```
fe-crm/src/
├── app/
│   ├── router.tsx              # Định nghĩa routes
│   └── App.tsx
├── core/
│   ├── auth/
│   │   ├── AuthContext.tsx     # AuthUser: { id, email, fullName, roles }
│   │   ├── authStorage.ts      # localStorage helpers
│   │   └── useAuth.ts
│   └── axios/
│       └── axiosInstance.ts    # Axios instance — đọc VITE_API_BASE_URL
├── features/
│   ├── auth/                   # Login page, useLogin hook, authService
│   ├── tiem-nang/              # Lead
│   ├── lien-he/                # Contact
│   ├── khach-hang/             # Customer
│   ├── co-hoi/                 # Opportunity
│   ├── bao-gia/                # Quotation
│   ├── don-hang/               # Order
│   ├── hoat-dong/              # Activity
│   ├── san-pham/               # Product
│   └── kho-hang/               # Warehouse
└── shared/
    ├── components/
    │   ├── layout/
    │   │   └── sidebar/
    │   │       └── sidebarConfig.ts   # NAV_ITEMS — thêm menu item ở đây
    │   ├── table/
    │   │   └── DataTable.tsx          # Component bảng dùng chung
    │   ├── ConfirmModal.tsx            # Modal xác nhận dùng chung
    │   └── import/                    # Shared wizard nhập file Excel/CSV
    │       ├── importTypes.ts
    │       ├── ImportWizard.tsx
    │       ├── StepUploadFile.tsx
    │       ├── StepMapColumns.tsx
    │       ├── StepOptions.tsx
    │       └── StepResult.tsx
    ├── types/
    │   └── api.ts              # ApiResponse<T>, PageResult<T>, PageParams
    └── utils/
        └── date.ts             # formatISODate helper
```

### Pattern tạo module mới

Mỗi feature folder có cấu trúc đầy đủ (kể cả edit/delete):

```
features/<ten-module>/
├── types/<Module>Types.ts            # TypeScript interfaces (kể cả Update*Payload)
├── services/<module>Service.ts       # Axios calls (getList, getById, update, remove)
├── hooks/use<Module>List.ts          # useQuery hook danh sách
├── hooks/useDelete<Module>.ts        # useMutation → DELETE /api/<module>/{id}
├── hooks/useUpdate<Module>.ts        # useMutation → PUT /api/<module>/{id}
├── config/<module>Columns.tsx        # ColumnDef[] cho DataTable (không có cột actions)
├── components/<Module>EditModal.tsx  # Modal chỉnh sửa đầy đủ
└── pages/<Module>Page.tsx            # Trang list view (thêm actions column + modals)
```

Ví dụ service đầy đủ:

```ts
export const leadService = {
    getList: (params: PageParams) =>
        axiosInstance.get<ApiResponse<PageResult<LeadResult>>>('/api/leads', { params }),
    getById: (id: number) =>
        axiosInstance.get<ApiResponse<LeadResult>>(`/api/leads/${id}`),
    update: (id: number, payload: UpdateLeadPayload) =>
        axiosInstance.put<ApiResponse<LeadResult>>(`/api/leads/${id}`, payload),
    remove: (id: number) =>
        axiosInstance.delete(`/api/leads/${id}`),
};
```

Ví dụ hook list:

```ts
export function useLeadList(params: PageParams = {}) {
    return useQuery({
        queryKey: ['leads', params],
        queryFn: () => leadService.getList(params).then(r => r.data.data),
    });
}
```

Ví dụ hook delete:

```ts
export function useDeleteLead() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (id: number) => leadService.remove(id),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['leads'] }),
    });
}
```

Ví dụ hook update:

```ts
export function useUpdateLead() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: ({ id, payload }: { id: number; payload: UpdateLeadPayload }) =>
            leadService.update(id, payload),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['leads'] }),
    });
}
```

---

## 4. Các module đã có

### Auth
- `POST /api/auth/login` → JWT token lưu localStorage. **Email phải là @gmail.com** (validate cả FE lẫn BE)
- `POST /api/auth/register-employee` → Admin đăng ký nhân viên, BE gửi email kích hoạt (route `/dang-ky-nhan-vien`)
- `POST /api/auth/activate` → Nhân viên kích hoạt tài khoản qua link email (route public `/activate?token=...`)
- Token tự động đính kèm vào mọi request qua axios interceptor
- 401 response → tự động redirect về `/login`

### Data modules (list view + edit/delete)

Tất cả 9 module đều có: danh sách, nút Sửa (mở modal), nút Xóa (ConfirmModal), chọn nhiều hàng + Xóa hàng loạt.

| Module | Route | Endpoint GET | Endpoint PUT | Endpoint DELETE |
|--------|-------|-------------|-------------|----------------|
| Tiềm năng | `/tiem-nang` | `GET /api/leads` | `PUT /api/leads/{id}` | `DELETE /api/leads/{id}` |
| Liên hệ | `/lien-he` | `GET /api/contacts` | `PUT /api/contacts/{id}` | `DELETE /api/contacts/{id}` |
| Khách hàng | `/khach-hang` | `GET /api/customers` | `PUT /api/customers/{id}` | `DELETE /api/customers/{id}` |
| Cơ hội | `/co-hoi` | `GET /api/opportunities` | `PUT /api/opportunities/{id}` | `DELETE /api/opportunities/{id}` |
| Báo giá | `/bao-gia` | `GET /api/quotations` | `PUT /api/quotations/{id}` | `DELETE /api/quotations/{id}` |
| Đơn hàng | `/don-hang` | `GET /api/orders` | `PUT /api/orders/{id}` | `DELETE /api/orders/{id}` |
| Hoạt động | `/hoat-dong` | `GET /api/activities` | `PUT /api/activities/{id}` | `DELETE /api/activities/{id}` |
| Sản phẩm | `/san-pham` | `GET /api/products` | `PUT /api/products/{id}` | `DELETE /api/products/{id}` |
| Kho hàng | `/kho-hang` | `GET /api/warehouses` | `PUT /api/warehouses/{id}` | `DELETE /api/warehouses/{id}` |

### Phân quyền — `/phan-quyen`

Trang quản lý nhóm người dùng và phân quyền theo nhóm. Kết nối đầy đủ với API.

- **Panel trái**: Danh sách nhóm (roles) — tạo/sửa/xóa nhóm
- **Tab Thành viên**: Xem + thêm/xóa người dùng trong nhóm; Thu hồi/kích hoạt lại tài khoản; Chỉnh sửa năm xem data (`dataAccessFromYear`)
- **Tab Phân quyền**: Accordion theo module, toggle gán/thu hồi quyền ngay lập tức

| Endpoint | Mô tả |
|----------|-------|
| `GET /api/roles` | Danh sách nhóm |
| `POST /api/roles` | Tạo nhóm |
| `PUT /api/roles/{id}` | Sửa nhóm |
| `DELETE /api/roles/{id}` | Xóa nhóm |
| `GET /api/roles/{id}/permissions` | Quyền đã gán cho nhóm |
| `GET /api/roles/{id}/members` | Thành viên trong nhóm |
| `POST /api/roles/{id}/permissions` | Gán quyền |
| `DELETE /api/roles/{id}/permissions/{permId}` | Thu hồi quyền |
| `POST /api/users/{userId}/roles` | Thêm thành viên vào nhóm |
| `DELETE /api/users/{userId}/roles/{roleId}` | Xóa thành viên khỏi nhóm |
| `PUT /api/users/{userId}/revoke` | Thu hồi tài khoản (→ locked) |
| `PUT /api/users/{userId}/reactivate` | Kích hoạt lại tài khoản (→ active) |
| `PUT /api/users/{userId}` | Cập nhật `dataAccessFromYear` |

Files: `features/phan-quyen/` — types, services, hooks (11 hooks), components (6 components), pages

### Thùng rác — `/thung-rac`

Trang hiển thị bản ghi đã xóa mềm trong 30 ngày gần nhất. Admin thấy tất cả, nhân viên chỉ thấy bản ghi do mình xóa.

- **7 tab**: Tiềm năng, Liên hệ, Khách hàng, Cơ hội, Báo giá, Đơn hàng, Sản phẩm
- **Khôi phục**: `POST /api/{module}/{id}/restore`
- **Xóa vĩnh viễn**: `DELETE /api/{module}/{id}/purge` — set `is_purged=1`, ẩn UI, DB giữ soft-delete

Files: `features/thung-rac/` — types/thungRacTypes.ts, services/trashService.ts, hooks/useTrash.ts, config/trashColumns.tsx, pages/ThungRacPage.tsx

### Shared component
- `shared/components/ConfirmModal.tsx` — modal xác nhận dùng chung, thay thế `window.confirm()`

### Cấu trúc thư mục đầy đủ (updated)

```
features/
├── auth/
├── users/             # Đăng ký NV + hooks liên quan user
├── phan-quyen/        # Phân quyền nhóm + thành viên
├── thung-rac/         # Thùng rác 7 module
├── tiem-nang/         # Lead — có LeadImportPage
├── lien-he/           # Contact — có ContactImportPage
├── khach-hang/        # Customer — có CustomerImportPage
├── co-hoi/            # Opportunity — có OpportunityImportPage
├── bao-gia/           # Quotation — có QuotationImportPage
├── don-hang/          # Order — có OrderImportPage
├── hoat-dong/         # Activity — có ActivityImportPage
├── san-pham/          # Product — có ProductImportPage
└── kho-hang/          # Warehouse — có WarehouseImportPage
```

### Nhập file Excel/CSV — 9 module (2026-06-11)

Mỗi module data có trang nhập file 4 bước riêng, truy cập qua nút "Nhập file" trên trang danh sách.

| Module | Route nhập file | Endpoint backend |
|--------|----------------|-----------------|
| Tiềm năng | `/tiem-nang/nhap-file` | `POST /api/leads/import-bulk` |
| Liên hệ | `/lien-he/nhap-file` | `POST /api/contacts/import-bulk` |
| Khách hàng | `/khach-hang/nhap-file` | `POST /api/customers/import-bulk` |
| Cơ hội | `/co-hoi/nhap-file` | `POST /api/opportunities/import-bulk` |
| Báo giá | `/bao-gia/nhap-file` | `POST /api/quotations/import-bulk` |
| Đơn hàng | `/don-hang/nhap-file` | `POST /api/orders/import-bulk` |
| Hoạt động | `/hoat-dong/nhap-file` | `POST /api/activities/import-bulk` |
| Sản phẩm | `/san-pham/nhap-file` | `POST /api/products/import-bulk` |
| Kho hàng | `/kho-hang/nhap-file` | `POST /api/warehouses/import-bulk` |

**Shared wizard** — `src/shared/components/import/`:

| File | Vai trò |
|------|---------|
| `importTypes.ts` | Types: `ImportField`, `ImportOptions`, `ImportRowError`, `ImportBulkResult`, `ColumnMapping` |
| `ImportWizard.tsx` | Component wizard 4 bước, nhận `fields[]`, `onImport()`, `backPath` |
| `StepUploadFile.tsx` | Bước 1: drag & drop, parse xlsx/csv (SheetJS), tải file mẫu |
| `StepMapColumns.tsx` | Bước 2: ghép cột file → trường CRM, auto-map theo label, tabs Tất cả/Đã ghép/Chưa ghép |
| `StepOptions.tsx` | Bước 3: importType (CREATE/UPDATE/BOTH), ownerMode (SPECIFIC/FROM_FILE) |
| `StepResult.tsx` | Bước 4: hiển thị successCount, failedCount, bảng lỗi, nút Quay về / Nhập thêm |

**importBulk trong service** — pattern chung:

```ts
importBulk: (payload: ImportBulkLeadCommand) =>
    axiosInstance.post<ApiResponse<ImportBulkResult>>('/api/leads/import-bulk', payload),
```

**Dependency**: `xlsx` (SheetJS) — parse file trong trình duyệt, backend chỉ nhận JSON.

**Lưu ý**: Import UPDATE/BOTH chỉ hoạt động cho module Lead (có `findByPhone`/`findByEmail`). 8 module còn lại chỉ hỗ trợ CREATE.

### Chưa implement
- Form tạo mới cho tất cả module (chỉ có edit modal, chưa có create modal)
- Pricing module chưa có frontend page

---

## 5. Quy tắc phát triển

### Imports
- Luôn dùng path alias `@/` — không dùng đường dẫn tương đối `../../`
- Ví dụ: `import { DataTable } from '@/shared/components/table/DataTable'`

### Icons
- **Chỉ** dùng `react-icons/fi` (Feather Icons)
- Ví dụ: `import { FiUser, FiBox } from 'react-icons/fi'`

### Thêm route mới
1. Tạo page component trong `features/<module>/pages/`
2. Thêm vào `children[]` của layout route trong `app/router.tsx`
3. Thêm vào `NAV_ITEMS` trong `shared/components/layout/sidebar/sidebarConfig.ts`

### Thêm cột bảng
- Tạo file `features/<name>/config/<name>Columns.tsx`
- Dùng `ColumnDef[]` từ `@tanstack/react-table`
- Dùng `formatISODate` từ `@/shared/utils/date` cho các cột ngày

### Màu sắc & style
- Chỉ dùng token đã định nghĩa trong `tailwind.config.js`
- Không hardcode màu hex trực tiếp trong class

---

## 6. Design tokens

Xem đầy đủ trong `fe-crm/tailwind.config.js`. Các token chính:

| Token | Dùng cho |
|-------|---------|
| `bg-bg-main` | Background trang chính |
| `text-text-main` | Text màu chính |
| `text-text-sub` | Text phụ, placeholder |
| `rounded-card` | Border radius card/panel |
| `border-border` | Màu border mặc định |
