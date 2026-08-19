import { useState } from 'react';
import { usePermission } from '@/core/permissions/usePermission';
import { useAuth } from '@/core/auth/useAuth';
import type { MoneyUnit } from '@/shared/utils/number';
import type { DashboardPeriod } from '../types/dashboardTypes';
import { useAdminDashboard } from '../hooks/useAdminDashboard';
import { PeriodSelect, UnitSelect, PERIOD_LABEL } from '../components/Selectors';
import { AdminDashboardView } from '../components/AdminDashboardView';
import { ManagerDashboardView } from '../components/ManagerDashboardView';
import { StaffDashboardView } from '../components/StaffDashboardView';

type Role = 'admin' | 'manager' | 'sale';

/**
 * Trang "Bàn làm việc" — chọn dashboard theo vai trò ưu tiên (ADMIN → Manager → Sale).
 * Manager/Sale tự quản lý dữ liệu + trạng thái tải bên trong view riêng của mình (mỗi khối 1 hook),
 * chỉ ADMIN còn dùng pattern loading/error tập trung ở đây (dữ liệu hệ thống, 1 truy vấn duy nhất).
 */
const DashboardPage = () => {
    const { hasRole } = usePermission();
    const { user } = useAuth();
    const [period, setPeriod] = useState<DashboardPeriod>('year');
    const [unit, setUnit] = useState<MoneyUnit>('vnd');

    const role: Role = hasRole('ADMIN') ? 'admin' : hasRole('SALES_MANAGER') ? 'manager' : 'sale';
    const admin = useAdminDashboard(period, role === 'admin');
    const periodLabel = PERIOD_LABEL[period];

    return (
        <div className="p-6 bg-bg-main min-h-[calc(100vh-50px)]">
            <div className="flex flex-wrap items-center justify-between gap-3 mb-4">
                <div>
                    <h1 className="text-xl font-semibold text-text-main">Bàn làm việc</h1>
                    <p className="text-sm text-gray-500">Xin chào, {user?.fullName ?? 'bạn'} 👋</p>
                </div>
                <div className="flex items-center gap-2">
                    {role !== 'admin' && <UnitSelect value={unit} onChange={setUnit} />}
                    <PeriodSelect value={period} onChange={setPeriod} />
                </div>
            </div>

            {role === 'admin' && (
                <>
                    {admin.isLoading && <div className="text-gray-400 py-10 text-center">Đang tải dữ liệu…</div>}
                    {admin.isError && <div className="text-danger py-10 text-center">Không tải được dữ liệu bảng điều khiển.</div>}
                    {admin.data && <AdminDashboardView data={admin.data} periodLabel={periodLabel} onRefresh={() => admin.refetch()} />}
                </>
            )}
            {role === 'manager' && <ManagerDashboardView period={period} unit={unit} periodLabel={periodLabel} />}
            {role === 'sale' && <StaffDashboardView period={period} unit={unit} periodLabel={periodLabel} />}
        </div>
    );
};

export default DashboardPage;
