import type { DonutSegment } from '../types/dashboardTypes';
import { formatNumber, formatMoney, type MoneyUnit } from '@/shared/utils/number';
import type { DashboardPeriod } from '../types/dashboardTypes';
import { useSaleDashboard } from '../hooks/useSaleDashboard';
import { useWinRateTrend } from '../hooks/useWinRateTrend';
import { useStalledOpportunities } from '../hooks/useStalledOpportunities';
import { useLossReasonsByOwner } from '../hooks/useLossReasonsByOwner';
import { COLORS, viStatus } from './chartTheme';
import { DashCard } from './DashCard';
import { KpiTile } from './KpiTile';
import { DonutChart } from './DonutChart';
import { FunnelChart } from './FunnelChart';
import { RankedList } from './RankedList';
import { AreaTrend } from './AreaTrend';
import { StalledList } from './StalledList';
import { OwnerDrilldown } from './OwnerDrilldown';

interface Props {
    period: DashboardPeriod;
    unit: MoneyUnit;
    periodLabel: string;
}

// dịch nhãn trạng thái các phần donut sang tiếng Việt
const seg = (module: string, arr: DonutSegment[]): DonutSegment[] =>
    arr.map((s) => ({ ...s, label: viStatus(module, s.label) }));

// dashboard "Bàn làm việc" cho SALES_STAFF — chỉ nội dung liên quan công việc của chính họ, KHÔNG có
// doanh thu/chi phí/lợi nhuận (đó là báo cáo tài chính, không phải việc nhân viên cần thấy hàng ngày)
// và KHÔNG có "việc cần xử lý gấp". Thêm 4 khối cá nhân: tỷ lệ thắng theo thời gian, lý do thua +
// cảnh báo cơ hội treo (gộp 1 khối), drill-down cơ hội của chính họ.
export const StaffDashboardView = ({ period, unit, periodLabel }: Props) => {
    const fmt = (v: number) => formatMoney(v, unit);
    const { data } = useSaleDashboard(period, true);
    const winRateTrend = useWinRateTrend(period);
    const stalled = useStalledOpportunities();
    const lossReasons = useLossReasonsByOwner(period);

    if (!data) return null;

    return (
        <div className="space-y-4">
            <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                <KpiTile label="Tổng cơ hội" value={formatNumber(data.oppTotal.current)} growthPct={data.oppTotal.growthPct} />
                <KpiTile label="Đang thực hiện" value={formatNumber(data.oppOpen.current)} growthPct={data.oppOpen.growthPct} />
                <KpiTile label="Kết thúc thắng" value={formatNumber(data.oppWon.current)} growthPct={data.oppWon.growthPct} />
                <KpiTile label="Kết thúc thua" value={formatNumber(data.oppLost.current)} growthPct={data.oppLost.growthPct} />
            </div>

            <DashCard title="Tỷ lệ cơ hội thắng" periodLabel={periodLabel}>
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

            <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
                <DashCard title="Phễu chuyển đổi theo giai đoạn" periodLabel={periodLabel}>
                    <FunnelChart stages={data.conversionFunnel} />
                </DashCard>
                <DashCard title="Cơ hội giá trị lớn" periodLabel={periodLabel}>
                    <RankedList items={data.topOpportunities} format={fmt} />
                </DashCard>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-4 gap-4">
                <DashCard title="Cơ hội theo trạng thái">
                    <DonutChart segments={seg('opportunity', data.opportunitiesByStatus)} />
                </DashCard>
                <DashCard title="Đơn hàng theo trạng thái">
                    <DonutChart segments={seg('order', data.ordersByStatus)} />
                </DashCard>
                <DashCard title="Hóa đơn theo trạng thái">
                    <DonutChart segments={seg('invoice', data.invoicesByStatus)} />
                </DashCard>
                <DashCard title="Chăm sóc theo trạng thái">
                    <DonutChart segments={seg('ticket', data.ticketsByStatus)} />
                </DashCard>
            </div>

            <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
                <DashCard title="Tỷ lệ thắng theo thời gian (của bạn)" periodLabel="12 tháng" onRefresh={() => winRateTrend.refetch()}>
                    {winRateTrend.data && <AreaTrend data={winRateTrend.data} color={COLORS.success} format={(v) => `${v}%`} />}
                </DashCard>
                <DashCard title="Lý do thua & cảnh báo cơ hội treo" periodLabel={periodLabel}
                          onRefresh={() => { lossReasons.refetch(); stalled.refetch(); }}>
                    <DonutChart centerLabel="Lý do thua" segments={lossReasons.data?.[0]?.segments ?? []} />
                    {stalled.data && (
                        <div className="mt-4 pt-4 border-t border-gray-100">
                            <StalledList data={stalled.data} unitLabel="cơ hội đang treo" />
                        </div>
                    )}
                </DashCard>
            </div>

            <DashCard title="Cơ hội của bạn" periodLabel="Đang mở / Đã thua">
                <OwnerDrilldown privileged={false} employees={[]} employeesLoading={false} />
            </DashCard>
        </div>
    );
};
