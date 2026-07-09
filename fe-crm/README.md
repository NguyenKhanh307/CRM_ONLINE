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

### Triển khai production — Netlify

Frontend deploy lên **Netlify**. Cấu hình nằm trong `fe-crm/netlify.toml`:
- `command = "npm run build"`, `publish = "dist"`, `NODE_VERSION = "22"` (Vite 7 cần Node ≥ 20.19).
- **SPA fallback** `/* → /index.html 200` — bắt buộc vì router dùng `createBrowserRouter` (deep-link như `/co-hoi/pipeline` sẽ 404 nếu thiếu).

Các bước:
1. Netlify → **New site from Git**, chọn repo, **Base directory = `fe-crm`** (tự đọc `netlify.toml`).
2. **Site settings → Environment variables**: đặt `VITE_API_BASE_URL = https://<ten-service>.onrender.com` (URL backend Render, **không** dấu `/` cuối).
3. Deploy → lấy URL `https://<ten-site>.netlify.app`. Điền URL này vào biến `APP_CORS_ALLOWED_ORIGINS` và `APP_FRONTEND_BASE_URL` bên Render, rồi build lại Netlify sau khi có domain backend.

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
│   │   ├── PermissionContext.tsx  # hasRole(), hasPermission(), hasModuleAccess()
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
    │   │   └── sidebar/
    │   │       └── sidebarConfig.ts   # NAV_ITEMS — thêm menu item ở đây
    │   ├── table/
    │   │   ├── DataTable.tsx          # Component bảng dùng chung
    │   │   └── RowContextMenu.tsx     # Menu chuột phải của dòng bảng (portal)
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
- `POST /api/auth/login` → JWT token lưu localStorage. **Email phải là @gmail.com** (validate cả FE lẫn BE)
- `POST /api/auth/register-employee` → Admin đăng ký nhân viên, BE gửi email kích hoạt (route `/dang-ky-nhan-vien`)
- `POST /api/auth/activate` → Nhân viên kích hoạt tài khoản qua link email (route public `/activate?token=...`)
- Token tự động đính kèm vào mọi request qua axios interceptor
- 401 response → tự động redirect về `/login`

### Data modules (list view + edit/delete)

Tất cả 8 module đều có: danh sách, nút Sửa (mở modal), nút Xóa (ConfirmModal), chọn nhiều hàng + Xóa hàng loạt. 5 module nghiệp vụ chính có thêm nút **Bàn giao** (chọn hàng → `POST /api/{module}/handover-bulk`).

| Module | Route | Endpoint GET | Endpoint PUT | Endpoint DELETE | Handover |
|--------|-------|-------------|-------------|----------------|---------|
| Tiềm năng | `/tiem-nang` | `GET /api/leads` | `PUT /api/leads/{id}` | `DELETE /api/leads/{id}` | ✓ |
| Liên hệ | `/lien-he` | `GET /api/contacts` | `PUT /api/contacts/{id}` | `DELETE /api/contacts/{id}` | — |
| Khách hàng | `/khach-hang` | `GET /api/customers` | `PUT /api/customers/{id}` | `DELETE /api/customers/{id}` | ✓ |
| Cơ hội | `/co-hoi` | `GET /api/opportunities` | `PUT /api/opportunities/{id}` | `DELETE /api/opportunities/{id}` | ✓ |
| Chiến dịch | `/chien-dich` (+ `/:id` chi tiết 3 tab) | `GET /api/campaigns` | `PUT /api/campaigns/{id}` | `DELETE /api/campaigns/{id}` | ✓ |
| Báo giá | `/bao-gia` | `GET /api/quotations` | `PUT /api/quotations/{id}` | `DELETE /api/quotations/{id}` | ✓ |
| Đơn hàng | `/don-hang` | `GET /api/orders` | `PUT /api/orders/{id}` | `DELETE /api/orders/{id}` | ✓ |
| Hóa đơn | `/hoa-don` | `GET /api/invoices` | `PUT /api/invoices/{id}` | `DELETE /api/invoices/{id}` | ✓ |
| Hoạt động | `/hoat-dong` | `GET /api/activities` | `PUT /api/activities/{id}` | `DELETE /api/activities/{id}` | — |
| Sản phẩm | `/san-pham` | `GET /api/products` | `PUT /api/products/{id}` | `DELETE /api/products/{id}` | — |

> Module **Kho hàng** đã được gỡ (phân hệ Kho không còn ở backend).

### Bàn làm việc (Dashboard) — `/dashboard` (MỚI 2026-07-04)

Trang tổng quan phân theo vai trò (`features/dashboard/`), phong cách AMIS. `DashboardPage` dùng `usePermission()` chọn view ưu tiên **ADMIN → SALES_MANAGER → SALES_STAFF**; có bộ chọn **kỳ** (Tháng này/Quý này/Năm nay) + **đơn vị tiền** (VND/Triệu đồng) cấp trang.

- **ADMIN** (`AdminDashboardView`): KPI tài khoản/vai trò/quyền, donut cơ cấu theo trạng thái/vai trò/đơn vị, cột tài khoản theo tháng, tổng quan bản ghi hệ thống → `GET /api/dashboard/admin`.
- **SALES_MANAGER** (`SalesDashboardView` với `showTeam`): thẻ KPI area-trend Doanh thu/Chi phí/Lợi nhuận, combo cột+đường tài chính, KPI cơ hội, tỷ lệ thắng, phễu chuyển đổi, cơ hội giá trị lớn, thống kê theo nhân viên, việc gấp → `GET /api/dashboard/manager`.
- **SALES_STAFF** (`SalesDashboardView` cá nhân): như trên nhưng phạm vi `owner_id = userId`, bỏ phần theo nhân viên → `GET /api/dashboard/sale`.
- Service/hooks: `services/dashboardService.ts` + `hooks/use{Admin,Manager,Sale}Dashboard.ts` (`useQuery`, `enabled` theo role).
- Component chart dùng chung: `components/` — `DashCard`, `KpiTile`, `DonutChart`, `RevenueCostProfitChart`, `AreaTrend`, `MonthlyBar`, `FunnelChart`, `StackedBarByGroup`, `RankedList`, `UrgentList`, `Selectors` (Period/Unit), `chartTheme` (palette + nhãn trạng thái tiếng Việt). Tiền tệ dùng `shared/utils/number.ts#formatMoney`.

### Tiềm năng — chấm điểm, web tracking & thông báo

- Trang `/tracking-demo` (`features/tracking-demo`): landing page mô phỏng — gọi `POST /api/tracking/visit|score|submit` (public) để tạo lead ẩn danh & cộng điểm `score`.
- Trang công khai `/bao-gia-phan-hoi/:token` (`features/bao-gia-phan-hoi`): khách xem báo giá (`GET /api/public/quotations/{token}`) + phản hồi Đồng ý/Điều chỉnh/Không đồng ý (`POST /api/public/quotations/{token}/respond`) — link gửi qua email báo giá (kèm PDF). Ngoài MainLayout, không cần đăng nhập.
- **Soạn email báo giá trước khi gửi**: menu chuột phải báo giá `approved` → "Gửi email cho khách" mở `features/bao-gia/components/SendQuotationModal.tsx`. Modal nạp nội dung mặc định qua `useQuotationEmailDraft` (`GET /api/quotations/{id}/email-draft`), cho sửa tiêu đề + nội dung (soạn thảo WYSIWYG `shared/components/RichTextEditor.tsx` — TinyMCE self-host bản GPL, deps `tinymce` + `@tinymce/tinymce-react`), rồi gửi kèm `{ subject, body }` qua `POST /api/quotations/{id}/send`. 3 nút phản hồi + PDF do BE tự chèn.
- Header có **chuông thông báo** (`shared/components/layout/header/NotificationPopup.tsx`) dùng `shared/notifications/{notificationService,useNotifications}.ts` → `GET /api/notifications`, `/unread-count`, `POST /{id}/read`, `/read-all`.

**Bấm thông báo → nhảy tới bản ghi**: `NotificationPopup` map tiền tố `type` → route (`lead→/tiem-nang`, `quotation→/bao-gia`, `ticket→/cham-soc`); bấm sẽ đánh dấu đã đọc rồi `navigate('{route}?focus={targetId}')`. Ba trang danh sách đó đọc `useSearchParams().get('focus')` và truyền vào prop **`focusId`** của `DataTable` — bảng tự nhảy đúng trang phân trang, highlight và cuộn tới dòng.

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
- Đặt tại `src/shared/copilot/` (theo tiền lệ `shared/notifications` — widget cross-cutting có service/hook riêng trong `shared/`, để `MainLayout` không import `features/`): `copilotTypes.ts`, `copilotService.ts` (`POST /api/copilot/ask`, timeout 60s), `useAskCopilot.ts` (`useMutation`, unwrap `.data.data`), `CopilotWidget.tsx`.
- Hỏi tiếng Việt về dữ liệu CRM — số liệu tổng hợp ("Doanh thu quý này so quý trước? Tỷ lệ thắng?") hoặc tình hình khách hàng cụ thể ("Khách ABC đang thế nào?"). BE giới hạn dữ liệu theo quyền của người đăng nhập.
- Cần backend cấu hình `APP_AI_API_KEY` (Gemini). FE không đổi biến môi trường nào.

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

**Shared form components** — `src/shared/components/form/`:

| File | Vai trò |
|------|---------|
| `formStyles.ts` | `inputCls`, `btnBase` dùng chung |
| `FormSection.tsx` | Section có tiêu đề (h2 + border-b) |
| `FieldRow.tsx` | Hàng field label 148px + dấu `*` required, prop `alignTop` cho textarea |
| `FormPageHeader.tsx` | Header form: Hủy / Lưu và thêm / Lưu, prop `saving` |
| `ProductLineItemsTable.tsx` | Bảng hàng hóa controlled cho báo giá/đơn hàng/cơ hội (props `showUnit`/`showTax`) |
| `productLineItem.ts` | Type `LineItemRow`/`ProductOption` + helper `computeTotals`, `toItemPayloads` |
| `DateInput.tsx` | Ô nhập ngày **dd/mm/yyyy** + lịch popup, `value`/`onChange` dùng ISO `yyyy-mm-dd` (thay `<input type="date">`) |
| `DateTimeInput.tsx` | Ngày dd/mm/yyyy + giờ HH:mm, `value`/`onChange` dùng ISO `yyyy-mm-ddTHH:mm` (thay `<input type="datetime-local">`) |
| `Calendar.tsx` / `useAnchoredPanel.ts` | Lưới lịch + hook panel nổi (portal) dùng bởi DateInput/DateTimeInput |

**Pattern mỗi module**: `types/<m>Types.ts` (thêm `Create*Payload`) → `services/<m>Service.ts` (`create()`) → `hooks/useCreate<X>.ts` → `pages/<X>AddPage.tsx`. State lift lên page (`useState<FormState>` + `set(patch)`); "Lưu" → `navigate` về list, "Lưu và thêm" → reset form. Mã (code) bắt buộc nhập tay.

**Lookup hooks dùng cho dropdown**:
- `features/users/hooks/useActiveUsers.ts` — `GET /api/users?status=active`
- `features/users/hooks/useOrgUnits.ts` — `GET /api/org-units`
- `features/co-hoi/hooks/useOpportunityStages.ts` — `GET /api/opportunity-stages`
- `features/san-pham/hooks/useProductCategories.ts` — `GET /api/product-categories`
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

**Lưu ý**: Import UPDATE/BOTH áp dụng cho các module có khóa duy nhất (lead, product, contact, customer, opportunity, order, quotation). **Hoạt động chỉ hỗ trợ CREATE** (không có khóa duy nhất).

### Xuất file Excel/CSV — 8 module (2026-06-19)

Mỗi trang danh sách (8 module data) có nút **"Xuất file"** (`FiDownload`) cạnh nút "Nhập file". Xuất **hoàn toàn ở frontend** (dùng `xlsx` đã có sẵn) — không cần endpoint backend.

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

### Thao tác dòng — menu chuột phải (2026-07-08)

Cột **"Thao tác"** (các nút icon ghim bên phải bảng) đã được **gỡ khỏi cả 13 trang danh sách**. Thay vào đó:

- **Chuột phải** vào một dòng → menu hiện tại vị trí con trỏ với các mục **dạng chữ** (Chỉnh sửa, Xóa, và các bước workflow theo trạng thái). Mục **Xóa** tô đỏ, tách bằng đường kẻ.
- **Nhấp đúp** dòng → chạy hành động chính: mở modal **Chỉnh sửa**, hoặc điều hướng **trang chi tiết** (Chăm sóc, Chiến dịch, Chính sách giá).
- Page khai báo `rowActions={(row) => RowAction[]}` + `onRowDoubleClick` cho `DataTable`; component `shared/components/table/RowContextMenu.tsx` render qua portal (`position: fixed`) nên không bị khung bảng cắt, tự lật vào trong khi chạm mép màn hình, đóng khi click ngoài / `Escape` / cuộn.

Chi tiết pattern: xem `CODE_GUIDE_FRONTEND.md` mục 8.

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
- **Tra giá theo chính sách (pricebook)**: `ProductLineItemsTable` nhận prop `pricePolicyId` — khi chọn dòng hàng gọi `pricingService.resolve` (`GET /api/pricing/resolve`) lấy đơn giá/CK, fallback về `basePrice`. Truyền từ form Cơ hội (`OpportunityAddPage`/`OpportunityEditModal`).
- **Đợt thanh toán hóa đơn**: `PaymentSchedulesTable` + `useInvoicePayments` (`GET/POST/PUT/DELETE /api/invoices/{id}/payment-schedules`) nhúng trong `InvoiceEditModal` — thêm/sửa/xóa đợt; BE tự suy ra `paymentStatus` (`partially_paid`/`paid`).
- **Nút Hoàn tất đơn hàng**: `DonHangPage` thêm hành động `complete` (khi `processing`).
- **Tự điều hướng sau convert**: convert tiềm năng → `/co-hoi`; báo giá → đơn hàng → `/don-hang`; đơn hàng → hóa đơn → `/hoa-don` (kèm toast).
- **Link chéo**: chi tiết phiếu Chăm sóc → click "Hóa đơn #id" mở `/hoa-don`.

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
