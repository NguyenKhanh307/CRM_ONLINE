import { lazy, Suspense } from 'react';
import { createBrowserRouter, Navigate } from 'react-router-dom';
import { MainLayout } from '@/shared/components/layout/MainLayout';
import { RequireAuth } from '@/core/auth/RequireAuth';

const RegisterEmployeePage    = lazy(() => import('@/features/users/pages/RegisterEmployeePage'));
const LoginPage               = lazy(() => import('@/features/auth/pages/LoginPage'));
const ActivatePage            = lazy(() => import('@/features/auth/pages/ActivatePage'));
const DashboardPage   = lazy(() => import('@/features/dashboard/pages/DashboardPage'));
const TiemNangPage    = lazy(() => import('@/features/tiem-nang/pages/TiemNangPage'));
const LeadAddPage     = lazy(() => import('@/features/tiem-nang/pages/LeadAddPage'));
const LeadImportPage  = lazy(() => import('@/features/tiem-nang/pages/LeadImportPage'));
const LienHePage      = lazy(() => import('@/features/lien-he/pages/LienHePage'));
const ContactAddPage  = lazy(() => import('@/features/lien-he/pages/ContactAddPage'));
const ContactImportPage = lazy(() => import('@/features/lien-he/pages/ContactImportPage'));
const KhachHangPage   = lazy(() => import('@/features/khach-hang/pages/KhachHangPage'));
const CustomerAddPage = lazy(() => import('@/features/khach-hang/pages/CustomerAddPage'));
const CustomerImportPage = lazy(() => import('@/features/khach-hang/pages/CustomerImportPage'));
const CoHoiPage       = lazy(() => import('@/features/co-hoi/pages/CoHoiPage'));
const OpportunityAddPage = lazy(() => import('@/features/co-hoi/pages/OpportunityAddPage'));
const OpportunityImportPage = lazy(() => import('@/features/co-hoi/pages/OpportunityImportPage'));
const OpportunityPipelinePage = lazy(() => import('@/features/co-hoi/pages/OpportunityPipelinePage'));
const BaoGiaPage      = lazy(() => import('@/features/bao-gia/pages/BaoGiaPage'));
const QuoteAddPage    = lazy(() => import('@/features/bao-gia/pages/QuoteAddPage'));
const QuotationImportPage = lazy(() => import('@/features/bao-gia/pages/QuotationImportPage'));
const HoaDonPage      = lazy(() => import('@/features/hoa-don/pages/HoaDonPage'));
const InvoiceAddPage  = lazy(() => import('@/features/hoa-don/pages/InvoiceAddPage'));
const InvoiceImportPage = lazy(() => import('@/features/hoa-don/pages/InvoiceImportPage'));
const DonHangPage     = lazy(() => import('@/features/don-hang/pages/DonHangPage'));
const OrderAddPage    = lazy(() => import('@/features/don-hang/pages/OrderAddPage'));
const OrderImportPage = lazy(() => import('@/features/don-hang/pages/OrderImportPage'));
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
const PhanQuyenPage   = lazy(() => import('@/features/phan-quyen/pages/PhanQuyenPage'));
const ThungRacPage    = lazy(() => import('@/features/thung-rac/pages/ThungRacPage'));
const ChamSocPage     = lazy(() => import('@/features/cham-soc/pages/ChamSocPage'));
const TicketAddPage   = lazy(() => import('@/features/cham-soc/pages/TicketAddPage'));
const TicketDetailPage = lazy(() => import('@/features/cham-soc/pages/TicketDetailPage'));
const ChinhSachGiaPage = lazy(() => import('@/features/chinh-sach-gia/pages/ChinhSachGiaPage'));
const ChinhSachGiaDetailPage = lazy(() => import('@/features/chinh-sach-gia/pages/ChinhSachGiaDetailPage'));
const TrackingDemoPage = lazy(() => import('@/features/tracking-demo/pages/TrackingDemoPage'));
const QuotationResponsePage = lazy(() => import('@/features/bao-gia-phan-hoi/pages/QuotationResponsePage'));

const fallback = <div className="p-6 text-gray-400">Đang tải...</div>;

export const router = createBrowserRouter([
    {
        element: <RequireAuth><MainLayout /></RequireAuth>,
        children: [
            { path: '/',                     element: <Navigate to="/dashboard" replace /> },
            { path: '/dang-ky-nhan-vien',    element: <Suspense fallback={fallback}><RegisterEmployeePage /></Suspense> },
            { path: '/dashboard',            element: <Suspense fallback={fallback}><DashboardPage /></Suspense> },
            { path: '/tiem-nang',              element: <Suspense fallback={fallback}><TiemNangPage /></Suspense> },
            { path: '/tiem-nang/them-moi',    element: <Suspense fallback={fallback}><LeadAddPage /></Suspense> },
            { path: '/tiem-nang/nhap-file',   element: <Suspense fallback={fallback}><LeadImportPage /></Suspense> },
            { path: '/lien-he',               element: <Suspense fallback={fallback}><LienHePage /></Suspense> },
            { path: '/lien-he/them-moi',      element: <Suspense fallback={fallback}><ContactAddPage /></Suspense> },
            { path: '/lien-he/nhap-file',     element: <Suspense fallback={fallback}><ContactImportPage /></Suspense> },
            { path: '/khach-hang',            element: <Suspense fallback={fallback}><KhachHangPage /></Suspense> },
            { path: '/khach-hang/them-moi',   element: <Suspense fallback={fallback}><CustomerAddPage /></Suspense> },
            { path: '/khach-hang/nhap-file',  element: <Suspense fallback={fallback}><CustomerImportPage /></Suspense> },
            { path: '/co-hoi',                element: <Suspense fallback={fallback}><CoHoiPage /></Suspense> },
            { path: '/co-hoi/them-moi',       element: <Suspense fallback={fallback}><OpportunityAddPage /></Suspense> },
            { path: '/co-hoi/nhap-file',      element: <Suspense fallback={fallback}><OpportunityImportPage /></Suspense> },
            { path: '/co-hoi/pipeline',       element: <Suspense fallback={fallback}><OpportunityPipelinePage /></Suspense> },
            { path: '/bao-gia',               element: <Suspense fallback={fallback}><BaoGiaPage /></Suspense> },
            { path: '/bao-gia/them-moi',      element: <Suspense fallback={fallback}><QuoteAddPage /></Suspense> },
            { path: '/bao-gia/nhap-file',     element: <Suspense fallback={fallback}><QuotationImportPage /></Suspense> },
            { path: '/hoa-don',               element: <Suspense fallback={fallback}><HoaDonPage /></Suspense> },
            { path: '/hoa-don/them-moi',      element: <Suspense fallback={fallback}><InvoiceAddPage /></Suspense> },
            { path: '/hoa-don/nhap-file',     element: <Suspense fallback={fallback}><InvoiceImportPage /></Suspense> },
            { path: '/don-hang',              element: <Suspense fallback={fallback}><DonHangPage /></Suspense> },
            { path: '/don-hang/them-moi',     element: <Suspense fallback={fallback}><OrderAddPage /></Suspense> },
            { path: '/don-hang/nhap-file',    element: <Suspense fallback={fallback}><OrderImportPage /></Suspense> },
            { path: '/chien-dich',            element: <Suspense fallback={fallback}><ChienDichPage /></Suspense> },
            { path: '/chien-dich/them-moi',   element: <Suspense fallback={fallback}><CampaignAddPage /></Suspense> },
            { path: '/chien-dich/nhap-file',  element: <Suspense fallback={fallback}><CampaignImportPage /></Suspense> },
            { path: '/chien-dich/:id',        element: <Suspense fallback={fallback}><CampaignDetailPage /></Suspense> },
            { path: '/hoat-dong',             element: <Suspense fallback={fallback}><HoatDongPage /></Suspense> },
            { path: '/hoat-dong/them-moi',    element: <Suspense fallback={fallback}><ActivityAddPage /></Suspense> },
            { path: '/hoat-dong/nhap-file',   element: <Suspense fallback={fallback}><ActivityImportPage /></Suspense> },
            { path: '/san-pham',              element: <Suspense fallback={fallback}><SanPhamPage /></Suspense> },
            { path: '/san-pham/them-moi',     element: <Suspense fallback={fallback}><ProductAddPage /></Suspense> },
            { path: '/san-pham/nhap-file',    element: <Suspense fallback={fallback}><ProductImportPage /></Suspense> },
            { path: '/cham-soc',              element: <Suspense fallback={fallback}><ChamSocPage /></Suspense> },
            { path: '/cham-soc/them-moi',     element: <Suspense fallback={fallback}><TicketAddPage /></Suspense> },
            { path: '/cham-soc/:id',          element: <Suspense fallback={fallback}><TicketDetailPage /></Suspense> },
            { path: '/chinh-sach-gia',        element: <Suspense fallback={fallback}><ChinhSachGiaPage /></Suspense> },
            { path: '/chinh-sach-gia/:id',   element: <Suspense fallback={fallback}><ChinhSachGiaDetailPage /></Suspense> },
            { path: '/phan-quyen',           element: <Suspense fallback={fallback}><PhanQuyenPage /></Suspense> },
            { path: '/thung-rac',            element: <Suspense fallback={fallback}><ThungRacPage /></Suspense> },
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
        path: '/forbidden',
        element: <div className="p-6">403 Forbidden</div>,
    },
]);
