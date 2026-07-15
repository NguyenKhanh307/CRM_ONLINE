import { useState } from 'react';
import { useParams } from 'react-router-dom';
import { FiEdit2 } from 'react-icons/fi';
import { ActionButton } from '@/shared/components/ActionButton';
import { DetailHeader } from '@/shared/components/detail/DetailHeader';
import { InfoCard } from '@/shared/components/detail/InfoCard';
import { StatCards, type StatCard } from '@/shared/components/detail/StatCards';
import { Tabs, type TabDef } from '@/shared/components/detail/Tabs';
import { RelatedTable } from '@/shared/components/detail/RelatedTable';
import { Timeline } from '@/shared/components/detail/Timeline';
import { OPPORTUNITY_COLUMNS } from '@/shared/components/detail/relatedColumns';
import { formatNumber } from '@/shared/utils/number';
import { useLeadDetail } from '../hooks/useLeadDetail';
import { useLeadRelated } from '../hooks/useLeadRelated';
import { LeadInfoPanel } from '../components/LeadInfoPanel';
import { LeadEditModal } from '../components/LeadEditModal';

const STATUS_LABELS: Record<string, string> = {
    new: 'Mới', contacting: 'Đang liên hệ', qualified: 'Đủ điều kiện', converted: 'Đã chuyển đổi', lost: 'Thất bại',
};
const STATUS_COLORS: Record<string, string> = {
    new: 'bg-gray-100 text-gray-600', contacting: 'bg-blue-100 text-blue-700', qualified: 'bg-green-100 text-green-700',
    converted: 'bg-emerald-100 text-emerald-700', lost: 'bg-red-100 text-red-600',
};

type TabKey = 'activities' | 'opportunities';

/** Trang chi tiết Tiềm năng — 2 cột: thông tin + hoạt động/cơ hội đã chuyển đổi. */
const LeadDetailPage = () => {
    const { id } = useParams<{ id: string }>();
    const leadId = Number(id);
    const { data: lead, isLoading } = useLeadDetail(leadId);
    const { data: related } = useLeadRelated(leadId);
    const [activeTab, setActiveTab] = useState<TabKey>('activities');
    const [editOpen, setEditOpen] = useState(false);

    if (isLoading) return <div className="p-6 text-gray-400">Đang tải...</div>;
    if (!lead) return <div className="p-6 text-gray-500">Không tìm thấy tiềm năng.</div>;

    const tabs: TabDef<TabKey>[] = [
        { key: 'activities', label: 'Hoạt động', count: related?.activities.total },
        { key: 'opportunities', label: 'Cơ hội', count: related?.opportunities.total },
    ];

    const stats: StatCard[] = [
        { label: 'Điểm', value: formatNumber(lead.score) },
        { label: 'Trạng thái', value: STATUS_LABELS[lead.status] ?? lead.status },
        { label: 'Số hoạt động', value: formatNumber(related?.activities.total ?? 0) },
    ];

    return (
        <div className="p-6 bg-bg-main min-h-screen">
            <DetailHeader
                backTo="/tiem-nang"
                title={lead.name}
                badge={
                    <span className={`inline-block px-2 py-0.5 rounded text-sm font-medium ${STATUS_COLORS[lead.status] ?? 'bg-gray-100 text-gray-600'}`}>
                        {STATUS_LABELS[lead.status] ?? lead.status}
                    </span>
                }
                meta={[
                    { label: 'Mã', value: lead.code },
                    { label: 'Công ty', value: lead.companyName },
                    { label: 'Người phụ trách', value: lead.ownerName },
                ]}
                actions={
                    <ActionButton variant="outline" icon={FiEdit2} onClick={() => setEditOpen(true)}>Sửa</ActionButton>
                }
            />

            <div className="grid grid-cols-1 lg:grid-cols-3 gap-4">
                <aside className="lg:col-span-1">
                    <InfoCard><LeadInfoPanel lead={lead} /></InfoCard>
                </aside>
                <main className="lg:col-span-2">
                    <StatCards stats={stats} />
                    <div className="bg-white rounded-card shadow-sm">
                        <Tabs tabs={tabs} active={activeTab} onChange={setActiveTab} />
                        <div className="p-4">
                            {activeTab === 'activities' && <Timeline group={related?.activities} />}
                            {activeTab === 'opportunities' && (
                                <RelatedTable group={related?.opportunities} columns={OPPORTUNITY_COLUMNS} module="opportunity"
                                    emptyText="Tiềm năng chưa chuyển đổi thành cơ hội." />
                            )}
                        </div>
                    </div>
                </main>
            </div>

            {editOpen && <LeadEditModal item={lead} onClose={() => setEditOpen(false)} />}
        </div>
    );
};

export default LeadDetailPage;
