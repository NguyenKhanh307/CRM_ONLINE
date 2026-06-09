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
    │   └── table/
    │       └── DataTable.tsx          # Component bảng dùng chung
    ├── types/
    │   └── api.ts              # ApiResponse<T>, PageResult<T>, PageParams
    └── utils/
        └── date.ts             # formatISODate helper
```

### Pattern tạo module mới

Mỗi feature folder có cấu trúc:

```
features/<ten-module>/
├── types/<Module>Types.ts      # TypeScript interfaces
├── services/<module>Service.ts # Axios calls
├── hooks/use<Module>List.ts    # useQuery hook
├── config/<module>Columns.tsx  # ColumnDef[] cho DataTable
└── pages/<Module>Page.tsx      # Trang list view
```

Ví dụ service:

```ts
export const leadService = {
    getList: (params: PageParams) =>
        axiosInstance.get<ApiResponse<PageResult<LeadResult>>>('/api/leads', { params }),
    getById: (id: number) =>
        axiosInstance.get<ApiResponse<LeadResult>>(`/api/leads/${id}`),
};
```

Ví dụ hook:

```ts
export function useLeadList(params: PageParams = {}) {
    return useQuery({
        queryKey: ['leads', params],
        queryFn: () => leadService.getList(params).then(r => r.data.data),
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

### Data modules (list view)

| Module | Route | Endpoint |
|--------|-------|---------|
| Tiềm năng | `/tiem-nang` | `GET /api/leads` |
| Liên hệ | `/lien-he` | `GET /api/contacts` |
| Khách hàng | `/khach-hang` | `GET /api/customers` |
| Cơ hội | `/co-hoi` | `GET /api/opportunities` |
| Báo giá | `/bao-gia` | `GET /api/quotations` |
| Đơn hàng | `/don-hang` | `GET /api/orders` |
| Hoạt động | `/hoat-dong` | `GET /api/activities` |
| Sản phẩm | `/san-pham` | `GET /api/products` |
| Kho hàng | `/kho-hang` | `GET /api/warehouses` |

### Phân quyền — `/phan-quyen`

Trang quản lý nhóm người dùng và phân quyền theo nhóm. Kết nối đầy đủ với API.

- **Panel trái**: Danh sách nhóm (roles) — tạo/sửa/xóa nhóm
- **Tab Thành viên**: Xem + thêm/xóa người dùng trong nhóm
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

Files: `features/phan-quyen/` — types, services, hooks (8 hooks), components (5 components), pages

### Chưa implement
- Form tạo mới / chỉnh sửa cho tất cả module
- Pricing module

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
