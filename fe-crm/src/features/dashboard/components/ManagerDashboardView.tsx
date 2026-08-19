import { formatMoney, formatNumber, type MoneyUnit } from '@/shared/utils/number';
import type { DashboardPeriod } from '../types/dashboardTypes';
import { useCampaignRoi } from '../hooks/useCampaignRoi';
import { useCampaignCac } from '../hooks/useCampaignCac';
import { useOpportunitySource } from '../hooks/useOpportunitySource';
import { useLeadPool } from '../hooks/useLeadPool';
import { useCampaignAttributedRevenue } from '../hooks/useCampaignAttributedRevenue';
import { useCampaignPeriodComparison } from '../hooks/useCampaignPeriodComparison';
import { useWinRateLeaderboard } from '../hooks/useWinRateLeaderboard';
import { useWinRateTrend } from '../hooks/useWinRateTrend';
import { useStalledOpportunities } from '../hooks/useStalledOpportunities';
import { useStalledByOwner } from '../hooks/useStalledByOwner';
import { useLossReasonsByOwner } from '../hooks/useLossReasonsByOwner';
import { DashCard } from './DashCard';
import { DonutChart } from './DonutChart';
import { RankedList } from './RankedList';
import { AreaTrend } from './AreaTrend';
import { StackedBarByGroup } from './StackedBarByGroup';
import { LeaderboardTable } from './LeaderboardTable';
import { StalledList } from './StalledList';
import { OwnerDrilldown } from './OwnerDrilldown';
import { COLORS } from './chartTheme';

interface Props {
    period: DashboardPeriod;
    unit: MoneyUnit;
    periodLabel: string;
}

// dashboard "Bàn làm việc" cho SALES_MANAGER — thay hẳn nội dung tài chính tổng quan cũ bằng các khối
// hiệu quả marketing (ROI/CAC chiến dịch, quy kết doanh thu, so sánh kỳ) và hiệu suất chăm sóc khách
// hàng theo nhân viên (win-rate, cơ hội treo, lý do thua, drill-down). Mỗi khối tự gọi hook riêng —
// không còn phụ thuộc `useManagerDashboard`/`SalesDashboardResult`.
export const ManagerDashboardView = ({ period, unit, periodLabel }: Props) => {
    const fmt = (v: number) => formatMoney(v, unit);

    const campaignRoi = useCampaignRoi(period);
    const campaignCac = useCampaignCac(period);
    const opportunitySource = useOpportunitySource(period);
    const leadPool = useLeadPool();
    const attributedRevenue = useCampaignAttributedRevenue(period);
    const periodComparison = useCampaignPeriodComparison(period);
    const winRateLeaderboard = useWinRateLeaderboard(period);
    const winRateTrend = useWinRateTrend(period);
    const stalled = useStalledOpportunities();
    const stalledByOwner = useStalledByOwner();
    const lossReasons = useLossReasonsByOwner(period);

    return (
        <div className="space-y-4">
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
                <DashCard title="Top chiến dịch theo ROI" periodLabel={periodLabel} onRefresh={() => campaignRoi.refetch()}>
                    {campaignRoi.isLoading
                        ? <p className="text-sm text-gray-400 py-8 text-center">Đang tải…</p>
                        : <RankedList items={campaignRoi.data ?? []} format={(v) => `${formatNumber(v)}%`} color={COLORS.success} />}
                </DashCard>
                <DashCard title="Chi phí trên mỗi lead/cơ hội/đơn hàng (CAC)" periodLabel={periodLabel} onRefresh={() => campaignCac.refetch()}>
                    <LeaderboardTable
                        rows={campaignCac.data ?? []}
                        rowKey={(r) => r.campaignId}
                        columns={[
                            { key: 'name', label: 'Chiến dịch', render: (r) => r.name },
                            { key: 'lead', label: 'Lead', align: 'right', render: (r) => formatNumber(r.leadCount) },
                            { key: 'opp', label: 'Cơ hội', align: 'right', render: (r) => formatNumber(r.opportunityCount) },
                            { key: 'order', label: 'Đơn', align: 'right', render: (r) => formatNumber(r.orderCount) },
                            { key: 'cacLead', label: 'CAC/lead', align: 'right', render: (r) => r.costPerLead != null ? fmt(r.costPerLead) : '—' },
                            { key: 'cacOpp', label: 'CAC/cơ hội', align: 'right', render: (r) => r.costPerOpportunity != null ? fmt(r.costPerOpportunity) : '—' },
                            { key: 'cacOrder', label: 'CAC/đơn hàng', align: 'right', render: (r) => r.costPerOrder != null ? fmt(r.costPerOrder) : '—' },

                        ]}
                    />
                </DashCard>
            </div>

            <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
                <DashCard title="Nguồn gốc cơ hội" periodLabel={periodLabel} onRefresh={() => opportunitySource.refetch()}>
                    <DonutChart centerLabel="Cơ hội" segments={opportunitySource.data ?? []} />
                </DashCard>
                <DashCard title="% doanh thu quy kết marketing" periodLabel={periodLabel} onRefresh={() => attributedRevenue.refetch()}>
                    {attributedRevenue.data && (
                        <div className="text-center py-4">
                            <div className="text-xl font-semibold text-primary">{formatNumber(attributedRevenue.data.current)}%</div>
                            <p className="text-sm text-gray-400 mt-1">Kỳ trước: {formatNumber(attributedRevenue.data.previous)}%</p>
                        </div>
                    )}
                </DashCard>
            </div>

            <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
                <DashCard title="Doanh thu chiến dịch đang chạy — kỳ này vs kỳ trước" periodLabel={periodLabel} onRefresh={() => periodComparison.refetch()}>
                    <LeaderboardTable
                        rows={periodComparison.data ?? []}
                        rowKey={(r) => r.campaignId}
                        emptyText="Không có chiến dịch nào đang chạy"
                        columns={[
                            { key: 'name', label: 'Chiến dịch', render: (r) => r.name },
                            { key: 'cur', label: 'Kỳ này', align: 'right', render: (r) => fmt(r.revenue.current) },
                            { key: 'prev', label: 'Kỳ trước', align: 'right', render: (r) => fmt(r.revenue.previous) },
                            { key: 'growth', label: 'Tăng trưởng', align: 'right', render: (r) => r.revenue.growthPct != null ? `${r.revenue.growthPct.toFixed(0)}%` : '—' },
                        ]}
                    />
                </DashCard>
                <DashCard title="Lead chờ trong pool chung" periodLabel="Hiện tại" onRefresh={() => leadPool.refetch()}>
                    {leadPool.data && <StalledList data={leadPool.data} unitLabel="lead" />}
                </DashCard>
            </div>

            <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
                <DashCard title="Bảng xếp hạng nhân viên (doanh thu + tỷ lệ thắng)" periodLabel={periodLabel} onRefresh={() => winRateLeaderboard.refetch()}>
                    <LeaderboardTable
                        rows={winRateLeaderboard.data ?? []}
                        rowKey={(r) => r.userId}
                        columns={[
                            { key: 'name', label: 'Nhân viên', render: (r) => r.fullName },
                            { key: 'revenue', label: 'Doanh thu', align: 'right', render: (r) => fmt(r.revenue) },
                            { key: 'won', label: 'Thắng', align: 'right', render: (r) => formatNumber(r.wonCount) },
                            { key: 'lost', label: 'Thua', align: 'right', render: (r) => formatNumber(r.lostCount) },
                            { key: 'rate', label: 'Tỷ lệ thắng', align: 'right', render: (r) => `${r.winRatePct}%` },
                        ]}
                    />
                </DashCard>
                <DashCard title="Tỷ lệ thắng theo thời gian (toàn đội)" periodLabel="12 tháng" onRefresh={() => winRateTrend.refetch()}>
                    {winRateTrend.data && <AreaTrend data={winRateTrend.data} color={COLORS.success} format={(v) => `${v}%`} />}
                </DashCard>
            </div>

            <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
                <DashCard title="Cơ hội đang treo (toàn đội)" periodLabel="≥14 ngày không hoạt động" onRefresh={() => stalled.refetch()}>
                    {stalled.data && <StalledList data={stalled.data} unitLabel="cơ hội" />}
                </DashCard>
                <DashCard title="Cơ hội treo theo nhân viên" periodLabel="≥14 ngày không hoạt động" onRefresh={() => stalledByOwner.refetch()}>
                    <RankedList items={stalledByOwner.data ?? []} format={(v) => `${formatNumber(v)} cơ hội`} color={COLORS.danger} />
                </DashCard>
            </div>

            <DashCard title="Lý do thua theo nhân viên" periodLabel={periodLabel} onRefresh={() => lossReasons.refetch()}>
                <StackedBarByGroup rows={lossReasons.data ?? []} module="opportunity" />
            </DashCard>

            <DashCard title="Drill-down cơ hội theo nhân viên" periodLabel="Đang mở / Đã thua">
                <OwnerDrilldown privileged employees={winRateLeaderboard.data ?? []} employeesLoading={winRateLeaderboard.isLoading} />
            </DashCard>
        </div>
    );
};
