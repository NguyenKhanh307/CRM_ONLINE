import { FiUsers, FiShield, FiKey } from 'react-icons/fi';
import type { AdminDashboard, DonutSegment } from '../types/dashboardTypes';
import { formatNumber } from '@/shared/utils/number';
import { COLORS, viStatus } from './chartTheme';
import { DashCard } from './DashCard';
import { KpiTile } from './KpiTile';
import { DonutChart } from './DonutChart';
import { MonthlyBar } from './MonthlyBar';

interface Props {
    data: AdminDashboard;
    periodLabel: string;
    onRefresh: () => void;
}

/** Dịch nhãn trạng thái tài khoản sang tiếng Việt. */
const userSeg = (arr: DonutSegment[]): DonutSegment[] =>
    arr.map((s) => ({ ...s, label: viStatus('user', s.label) }));

/**
 * Dashboard ADMIN — sức khỏe hệ thống: tài khoản, vai trò, quyền, cơ cấu tổ chức.
 */
export const AdminDashboardView = ({ data, periodLabel, onRefresh }: Props) => (
    <div className="space-y-4">
        {/* KPI hệ thống */}
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
            <KpiTile label="Tổng tài khoản" value={formatNumber(data.userTotal.current)} growthPct={data.userTotal.growthPct} />
            <KpiTile label="Tài khoản mới trong kỳ" value={formatNumber(data.newUsersThisMonth.current)}
                     growthPct={data.newUsersThisMonth.growthPct} sparkline={data.usersByMonth} color={COLORS.primary} />
            <div className="bg-white rounded-card p-4 shadow-sm flex items-center gap-3">
                <FiShield className="text-primary" size={28} />
                <div>
                    <div className="text-sm text-gray-500">Số vai trò</div>
                    <div className="text-xl font-semibold text-text-main">{formatNumber(data.roleCount)}</div>
                </div>
            </div>
            <div className="bg-white rounded-card p-4 shadow-sm flex items-center gap-3">
                <FiKey className="text-warning" size={28} />
                <div>
                    <div className="text-sm text-gray-500">Số quyền</div>
                    <div className="text-xl font-semibold text-text-main">{formatNumber(data.permissionCount)}</div>
                </div>
            </div>
        </div>

        {/* Cột theo tháng + donut trạng thái */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
            <DashCard title="Số tài khoản tạo theo tháng" periodLabel="12 tháng" onRefresh={onRefresh}>
                <MonthlyBar data={data.usersByMonth} color={COLORS.primary} />
            </DashCard>
            <DashCard title="Tài khoản theo trạng thái" periodLabel={periodLabel} onRefresh={onRefresh}>
                <DonutChart centerLabel="Tài khoản" segments={userSeg(data.usersByStatus)} />
            </DashCard>
        </div>

        {/* Cơ cấu vai trò */}
        <DashCard title="Cơ cấu theo vai trò" onRefresh={onRefresh}>
            <DonutChart centerLabel="Tài khoản" segments={data.usersByRole} />
        </DashCard>

        {/* Tổng quan bản ghi toàn hệ thống */}
        <DashCard title="Tổng quan dữ liệu toàn hệ thống" onRefresh={onRefresh}>
            <div className="flex items-center gap-2 text-gray-400 mb-3">
                <FiUsers size={16} /><span className="text-sm">Số bản ghi các phân hệ nghiệp vụ (chưa xóa)</span>
            </div>
            <DonutChart centerLabel="Bản ghi" segments={data.recordTotals} />
        </DashCard>
    </div>
);
