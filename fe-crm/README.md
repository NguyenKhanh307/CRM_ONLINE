# CRM Frontend — Tài liệu kỹ thuật

> 📘 Mới đọc code frontend? Xem [`../CODE_GUIDE_FRONTEND.md`](../CODE_GUIDE_FRONTEND.md) — hướng
> dẫn đọc hiểu code kiểu sách giáo khoa (TypeScript, hooks, React Query, các pattern…).

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

# Đăng nhập Google (nút "Đăng nhập bằng Google" ở trang Login)
# OAuth Client ID (Web) tạo ở Google Cloud Console → APIs & Services → Credentials.
# Authorized JavaScript origins: http://localhost:5173 (dev) + domain Render (prod).
VITE_GOOGLE_CLIENT_ID=<client-id>.apps.googleusercontent.com

# Ảnh đại diện (trang Thiết lập tài khoản) — upload qua Cloudinary unsigned preset.
# Tạo preset kiểu Unsigned ở Cloudinary Dashboard → Settings → Upload → Upload presets.
VITE_CLOUDINARY_CLOUD_NAME=<cloud-name>
VITE_CLOUDINARY_UPLOAD_PRESET=<unsigned-preset>
```

### Bước 2 — Cài dependencies

```bash
cd fe-crm
npm install
```

> Dependency biểu đồ: **`recharts`** (dùng cho Dashboard). Đã có trong `package.json` — `npm install` cài sẵn.

### Bước 3 — Chạy dev server

```bash
npm run dev
```

### Build production

```bash
npx vite build
```

> File `.env` **không commit** (đã thêm vào `.gitignore`). Tham khảo `.env-example`. Vite nhúng `VITE_API_BASE_URL` **lúc build**, nên đổi giá trị phải build lại.

### Triển khai production — Render Static Site

Frontend deploy lên **Render** (trước đây là Netlify — đã gỡ, cả hai phía giờ cùng một nhà cung cấp).
Cấu hình nằm trong **`render.yaml` ở gốc repo**, khai báo chung cả backend lẫn frontend:
- `buildCommand = "npm ci && npm run build"`, `staticPublishPath = "./dist"`, `NODE_VERSION = "22"` (Vite 7 cần Node ≥ 20.19).
- **SPA rewrite** `/* → /index.html` — bắt buộc vì router dùng `createBrowserRouter` (deep-link như `/co-hoi/pipeline` sẽ 404 nếu thiếu).
- Static Site **miễn phí vĩnh viễn, có CDN và KHÔNG ngủ** (khác Web Service free bị sleep sau 15 phút).

Các bước (**đúng thứ tự** — CORS phụ thuộc domain nên không đảo được):
1. Render → **Blueprints → New Blueprint Instance**, chọn repo này → Render đọc `render.yaml` và tạo cả `crm-backend` lẫn `crm-frontend`.
2. `crm-frontend` → **Environment**: đặt `VITE_API_BASE_URL = https://<ten-backend>.onrender.com` (**không** dấu `/` cuối), `VITE_GOOGLE_CLIENT_ID`, `VITE_CLOUDINARY_CLOUD_NAME`, `VITE_CLOUDINARY_UPLOAD_PRESET`. Deploy → lấy URL `https://<ten-site>.onrender.com`.
3. `crm-backend` → **Environment**: điền `APP_CORS_ALLOWED_ORIGINS` và `APP_FRONTEND_BASE_URL` = URL frontend vừa có.
4. Google Cloud Console → OAuth Client → **Authorized JavaScript origins**: thêm domain Render mới (bỏ domain Netlify cũ). **Thiếu bước này thì nút đăng nhập Google im lặng không hoạt động.**
5. Build lại frontend — Vite nhúng các biến `VITE_*` **lúc build**, đổi giá trị bắt buộc build lại.

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
│   │   ├── AuthContext.tsx     # AuthUser: { id, email, fullName, roles, permissions }
│   │   ├── authStorage.ts      # localStorage helpers
│   │   └── useAuth.ts
│   ├── permissions/
│   │   ├── PermissionContext.tsx  # hasRole(), hasPermission(), hasModuleAccess(), can(module, action)
│   │   ├── RequirePermission.tsx   # guard route theo quyền → redirect /forbidden
│   │   └── usePermission.ts
│   └── axios/
│       └── axiosInstance.ts    # Axios instance — đọc VITE_API_BASE_URL
├── features/
│   ├── auth/                   # Login page, useLogin hook, authService
│   ├── tiem-nang/              # Lead
│   ├── lien-he/                # Contact
│   ├── khach-hang/             # Customer
│   ├── co-hoi/                 # Opportunity
│   ├── bao-gia/                # Quotation
│   ├── hoa-don/                # Invoice (thay Order)
│   ├── hoat-dong/              # Activity
│   ├── san-pham/               # Product
│   └── tracking-demo/          # Landing page demo web tracking (/tracking-demo)
└── shared/
    ├── components/
    │   ├── layout/
    │   │   ├── PageHeaderSlot.tsx     # Portal đẩy tiêu đề + nút của trang lên Header chung
    │   │   └── sidebar/
    │   │       └── sidebarConfig.ts   # NAV_ITEMS — thêm menu item ở đây
    │   ├── table/
    │   │   ├── DataTable.tsx          # Component bảng dùng chung
    │   │   ├── RowContextMenu.tsx     # Menu chuột phải của dòng bảng (portal)
    │   │   ├── RecordItemsPanel.tsx   # Bảng dưới (chỉ-xem) — dòng hàng của bản ghi đang chọn
    │   │   ├── lineItemPanelColumns.tsx # Factory cột cho RecordItemsPanel (tra tên/mã sản phẩm)
    │   │   └── tableMetrics.ts        # Chiều cao dòng/header → giới hạn khung cuộn N dòng
    │   ├── ConfirmModal.tsx            # Modal xác nhận dùng chung
    │   ├── import/                    # Shared wizard nhập file Excel/CSV
    │   │   ├── importTypes.ts
    │   │   ├── ImportWizard.tsx
    │   │   ├── StepUploadFile.tsx
    │   │   ├── StepMapColumns.tsx
    │   │   ├── StepOptions.tsx
    │   │   └── StepResult.tsx
    │   └── export/                    # Shared xuất file Excel/CSV (chọn cột)
    │       ├── exportTypes.ts         # ExportColumn<T>, ExportFormat
    │       ├── exportFile.ts          # exportRows() — build xlsx/csv (SheetJS)
    │       └── ExportModal.tsx        # Modal chọn cột + định dạng
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
├── hooks/useImport<Module>Bulk.ts    # wrap service.importBulk (page Import gọi qua hook, không gọi thẳng service)
├── config/<module>Columns.tsx        # ColumnDef[] cho DataTable (chỉ cột dữ liệu — thao tác dùng rowActions)
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
- `POST /api/auth/login` → JWT token lưu localStorage. Email chỉ cần đúng định dạng chung (không còn ràng buộc @gmail.com); banner lỗi hiện đúng message BE trả (sai mật khẩu / email không tồn tại / chưa kích hoạt)
- `POST /api/auth/register-employee` → Admin đăng ký nhân viên, BE gửi email kích hoạt (route `/dang-ky-nhan-vien`)
- `POST /api/auth/activate` → Nhân viên kích hoạt tài khoản qua link email (route public `/activate?token=...`)
- `POST /api/auth/google` → Đăng nhập bằng Google (`GoogleLoginButton` dùng Google Identity Services lấy ID token). Chỉ vào được nếu email có trong bảng `users` + active. Cần `VITE_GOOGLE_CLIENT_ID` + script GSI trong `index.html`
- Token tự động đính kèm vào mọi request qua axios interceptor
- 401 response → tự động redirect về `/login`

### Tài khoản cá nhân (route `/tai-khoan`)
- Mở từ menu người dùng (avatar góc phải) → **Thiết lập tài khoản**: `AccountSettingsPage` sửa họ tên + SĐT + **ảnh đại diện** (upload qua Cloudinary — `shared/utils/cloudinary.ts`, cần `VITE_CLOUDINARY_*`). Email chỉ-đọc. Lưu qua `PUT /api/auth/me` (`useUpdateProfile`), rồi `AuthContext.updateUser({fullName})` cập nhật tên trên header ngay
- **Đổi mật khẩu**: `ChangePasswordModal` (menu người dùng) → `POST /api/auth/change-password` (`useChangePassword`), BE xác minh mật khẩu hiện tại
- `AuthContext` thêm helper `updateUser(patch)` — đồng bộ state + localStorage

### Data modules (list view + edit/delete)

Tất cả 8 module đều có: danh sách, nút Sửa (mở modal), nút Xóa (ConfirmModal), chọn nhiều hàng + Xóa hàng loạt. 5 module nghiệp vụ chính có thêm nút **Bàn giao** (chọn hàng → `POST /api/{module}/handover-bulk`).

| Module | Route | Endpoint GET | Endpoint PUT | Endpoint DELETE | Handover |
|--------|-------|-------------|-------------|----------------|---------|
| Tiềm năng | `/tiem-nang` (+ **`/:id` chi tiết 2 cột**) | `GET /api/leads` | `PUT /api/leads/{id}` | `DELETE /api/leads/{id}` | ✓ |
| Liên hệ | `/lien-he` (+ **`/:id` chi tiết 2 cột**) | `GET /api/contacts` | `PUT /api/contacts/{id}` | `DELETE /api/contacts/{id}` | — |
| Khách hàng | `/khach-hang` (+ **`/:id` chi tiết 360°**) | `GET /api/customers` | `PUT /api/customers/{id}` | `DELETE /api/customers/{id}` | ✓ |
| Cơ hội | `/co-hoi` (+ **`/:id` chi tiết 360°**, **`/kanban` bảng kéo-thả**) | `GET /api/opportunities` | `PUT /api/opportunities/{id}` | `DELETE /api/opportunities/{id}` | ✓ |
| Chiến dịch | `/chien-dich` (+ `/:id` chi tiết 3 tab) | `GET /api/campaigns` | `PUT /api/campaigns/{id}` | `DELETE /api/campaigns/{id}` | ✓ |
| Báo giá | `/bao-gia` (+ **`/:id` chi tiết 2 cột**) | `GET /api/quotations` | `PUT /api/quotations/{id}` | `DELETE /api/quotations/{id}` | ✓ |
| Đơn hàng | `/don-hang` (+ **`/:id` chi tiết 2 cột**) | `GET /api/orders` | `PUT /api/orders/{id}` | `DELETE /api/orders/{id}` | ✓ |
| Hóa đơn | `/hoa-don` (+ **`/:id` chi tiết 2 cột**) | `GET /api/invoices` | `PUT /api/invoices/{id}` | `DELETE /api/invoices/{id}` | ✓ |
| Hoạt động | `/hoat-dong` | `GET /api/activities` | `PUT /api/activities/{id}` | `DELETE /api/activities/{id}` | — |
| Sản phẩm | `/san-pham` | `GET /api/products` | `PUT /api/products/{id}` | `DELETE /api/products/{id}` | — |

> Module **Kho hàng** đã được gỡ (phân hệ Kho không còn ở backend).

### Trang chi tiết 360° — Khách hàng & Cơ hội (MỚI 2026-07-12)

Trước đây mọi thứ là edit-modal nên không có màn hình nào xem được "khách này đã có báo giá/đơn/hóa đơn/ticket gì". Nay:

- **`/khach-hang/:id`** (`CustomerDetailPage`): header + 8 tab — Tổng quan (`CustomerInfoPanel`), Liên hệ, Cơ hội, Báo giá, Đơn hàng, Hóa đơn, Chăm sóc, Hoạt động (dòng thời gian).
- **`/co-hoi/:id`** (`OpportunityDetailPage`): header + 5 tab — Tổng quan (thông tin + dòng hàng qua `RecordItemsPanel`), Báo giá, Đơn hàng, Hóa đơn, Hoạt động. Header có nút **Tạo báo giá** (clone từ cơ hội).
- Dữ liệu từ **một** request: `GET /api/customers/{id}/related` · `GET /api/opportunities/{id}/related` → hooks `useCustomerRelated` / `useOpportunityRelated`. Badge trên tab = tổng số **thật** (mỗi nhóm chỉ nạp tối đa 50 dòng; vượt thì hiện "còn N bản ghi — xem trong danh sách").
- **Vào trang**: nhấp đúp dòng ở `/khach-hang` và `/co-hoi` (trước đây mở edit-modal — modal chuyển vào menu chuột phải).
- Component dùng chung mới `shared/components/detail/`: `DetailHeader`, `Tabs`, `InfoRow`, `RelatedTable`, `Timeline`, `relatedColumns.tsx` (cấu hình cột từng tab). Cross-link qua `shared/utils/moduleRoutes.ts#recordPath` — module có trang chi tiết thì mở thẳng, chưa có thì nhảy về danh sách + `?focus={id}` (tái dùng prop `focusId` của `DataTable`).
- ⚠️ Tab liên quan hiện **đủ** bản ghi con kể cả của đồng nghiệp (BE kiểm quyền một lần trên bản ghi cha). Không dùng list endpoint chung cho các tab này — nó lọc `ownerId` nên sẽ **giấu** báo giá của đồng đội mà không có dấu hiệu gì → sale báo giá trùng.

### Trang chi tiết 2 cột — Tiềm năng / Liên hệ / Báo giá / Đơn hàng / Hóa đơn (MỚI 2026-07-15)

5 phân hệ giao dịch có trang chi tiết bố cục **2 cột** (giống ảnh mẫu), giữ nguyên trang 360° Khách hàng/Cơ hội (tab):

- **Bố cục**: `DetailHeader` (back + tiêu đề + badge trạng thái + nút **Sửa**) → lưới `lg:grid-cols-3`: **trái** (`InfoCard` chứa `<Mod>InfoPanel` — mọi field nghiệp vụ, một field/dòng), **phải** (`StatCards` 3 thẻ thống kê + `Tabs` bản ghi liên quan / `Timeline`).
- Routes: `/tiem-nang/:id` (`LeadDetailPage`), `/lien-he/:id` (`ContactDetailPage`), `/bao-gia/:id` (`QuotationDetailPage`), `/don-hang/:id` (`OrderDetailPage`), `/hoa-don/:id` (`InvoiceDetailPage`).
- Dữ liệu: `use<Mod>Detail` (`GET /api/<m>/{id}`, FK đã enrich tên) + `use<Mod>Related` (`GET /api/<m>/{id}/related`).
- **Component shared mới**: `shared/components/detail/StatCards.tsx` (thẻ thống kê, tone `neutral/success/warning`), `InfoCard.tsx` (thẻ thông tin cột trái). Dùng lại `DetailHeader/Tabs/RelatedTable/Timeline/InfoRow/relatedColumns`.
- **Vào trang**: nhấp đúp dòng ở danh sách → mở chi tiết; nút "Chỉnh sửa" (mở EditModal) nằm trong menu chuột phải. `moduleRoutes.HAS_DETAIL_PAGE` thêm `contact/quotation/order/invoice` → link chéo mở thẳng trang chi tiết.

### Xác suất cơ hội — read-only theo giai đoạn (CẬP NHẬT 2026-07-22)

Ô "Xác suất (%)" ở form Thêm/Sửa Cơ hội trước đây nhập tay, trong khi con số này đã được định nghĩa ở giai đoạn pipeline (`opportunity_stages.probability`, quản lý tại `/co-hoi/pipeline`) → hai nguồn cho cùng một số.

- Nay **BE suy ra** xác suất từ giai đoạn, đi cùng đường với `status` (`OpportunityCommandMapper` nhận thẳng `OpportunityStage`). `POST`/`PUT /api/opportunities` **không** còn nhận `probability`.
- FE: `OpportunityAddPage` + `OpportunityEditModal` hiện số **read-only** kèm chú thích "(tự động theo giai đoạn)", lấy từ `useOpportunityStages()` đã nạp sẵn — đổi Giai đoạn thì số đổi ngay trước cả khi lưu. Kéo thẻ Kanban sang cột khác cũng cập nhật xác suất.
- Các chỗ **chỉ đọc** giữ nguyên: cột "Xác suất" ở bảng danh sách, file xuất, `OpportunityInfoPanel`, thẻ Kanban.
- Luồng **Nhập file Excel/CSV giữ nguyên** cột "Xác suất (%)" (theo yêu cầu) — cơ hội nhập từ file không gắn giai đoạn nên vẫn nhận giá trị từ file.
- Không cần backfill: seed vốn đã sinh `probability` khớp giai đoạn tương ứng.

### Trang chi tiết Chiến dịch — `/chien-dich/:id` (CẬP NHẬT 2026-07-22)

- Tab **"Thành viên" đổi tên thành "Khách hàng"** cho đúng nghĩa (dữ liệu vẫn là bảng `campaign_members`, API/DTO không đổi).
- Thêm 4 tab **bản ghi quy về chiến dịch**: Tiềm năng / Cơ hội / Đơn hàng / Hóa đơn — dữ liệu từ `GET /api/campaigns/{id}/related` qua hook `useCampaignRelated`, render bằng `RelatedTable` sẵn có. Đây là chiều đọc ngược của attribution (`campaign_id`).
- Trang đã chuyển sang dùng toolkit chung `shared/components/detail/*` (`DetailHeader`, `Tabs`, `InfoRow`, `StatCards`) thay cho tab + thẻ tự chế trong file.
- `RelatedModule` thêm `'lead'`; `moduleRoutes` thêm `lead: '/tiem-nang'` và `'lead'` vào `HAS_DETAIL_PAGE`; `relatedColumns` thêm `LEAD_COLUMNS`.
- **Ô "Chiến dịch"** nay có ở form thêm mới + edit modal của **Cơ hội / Báo giá / Hóa đơn** (Tiềm năng và Đơn hàng đã có từ trước) — kèm cột `campaignName` ở bảng danh sách, dòng "Chiến dịch" ở `*InfoPanel` và cột trong file xuất. Trước đây tạo trực tiếp thì không quy về được chiến dịch nên báo cáo ROI đếm thiếu.

### Cuộn theo khối trên trang chi tiết — `ScrollFrame` (MỚI 2026-07-22)

Trước đây danh sách con đổ thẳng ra trang (mỗi nhóm `/related` tới **50 dòng**) nên phải cuộn thanh cuộn chính, header và tab trôi mất.

- **`shared/components/table/ScrollFrame.tsx`** — khung cuộn dùng chung: prop `visibleRows` (mặc định 10, quy đổi qua `tableScrollMaxHeight`) hoặc `maxHeight` (px, cho danh sách dòng cao không đều), `headBg` (`white` | `gray`).
- Header bảng bên trong **tự dính** nhờ arbitrary variant `[&_thead_th]:sticky …` — nơi dùng không phải sửa từng thẻ `<th>`. ⚠️ Class phải viết **literal**, không nội suy chuỗi, vì Tailwind quét class tĩnh trong mã nguồn.
- ⚠️ Header dính dùng `shadow-[inset_0_-1px_0_0_…]` thay `border-b`: `border-collapse` vẽ viền theo bảng nên viền sẽ trôi khi cuộn (bẫy đã gặp ở `RecordItemsPanel`).
- Áp dụng: `RelatedTable` (prop `visibleRows`), `Timeline` (prop `maxHeight`, mặc định 420px), `InfoCard` (thân thẻ tự cuộn), `CampaignMembersTable`, bảng hàng trả/đổi ở `TicketDetailPage`, `TicketTimeline`, 3 tab của `ChinhSachGiaDetailPage`, **`UrgentList` của Dashboard** (widget "Việc cần xử lý gấp" — BE trả tới 18 mục nên thẻ từng cao ~950px).
- Chọn prop: `visibleRows` cho **bảng** (dòng đều 37px), `maxHeight` cho **danh sách dòng cao không đều**.
- **Root 10 trang chi tiết bỏ `min-h-screen`** (chỉ còn `p-6 bg-bg-main`): `MainLayout` đã `h-screen overflow-hidden` và `<main>` mới là vùng cuộn — cùng lý do đã áp cho trang danh sách.

### Tự điền form thêm mới — `fillEmpty` (MỚI 2026-07-22)

Chọn một bản ghi ở ô khóa ngoại sẵn có → các ô liên quan **đang trống** tự điền. Không thêm nút/modal mới.

- **`shared/utils/prefill.ts`**: `fillEmpty(current, patch)` chỉ giữ khóa mà ô hiện tại còn trống (`''`/`null`/`undefined`) → **không bao giờ đè** thứ người dùng đã gõ, kể cả khi họ đổi lựa chọn; `hasFilled(patch)`; `primaryContactOf(contacts, customerId)` (ưu tiên liên hệ chính).
- **`shared/components/form/PrefillHint.tsx`**: dòng chữ nhạt "Đã tự điền các ô còn trống từ …" dưới ô nguồn. Cố ý **không** dùng `showAlert` — đó là modal chặn thao tác.
- Áp dụng ở **7 form thêm mới**: Cơ hội, Báo giá, Đơn hàng, Hóa đơn (chọn Khách hàng → liên hệ chính / người phụ trách / MST / địa chỉ xuất HĐ), Chăm sóc (→ liên hệ), Tiềm năng (→ công ty, MST, website, ngành, SĐT, email, liên hệ), Liên hệ (chọn Tổ chức → địa chỉ, SĐT cơ quan, nguồn gốc).
- Dữ liệu nguồn lấy từ `useCustomerList` / `useContactList` / `useOrderList` mà các trang này **đã nạp sẵn** (size 500) — không thêm request.
- **Chỉ áp cho trang thêm mới**, không áp cho `*EditModal` (tự điền khi sửa bản ghi cũ dễ gây bất ngờ).

**Hóa đơn — ô "Đơn hàng" (2026-07-22)**: trước đây form tạo hóa đơn không có chỗ nào cho biết hóa đơn thu tiền cho đơn nào (backend vốn đã có `invoices.order_id`, chỉ FE chưa dùng). Nay `InvoiceAddPage`/`InvoiceEditModal` có ô **Đơn hàng**; chọn đơn → tự điền khách hàng / liên hệ / người phụ trách / chiến dịch / MST / địa chỉ xuất HĐ (vẫn qua `fillEmpty` nên không đè ô đã gõ).

- Dropdown **chỉ hiện đơn chưa bị khóa** (`!isLocked`) — đơn đã khóa nghĩa là đã xuất hóa đơn, giữ đúng quan hệ Đơn hàng ↔ Hóa đơn **1-1**. Riêng `InvoiceEditModal` giữ thêm chính đơn đang gắn vào options (đơn đó đã bị khóa bởi chính hóa đơn này) và truyền `fallbackLabel={item.orderCode}` để không hiện như chưa chọn.
- **Không** chép `order_items` sang hóa đơn ở đây — đó là việc của nút "Xuất hóa đơn" bên Đơn hàng (`POST /api/orders/{id}/create-invoice`, có khóa đơn + chuyển đơn sang `completed`); làm ở form sẽ sinh hóa đơn trùng mà không khóa gì.
- **Chọn đơn còn chép luôn dòng hàng** — nhưng **chỉ khi bảng Hàng hóa chưa chọn sản phẩm nào** (`rows.every(r => !r.productId)`), để không xóa thứ đang gõ dở; dòng gợi ý nói rõ đã chép hay đã bỏ qua. Chép qua `orderService.getItems` → `fromItemResult` và **xóa `backendId`** (đó là id dòng hàng của *đơn hàng*, mang sang hóa đơn mới là id lạ). Vẫn **không** khóa đơn — rào chắn 1-1 là bộ lọc `isLocked` ở dropdown.
- Ô Đơn hàng dùng **tìm kiếm phía server** (`useOrderSearch`), xem prop `onSearchChange` của `SearchableSelect` — nạp sẵn 500 dòng là vô dụng với bảng 10.000 đơn.
- Hiển thị: cột **Đơn hàng** (`orderCode`) ở danh sách + file xuất, và dòng "Đơn hàng" trong `InvoiceInfoPanel` — nhờ vậy hóa đơn sinh từ luồng chuẩn cũng thấy được đơn nguồn (trước nay dữ liệu có sẵn nhưng không hiện ở đâu).

### Ô chọn bản ghi liên kết — `RecordPicker` (2026-07-22)

Rà lại toàn bộ form thêm mới cho thấy ba lỗi: nhiều FK bị **hardcode `null`** (báo giá thiếu Cơ hội; đơn hàng & hóa đơn thiếu Báo giá + Cơ hội), vài ô bắt **gõ ID thô** (Hóa đơn ở Chăm sóc, "ID đối tượng" ở Hoạt động), và các dropdown trên bảng lớn **không tìm ra bản ghi** vì hook lookup chỉ nạp một trang 500 dòng (liên hệ/cơ hội/báo giá/đơn/hóa đơn đều ~10.000 dòng, sản phẩm đã vượt 500).

- **`shared/lookup/useRecordSearch.ts`** — map `module → service.getList({ q, size: 20 })` cho `customer | contact | lead | opportunity | quotation | order | invoice`, kèm `searchHintOf()` nói rõ backend tìm theo cột nào (quotation/order/invoice **chỉ theo mã**).
- **`shared/components/form/RecordPicker.tsx`** — bọc `SearchableSelect` ở chế độ server; props `module`, `value`, `onChange`, `fallbackLabel`, `customerId`. `module=''` (chưa chọn loại) → ô khóa lại, dùng cho ô đa hình ở Hoạt động.
- **`shared/lookup/recordPrefill.ts#fetchPrimaryContactId`** — hỏi thẳng `GET /api/contacts?customerId=` thay cho `primaryContactOf` cũ (lục trong 500 dòng nạp sẵn nên thường không thấy liên hệ của khách vừa chọn).
- **`useProductList` nạp `size: 1000`** — bảng sản phẩm đã 509 dòng, nạp 500 là âm thầm mất 9 sản phẩm cuối.

**Ô liên kết theo từng form** (Add + Edit tương ứng): Báo giá → Cơ hội · Đơn hàng → Báo giá, Cơ hội · Hóa đơn → Đơn hàng, Báo giá, Cơ hội · Chăm sóc → Hóa đơn · Hoạt động → Đối tượng/Bản ghi liên quan theo loại đang chọn · tất cả form có Khách hàng → ô **Liên hệ lọc theo khách đó**.

- Chọn một bản ghi liên kết thì **tự điền các ô còn trống** (`fillEmpty`): cơ hội → khách/liên hệ/chiến dịch/chính sách giá/người phụ trách; báo giá → thêm cơ hội; hóa đơn (ở Chăm sóc) → khách + liên hệ. Đổi khách hàng thì **xóa liên hệ cũ** rồi mới điền lại — liên hệ của khách khác gắn vào là dữ liệu sai.
- Đổi "Loại đối tượng" ở Hoạt động cũng **xóa id cũ** (id của phân hệ khác là rác).
- ⚠️ **Cố ý KHÔNG chép dòng hàng** khi chọn Báo giá/Cơ hội: hai luồng đó đã có endpoint chuyển đổi riêng (`from-opportunity`, `convert-to-order`) và **khóa bản ghi nguồn + đổi trạng thái**; chép ngầm ở form thêm sẽ cho phép sinh nhiều đơn từ một báo giá. Riêng Đơn hàng → Hóa đơn có chép (rào chắn là bộ lọc `isLocked`).
- ⚠️ Ở `*EditModal` **bắt buộc truyền `fallbackLabel`** (`quotationCode`, `orderCode`, `opportunityName`, `contactName`, `invoiceCode`): kết quả tìm chỉ 20 dòng nên bản ghi đang gắn hầu như không nằm trong đó.

### Bảng Kanban Cơ hội — `/co-hoi/kanban` (MỚI 2026-07-12)

`OpportunityBoardPage` — cột = giai đoạn pipeline, thẻ = cơ hội. Kéo thẻ sang cột khác = **đổi giai đoạn**; `status` (open/won/lost) vẫn do BE **suy ra tự động** từ giai đoạn, FE không bao giờ gửi `status`.

- Dependency mới: **`@dnd-kit/core`** (^6.3.1 — bản 6.0.x peer-dep React ≤18, fail trên React 19). Không cần `@dnd-kit/sortable` (thẻ trong cột không sắp thứ tự thủ công).
- Files: `pages/OpportunityBoardPage.tsx`, `components/board/{BoardColumn,OpportunityCard}.tsx`, `hooks/{useOpportunityBoard,useChangeOpportunityStage}.ts`, `types/boardTypes.ts`.
- API: `GET /api/opportunities/board?q=` (1 request, kèm số cơ hội + tổng tiền mỗi cột, tối đa 50 thẻ/cột) · `POST /api/opportunities/{id}/stage`.
- **Bẫy đã xử lý**: (1) bắt buộc `DragOverlay` — refetch giữa lúc kéo sẽ remount cột và thẻ đang kéo bị "snap" về chỗ cũ; (2) `PointerSensor` với `activationConstraint: { distance: 5 }` để **click** (không di chuột) vẫn mở được trang chi tiết; (3) kéo vào cột **thua** → `ReasonModal` bắt nhập lý do, hủy modal thì rollback optimistic; (4) sau khi đổi giai đoạn phải invalidate `['opportunities']` (khớp tiền tố nên làm mới cả board lẫn danh sách).

### Cảnh báo trùng dữ liệu (MỚI 2026-07-12)

`shared/hooks/useDuplicateCheck.ts` (debounce 500ms) + `shared/components/DuplicateWarning.tsx` → banner vàng ở `LeadAddPage`, `CustomerAddPage`, `ContactAddPage` khi email/SĐT/MST trùng bản ghi đã có. **Không chặn lưu** (nhiều khách dùng chung số tổng đài / email công ty), chỉ liệt kê + link tới bản ghi trùng.

Khi **convert tiềm năng**: `ConvertLeadModal` dò trùng theo MST/email/SĐT của lead → nếu có khách hàng trùng, cho chọn **"Dùng khách hàng hiện có"** (gửi `customerId` → BE gắn Liên hệ + Cơ hội vào KH đó, không tạo KH thứ hai) hoặc **"Tạo khách hàng mới"**.

### Bàn làm việc (Dashboard) — `/dashboard` (MỚI 2026-07-04)

Trang tổng quan phân theo vai trò (`features/dashboard/`), phong cách AMIS. `DashboardPage` dùng `usePermission()` chọn view ưu tiên **ADMIN → SALES_MANAGER → SALES_STAFF**; có bộ chọn **kỳ** (Tháng này/Quý này/Năm nay) + **đơn vị tiền** (VND/Triệu đồng) cấp trang.

- **ADMIN** (`AdminDashboardView`): KPI tài khoản/vai trò/quyền, donut cơ cấu theo trạng thái/vai trò/đơn vị, cột tài khoản theo tháng, tổng quan bản ghi hệ thống → `GET /api/dashboard/admin`.
- **SALES_MANAGER** (`SalesDashboardView` với `showTeam`): thẻ KPI area-trend Doanh thu/Chi phí/Lợi nhuận, combo cột+đường tài chính, KPI cơ hội, tỷ lệ thắng, phễu chuyển đổi, cơ hội giá trị lớn, thống kê theo nhân viên, việc gấp → `GET /api/dashboard/manager`.
- **SALES_STAFF** (`SalesDashboardView` cá nhân): như trên nhưng phạm vi `owner_id = userId`, bỏ phần theo nhân viên → `GET /api/dashboard/sale`.
- Service/hooks: `services/dashboardService.ts` + `hooks/use{Admin,Manager,Sale}Dashboard.ts` (`useQuery`, `enabled` theo role).
- Component chart dùng chung: `components/` — `DashCard`, `KpiTile`, `DonutChart`, `RevenueCostProfitChart`, `AreaTrend`, `MonthlyBar`, `FunnelChart`, `StackedBarByGroup`, `RankedList`, `UrgentList`, `Selectors` (Period/Unit), `chartTheme` (palette + nhãn trạng thái tiếng Việt). Tiền tệ dùng `shared/utils/number.ts#formatMoney`.

### Tiềm năng — chấm điểm, web tracking & thông báo

- Trang `/tracking-demo` (`features/tracking-demo`): landing page mô phỏng — gọi `GET /api/tracking/campaigns` + `POST /api/tracking/visit|score|submit` (đều public) để tạo lead ẩn danh, gắn chiến dịch nguồn & cộng điểm `score`.
- **Nâng cấp 2026-07-22 — demo theo 4 bước đánh số**: (1) chọn **Chiến dịch nguồn** từ dropdown, kèm dòng URL quảng cáo minh họa tự điền `utm_campaign=<mã>` theo lựa chọn; (2) mở phiên → mã `TNW…`, điểm, **thanh tiến trình tới ngưỡng 50**, nút "Đặt lại phiên"; (3) hành vi (5 nút, tổng 80 điểm) + **nhật ký sự kiện** trong phiên; (4) form liên hệ. Cuối trang là bảng **"Điều gì đang xảy ra bên trong CRM"** nối từng thao tác với dữ liệu thật.
- Mở trang bằng `?utm_campaign=<mã>` (giống khách bấm quảng cáo thật) thì chiến dịch được chọn sẵn và phiên **tự bắt đầu**.
- Feature tách theo đúng `pages → hooks → services → API`: `services/trackingService.ts`, `hooks/useTrackingCampaigns.ts` + `hooks/useTrackingSession.ts`, 5 component trong `components/`, hằng số ở `config/trackingDemoConfig.ts` (trước đây page gọi thẳng `axiosInstance` — sai tầng).
- Trang công khai `/bao-gia-phan-hoi/:token` (`features/bao-gia-phan-hoi`): khách xem báo giá (`GET /api/public/quotations/{token}`) + phản hồi Đồng ý/Điều chỉnh/Không đồng ý (`POST /api/public/quotations/{token}/respond`) — link gửi qua email báo giá (kèm PDF). Ngoài MainLayout, không cần đăng nhập.
- **Soạn email báo giá trước khi gửi**: menu chuột phải báo giá `approved` → "Gửi email cho khách" mở `features/bao-gia/components/SendQuotationModal.tsx`. Modal nạp nội dung mặc định qua `useQuotationEmailDraft` (`GET /api/quotations/{id}/email-draft`), cho sửa tiêu đề + nội dung (soạn thảo WYSIWYG `shared/components/RichTextEditor.tsx` — TinyMCE self-host bản GPL, deps `tinymce` + `@tinymce/tinymce-react`), rồi gửi kèm `{ subject, body }` qua `POST /api/quotations/{id}/send`. 3 nút phản hồi + PDF do BE tự chèn.
- Header có **chuông thông báo** (`shared/components/layout/header/NotificationPopup.tsx`) dùng `shared/notifications/{notificationService,useNotifications}.ts` → `GET /api/notifications`, `/unread-count`, `POST /{id}/read`, `/read-all`, `/delete-bulk`, `/delete-all`.

**Bấm thông báo → nhảy tới bản ghi**: `NotificationPopup` suy phân hệ từ tiền tố `type` rồi dùng `recordPath()` của `shared/utils/moduleRoutes.ts` (nguồn sự thật duy nhất cho link chéo) — module có trang chi tiết mở thẳng `/{route}/{id}`, còn lại `navigate('{route}?focus={targetId}')`; các trang danh sách đọc `useSearchParams().get('focus')` và truyền vào prop **`focusId`** của `DataTable` để tự nhảy đúng trang phân trang, highlight và cuộn tới dòng. Tiền tố không khớp module nào (vd `handover_all`) thì chỉ đánh dấu đã đọc.

**Dọn hộp thông báo (2026-07-22)**: header popup có 3 icon — `FiCheckSquare` đánh dấu đã đọc tất cả, `FiList` bật/tắt **chế độ chọn**, `FiTrash2` **xóa tất cả**. Chế độ chọn hiện checkbox từng dòng + ô "Chọn tất cả" + thanh "Đã chọn n / Hủy / Xóa"; lúc này bấm dòng là tick chứ không điều hướng. Cả hai luồng xóa đều hỏi qua `useConfirm()` và là **xóa mềm** — chỉ ẩn khỏi hộp thông báo của chính mình, người khác vẫn còn tin, không khôi phục được và không có tab trong Thùng rác.

> ⚠️ Popup đóng theo listener "click ngoài `ref`", mà `ConfirmModal`/`AlertModal` render ở gốc app nên nằm ngoài `ref`. Hai modal đó mang attribute **`data-modal-layer`**, popup bỏ qua click có `closest('[data-modal-layer]')` — thiếu bước này thì vừa bấm "Xóa" là popup unmount và promise `confirm()` treo.

**Chấm thông báo trên sidebar**: `Sidebar.tsx` chỉ xét thông báo **chưa đọc**, suy module từ tiền tố `type`. Chấm **đỏ** = tạo mới/cần hành động; **vàng** = cập nhật/thay đổi (`UPDATE_NOTIFICATION_TYPES`); có cả hai → đỏ; đã đọc hết → không chấm. Màu chấm không đổi theo việc mục có đang được chọn hay không.

### Chính sách giá — `/chinh-sach-gia` (admin only)

Trang quản lý chính sách giá. Chỉ ADMIN thấy trên sidebar.

- **Danh sách** (`/chinh-sach-gia`): bảng price policies + tạo mới/sửa/xóa/bulk-delete
- **Chi tiết** (`/chinh-sach-gia/:id`): header thông tin policy + 5 tab sub-entity

### Chăm sóc sau bán — `/cham-soc` (module `ticket`)

Phân hệ Dịch vụ sau bán (feature `features/cham-soc/`): phiếu hỗ trợ / trả hàng / đổi hàng / khiếu nại.

- **Danh sách** (`/cham-soc`): `GET /api/tickets` — bảng đủ trường + badge loại/trạng thái/ưu tiên + cột "Quá hạn" (`isOverdue`); quick-filter theo loại; export/handover/bulk-delete; nút Xem → trang chi tiết.
- **Thêm mới** (`/cham-soc/them-moi`): form phiếu; hiện `ReturnItemsTable` khi loại là trả/đổi; người xử lý mặc định = user đăng nhập.
- **Chi tiết** (`/cham-soc/:id`): thông tin + dòng hàng trả/đổi + timeline ghi chú (`TicketTimeline`, system vs note) + nút hành động (`TicketWorkflowButtons`) đổi trạng thái (assign/start/resolve/approve/reject/receive/inspect/complete/close/reopen) + form CSAT khi resolved/closed. Status là **badge read-only**; reject dùng `ReasonModal`, resolve/complete dùng `ResolutionModal`, assign dùng `AssignTicketModal`.
- Menu **Chăm sóc** gắn `module: 'ticket'` (icon `FiLifeBuoy`); ẩn/hiện theo quyền `ticket`.

| Endpoint | Mô tả |
|----------|-------|
| `GET /api/price-policies` | Danh sách chính sách |
| `POST /api/price-policies` | Tạo mới |
| `PUT /api/price-policies/{id}` | Cập nhật |
| `DELETE /api/price-policies/{id}` | Xóa |
| `GET/POST/PUT/DELETE /api/price-policies/{id}/products` | Sản phẩm trong chính sách |
| `GET/POST/DELETE /api/price-policies/{id}/customers` | Khách hàng áp dụng |
| `GET/POST/DELETE /api/price-policies/{id}/customer-categories` | Nhóm KH áp dụng |
| `GET/POST/DELETE /api/price-policies/{id}/product-types` | Loại SP áp dụng |
| `GET/POST/DELETE /api/price-policies/{id}/employees` | Nhân viên áp dụng |

Files: `features/chinh-sach-gia/` — types, services, hooks (9 hooks), config, components (PricePolicyFormModal + 5 tabs), pages (ChinhSachGiaPage + ChinhSachGiaDetailPage)

### Phân quyền — `/phan-quyen`

Trang quản lý nhóm người dùng và phân quyền theo nhóm. Kết nối đầy đủ với API.

- **Panel trái**: Danh sách nhóm (roles) — tạo/sửa/xóa nhóm
- **Tab Thành viên**: Xem + thêm/xóa người dùng trong nhóm; Thu hồi/kích hoạt lại tài khoản; Chỉnh sửa năm xem data (`dataAccessFromYear`)
- **Tab Phân quyền**: Accordion theo module, toggle gán/thu hồi quyền ngay lập tức

#### Ẩn/hiện nút thao tác theo quyền — `can(module, action)`

Mọi nút Thêm/Sửa/Xóa/Nhập/Xuất/Bàn giao + menu chuột phải + phím tắt Alt+N + route `them-moi`/`nhap-file` đều gate theo quyền, **khớp guard BE**. Dùng `const { can } = usePermission();`:

- `can(m,'create')` → `CreateButton`, Alt+N (`usePageShortcuts({ onCreate: can(m,'create') ? goCreate : undefined })`), route `them-moi`
- `can(m,'import')` → nút Nhập + route `nhap-file` (mã quyền riêng `<m>.import`, 2026-07-28 — trước đây dùng chung `<m>.create`)
- `can(m,'export')` → nút Xuất (mã quyền `<m>.export`)
- `can(m,'edit')` / `can(m,'delete')` → menu Chỉnh sửa / Xóa + nút Xóa hàng loạt (2026-07-28: mọi module đều có mã `.edit`/`.delete` thật, kể cả quotation/invoice/product — không còn gate bằng role hardcode)
- `can(m,'handover')` → nút Bàn giao (role ADMIN/SALES_MANAGER)
- `can(m,'approve'|'process'|'approve_return')` → hành động workflow (duyệt báo giá, phát hành hóa đơn, xử lý phiếu...)
- `can(m,'submit'|'send')` (báo giá), `can('customer','activate')`, `can('opportunity','change_stage')`, `can('order','create_invoice')`, `can('campaign','send_email')`, `can('lead','convert')` — quyền đặc thù theo hành động thật (2026-07-28), thay cho các chỗ trước đây gate sai module/action hoặc không gate gì

`PermissionContext.tsx#can()` nay chỉ còn một nhánh chung `has(`${module}.${action}`)` cho mọi action (trừ `handover` theo role) — đã bỏ 2 cơ chế đặc cách `MANAGE_BY_ROLE`/`DELETE_BY_ROLE` vì product/quotation/invoice giờ có đủ mã quyền thật. Route thiếu quyền → `RequirePermission` redirect `/forbidden`. ⚠️ Token cũ (trước 2026-07-28) không có các mã quyền mới — phải đăng nhập lại.

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

- **7 tab**: Tiềm năng, Liên hệ, Khách hàng, Cơ hội, Báo giá, Hóa đơn, Sản phẩm
- **Khôi phục**: `POST /api/{module}/{id}/restore`
- **Xóa vĩnh viễn**: `DELETE /api/{module}/{id}/purge` — set `is_purged=1`, ẩn UI, DB giữ soft-delete

Files: `features/thung-rac/` — types/thungRacTypes.ts, services/trashService.ts, hooks/useTrash.ts, config/trashColumns.tsx, pages/ThungRacPage.tsx

### Nhật ký hệ thống — `/nhat-ky-he-thong` (MỚI 2026-07-27, chỉ ADMIN)

Trang admin xem nhật ký sự kiện thao tác người dùng — gộp từ các bảng đã có sẵn trong DB (đọc-only, không có bảng riêng). Tab lọc theo nguồn: Tất cả / Duyệt báo giá / Bàn giao tiềm năng / Phiếu chăm sóc / Thông báo hệ thống / Tạo-sửa-xóa bản ghi. Server-side pagination + search, theo mẫu `usePagedLeadList`.

- `GET /api/audit-log?source=&q=&page=&size=` — chỉ ADMIN được gọi (chặn ở BE `SecurityConfig`, kể cả GET)
- Sidebar: mục "Nhật ký hệ thống" với `adminOnly: true` (chỉ ADMIN nhìn thấy trong menu, giống "Phân quyền"/"Đăng ký NV")

Files: `features/nhat-ky-he-thong/` — types/auditLogTypes.ts, services/auditLogService.ts, hooks/useAuditLogList.ts, config/auditLogColumns.tsx, pages/NhatKyHeThongPage.tsx

### Bàn giao công việc (2026-06-12)

**Per-module** — 5 trang danh sách (Tiềm năng, Khách hàng, Cơ hội, Báo giá, Hóa đơn): chọn hàng → nút "Bàn giao (n)" → `HandoverModal` → `POST /api/{module}/handover-bulk`.

**Toàn bộ** — `TransferWorkModal` (icon header): chọn "từ user A → user B" → `POST /api/handover/all` — chuyển toàn bộ 5 module cùng lúc. Chỉ ADMIN/SALES_MANAGER có quyền.

| File mới | Vai trò |
|----------|---------|
| `shared/components/HandoverModal.tsx` | Modal chọn người nhận + lý do; fetch user active qua `userService.listActive()` |
| `features/users/hooks/useHandoverAll.ts` | `useMutation` → `POST /api/handover/all` |
| `features/{module}/hooks/useHandoverBulk*.ts` × 5 | `useMutation` → `POST /api/{module}/handover-bulk`, invalidate queryKey |

**Quyền:** ADMIN/SALES_MANAGER bàn giao bất kỳ bản ghi; nhân viên chỉ bàn giao bản ghi do mình là `owner_id`.

### Trợ lý AI Copilot — bong bóng chat nổi (MỚI)
- **Widget nổi góc phải-dưới**, hiện trên **mọi trang** (mount trong `shared/components/layout/MainLayout.tsx`). Không phải route, không có mục sidebar.
- Đặt tại `src/shared/copilot/` (theo tiền lệ `shared/notifications` — widget cross-cutting có service/hook riêng trong `shared/`, để `MainLayout` không import `features/`): `copilotTypes.ts`, `copilotService.ts` (`POST /api/copilot/ask`, timeout 60s), `useAskCopilot.ts` (`useMutation`, unwrap `.data.data`), `CopilotWidget.tsx`, `answerRenderer.tsx` (bullet + bold + màu tăng/giảm).
- Hỏi tiếng Việt về dữ liệu CRM — số liệu tổng hợp ("Doanh thu quý này so quý trước? Tỷ lệ thắng?") hoặc tình hình khách hàng cụ thể ("Khách ABC đang thế nào?"). BE giới hạn dữ liệu theo quyền của người đăng nhập.
- **Hành động (`action` trong response)**: `type="navigate"` → widget **tự điều hướng** (`useNavigate`) + đóng panel — dùng cho lệnh "mở trang khách hàng", "tạo báo giá mới", "mở khách hàng ABC" (BE tìm bản ghi theo quyền → `/khach-hang/:id`); `type="link"` → hiện **nút** trong bong bóng (vd "Xem biểu đồ so sánh"), bấm mới điều hướng.
- Cần backend cấu hình `APP_AI_API_KEY` (Gemini). FE không đổi biến môi trường nào.

### Phân tích so sánh — `/phan-tich` (MỚI)
- Trang đích của link "Xem biểu đồ so sánh" từ Copilot (`/phan-tich?period=month|quarter|year`); **không có mục sidebar** (vào qua bot hoặc gõ URL).
- Feature `src/features/phan-tich/`: `pages/PhanTichPage.tsx` + `hooks/useRevenueByCampaign.ts` (gọi `dashboardService.getRevenueByCampaign` — method mới trong service dashboard).
- **Tái dùng toàn bộ chart Dashboard** (`@/features/dashboard/components/*`: KpiTile, DashCard, RevenueCostProfitChart, DonutChart, FunnelChart, RankedList, Selectors) và hooks `useManagerDashboard`/`useSaleDashboard` → số liệu đồng nhất Dashboard.
- Nội dung: KPI doanh thu/chi phí/lợi nhuận **kỳ này vs kỳ trước** (kèm dòng "Kỳ trước: ..."), xu hướng 12 tháng, tỷ lệ thắng, phễu, **so sánh theo nhân viên** (chỉ ADMIN/SALES_MANAGER) + **theo chiến dịch**. Nhân viên chỉ thấy số liệu mình phụ trách (BE scope sẵn).

### Shared component
- `shared/components/ConfirmModal.tsx` — modal xác nhận dùng chung, thay thế `window.confirm()`
- `shared/components/HandoverModal.tsx` — modal bàn giao, dùng chung cho 5 module list page

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
├── hoa-don/           # Invoice — có InvoiceImportPage
├── hoat-dong/         # Activity — có ActivityImportPage
├── san-pham/          # Product — có ProductImportPage
├── tracking-demo/     # Landing page demo web tracking
└── chinh-sach-gia/    # Price Policy — danh sách + chi tiết 5 tab sub-entity
```

### Hoàn thiện 3 hạng mục (2026-06-13)

- **Roles động**: `RegisterEmployeePage` dùng `useRoleGroups()` (`features/phan-quyen/hooks`) thay ROLE_OPTIONS hardcode — dropdown vai trò lấy từ `GET /api/roles`.
- **Import UPDATE/BOTH**: 3 trang import opp/order/quotation thêm field "Mã (để cập nhật)" trong `FIELDS` để map cột Mã; backend dò trùng theo mã. Sản phẩm/khách hàng/liên hệ dò theo sku/taxCode/email (không cần thêm field).
- **Edit modal field đầy đủ + sửa dòng con**: các edit modal hiển thị đầy đủ field. Báo giá/Đơn hàng/Cơ hội: edit modal nạp `getItems()`, sửa bằng `ProductLineItemsTable`, lưu bằng diff (`diffLineItems`/`fromItemResult`/`toItemPayload` trong `shared/components/form/productLineItem.ts`) → `createItem`/`updateItem`/`deleteItem`. Liên hệ: trình sửa danh sách SĐT (`getPhones`/`createPhone`/`updatePhone`/`deletePhone`). Service con thêm vào `quotationService`/`orderService`/`opportunityService`/`contactService`.

### Form thêm mới full-page — 8 module (2026-06-13)

Mỗi module data có trang thêm mới full-page (layout AMIS), truy cập qua nút "Thêm mới" (FiPlus) trên trang danh sách.

| Module | Route thêm mới | Endpoint backend |
|--------|---------------|-----------------|
| Tiềm năng | `/tiem-nang/them-moi` | `POST /api/leads` |
| Liên hệ | `/lien-he/them-moi` | `POST /api/contacts` (nhận `phones[]`) |
| Khách hàng | `/khach-hang/them-moi` | `POST /api/customers` |
| Cơ hội | `/co-hoi/them-moi` | `POST /api/opportunities` (nhận `items[]`) |
| Chiến dịch | `/chien-dich/them-moi` | `POST /api/campaigns` |
| Báo giá | `/bao-gia/them-moi` | `POST /api/quotations` (nhận `items[]`) |
| Đơn hàng | `/don-hang/them-moi` | `POST /api/orders` (nhận `items[]`) |
| Hóa đơn | `/hoa-don/them-moi` | `POST /api/invoices` (nhận `items[]`) |
| Hoạt động | `/hoat-dong/them-moi` | `POST /api/activities` |
| Sản phẩm | `/san-pham/them-moi` | `POST /api/products` |

> **Trạng thái + dropdown nổi (2026-06-24):** form thêm mới **không còn chọn `status`** (tự động/qua hành động); trường người phụ trách mặc định = user đăng nhập (`useAuth()`). Mỗi list page có **nút hành động** theo status gọi `features/<m>/hooks/use<Module>Workflow.ts` (lead convert/lose, customer activate/deactivate, activity start/complete/cancel, order confirm/process/complete/cancel, quotation submit/approve/reject/send). Route mới **`/co-hoi/pipeline`** (`OpportunityPipelinePage`) để CRUD giai đoạn — status cơ hội suy ra từ giai đoạn. `SearchableSelect` render qua **portal** (`createPortal` + `position:fixed`, `z-[10000]`) nên không bị section/bảng/modal đè. Lý do nhập qua `shared/components/ReasonModal.tsx`. Ngày/số dùng `@/shared/utils/date.ts` + `@/shared/utils/number.ts`.
>
> **Route mới `/san-pham/danh-muc` (2026-07-29)** — `ProductCategoryPage.tsx` (mirror y hệt `OpportunityPipelinePage.tsx`): CRUD `product_categories` (bảng thủ công, modal thêm/sửa dùng chung, `code` khóa khi sửa vì không đổi được sau khi tạo). Nút "Danh mục" trên `SanPhamPage.tsx` (`PageHeaderSlot`, không gate theo `can()` — giống nút "Giai đoạn" của Cơ hội). `useProductCategoryMutations.ts` invalidate `['product-categories']` — dropdown chọn danh mục ở `ProductAddPage`/`ProductEditModal`/`PolicyProductCategoriesTab` (cùng queryKey qua `useProductCategories()`) tự cập nhật theo, không cần sửa 3 nơi đó. Cột `parent_id` (danh mục cha) đã bị xóa hẳn khỏi DB — chưa từng dùng để dựng cây.

**Shared form components** — `src/shared/components/form/`:

| File | Vai trò |
|------|---------|
| `formStyles.ts` | `inputCls`, `btnBase` dùng chung |
| `FormSection.tsx` | Section có tiêu đề (h2 + border-b) |
| `FieldRow.tsx` | Hàng field label 148px + dấu `*` required, prop `alignTop` cho textarea, prop `error` (viền đỏ + dòng lỗi) |
| `FormField.tsx` | **Form một cột**: label trên / ô dưới, props `required`, `hint`, `error`, `icon`. Kèm export `FieldError` — chỉ bọc ô nhập để gắn phần báo lỗi cho form đã có `<label>` riêng (các `*EditModal`) |
| `CenteredFormCard.tsx` | Khung trang form một cột: khối tiêu đề (icon + h1 + phụ đề + nút back tùy chọn) và thẻ trắng **cùng căn giữa** (`mx-auto max-w-xl`). Dùng ở `/dang-ky-nhan-vien`, `/tai-khoan` |
| `FormPageHeader.tsx` | Header form: Hủy / Lưu và thêm / Lưu, prop `saving` |
| `ProductLineItemsTable.tsx` | Bảng hàng hóa controlled cho báo giá/đơn hàng/cơ hội (props `showUnit`/`showTax`/`pricePolicyId`; `onChange` là setter dạng `useState`) |
| `usePolicyPricing.ts` | Tra & tự điền đơn giá/CK theo chính sách giá cho từng dòng hàng (dùng trong `ProductLineItemsTable`) |
| `productLineItem.ts` | Type `LineItemRow`/`ProductOption` + helper `computeTotals`, `toItemPayloads` |
| `DateInput.tsx` | Ô nhập ngày **dd/mm/yyyy** + lịch popup, `value`/`onChange` dùng ISO `yyyy-mm-dd` (thay `<input type="date">`) |
| `DateTimeInput.tsx` | Ngày dd/mm/yyyy + giờ HH:mm, `value`/`onChange` dùng ISO `yyyy-mm-ddTHH:mm` (thay `<input type="datetime-local">`) |
| `Calendar.tsx` / `useAnchoredPanel.ts` | Lưới lịch + hook panel nổi (portal) dùng bởi DateInput/DateTimeInput |

**Pattern mỗi module**: `types/<m>Types.ts` (thêm `Create*Payload`) → `services/<m>Service.ts` (`create()`) → `hooks/useCreate<X>.ts` → `pages/<X>AddPage.tsx`. State lift lên page (`useState<FormState>` + `set(patch)`); "Lưu" → `navigate` về list, "Lưu và thêm" → reset form. Mã (code) bắt buộc nhập tay.

**Lookup hooks dùng cho dropdown**:
- `features/users/hooks/useActiveUsers.ts` — `GET /api/users?status=active`
- `features/users/hooks/useOrgUnits.ts` — `GET /api/org-units`
- `features/co-hoi/hooks/useOpportunityStages.ts` — `GET /api/opportunity-stages`
- `features/san-pham/hooks/useProductCategories.ts` — `GET /api/product-categories` (CRUD đầy đủ ở `useProductCategoryMutations.ts` + trang `/san-pham/danh-muc`, xem mục Trạng thái + dropdown nổi phía trên)
- Tái sử dụng `useCustomerList` / `useContactList` / `useProductList`

### Nhập file Excel/CSV — 8 module (2026-06-11)

Mỗi module data có trang nhập file 4 bước riêng, truy cập qua nút "Nhập file" trên trang danh sách.

| Module | Route nhập file | Endpoint backend |
|--------|----------------|-----------------|
| Tiềm năng | `/tiem-nang/nhap-file` | `POST /api/leads/import-bulk` |
| Liên hệ | `/lien-he/nhap-file` | `POST /api/contacts/import-bulk` |
| Khách hàng | `/khach-hang/nhap-file` | `POST /api/customers/import-bulk` |
| Cơ hội | `/co-hoi/nhap-file` | `POST /api/opportunities/import-bulk` |
| Chiến dịch | `/chien-dich/nhap-file` | `POST /api/campaigns/import-bulk` |
| Báo giá | `/bao-gia/nhap-file` | `POST /api/quotations/import-bulk` |
| Đơn hàng | `/don-hang/nhap-file` | `POST /api/orders/import-bulk` |
| Hóa đơn | `/hoa-don/nhap-file` | `POST /api/invoices/import-bulk` |
| Hoạt động | `/hoat-dong/nhap-file` | `POST /api/activities/import-bulk` |
| Sản phẩm | `/san-pham/nhap-file` | `POST /api/products/import-bulk` |
| Chăm sóc | `/cham-soc/nhap-file` | `POST /api/tickets/import-bulk` |
| Chính sách giá | `/chinh-sach-gia/nhap-file` | `POST /api/price-policies/import-bulk` |

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

**Lưu ý**: Import UPDATE/BOTH áp dụng cho các module có khóa duy nhất (lead, product, contact, customer, opportunity, order, quotation, invoice, campaign, ticket, price-policy — khớp `code`). **Hoạt động chỉ hỗ trợ CREATE** (không có khóa duy nhất).

### Xuất file Excel/CSV — 8 module (2026-06-19), +Chăm sóc +Chính sách giá (2026-07-28)

Mỗi trang danh sách có nút **"Xuất file"** (`FiDownload`) cạnh nút "Nhập file". Xuất **hoàn toàn ở frontend** (dùng `xlsx` đã có sẵn) — không cần endpoint backend. Chăm sóc (ticket) đã có từ trước; Chính sách giá (`pricingExportColumns.ts`) mới thêm 2026-07-28 cùng đợt xây tính năng Nhập file cho module này.

- **Phạm vi dòng**: có dòng đang tick → xuất các dòng đó; không tick → xuất toàn bộ danh sách (`rowsToExport = selectedRows.length > 0 ? selectedRows : data`).
- **Chọn cột**: `ExportModal` liệt kê toàn bộ cột (mặc định tick tất cả), có nút Chọn/Bỏ chọn tất cả.
- **Định dạng**: `.xlsx` hoặc `.csv` (CSV prepend BOM UTF-8 cho tiếng Việt). Tên file: `<module>_yyyymmdd.<ext>`.

**Shared** — `src/shared/components/export/`:

| File | Vai trò |
|------|---------|
| `exportTypes.ts` | `ExportColumn<T>` (`key`, `label`, `format?(row)`), `ExportFormat` |
| `exportFile.ts` | `exportRows(rows, columns, selectedKeys, format, fileName)` — build AOA → xlsx/csv |
| `ExportModal.tsx` | Modal chọn cột (checkbox) + radio định dạng + nút Xuất file |

**Per-module** — `features/<module>/config/<module>ExportColumns.ts`: mảng `ExportColumn[]` định nghĩa cột xuất, tái dùng nhãn enum (`STATUS_LABELS`…) + `formatISODate` để ghi giá trị thuần đã format.

### Hiển thị đầy đủ trường DB + resolve tên khóa ngoại — 8 module (2026-06-20)

Các bảng danh sách trước đây chỉ hiện một phần nhỏ số cột. Nay **hiển thị đầy đủ mọi trường nghiệp vụ** giống DB, và **đổi ID khóa ngoại sang tên** (người phụ trách, khách hàng, liên hệ, giai đoạn, đơn vị, danh mục…).

- Column config chuyển từ mảng tĩnh sang **factory** `get<Module>Columns(lookups)` — nhận `Map<id, tên>`.
- Page gọi lookup hook (`useActiveUsers`, `useCustomerList`…), dựng map bằng `toIdNameMap`, truyền vào factory trong `useMemo`.
- Tiện ích mới: `shared/utils/lookup.ts` (`toIdNameMap`, `lookupName`) + `shared/components/table/cells.tsx` (`fkCell`, `currencyCell`, `dateCell`, `boolBadge`, `labelCell`, `badgeCell`…).
- Mặc định hiện tất cả cột — người dùng ẩn bớt qua panel **"ẩn/hiện cột"** có sẵn của DataTable.
- `ProductResult` (FE) chỉ còn các trường cốt lõi (sku/name/category/type/unit/basePrice/costPrice/vatRate/description) — đã bỏ secondaryUnit/conversionRate/brand/origin/barcode và các field dệt may.

Chi tiết pattern: xem `CODE_GUIDE_FRONTEND.md` mục 8b.

### Tên khóa ngoại do BE trả sẵn — bỏ hiển thị "#id" (cập nhật)

Trước đây FE resolve tên khóa ngoại phía client nên đôi khi lộ `#5`/`#128` (owner bị vô hiệu hóa,
vượt ngưỡng trang lookup, bản ghi đã xóa). Nay **BE trả sẵn tên** trong DTO danh sách (`ownerName`,
`customerName`, `contactName`, `quotationCode`…), FE chỉ việc hiển thị:

- Result type thêm các field `*Name`/`*Code`.
- `get<Module>Columns()` **bỏ tham số lookup**; cột khóa ngoại dùng `{ accessorKey: 'ownerName', cell: textCell }`.
- Trang danh sách **bỏ** các hook lookup (`useActiveUsers`, `useCustomerList`…) + `toIdNameMap` vốn chỉ dùng để hiển thị.
- Timeline Chăm sóc đọc `comment.authorName`. Fallback `lookupName` đổi `#id` → `'—'` (không lộ mã).
- Áp dụng 11 module danh sách: Tiềm năng, Liên hệ, Khách hàng, Cơ hội, Báo giá, Đơn hàng, Hóa đơn, Hoạt động, Sản phẩm, Chiến dịch, Chăm sóc.

### Thao tác dòng — menu chuột phải (2026-07-08)

Cột **"Thao tác"** (các nút icon ghim bên phải bảng) đã được **gỡ khỏi cả 13 trang danh sách**. Thay vào đó:

- **Chuột phải** vào một dòng → menu hiện tại vị trí con trỏ với các mục **dạng chữ** (Chỉnh sửa, Xóa, và các bước workflow theo trạng thái). Mục **Xóa** tô đỏ, tách bằng đường kẻ.
- **Nhấp đúp** dòng → chạy hành động chính: mở modal **Chỉnh sửa**, hoặc điều hướng **trang chi tiết** (Chăm sóc, Chiến dịch, Chính sách giá).
- Page khai báo `rowActions={(row) => RowAction[]}` + `onRowDoubleClick` cho `DataTable`; component `shared/components/table/RowContextMenu.tsx` render qua portal (`position: fixed`) nên không bị khung bảng cắt, tự lật vào trong khi chạm mép màn hình, đóng khi click ngoài / `Escape` / cuộn.

Chi tiết pattern: xem `CODE_GUIDE_FRONTEND.md` mục 8.

### Bố cục 2 bảng (master–detail) cho phân hệ có dòng hàng (2026-07-09)

Bốn trang danh sách có dòng hàng — **Cơ hội, Báo giá, Đơn hàng, Hóa đơn** — nay chia làm 2 bảng:

- **Bảng trên**: `DataTable` danh sách bản ghi (giữ nguyên chuột phải, nhấp đúp, lọc nhanh, chọn nhiều dòng).
- **Bảng dưới**: `RecordItemsPanel` — **chỉ-xem**, hiện dòng hàng của bản ghi đang được **nhấp chọn** ở bảng trên (Mã hàng / Tên hàng / ĐVT / Số lượng / Đơn giá / Chiết khấu / Thuế % / Thành tiền / Ghi chú). Chưa chọn dòng nào → hiện gợi ý; bản ghi không có dòng hàng → hiện thông báo rỗng.

Cách hoạt động:
- `DataTable` có prop mới **`onRowSelect?: (row: T | null) => void`** — phát ra dòng đang highlight (nhấp đơn để chọn, nhấp lại để bỏ chọn; chuột phải cũng chọn). Prop tùy chọn nên các trang khác không đổi.
- Trang nạp dòng hàng qua hook React Query `use{Module}Items(id)` (`useOpportunityItems`, `useQuotationItems`, `useOrderItems`, `useInvoiceItems`) — chỉ gọi API khi có bản ghi được chọn (`enabled: id != null`).
- Item backend chỉ trả `productId`, nên Mã hàng/Tên hàng/ĐVT được tra qua hook **`useProductMap()`** (`features/san-pham/hooks/useProductMap.ts`) rồi truyền vào factory cột dùng chung `getLineItemPanelColumns(productMap, { showTax })` (Cơ hội không có `taxRate` → tắt cột Thuế).

**Luôn chọn dòng đầu tiên**: `DataTable` prop `autoSelectFirstRow` — 4 trang này bật, nên bảng dòng hàng có dữ liệu ngay khi mở trang. Khi bật, nhấp lại dòng đang chọn **không** bỏ chọn (bảng dưới không bao giờ trống); dòng đầu được chọn lại khi tải xong dữ liệu, đổi trang, đổi bộ lọc, hoặc dòng đang chọn biến mất.

**Chiều cao & vùng cuộn riêng**: bảng trên cao **7 dòng** (`DataTable` prop `visibleRows={7}`), bảng dưới cao **3 dòng** (`RecordItemsPanel` mặc định `visibleRows=3`); phần dư cuộn **trong từng khung**, trang không phải cuộn dọc. Chiều cao tính từ design token qua `tableScrollMaxHeight(n)` (`shared/components/table/tableMetrics.ts`). `visibleRows` của `DataTable` là **tùy chọn** — trang không truyền thì bảng cao theo số dòng như trước. Header cột **dính** khi cuộn; đường kẻ dưới header dùng `inset shadow` thay `border` vì viền của `th` dính bị `border-collapse` vẽ theo bảng nên sẽ trôi mất.

### Tiêu đề + nút hành động nằm trên Header chung (2026-07-09)

Cả **13 trang danh sách** không còn khối tiêu đề riêng trong thân trang. Thay vào đó:

- `Header.tsx` có `<div id="page-header-slot">` ở khoảng giữa hamburger và nhóm icon.
- Trang bọc tiêu đề + nhóm nút bằng **`<PageHeaderSlot>`** (`shared/components/layout/PageHeaderSlot.tsx`) — dùng `createPortal` để bơm vào slot đó. Chọn portal thay vì Context vì nhóm nút phụ thuộc state của page (`selectedRows`, handler mở modal…), portal giữ nguyên JSX + state tại chỗ.
- **Root của trang danh sách không dùng `min-h-screen`** — `MainLayout` đã là `h-screen overflow-hidden` và `<main>` là vùng cuộn duy nhất; `min-h-screen` bên trong ép chiều cao thừa nên trang phải cuộn dọc. Từ 2026-07-22 quy tắc này áp cho **cả trang chi tiết**; chỉ trang ngoài layout (`LoginPage`, `ActivatePage`…) mới giữ `min-h-screen`.

### Sắp xếp mặc định: bản ghi mới nhất lên đầu (2026-07-09)

Các list hook FE vốn đã gửi `sortBy: 'createdAt', sortDir: 'desc'`. Nay **mặc định của backend** cũng là `createdAt` / `desc` ở 4 controller có dòng hàng (`OpportunityController`, `QuotationController`, `OrderController`, `InvoiceController`) — trước đó là `id` / `asc` (cũ nhất trước). `DataTable` khởi tạo sort rỗng nên giữ nguyên thứ tự server trả về.

### Chuẩn hóa tiếng Việt filter bảng + tag lọc nhanh hoạt động — DataTable (2026-06-20)

- **Việt hóa toàn bộ** các panel của DataTable (icon trên thanh công cụ): Lọc bản ghi, Sắp xếp theo cột, Tô màu có điều kiện — gồm toán tử (Bằng/Không bằng/Chứa/Không chứa/Để trống/Không để trống), nút (Áp dụng/Hủy/Thêm điều kiện/Thêm quy tắc), placeholder, phạm vi Ô/Hàng. Toán tử gom về `OPERATOR_OPTIONS` trong `shared/components/table/filterConditions.helpers.ts` (hết lặp giữa 2 panel).
- **Tag lọc nhanh giờ thực sự lọc**: đổi prop `quickFilters` sang khai báo `QuickFilterDef[]` (`{id,label,field,value}`). `DataTable` tự quản trạng thái chọn (single-select), lọc `String(row[field]) === value` (hợp cả boolean). 8 trang lọc theo `status` hoặc cờ boolean (`isActive`/`isPrimary`).

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
3. Thêm vào `NAV_ITEMS` trong `shared/components/layout/sidebar/sidebarConfig.ts` — gán đúng flag:
   - Module nghiệp vụ: thêm `module: '<ten-module>'` (vd: `module: 'lead'`) — hiện khi user có ít nhất 1 permission `<module>.*`
   - Mục admin: thêm `adminOnly: true` — chỉ hiện với ADMIN
   - Mục chỉ nhân viên: thêm `nonAdminOnly: true` — ẩn với ADMIN
   - Không flag: luôn hiện với mọi user đã đăng nhập

### Thêm cột bảng
- Tạo file `features/<name>/config/<name>Columns.tsx`
- Dùng `ColumnDef[]` từ `@tanstack/react-table`
- Dùng helper cell dùng chung trong `@/shared/components/table/cells` (`dateCell`, `currencyCell`, `textCell`, `boolBadge`, `labelCell`, `fkCell`…) thay vì viết lại format
- Cột khóa ngoại: export config dạng factory `get<Module>Columns(lookups)` nhận `Map<id, tên>`, render bằng `fkCell(map)`; page dựng map bằng `toIdNameMap` từ `@/shared/utils/lookup` (xem `CODE_GUIDE_FRONTEND.md` mục 8b)

### Màu sắc & style
- Chỉ dùng token đã định nghĩa trong `tailwind.config.js`
- Không hardcode màu hex trực tiếp trong class

---

## 5b. Hoàn thiện luồng nghiệp vụ (2026-07-07)

Đối chiếu `luongnghiep.md` với code và vá các lỗ hổng UI để luồng chảy mượt:

- **Chiến dịch trong form Tiềm năng**: `LeadAddPage` + `LeadEditModal` có ô "Chiến dịch nguồn" (`useCampaignList`); cột "Chiến dịch" trong `leadColumns` (attribution bắt đầu từ đây).
- **Tra giá theo chính sách (pricebook)**: `ProductLineItemsTable` nhận prop `pricePolicyId`, logic tra giá nằm ở hook `shared/components/form/usePolicyPricing.ts` (`pricingService.resolve` → `GET /api/pricing/resolve`). Ô "Chính sách giá" có ở **cả 4 form** Cơ hội + Báo giá (`OpportunityAddPage`/`OpportunityEditModal`/`QuoteAddPage`/`QuotationEditModal`); Đơn hàng/Hóa đơn **không có** vì hai bảng đó không có cột `price_policy_id` (giá đã chốt từ báo giá chuyển sang).

  Quy tắc tự điền: chọn sản phẩm → điền ngay ĐVT/giá gốc/thuế từ `ProductOption`, rồi chính sách **ghi đè** đơn giá + CK% nếu sản phẩm nằm trong chính sách (`discount` backend trả về là **số tiền/đơn vị** → quy đổi sang %); **đổi chính sách** → tính lại mọi dòng đã chọn sản phẩm; **đổi số lượng** → tra lại (debounce 400ms) vì `price_policy_products.min_qty` là ngưỡng áp dụng; ngoài chính sách / dưới ngưỡng / bỏ chọn chính sách → **quay về giá gốc** của sản phẩm.

  Khi giá ưu đãi **không** được áp, hook giữ một dòng chú thích cho từng dòng hàng (chữ hổ phách dưới ô hàng hóa): `"Cần tối thiểu N để hưởng giá chính sách"` (BE trả `minQty`) hoặc `"Sản phẩm ngoài chính sách giá"`. Không im lặng trả về giá gốc như trước — người dùng từng tưởng tính năng hỏng. Chú thích sống trong state của hook, **không** nhét vào `LineItemRow` để khỏi lẫn vào payload gửi backend.

  ⚠️ Hai bẫy đã xử lý: (1) prop `onChange` của bảng là **`Dispatch<SetStateAction<LineItemRow[]>>`** — patch bất đồng bộ phải dùng hàm cập nhật, bản cũ `map` trên `rows` của render cũ nên kết quả tra giá về là **xóa mất sản phẩm vừa chọn**; (2) effect tính lại **lấy baseline ngay lúc mount** để mở modal sửa báo giá cũ không tự nhảy giá — modal phải nạp form (đặt `pricePolicyId`) **trước** khi dòng hàng về, đảo thứ tự sẽ làm giá tự đổi lúc vừa mở. Mỗi dòng có token chống phản hồi cũ đè lên thao tác mới.
- **Đợt thanh toán hóa đơn**: `PaymentSchedulesTable` + `useInvoicePayments` (`GET/POST/PUT/DELETE /api/invoices/{id}/payment-schedules`) — thêm/sửa/xóa đợt; BE tự suy ra `paymentStatus` + `status` (`partially_paid`/`paid`).

  **Chỗ đặt (sửa 2026-07-22)**: bảng này nay là **tab "Đợt thanh toán" ở trang chi tiết `/hoa-don/:id`**, không còn chỉ nằm trong `InvoiceEditModal`. Lý do: "Phát hành" (`POST /{id}/issue`) **khóa hóa đơn**, mà modal sửa chỉ mở khi `!isLocked` → phát hành xong là không nhập được thanh toán nữa, hóa đơn **kẹt vĩnh viễn ở `sent`** dù backend không hề chặn (`/payment-schedules` không kiểm `isLocked`). Modal sửa vẫn giữ bảng cho hóa đơn nháp.
  ⚠️ `useInvoicePayments` phải invalidate **cả `['invoice', id]` (số ít)** ngoài `['invoice-payments', id]` + `['invoices']` — `useInvoiceDetail` dùng key số ít, thiếu nó thì thêm đợt xong badge trạng thái trên header trang chi tiết đứng yên.
- **Nút Hoàn tất đơn hàng**: `DonHangPage` thêm hành động `complete` (khi `processing`).
- **Tự điều hướng sau convert**: convert tiềm năng → `/co-hoi`; báo giá → đơn hàng → `/don-hang`; đơn hàng → hóa đơn → `/hoa-don` (kèm toast).
- **Link chéo**: chi tiết phiếu Chăm sóc → click "Hóa đơn #id" mở `/hoa-don`.

---

## 5c. Điều hướng bàn phím (2026-07-09)

Toàn bộ logic nằm ở `src/shared/keyboard/`. Trước đó `ShortcutsPopup` đã liệt kê phím tắt cho người dùng nhưng **chưa có handler nào** — nay đã cài đặt thật.

**`shortcuts.ts` là nguồn sự thật duy nhất.** Mỗi phím tắt khai báo một lần (nhãn hiển thị `keys` + mã phím vật lý `code` + modifier + `description`), rồi ba nơi cùng đọc từ đó: `PageShortcutsProvider` khớp phím qua `matchesShortcut(e, SHORTCUTS.CREATE)`, `ShortcutsPopup` sinh bảng hiển thị, `ActionButton` lấy nhãn. Đổi một phím chỉ sửa một chỗ. `matchesShortcut` so khớp **chính xác** cả modifier — `Alt+N` không nhận khi đang giữ thêm `Ctrl`/`Shift`.

`focusHelpers.ts` gom phần dùng chung của hai hook: `getVisibleElements`, `focusAndSelect`, `useRafFocus`, `useContainerKeydown`.

**Phím tắt toàn cục** — `PageShortcutsProvider` (bọc trong `MainLayout`) gắn một listener duy nhất lên `document`:

| Phím | Hành vi |
|------|---------|
| `Alt+N` | Hành động "thêm mới" của trang hiện tại |
| `Alt+F` / `Ctrl+K` | Focus ô tìm kiếm của bảng |
| `Alt+H` | Về `/dashboard` |
| `Alt+←` | Quay lại trang trước |
| `Ctrl+/` | Mở/đóng popup phím tắt |
| `Ctrl+Shift+L` | Đăng xuất |

`Alt+N` mang tính ngữ cảnh: mỗi trang danh sách tự đăng ký qua `usePageShortcuts({ onCreate })` (ghi vào ref, không re-render, tự gỡ khi unmount). Chính sách giá đăng ký `openCreate` (mở modal); Thùng rác không đăng ký nên `Alt+N` no-op. `Alt+F` tìm ô search bằng attribute `[data-table-search]` trên `TableToolbar` — không phải luồn ref qua `DataTable`.

**Trong form** — `useFormKeyboardNav(ref, { onSubmit, onCancel?, enabled? })` gắn một listener lên container và dò ô nhập theo thứ tự DOM (`input`, `textarea`, `select`, `[data-form-field]`):

- Tự focus ô đầu tiên khi mở form.
- `Enter` sang ô kế tiếp, bôi đen nội dung sẵn có; ở ô cuối thì submit.
- `↑` `↓` đổi focus. `←` `→` di chuyển con trỏ như thường, **chỉ đổi focus khi con trỏ đã ở đầu/cuối ô** (`isCaretAtEdge`) — nhờ vậy vẫn sửa được chữ giữa ô.
- Trong `textarea`, `↑` `↓` cũng theo quy tắc biên (để còn xuống dòng được).
- `↑` `↓` gọi `preventDefault()` nên ô `type="number"` **không** còn tăng/giảm giá trị và `<select>` không đổi option — đánh đổi có chủ ý.
- `Ctrl+S` lưu.
- `Enter` bị bỏ qua trong `textarea` và TinyMCE (ở đó Enter xuống dòng) — rời ô bằng `↓`.

Áp dụng cho 11 `*AddPage` (ref trên `<div>` gốc, `onSubmit` gọi thẳng `submit(false)` vì các trang này không dùng `<form>`) và 11 `*EditModal` (ref trên `<form>`, `onSubmit` gọi `requestSubmit()` để giữ validation `required`; `onCancel` = `onClose`; `enabled: !!item` vì modal render rỗng lúc chưa có bản ghi).

`Esc` **chỉ** đóng modal — cố ý không gắn vào trang thêm mới để tránh lỡ tay mất dữ liệu đang nhập.

`SearchableSelect` có `data-form-field` trên trigger nên tham gia chuỗi điều hướng; `Enter` mở panel, `Esc` đóng, chọn xong trả focus về trigger. (Chưa hỗ trợ ↑↓ chọn option trong danh sách.)

⚠️ **Prop `onSearchChange` — tìm kiếm phía server (2026-07-22)**: các hook lookup nạp sẵn một trang 500 dòng, nhưng `orders` và `contacts` trong dữ liệu thật đều **10.000 bản ghi** → bản ghi cần tìm gần như chắc chắn nằm ngoài trang đó, mà `SearchableSelect` chỉ lọc trong số option **đã nạp** ⇒ gõ gì cũng "Không tìm thấy". Truyền `onSearchChange` để bật chế độ server: component debounce **350ms** rồi báo từ khóa ra ngoài, nơi dùng gọi API (`useOrderSearch` → `GET /api/orders?q=&size=20`), và component **bỏ lọc cục bộ** (lọc lại sẽ cắt mất kết quả server). Kèm `loading` để hiện "Đang tìm…" và **bắt buộc** `fallbackLabel` vì bản ghi đang chọn thường không nằm trong trang kết quả. ⚠️ Backend chỉ tìm theo **mã** (`likeClause(q, "code")`) — đặt `searchPlaceholder` nói rõ điều đó.

⚠️ **Prop `fallbackLabel` — ô khóa ngoại đừng hiện như "chưa chọn" (2026-07-22)**: `SearchableSelect` vốn lấy nhãn bằng `options.find(o => o.value === value)?.label`, không thấy thì rơi về placeholder `— Không chọn —`. Mà mọi hook lookup của form (`useCampaignList`, `useActiveUsers`, `useCustomerList`…) gọi list API — **bị lọc `owner_id` cho nhân viên, lọc `dataAccessFromYear`, và giới hạn 500 dòng**. Nên bản ghi trỏ tới chiến dịch/người phụ trách ngoài phạm vi đó hiển thị **y hệt như trống**, người dùng tưởng dữ liệu bị mất (dữ liệu vẫn nguyên trong DB). Các `*EditModal` nay truyền tên khóa ngoại BE đã trả sẵn (`item.campaignName`, `stageName`, `customerName`, `contactName`, `assignedUserName`, `ownerName`) vào `fallbackLabel`; `LeadEditModal` dùng `<select>` gốc nên chèn thêm một `<option>` cho giá trị ngoài danh sách. **Còn thiếu**: Result chưa có `pricePolicyName`/`unitName`/`categoryName` nên vài ô vẫn có thể trống — muốn vá phải bổ sung nguồn tên ở `INameResolver` (BE).

**Trong popup xác nhận** — `useDialogKeyboardNav(ref, { onCancel, autoFocus })`: 4 mũi tên trần đổi qua lại giữa các nút footer (đánh dấu `data-dialog-button`), đi vòng tròn. Hook **chỉ bắt phím khi focus đang ở một nút** — đang gõ trong `textarea`/`select` thì mũi tên vẫn thuộc về ô đó. `Enter` do trình duyệt tự kích hoạt nút đang focus; `Esc` gọi `onCancel`.

`ConfirmModal` tự focus nút **Hủy** khi `confirmDanger` (lỡ tay Enter không xóa mất bản ghi), ngược lại focus nút xác nhận. `ReasonModal`/`HandoverModal`/`ExportModal`/`TransferWorkModal` dùng `autoFocus: 'none'` vì có ô nhập cần điền trước.

**Popup xác nhận Thêm / Sửa** — `shared/confirm/ConfirmContext.tsx` + `useConfirm()`. Provider bọc app trong `app/App.tsx` (cạnh `AlertProvider`), **tái dùng nguyên `ConfirmModal`**; `confirm(opts)` trả `Promise<boolean>` nên luồng submit chờ được câu trả lời:

```ts
const { confirmSave } = useConfirm();
if (!(await confirmSave('tiềm năng'))) return;
```

Context cung cấp `confirmCreate(noun)` / `confirmSave(noun)` cho hai luồng phổ biến, và `confirm(opts)` thô cho trường hợp cần `confirmDanger` hoặc nhãn nút riêng (popup xóa).

Áp cho 11 `*AddPage` (chèn **sau** validate, trước `mutate`) và 11 `*EditModal` (sau `e.preventDefault()`). Popup luôn hiện, kể cả khi bấm chuột. Xóa vẫn dùng `ConfirmModal` trực tiếp như cũ.

`useDialogKeyboardNav` bỏ qua `Enter` có `e.repeat`: popup thường mở ra do chính một phím Enter, nếu người dùng **giữ** Enter thì keydown lặp lại sẽ bấm luôn nút vừa focus — đúng cái popup sinh ra để ngăn.

**Component nút**: `shared/components/ActionButton.tsx` (variant `primary|secondary|outline|info|danger|dangerSolid`, props `icon`, `shortcut`, `type`, `dialogButton`) + `shared/components/Kbd.tsx`. Đây là **nguồn sự thật duy nhất** cho mọi nút có nhãn: header 13 trang danh sách, `FormPageHeader`, footer 6 popup dùng chung, footer 11 `*EditModal`.

Ba component gói sẵn các tổ hợp lặp lại: `CreateButton.tsx` (nút "Thêm" + `Alt N`, dùng ở 12 trang danh sách), `ModalFooter` và `DialogFooter` (cùng file `ModalFooter.tsx`) — lần lượt cho footer modal có `<form>` (Hủy/Esc + Lưu/Ctrl+S, `type="submit"`) và footer popup xác nhận (nút mang `data-dialog-button`). `AlertModal` chỉ một nút nên dùng `ActionButton` trực tiếp. Phím tắt render thành **khối liền sát mép phải nút** (`items-stretch` + `overflow-hidden`, nền `SHORTCUT_CLS` đậm hơn nền nút) — không phải chip `<kbd>` xám nằm giữa. `Kbd.tsx` nay chỉ còn dùng ở `ShortcutsPopup`.

Kích thước nút: `px-2.5 py-1`, chữ `text-table` (13px), icon 13, `gap-1`; khe giữa các nút `gap-1.5`. `btnBase` trong `formStyles.ts` đã bị **xóa** (không còn ai dùng).

Nhãn rút về một chữ: **Nhập / Xuất / Thêm / Bàn giao / Xóa**, thứ tự thống nhất mọi trang. Nút popup hiện `Hủy │ Esc` và `Xác nhận │ Enter` — hai phím này không cần handler mới (`Esc` đã có trong hook, `Enter` là hành vi mặc định của nút đang focus).

---

## 5d. Chuẩn báo lỗi & xác nhận trên mọi form (2026-07-22)

Trước đợt này, bộ khung bàn phím + popup xác nhận chỉ phủ 11 `*AddPage` và 11 `*EditModal`.
Các form còn lại (đăng nhập, kích hoạt, đăng ký NV, thiết lập tài khoản, Phân quyền, Chính sách giá,
Pipeline, gửi email, thành viên chiến dịch, đợt thanh toán) chưa có Enter nhảy ô, chưa có Esc,
và ghi dữ liệu không hỏi lại. Nay áp một quy ước duy nhất cho **toàn dự án**:

| Tình huống | Cách xử lý |
|-----------|-----------|
| Thiếu / sai dữ liệu nhập | **Chữ đỏ ngay dưới ô đó** + viền đỏ (`FieldRow`/`FormField`/`FieldError` prop `error`). **Không** popup |
| Sắp **thêm / sửa / xóa** bản ghi | **Popup xác nhận** — `useConfirm()`: `confirmCreate` / `confirmSave` / `confirmDelete` / `confirm` thô |
| Form tra cứu, lọc, tìm kiếm | Không popup, không confirm. `Esc` đóng panel / xóa ô tìm kiếm |
| Đăng nhập, kích hoạt | Chỉ lỗi đỏ dưới ô. **Không** popup xác nhận (không phải thao tác ghi bản ghi) |

**Thứ tự bắt buộc trong hàm submit**: `e.preventDefault()` → `validate()` + `setErrors` → return nếu có lỗi
→ `await confirm*()` → `mutate()`. Popup xác nhận **không bao giờ** mở khi dữ liệu còn sai.

- **Khuôn validate**: state `errors: Record<string,string>`, tính bằng `collectErrors` + các hàm trong
  `shared/utils/validators.ts`; hàm `set(patch)` xóa lỗi của đúng những field vừa gõ. Mọi `<form>` để
  `noValidate` để bong bóng mặc định của trình duyệt không tranh chỗ với thông báo tiếng Việt.
- **`confirmDelete(noun)`** (mới, `shared/confirm/ConfirmContext.tsx`) — popup xóa nền đỏ, focus sẵn nút Hủy.
- **Gửi email ra ngoài cũng phải xác nhận**: `SendQuotationModal` (gửi báo giá cho khách) và
  `SendEmailModal` (gửi hàng loạt cho thành viên chiến dịch) — email đã gửi không thu hồi được.
  `ImportWizard` xác nhận kèm số dòng sắp nhập.
- **Tab Phân quyền chuyển sang gom thay đổi**: tick checkbox chỉ đổi bản nháp trong bộ nhớ; hiện thanh
  "Đã thay đổi n quyền" với **Hoàn tác** / **Lưu** (`Ctrl+S`), bấm Lưu mới qua popup rồi gọi API.
  `PermissionsTab` báo `onDirtyChange` lên `PhanQuyenPage` để chặn đổi nhóm/đổi tab làm mất thay đổi.
- **Form tra cứu**: `DataTable` đóng panel lọc/sắp xếp/tô màu/ẩn cột bằng `Esc` (một effect chung cho cả
  4 panel); ô tìm kiếm của `TableToolbar` — `Esc` xóa nội dung và rời ô, `Enter` cố ý không làm gì.
- **Bug đã vá**: `ContactAddPage` có khối kiểm tra email lọt **vào trong** `if (!form.ten.trim())` nên
  email/SĐT sai chỉ được kiểm khi bỏ trống tên; `CampaignMembersTable` xóa khách hàng khỏi chiến dịch
  không hỏi gì.

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
