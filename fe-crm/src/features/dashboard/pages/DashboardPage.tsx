import { useState } from 'react';
import { usePermission } from '@/core/permissions/usePermission';
import { useAuth } from '@/core/auth/useAuth';
import type { MoneyUnit } from '@/shared/utils/number';
import type { DashboardPeriod } from '../types/dashboardTypes';
import { useAdminDashboard } from '../hooks/useAdminDashboard';
import { useManagerDashboard } from '../hooks/useManagerDashboard';
import { useSaleDashboard } from '../hooks/useSaleDashboard';
import { PeriodSelect, UnitSelect, PERIOD_LABEL } from '../components/Selectors';
import { AdminDashboardView } from '../components/AdminDashboardView';
import { SalesDashboardView } from '../components/SalesDashboardView';

type Role = 'admin' | 'manager' | 'sale';

/**
 * Trang "Bàn làm việc" — chọn dashboard theo vai trò ưu tiên (ADMIN → Manager → Sale).
 */
const DashboardPage = () => {
    const { hasRole } = usePermission();
    const { user } = useAuth();
    const [period, setPeriod] = useState<DashboardPeriod>('year');
    const [unit, setUnit] = useState<MoneyUnit>('vnd');

    const role: Role = hasRole('ADMIN') ? 'admin' : hasRole('SALES_MANAGER') ? 'manager' : 'sale';

    const admin = useAdminDashboard(period, role === 'admin');
    const manager = useManagerDashboard(period, role === 'manager');
    const sale = useSaleDashboard(period, role === 'sale');

    const active = role === 'admin' ? admin : role === 'manager' ? manager : sale;
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

            {active.isLoading && <div className="text-gray-400 py-10 text-center">Đang tải dữ liệu…</div>}
            {active.isError && (
                <div className="text-danger py-10 text-center">Không tải được dữ liệu bảng điều khiển.</div>
            )}

            {role === 'admin' && admin.data && (
                <AdminDashboardView data={admin.data} periodLabel={periodLabel} onRefresh={() => admin.refetch()} />
            )}
            {role === 'manager' && manager.data && (
                <SalesDashboardView data={manager.data} unit={unit} periodLabel={periodLabel}
                                    onRefresh={() => manager.refetch()} showTeam />
            )}
            {role === 'sale' && sale.data && (
                <SalesDashboardView data={sale.data} unit={unit} periodLabel={periodLabel}
                                    onRefresh={() => sale.refetch()} showTeam={false} />
            )}
        </div>
    );
};

export default DashboardPage;
