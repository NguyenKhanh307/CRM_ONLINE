import { lazy, Suspense } from 'react';
import { createBrowserRouter, Navigate } from 'react-router-dom';
import { MainLayout } from '@/shared/components/layout/MainLayout';
import { RequireAuth } from '@/core/auth/RequireAuth';
import { RequirePermission } from '@/core/permissions/RequirePermission';

const RegisterEmployeePage    = lazy(() => import('@/features/users/pages/RegisterEmployeePage'));
const AccountSettingsPage     = lazy(() => import('@/features/users/pages/AccountSettingsPage'));
const LoginPage               = lazy(() => import('@/features/auth/pages/LoginPage'));
const ActivatePage            = lazy(() => import('@/features/auth/pages/ActivatePage'));
const DashboardPage   = lazy(() => import('@/features/dashboard/pages/DashboardPage'));
const PhanTichPage    = lazy(() => import('@/features/phan-tich/pages/PhanTichPage'));
const TiemNangPage    = lazy(() => import('@/features/tiem-nang/pages/TiemNangPage'));
const LeadAddPage     = lazy(() => import('@/features/tiem-nang/pages/LeadAddPage'));
const LeadImportPage  = lazy(() => import('@/features/tiem-nang/pages/LeadImportPage'));
const LeadDetailPage  = lazy(() => import('@/features/tiem-nang/pages/LeadDetailPage'));
const LienHePage      = lazy(() => import('@/features/lien-he/pages/LienHePage'));
const ContactAddPage  = lazy(() => import('@/features/lien-he/pages/ContactAddPage'));
const ContactImportPage = lazy(() => import('@/features/lien-he/pages/ContactImportPage'));
const ContactDetailPage = lazy(() => import('@/features/lien-he/pages/ContactDetailPage'));
const KhachHangPage   = lazy(() => import('@/features/khach-hang/pages/KhachHangPage'));
const CustomerAddPage = lazy(() => import('@/features/khach-hang/pages/CustomerAddPage'));
const CustomerImportPage = lazy(() => import('@/features/khach-hang/pages/CustomerImportPage'));
const CustomerDetailPage = lazy(() => import('@/features/khach-hang/pages/CustomerDetailPage'));
const CoHoiPage       = lazy(() => import('@/features/co-hoi/pages/CoHoiPage'));
const OpportunityAddPage = lazy(() => import('@/features/co-hoi/pages/OpportunityAddPage'));
const OpportunityImportPage = lazy(() => import('@/features/co-hoi/pages/OpportunityImportPage'));
const OpportunityPipelinePage = lazy(() => import('@/features/co-hoi/pages/OpportunityPipelinePage'));
const OpportunityDetailPage = lazy(() => import('@/features/co-hoi/pages/OpportunityDetailPage'));
const OpportunityBoardPage = lazy(() => import('@/features/co-hoi/pages/OpportunityBoardPage'));
const BaoGiaPage      = lazy(() => import('@/features/bao-gia/pages/BaoGiaPage'));
const QuoteAddPage    = lazy(() => import('@/features/bao-gia/pages/QuoteAddPage'));
const QuotationImportPage = lazy(() => import('@/features/bao-gia/pages/QuotationImportPage'));
const QuotationDetailPage = lazy(() => import('@/features/bao-gia/pages/QuotationDetailPage'));
const HoaDonPage      = lazy(() => import('@/features/hoa-don/pages/HoaDonPage'));
const InvoiceAddPage  = lazy(() => import('@/features/hoa-don/pages/InvoiceAddPage'));
const InvoiceImportPage = lazy(() => import('@/features/hoa-don/pages/InvoiceImportPage'));
const InvoiceDetailPage = lazy(() => import('@/features/hoa-don/pages/InvoiceDetailPage'));
const DonHangPage     = lazy(() => import('@/features/don-hang/pages/DonHangPage'));
const OrderAddPage    = lazy(() => import('@/features/don-hang/pages/OrderAddPage'));
const OrderImportPage = lazy(() => import('@/features/don-hang/pages/OrderImportPage'));
const OrderDetailPage = lazy(() => import('@/features/don-hang/pages/OrderDetailPage'));
const ChienDichPage   = lazy(() => import('@/features/chien-dich/pages/ChienDichPage'));
const CampaignAddPage = lazy(() => import('@/features/chien-dich/pages/CampaignAddPage'));
const CampaignDetailPage = lazy(() => import('@/features/chien-dich/pages/CampaignDetailPage'));
const CampaignImportPage = lazy(() => import('@/features/chien-dich/pages/CampaignImportPage'));
const HoatDongPage    = lazy(() => import('@/features/hoat-dong/pages/HoatDongPage'));
const ActivityAddPage = lazy(() => import('@/features/hoat-dong/pages/ActivityAddPage'));
const ActivityImportPage = lazy(() => import('@/features/hoat-dong/pages/ActivityImportPage'));
const SanPhamPage     = lazy(() => import('@/features/san-pham/pages/SanPhamPage'));
const ProductAddPage  = lazy(() => import('@/features/san-pham/pages/ProductAddPage'));
const ProductImportPage = lazy(() => import('@/features/san-pham/pages/ProductImportPage'));
const ProductCategoryPage = lazy(() => import('@/features/san-pham/pages/ProductCategoryPage'));
const PhanQuyenPage   = lazy(() => import('@/features/phan-quyen/pages/PhanQuyenPage'));
const ThungRacPage    = lazy(() => import('@/features/thung-rac/pages/ThungRacPage'));
const NhatKyHeThongPage = lazy(() => import('@/features/nhat-ky-he-thong/pages/NhatKyHeThongPage'));
const ChamSocPage     = lazy(() => import('@/features/cham-soc/pages/ChamSocPage'));
const TicketAddPage   = lazy(() => import('@/features/cham-soc/pages/TicketAddPage'));
const TicketImportPage = lazy(() => import('@/features/cham-soc/pages/TicketImportPage'));
const TicketDetailPage = lazy(() => import('@/features/cham-soc/pages/TicketDetailPage'));
const ChinhSachGiaPage = lazy(() => import('@/features/chinh-sach-gia/pages/ChinhSachGiaPage'));
const ChinhSachGiaDetailPage = lazy(() => import('@/features/chinh-sach-gia/pages/ChinhSachGiaDetailPage'));
const PricePolicyImportPage = lazy(() => import('@/features/chinh-sach-gia/pages/PricePolicyImportPage'));
const TrackingDemoPage = lazy(() => import('@/features/tracking-demo/pages/TrackingDemoPage'));
const QuotationResponsePage = lazy(() => import('@/features/bao-gia-phan-hoi/pages/QuotationResponsePage'));
const SupportPublicPage = lazy(() => import('@/features/support-public/pages/SupportPublicPage'));

const fallback = <div className="p-6 text-gray-400">Đang tải...</div>;

export const router = createBrowserRouter([
    {
        element: <RequireAuth><MainLayout /></RequireAuth>,
        children: [
            { path: '/',                     element: <Navigate to="/dashboard" replace /> },
            { path: '/dang-ky-nhan-vien',    element: <Suspense fallback={fallback}><RegisterEmployeePage /></Suspense> },
            { path: '/tai-khoan',            element: <Suspense fallback={fallback}><AccountSettingsPage /></Suspense> },
            { path: '/dashboard',            element: <Suspense fallback={fallback}><DashboardPage /></Suspense> },
            { path: '/phan-tich',            element: <Suspense fallback={fallback}><PhanTichPage /></Suspense> },
            { path: '/tiem-nang',              element: <Suspense fallback={fallback}><TiemNangPage /></Suspense> },
            { path: '/tiem-nang/them-moi',    element: <RequirePermission module="lead"><Suspense fallback={fallback}><LeadAddPage /></Suspense></RequirePermission> },
            { path: '/tiem-nang/nhap-file',   element: <RequirePermission module="lead"><Suspense fallback={fallback}><LeadImportPage /></Suspense></RequirePermission> },
            { path: '/tiem-nang/:id',         element: <Suspense fallback={fallback}><LeadDetailPage /></Suspense> },
            { path: '/lien-he',               element: <Suspense fallback={fallback}><LienHePage /></Suspense> },
            { path: '/lien-he/them-moi',      element: <RequirePermission module="contact"><Suspense fallback={fallback}><ContactAddPage /></Suspense></RequirePermission> },
            { path: '/lien-he/nhap-file',     element: <RequirePermission module="contact"><Suspense fallback={fallback}><ContactImportPage /></Suspense></RequirePermission> },
            { path: '/lien-he/:id',           element: <Suspense fallback={fallback}><ContactDetailPage /></Suspense> },
            { path: '/khach-hang',            element: <Suspense fallback={fallback}><KhachHangPage /></Suspense> },
            { path: '/khach-hang/them-moi',   element: <RequirePermission module="customer"><Suspense fallback={fallback}><CustomerAddPage /></Suspense></RequirePermission> },
            { path: '/khach-hang/nhap-file',  element: <RequirePermission module="customer"><Suspense fallback={fallback}><CustomerImportPage /></Suspense></RequirePermission> },
            { path: '/khach-hang/:id',        element: <Suspense fallback={fallback}><CustomerDetailPage /></Suspense> },
            { path: '/co-hoi',                element: <Suspense fallback={fallback}><CoHoiPage /></Suspense> },
            { path: '/co-hoi/them-moi',       element: <RequirePermission module="opportunity"><Suspense fallback={fallback}><OpportunityAddPage /></Suspense></RequirePermission> },
            { path: '/co-hoi/nhap-file',      element: <RequirePermission module="opportunity"><Suspense fallback={fallback}><OpportunityImportPage /></Suspense></RequirePermission> },
            { path: '/co-hoi/pipeline',       element: <Suspense fallback={fallback}><OpportunityPipelinePage /></Suspense> },
            { path: '/co-hoi/kanban',         element: <Suspense fallback={fallback}><OpportunityBoardPage /></Suspense> },
            { path: '/co-hoi/:id',            element: <Suspense fallback={fallback}><OpportunityDetailPage /></Suspense> },
            { path: '/bao-gia',               element: <Suspense fallback={fallback}><BaoGiaPage /></Suspense> },
            { path: '/bao-gia/them-moi',      element: <RequirePermission module="quotation"><Suspense fallback={fallback}><QuoteAddPage /></Suspense></RequirePermission> },
            { path: '/bao-gia/nhap-file',     element: <RequirePermission module="quotation"><Suspense fallback={fallback}><QuotationImportPage /></Suspense></RequirePermission> },
            { path: '/bao-gia/:id',           element: <Suspense fallback={fallback}><QuotationDetailPage /></Suspense> },
            { path: '/hoa-don',               element: <Suspense fallback={fallback}><HoaDonPage /></Suspense> },
            { path: '/hoa-don/them-moi',      element: <RequirePermission module="invoice"><Suspense fallback={fallback}><InvoiceAddPage /></Suspense></RequirePermission> },
            { path: '/hoa-don/nhap-file',     element: <RequirePermission module="invoice"><Suspense fallback={fallback}><InvoiceImportPage /></Suspense></RequirePermission> },
            { path: '/hoa-don/:id',           element: <Suspense fallback={fallback}><InvoiceDetailPage /></Suspense> },
            { path: '/don-hang',              element: <Suspense fallback={fallback}><DonHangPage /></Suspense> },
            { path: '/don-hang/them-moi',     element: <RequirePermission module="order"><Suspense fallback={fallback}><OrderAddPage /></Suspense></RequirePermission> },
            { path: '/don-hang/nhap-file',    element: <RequirePermission module="order"><Suspense fallback={fallback}><OrderImportPage /></Suspense></RequirePermission> },
            { path: '/don-hang/:id',          element: <Suspense fallback={fallback}><OrderDetailPage /></Suspense> },
            { path: '/chien-dich',            element: <Suspense fallback={fallback}><ChienDichPage /></Suspense> },
            { path: '/chien-dich/them-moi',   element: <RequirePermission module="campaign"><Suspense fallback={fallback}><CampaignAddPage /></Suspense></RequirePermission> },
            { path: '/chien-dich/nhap-file',  element: <RequirePermission module="campaign"><Suspense fallback={fallback}><CampaignImportPage /></Suspense></RequirePermission> },
            { path: '/chien-dich/:id',        element: <Suspense fallback={fallback}><CampaignDetailPage /></Suspense> },
            { path: '/hoat-dong',             element: <Suspense fallback={fallback}><HoatDongPage /></Suspense> },
            { path: '/hoat-dong/them-moi',    element: <RequirePermission module="activity"><Suspense fallback={fallback}><ActivityAddPage /></Suspense></RequirePermission> },
            { path: '/hoat-dong/nhap-file',   element: <RequirePermission module="activity"><Suspense fallback={fallback}><ActivityImportPage /></Suspense></RequirePermission> },
            { path: '/san-pham',              element: <Suspense fallback={fallback}><SanPhamPage /></Suspense> },
            { path: '/san-pham/danh-muc',     element: <Suspense fallback={fallback}><ProductCategoryPage /></Suspense> },
            { path: '/san-pham/them-moi',     element: <RequirePermission module="product"><Suspense fallback={fallback}><ProductAddPage /></Suspense></RequirePermission> },
            { path: '/san-pham/nhap-file',    element: <RequirePermission module="product"><Suspense fallback={fallback}><ProductImportPage /></Suspense></RequirePermission> },
            { path: '/cham-soc',              element: <Suspense fallback={fallback}><ChamSocPage /></Suspense> },
            { path: '/cham-soc/them-moi',     element: <RequirePermission module="ticket"><Suspense fallback={fallback}><TicketAddPage /></Suspense></RequirePermission> },
            { path: '/cham-soc/nhap-file',    element: <RequirePermission module="ticket"><Suspense fallback={fallback}><TicketImportPage /></Suspense></RequirePermission> },
            { path: '/cham-soc/:id',          element: <Suspense fallback={fallback}><TicketDetailPage /></Suspense> },
            { path: '/chinh-sach-gia',        element: <Suspense fallback={fallback}><ChinhSachGiaPage /></Suspense> },
            { path: '/chinh-sach-gia/nhap-file', element: <RequirePermission module="pricing"><Suspense fallback={fallback}><PricePolicyImportPage /></Suspense></RequirePermission> },
            { path: '/chinh-sach-gia/:id',   element: <Suspense fallback={fallback}><ChinhSachGiaDetailPage /></Suspense> },
            { path: '/phan-quyen',           element: <Suspense fallback={fallback}><PhanQuyenPage /></Suspense> },
            { path: '/thung-rac',            element: <Suspense fallback={fallback}><ThungRacPage /></Suspense> },
            { path: '/nhat-ky-he-thong',     element: <Suspense fallback={fallback}><NhatKyHeThongPage /></Suspense> },
        ],
    },
    {
        path: '/tracking-demo',
        element: (
            <Suspense fallback={<div className="min-h-screen bg-bg-main" />}>
                <TrackingDemoPage />
            </Suspense>
        ),
    },
    {
        path: '/login',
        element: (
            <Suspense fallback={<div className="min-h-screen bg-bg-main" />}>
                <LoginPage />
            </Suspense>
        ),
    },
    {
        path: '/activate',
        element: (
            <Suspense fallback={<div className="min-h-screen bg-blue-200" />}>
                <ActivatePage />
            </Suspense>
        ),
    },
    {
        path: '/bao-gia-phan-hoi/:token',
        element: (
            <Suspense fallback={<div className="min-h-screen bg-bg-main" />}>
                <QuotationResponsePage />
            </Suspense>
        ),
    },
    {
        path: '/support-page/:code',
        element: (
            <Suspense fallback={<div className="min-h-screen bg-bg-main" />}>
                <SupportPublicPage />
            </Suspense>
        ),
    },
    {
        path: '/forbidden',
        element: <div className="p-6">403 Forbidden</div>,
    },
]);
