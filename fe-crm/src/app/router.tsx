import { lazy, Suspense } from 'react';
import { createBrowserRouter, Navigate } from 'react-router-dom';
import { MainLayout } from '@/shared/components/layout/MainLayout';

const UserListPage            = lazy(() => import('@/features/users/pages/UserListPage'));
const RegisterEmployeePage    = lazy(() => import('@/features/users/pages/RegisterEmployeePage'));
const LoginPage               = lazy(() => import('@/features/auth/pages/LoginPage'));
const ActivatePage            = lazy(() => import('@/features/auth/pages/ActivatePage'));
const DashboardPage   = lazy(() => import('@/features/dashboard/pages/DashboardPage'));
const TiemNangPage    = lazy(() => import('@/features/tiem-nang/pages/TiemNangPage'));
const LeadAddPage     = lazy(() => import('@/features/tiem-nang/pages/LeadAddPage'));
const LienHePage      = lazy(() => import('@/features/lien-he/pages/LienHePage'));
const KhachHangPage   = lazy(() => import('@/features/khach-hang/pages/KhachHangPage'));
const CoHoiPage       = lazy(() => import('@/features/co-hoi/pages/CoHoiPage'));
const BaoGiaPage      = lazy(() => import('@/features/bao-gia/pages/BaoGiaPage'));
const QuoteAddPage    = lazy(() => import('@/features/bao-gia/pages/QuoteAddPage'));
const DonHangPage     = lazy(() => import('@/features/don-hang/pages/DonHangPage'));
const HoatDongPage    = lazy(() => import('@/features/hoat-dong/pages/HoatDongPage'));
const SanPhamPage     = lazy(() => import('@/features/san-pham/pages/SanPhamPage'));
const KhoHangPage     = lazy(() => import('@/features/kho-hang/pages/KhoHangPage'));
const PhanQuyenPage   = lazy(() => import('@/features/phan-quyen/pages/PhanQuyenPage'));
const ThungRacPage    = lazy(() => import('@/features/thung-rac/pages/ThungRacPage'));

const fallback = <div className="p-6 text-gray-400">Đang tải...</div>;

export const router = createBrowserRouter([
    {
        element: <MainLayout />,
        children: [
            { path: '/',                     element: <Navigate to="/dashboard" replace /> },
            { path: '/users',                element: <Suspense fallback={fallback}><UserListPage /></Suspense> },
            { path: '/dang-ky-nhan-vien',    element: <Suspense fallback={fallback}><RegisterEmployeePage /></Suspense> },
            { path: '/dashboard',            element: <Suspense fallback={fallback}><DashboardPage /></Suspense> },
            { path: '/tiem-nang',            element: <Suspense fallback={fallback}><TiemNangPage /></Suspense> },
            { path: '/tiem-nang/them-moi',   element: <Suspense fallback={fallback}><LeadAddPage /></Suspense> },
            { path: '/lien-he',              element: <Suspense fallback={fallback}><LienHePage /></Suspense> },
            { path: '/khach-hang',           element: <Suspense fallback={fallback}><KhachHangPage /></Suspense> },
            { path: '/co-hoi',               element: <Suspense fallback={fallback}><CoHoiPage /></Suspense> },
            { path: '/bao-gia',              element: <Suspense fallback={fallback}><BaoGiaPage /></Suspense> },
            { path: '/bao-gia/them-moi',     element: <Suspense fallback={fallback}><QuoteAddPage /></Suspense> },
            { path: '/don-hang',             element: <Suspense fallback={fallback}><DonHangPage /></Suspense> },
            { path: '/hoat-dong',            element: <Suspense fallback={fallback}><HoatDongPage /></Suspense> },
            { path: '/san-pham',             element: <Suspense fallback={fallback}><SanPhamPage /></Suspense> },
            { path: '/kho-hang',             element: <Suspense fallback={fallback}><KhoHangPage /></Suspense> },
            { path: '/phan-quyen',           element: <Suspense fallback={fallback}><PhanQuyenPage /></Suspense> },
            { path: '/thung-rac',            element: <Suspense fallback={fallback}><ThungRacPage /></Suspense> },
        ],
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
        path: '/forbidden',
        element: <div className="p-6">403 Forbidden</div>,
    },
]);
