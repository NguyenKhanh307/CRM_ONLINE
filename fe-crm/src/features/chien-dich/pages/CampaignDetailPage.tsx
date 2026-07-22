import { useState } from 'react';
import { useParams } from 'react-router-dom';
import { FiSend } from 'react-icons/fi';
import { ActionButton } from '@/shared/components/ActionButton';
import { DetailHeader } from '@/shared/components/detail/DetailHeader';
import { InfoRow } from '@/shared/components/detail/InfoRow';
import { StatCards, type StatCard } from '@/shared/components/detail/StatCards';
import { Tabs, type TabDef } from '@/shared/components/detail/Tabs';
import { RelatedTable } from '@/shared/components/detail/RelatedTable';
import {
    INVOICE_COLUMNS, LEAD_COLUMNS, OPPORTUNITY_COLUMNS, ORDER_COLUMNS,
} from '@/shared/components/detail/relatedColumns';
import { formatISODate } from '@/shared/utils/date';
import { formatCurrency, formatNumber } from '@/shared/utils/number';
import { useCampaign } from '../hooks/useCampaign';
import { useCampaignStats } from '../hooks/useCampaignStats';
import { useCampaignRelated } from '../hooks/useCampaignRelated';
import { CampaignMembersTable } from '../components/CampaignMembersTable';
import { SendEmailModal } from '../components/SendEmailModal';
import { CAMPAIGN_STATUS_LABELS, CAMPAIGN_TYPE_LABELS } from '../config/campaignColumns';

type TabKey = 'overview' | 'members' | 'leads' | 'opportunities' | 'orders' | 'invoices' | 'stats';

/**
 * Trang chi tiết Chiến dịch — thông tin, danh sách khách hàng của chiến dịch,
 * các bản ghi quy về chiến dịch (attribution) và hiệu quả (ROI).
 */
const CampaignDetailPage = () => {
    const { id } = useParams();
    const campaignId = Number(id);
    const { data: campaign, isLoading } = useCampaign(campaignId);
    const { data: stats } = useCampaignStats(campaignId);
    const { data: related } = useCampaignRelated(campaignId);
    const [tab, setTab] = useState<TabKey>('overview');
    const [emailOpen, setEmailOpen] = useState(false);

    if (isLoading || !campaign) {
        return <div className="p-6 text-gray-400">Đang tải...</div>;
    }

    const tabs: TabDef<TabKey>[] = [
        { key: 'overview', label: 'Tổng quan' },
        { key: 'members', label: 'Khách hàng', count: stats?.memberCount },
        { key: 'leads', label: 'Tiềm năng', count: related?.leads.total },
        { key: 'opportunities', label: 'Cơ hội', count: related?.opportunities.total },
        { key: 'orders', label: 'Đơn hàng', count: related?.orders.total },
        { key: 'invoices', label: 'Hóa đơn', count: related?.invoices.total },
        { key: 'stats', label: 'Hiệu quả' },
    ];

    /** Thẻ số liệu ở tab Hiệu quả — chỉ dựng khi API stats đã trả về. */
    const statCards: StatCard[] = stats
        ? [
            { label: 'Khách hàng', value: formatNumber(stats.memberCount) },
            { label: 'Đã gửi email', value: formatNumber(stats.sentCount) },
            { label: 'Tiềm năng', value: formatNumber(stats.leadCount) },
            { label: 'Cơ hội', value: formatNumber(stats.opportunityCount) },
            { label: 'Cơ hội thắng', value: formatNumber(stats.wonOpportunityCount), tone: 'success' },
            { label: 'Đơn hàng', value: formatNumber(stats.orderCount) },
            { label: 'Doanh thu quy về', value: formatCurrency(stats.revenue ?? 0), tone: 'success' },
            { label: 'Chi phí thực tế', value: formatCurrency(stats.actualCost ?? 0), tone: 'warning' },
        ]
        : [];

    return (
        <div className="p-6 bg-bg-main">
            <DetailHeader
                backTo="/chien-dich"
                title={campaign.name}
                badge={
                    <span className="inline-block px-2 py-0.5 rounded text-sm font-medium bg-blue-100 text-blue-700">
                        {CAMPAIGN_STATUS_LABELS[campaign.status] ?? campaign.status}
                    </span>
                }
                meta={[
                    { label: 'Mã', value: campaign.code },
                    { label: 'Loại', value: CAMPAIGN_TYPE_LABELS[campaign.type] ?? campaign.type },
                    { label: 'Kênh', value: campaign.channel },
                    { label: 'Ngân sách', value: campaign.budget != null ? formatCurrency(campaign.budget) : null },
                ]}
                actions={
                    <ActionButton variant="primary" icon={FiSend} onClick={() => setEmailOpen(true)}>
                        Gửi email
                    </ActionButton>
                }
            />

            <div className="bg-white rounded-card shadow-sm">
                <Tabs tabs={tabs} active={tab} onChange={setTab} />
                <div className="p-4">
                    {tab === 'overview' && (
                        <div className="max-w-2xl">
                            <InfoRow label="Kênh" value={campaign.channel} />
                            <InfoRow label="Ngày bắt đầu" value={campaign.startDate ? formatISODate(campaign.startDate) : null} />
                            <InfoRow label="Ngày kết thúc" value={campaign.endDate ? formatISODate(campaign.endDate) : null} />
                            <InfoRow label="Ngân sách" value={campaign.budget != null ? formatCurrency(campaign.budget) : null} />
                            <InfoRow label="Chi phí thực tế" value={campaign.actualCost != null ? formatCurrency(campaign.actualCost) : null} />
                            <InfoRow label="Quy mô mục tiêu" value={campaign.targetSize != null ? formatNumber(campaign.targetSize) : null} />
                            <InfoRow label="Doanh số kỳ vọng" value={campaign.expectedRevenue != null ? formatCurrency(campaign.expectedRevenue) : null} />
                            <InfoRow label="Mô tả" value={campaign.description} />
                        </div>
                    )}

                    {tab === 'members' && <CampaignMembersTable campaignId={campaignId} />}

                    {tab === 'leads' && (
                        <RelatedTable group={related?.leads} columns={LEAD_COLUMNS} module="lead"
                            emptyText="Chưa có tiềm năng nào quy về chiến dịch này." />
                    )}
                    {tab === 'opportunities' && (
                        <RelatedTable group={related?.opportunities} columns={OPPORTUNITY_COLUMNS} module="opportunity"
                            emptyText="Chưa có cơ hội nào quy về chiến dịch này." />
                    )}
                    {tab === 'orders' && (
                        <RelatedTable group={related?.orders} columns={ORDER_COLUMNS} module="order"
                            emptyText="Chưa có đơn hàng nào quy về chiến dịch này." />
                    )}
                    {tab === 'invoices' && (
                        <RelatedTable group={related?.invoices} columns={INVOICE_COLUMNS} module="invoice"
                            emptyText="Chưa có hóa đơn nào quy về chiến dịch này." />
                    )}

                    {tab === 'stats' && stats && (
                        <div>
                            <StatCards stats={statCards} />
                            <div className="text-md text-gray-600 space-y-1">
                                {stats.leadCount > 0 && stats.actualCost != null && stats.actualCost > 0 && (
                                    <div>Chi phí trên mỗi tiềm năng (CPL): <b>{formatCurrency(stats.actualCost / stats.leadCount)}</b></div>
                                )}
                                {stats.leadCount > 0 && (
                                    <div>Tỷ lệ chuyển đổi tiềm năng → cơ hội thắng: <b>{((stats.wonOpportunityCount / stats.leadCount) * 100).toFixed(1)}%</b></div>
                                )}
                            </div>
                        </div>
                    )}
                </div>
            </div>

            <SendEmailModal campaignId={campaignId} open={emailOpen} onClose={() => setEmailOpen(false)} />
        </div>
    );
};

export default CampaignDetailPage;
