import { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { FiArrowLeft, FiSend } from 'react-icons/fi';
import { formatISODate } from '@/shared/utils/date';
import { formatCurrency, formatNumber } from '@/shared/utils/number';
import { useCampaign } from '../hooks/useCampaign';
import { useCampaignStats } from '../hooks/useCampaignStats';
import { CampaignMembersTable } from '../components/CampaignMembersTable';
import { SendEmailModal } from '../components/SendEmailModal';
import { CAMPAIGN_STATUS_LABELS, CAMPAIGN_TYPE_LABELS } from '../config/campaignColumns';

type Tab = 'overview' | 'members' | 'stats';

/** Trang chi tiết Chiến dịch — 3 tab: Tổng quan / Thành viên / Hiệu quả (ROI). */
const CampaignDetailPage = () => {
    const { id } = useParams();
    const campaignId = Number(id);
    const navigate = useNavigate();
    const { data: campaign, isLoading } = useCampaign(campaignId);
    const { data: stats } = useCampaignStats(campaignId);
    const [tab, setTab] = useState<Tab>('overview');
    const [emailOpen, setEmailOpen] = useState(false);

    if (isLoading || !campaign) {
        return <div className="p-6 text-gray-400">Đang tải...</div>;
    }

    const tabCls = (t: Tab) =>
        `px-4 py-2 text-md font-medium border-b-2 ${tab === t ? 'border-primary text-primary' : 'border-transparent text-gray-500 hover:text-text-main'}`;

    const InfoRow = ({ label, value }: { label: string; value: React.ReactNode }) => (
        <div className="flex gap-3 py-1.5">
            <span className="w-40 shrink-0 text-gray-500">{label}</span>
            <span className="text-text-main">{value ?? '—'}</span>
        </div>
    );

    const StatCard = ({ label, value, accent }: { label: string; value: React.ReactNode; accent?: string }) => (
        <div className="bg-white rounded-card shadow-sm p-4">
            <div className="text-sm text-gray-500">{label}</div>
            <div className={`text-xl font-semibold mt-1 ${accent ?? 'text-text-main'}`}>{value}</div>
        </div>
    );

    return (
        <div className="p-6 bg-bg-main min-h-screen">
            <div className="flex items-center justify-between mb-4">
                <div className="flex items-center gap-3">
                    <button onClick={() => navigate('/chien-dich')} className="p-1.5 rounded hover:bg-gray-100 text-gray-500">
                        <FiArrowLeft size={18} />
                    </button>
                    <div>
                        <h1 className="text-xl font-semibold text-text-main">{campaign.name}</h1>
                        <div className="text-sm text-gray-500">
                            {campaign.code} · {CAMPAIGN_TYPE_LABELS[campaign.type] ?? campaign.type} · {CAMPAIGN_STATUS_LABELS[campaign.status] ?? campaign.status}
                        </div>
                    </div>
                </div>
                <button onClick={() => setEmailOpen(true)}
                    className="flex items-center gap-1.5 px-3 py-1.5 rounded-btn bg-primary text-white text-md hover:opacity-90">
                    <FiSend size={14} /> Gửi email
                </button>
            </div>

            <div className="flex gap-1 border-b border-gray-200 mb-4">
                <button className={tabCls('overview')} onClick={() => setTab('overview')}>Tổng quan</button>
                <button className={tabCls('members')} onClick={() => setTab('members')}>Thành viên</button>
                <button className={tabCls('stats')} onClick={() => setTab('stats')}>Hiệu quả</button>
            </div>

            {tab === 'overview' && (
                <div className="bg-white rounded-card shadow-sm p-6 max-w-2xl">
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

            {tab === 'members' && (
                <div className="bg-white rounded-card shadow-sm p-6">
                    <CampaignMembersTable campaignId={campaignId} />
                </div>
            )}

            {tab === 'stats' && stats && (
                <div className="space-y-4">
                    <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                        <StatCard label="Thành viên" value={formatNumber(stats.memberCount)} />
                        <StatCard label="Đã gửi email" value={formatNumber(stats.sentCount)} />
                        <StatCard label="Tiềm năng" value={formatNumber(stats.leadCount)} />
                        <StatCard label="Cơ hội" value={formatNumber(stats.opportunityCount)} />
                        <StatCard label="Cơ hội thắng" value={formatNumber(stats.wonOpportunityCount)} accent="text-success" />
                        <StatCard label="Đơn hàng" value={formatNumber(stats.orderCount)} />
                        <StatCard label="Doanh thu quy về" value={formatCurrency(stats.revenue ?? 0)} accent="text-primary" />
                        <StatCard label="Chi phí thực tế" value={formatCurrency(stats.actualCost ?? 0)} accent="text-danger" />
                    </div>
                    <div className="bg-white rounded-card shadow-sm p-4 text-md text-gray-600">
                        {stats.leadCount > 0 && stats.actualCost != null && stats.actualCost > 0 && (
                            <div>Chi phí trên mỗi tiềm năng (CPL): <b>{formatCurrency(stats.actualCost / stats.leadCount)}</b></div>
                        )}
                        {stats.leadCount > 0 && (
                            <div>Tỷ lệ chuyển đổi tiềm năng → cơ hội thắng: <b>{((stats.wonOpportunityCount / stats.leadCount) * 100).toFixed(1)}%</b></div>
                        )}
                    </div>
                </div>
            )}

            <SendEmailModal campaignId={campaignId} open={emailOpen} onClose={() => setEmailOpen(false)} />
        </div>
    );
};

export default CampaignDetailPage;
