import { useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { usePermission } from '@/core/permissions/usePermission';
import { formatMoney, formatNumber, type MoneyUnit } from '@/shared/utils/number';
import type { DashboardPeriod } from '@/features/dashboard/types/dashboardTypes';
import { useManagerDashboard } from '@/features/dashboard/hooks/useManagerDashboard';
import { useSaleDashboard } from '@/features/dashboard/hooks/useSaleDashboard';
import { PeriodSelect, UnitSelect, PERIOD_LABEL } from '@/features/dashboard/components/Selectors';
import { COLORS } from '@/features/dashboard/components/chartTheme';
import { DashCard } from '@/features/dashboard/components/DashCard';
import { KpiTile } from '@/features/dashboard/components/KpiTile';
import { DonutChart } from '@/features/dashboard/components/DonutChart';
import { RevenueCostProfitChart } from '@/features/dashboard/components/RevenueCostProfitChart';
import { FunnelChart } from '@/features/dashboard/components/FunnelChart';
import { RankedList } from '@/features/dashboard/components/RankedList';
import { useRevenueByCampaign } from '../hooks/useRevenueByCampaign';

/** Ép giá trị query param về mã kỳ hợp lệ (mặc định quarter). */
const toPeriod = (v: string | null): DashboardPeriod =>
    v === 'month' || v === 'year' ? v : 'quarter';

/**
 * Trang "Phân tích so sánh" — đích đến của link trong trợ lý AI Copilot (/phan-tich?period=...).
 * So sánh kỳ này vs kỳ trước (doanh thu/chi phí/lợi nhuận, tỷ lệ thắng, phễu) + so sánh theo
 * nhân viên (chỉ ADMIN/quản lý) và theo chiến dịch. Dữ liệu tái dùng API Dashboard → đồng nhất số liệu.
 */
const PhanTichPage = () => {
    const { hasRole } = usePermission();
    const [searchParams, setSearchParams] = useSearchParams();
    const period = toPeriod(searchParams.get('period'));
    const [unit, setUnit] = useState<MoneyUnit>('vnd');

    const privileged = hasRole('ADMIN') || hasRole('SALES_MANAGER');
    const manager = useManagerDashboard(period, privileged);
    const sale = useSaleDashboard(period, !privileged);
    const active = privileged ? manager : sale;
    const campaigns = useRevenueByCampaign(period);

    const data = active.data;
    const periodLabel = PERIOD_LABEL[period];
    const fmt = (v: number) => formatMoney(v, unit);

    /** Đổi kỳ → ghi lại query param để link chia sẻ được. */
    const changePeriod = (p: DashboardPeriod) => setSearchParams({ period: p });

    return (
        <div className="p-6 bg-bg-main min-h-[calc(100vh-50px)]">
            <div className="flex flex-wrap items-center justify-between gap-3 mb-4">
                <div>
                    <h1 className="text-xl font-semibold text-text-main">Phân tích so sánh</h1>
                    <p className="text-sm text-gray-500">
                        {periodLabel} so với kỳ trước{privileged ? ' — toàn bộ dữ liệu' : ' — dữ liệu bạn phụ trách'}
                    </p>
                </div>
                <div className="flex items-center gap-2">
                    <UnitSelect value={unit} onChange={setUnit} />
                    <PeriodSelect value={period} onChange={changePeriod} />
                </div>
            </div>

            {active.isLoading && <div className="text-gray-400 py-10 text-center">Đang tải dữ liệu…</div>}
            {active.isError && <div className="text-danger py-10 text-center">Không tải được dữ liệu phân tích.</div>}

            {data && (
                <div className="space-y-4">
                    {/* So sánh tài chính kỳ này vs kỳ trước */}
                    <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                        <KpiTile label="Doanh thu" value={fmt(data.totalRevenue.current)}
                                 growthPct={data.totalRevenue.growthPct} sparkline={data.revenueByMonth}
                                 color={COLORS.success}>
                            <p className="text-sm text-gray-400 mt-1">Kỳ trước: {fmt(data.totalRevenue.previous)}</p>
                        </KpiTile>
                        <KpiTile label="Chi phí" value={fmt(data.totalCost.current)}
                                 growthPct={data.totalCost.growthPct} sparkline={data.costByMonth}
                                 color={COLORS.danger}>
                            <p className="text-sm text-gray-400 mt-1">Kỳ trước: {fmt(data.totalCost.previous)}</p>
                        </KpiTile>
                        <KpiTile label="Lợi nhuận" value={fmt(data.totalProfit.current)}
                                 growthPct={data.totalProfit.growthPct} sparkline={data.profitByMonth}
                                 color={COLORS.primary}>
                            <p className="text-sm text-gray-400 mt-1">Kỳ trước: {fmt(data.totalProfit.previous)}</p>
                        </KpiTile>
                    </div>

                    <DashCard title="Xu hướng doanh thu - chi phí - lợi nhuận" periodLabel="12 tháng"
                              onRefresh={() => active.refetch()}>
                        <RevenueCostProfitChart revenue={data.revenueByMonth} cost={data.costByMonth}
                                                profit={data.profitByMonth} format={fmt} />
                    </DashCard>

                    <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
                        <DashCard title="Tỷ lệ cơ hội thắng" periodLabel={periodLabel} onRefresh={() => active.refetch()}>
                            <div className="flex items-center justify-center">
                                <div className="text-center mr-6">
                                    <div className="text-xl font-semibold text-success">{data.winRate.current}%</div>
                                    <div className="text-sm text-gray-400">
                                        Kỳ trước: {formatNumber(data.winRate.previous)}%
                                    </div>
                                </div>
                                <DonutChart centerLabel="Cơ hội" segments={[
                                    { label: 'Thắng', count: data.oppWon.current, pct: data.winRate.current },
                                    { label: 'Thua', count: data.oppLost.current, pct: 100 - data.winRate.current },
                                ]} />
                            </div>
                        </DashCard>
                        <DashCard title="Phễu chuyển đổi theo giai đoạn" periodLabel={periodLabel}
                                  onRefresh={() => active.refetch()}>
                            <FunnelChart stages={data.conversionFunnel} />
                        </DashCard>
                    </div>

                    <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
                        {privileged && data.revenueByOwner && (
                            <DashCard title="So sánh doanh thu theo nhân viên" periodLabel={periodLabel}
                                      onRefresh={() => active.refetch()}>
                                <RankedList items={data.revenueByOwner} format={fmt} color={COLORS.success} />
                            </DashCard>
                        )}
                        <DashCard title="So sánh doanh thu theo chiến dịch" periodLabel={periodLabel}
                                  onRefresh={() => campaigns.refetch()}>
                            {campaigns.isLoading
                                ? <p className="text-sm text-gray-400 py-8 text-center">Đang tải…</p>
                                : <RankedList items={campaigns.data ?? []} format={fmt} color={COLORS.warning} />}
                        </DashCard>
                    </div>
                </div>
            )}
        </div>
    );
};

export default PhanTichPage;
