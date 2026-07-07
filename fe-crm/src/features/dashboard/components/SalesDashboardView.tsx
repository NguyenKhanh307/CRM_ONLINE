import type { SalesDashboard, DonutSegment } from '../types/dashboardTypes';
import { formatMoney, formatNumber, type MoneyUnit } from '@/shared/utils/number';
import { COLORS, viStatus } from './chartTheme';
import { DashCard } from './DashCard';
import { KpiTile } from './KpiTile';
import { DonutChart } from './DonutChart';
import { AreaTrend } from './AreaTrend';
import { RevenueCostProfitChart } from './RevenueCostProfitChart';
import { FunnelChart } from './FunnelChart';
import { RankedList } from './RankedList';
import { StackedBarByGroup } from './StackedBarByGroup';
import { UrgentList } from './UrgentList';

interface Props {
    data: SalesDashboard;
    unit: MoneyUnit;
    periodLabel: string;
    onRefresh: () => void;
    /** Hiển thị phần thống kê theo nhân viên (chỉ manager). */
    showTeam: boolean;
}

/** Dịch nhãn trạng thái các phần donut sang tiếng Việt. */
const seg = (module: string, arr: DonutSegment[]): DonutSegment[] =>
    arr.map((s) => ({ ...s, label: viStatus(module, s.label) }));

/**
 * Thân dashboard kinh doanh dùng chung cho SALES_MANAGER (showTeam) và SALES_STAFF (cá nhân).
 */
export const SalesDashboardView = ({ data, unit, periodLabel, onRefresh, showTeam }: Props) => {
    const fmt = (v: number) => formatMoney(v, unit);

    return (
        <div className="space-y-4">
            {/* Tài chính: 3 thẻ KPI area-trend */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                <KpiTile label="Tổng doanh thu" value={fmt(data.totalRevenue.current)} growthPct={data.totalRevenue.growthPct}
                         sparkline={data.revenueByMonth} color={COLORS.success} />
                <KpiTile label="Tổng chi phí" value={fmt(data.totalCost.current)} growthPct={data.totalCost.growthPct}
                         sparkline={data.costByMonth} color={COLORS.danger} />
                <KpiTile label="Lợi nhuận" value={fmt(data.totalProfit.current)} growthPct={data.totalProfit.growthPct}
                         sparkline={data.profitByMonth} color={COLORS.primary} />
            </div>

            {/* Combo doanh thu - chi phí - lợi nhuận */}
            <DashCard title="Doanh thu, chi phí, lợi nhuận" periodLabel={periodLabel} onRefresh={onRefresh}>
                <RevenueCostProfitChart revenue={data.revenueByMonth} cost={data.costByMonth} profit={data.profitByMonth} format={fmt} />
            </DashCard>

            {/* KPI cơ hội */}
            <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                <KpiTile label="Tổng cơ hội" value={formatNumber(data.oppTotal.current)} growthPct={data.oppTotal.growthPct} />
                <KpiTile label="Đang thực hiện" value={formatNumber(data.oppOpen.current)} growthPct={data.oppOpen.growthPct} />
                <KpiTile label="Kết thúc thắng" value={formatNumber(data.oppWon.current)} growthPct={data.oppWon.growthPct} />
                <KpiTile label="Kết thúc thua" value={formatNumber(data.oppLost.current)} growthPct={data.oppLost.growthPct} />
            </div>

            <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
                <DashCard title="Tỷ lệ cơ hội thắng" periodLabel={periodLabel} onRefresh={onRefresh}>
                    <div className="flex items-center justify-center">
                        <div className="text-center mr-6">
                            <div className="text-xl font-semibold text-success">{data.winRate.current}%</div>
                            <div className="text-sm text-gray-400">Tỷ lệ thắng</div>
                        </div>
                        <DonutChart centerLabel="Cơ hội" segments={[
                            { label: 'Thắng', count: data.oppWon.current, pct: data.winRate.current },
                            { label: 'Thua', count: data.oppLost.current, pct: 100 - data.winRate.current },
                        ]} />
                    </div>
                </DashCard>
                <DashCard title="Doanh số kỳ vọng theo tháng" periodLabel="12 tháng" onRefresh={onRefresh}>
                    <AreaTrend data={data.expectedByMonth} color={COLORS.primary} format={fmt} />
                </DashCard>
            </div>

            <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
                <DashCard title="Phễu chuyển đổi theo giai đoạn" periodLabel={periodLabel} onRefresh={onRefresh}>
                    <FunnelChart stages={data.conversionFunnel} />
                </DashCard>
                <DashCard title="Cơ hội giá trị lớn" periodLabel={periodLabel} onRefresh={onRefresh}>
                    <RankedList items={data.topOpportunities} format={fmt} />
                </DashCard>
            </div>

            {showTeam && data.teamByOwner && data.revenueByOwner && (
                <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
                    <DashCard title="Doanh thu theo nhân viên" periodLabel={periodLabel} onRefresh={onRefresh}>
                        <RankedList items={data.revenueByOwner} format={fmt} color={COLORS.success} />
                    </DashCard>
                    <DashCard title="Cơ hội theo người thực hiện" periodLabel={periodLabel} onRefresh={onRefresh}>
                        <StackedBarByGroup rows={data.teamByOwner} module="opportunity" />
                    </DashCard>
                </div>
            )}

            {/* Trạng thái các phân hệ */}
            <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-4 gap-4">
                <DashCard title="Cơ hội theo trạng thái" onRefresh={onRefresh}>
                    <DonutChart segments={seg('opportunity', data.opportunitiesByStatus)} />
                </DashCard>
                <DashCard title="Đơn hàng theo trạng thái" onRefresh={onRefresh}>
                    <DonutChart segments={seg('order', data.ordersByStatus)} />
                </DashCard>
                <DashCard title="Hóa đơn theo trạng thái" onRefresh={onRefresh}>
                    <DonutChart segments={seg('invoice', data.invoicesByStatus)} />
                </DashCard>
                <DashCard title="Chăm sóc theo trạng thái" onRefresh={onRefresh}>
                    <DonutChart segments={seg('ticket', data.ticketsByStatus)} />
                </DashCard>
            </div>

            <DashCard title="Việc cần xử lý gấp" onRefresh={onRefresh}>
                <UrgentList items={data.urgentItems} />
            </DashCard>
        </div>
    );
};
