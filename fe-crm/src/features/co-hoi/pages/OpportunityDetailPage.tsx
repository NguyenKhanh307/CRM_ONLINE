import { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { FiEdit2, FiFileText } from 'react-icons/fi';
import { ActionButton } from '@/shared/components/ActionButton';
import { DetailHeader } from '@/shared/components/detail/DetailHeader';
import { Tabs, type TabDef } from '@/shared/components/detail/Tabs';
import { RelatedTable } from '@/shared/components/detail/RelatedTable';
import { Timeline } from '@/shared/components/detail/Timeline';
import { INVOICE_COLUMNS, ORDER_COLUMNS, QUOTATION_COLUMNS } from '@/shared/components/detail/relatedColumns';
import { useAlert } from '@/shared/alert/useAlert';
import { formatCurrency } from '@/shared/utils/number';
import { useOpportunityDetail } from '../hooks/useOpportunityDetail';
import { useOpportunityRelated } from '../hooks/useOpportunityRelated';
import { useCreateQuotationFromOpportunity } from '../hooks/useCreateQuotationFromOpportunity';
import { OpportunityInfoPanel } from '../components/OpportunityInfoPanel';
import { OpportunityEditModal } from '../components/OpportunityEditModal';

const STATUS_LABELS: Record<string, string> = { open: 'Đang mở', won: 'Đã thắng', lost: 'Đã thua' };
const STATUS_COLORS: Record<string, string> = {
    open: 'bg-blue-100 text-blue-700', won: 'bg-green-100 text-green-700', lost: 'bg-red-100 text-red-600',
};

type TabKey = 'overview' | 'quotations' | 'orders' | 'invoices' | 'activities';

/** Trang chi tiết 360° Cơ hội — thông tin + dòng hàng + báo giá/đơn/hóa đơn sinh ra + hoạt động. */
const OpportunityDetailPage = () => {
    const { id } = useParams<{ id: string }>();
    const opportunityId = Number(id);
    const navigate = useNavigate();
    const { showAlert } = useAlert();
    const { data: opportunity, isLoading } = useOpportunityDetail(opportunityId);
    const { data: related } = useOpportunityRelated(opportunityId);
    const { mutate: createQuotation, isPending } = useCreateQuotationFromOpportunity();
    const [activeTab, setActiveTab] = useState<TabKey>('overview');
    const [editOpen, setEditOpen] = useState(false);

    if (isLoading) return <div className="p-6 text-gray-400">Đang tải...</div>;
    if (!opportunity) return <div className="p-6 text-gray-500">Không tìm thấy cơ hội.</div>;

    const tabs: TabDef<TabKey>[] = [
        { key: 'overview', label: 'Tổng quan' },
        { key: 'quotations', label: 'Báo giá', count: related?.quotations.total },
        { key: 'orders', label: 'Đơn hàng', count: related?.orders.total },
        { key: 'invoices', label: 'Hóa đơn', count: related?.invoices.total },
        { key: 'activities', label: 'Hoạt động', count: related?.activities.total },
    ];

    const handleCreateQuotation = () => {
        createQuotation(opportunityId, {
            onSuccess: () => {
                showAlert('Đã tạo báo giá từ cơ hội này.');
                navigate('/bao-gia');
            },
        });
    };

    return (
        <div className="p-6 bg-bg-main">
            <DetailHeader
                backTo="/co-hoi"
                title={opportunity.name}
                badge={
                    <span className={`inline-block px-2 py-0.5 rounded text-sm font-medium ${STATUS_COLORS[opportunity.status] ?? 'bg-gray-100 text-gray-600'}`}>
                        {STATUS_LABELS[opportunity.status] ?? opportunity.status}
                    </span>
                }
                meta={[
                    { label: 'Mã', value: opportunity.code },
                    { label: 'Khách hàng', value: opportunity.customerName },
                    { label: 'Giai đoạn', value: opportunity.stageName },
                    { label: 'Giá trị', value: opportunity.amount != null ? formatCurrency(opportunity.amount) : null },
                    { label: 'Người phụ trách', value: opportunity.ownerName },
                ]}
                actions={
                    <>
                        <ActionButton variant="outline" icon={FiFileText} onClick={handleCreateQuotation} disabled={isPending}>
                            Tạo báo giá
                        </ActionButton>
                        <ActionButton variant="outline" icon={FiEdit2} onClick={() => setEditOpen(true)}>
                            Sửa
                        </ActionButton>
                    </>
                }
            />

            <div className="bg-white rounded-card shadow-sm">
                <Tabs tabs={tabs} active={activeTab} onChange={setActiveTab} />
                <div className="p-4">
                    {activeTab === 'overview' && <OpportunityInfoPanel opportunity={opportunity} />}
                    {activeTab === 'quotations' && (
                        <RelatedTable group={related?.quotations} columns={QUOTATION_COLUMNS} module="quotation"
                            emptyText="Cơ hội chưa có báo giá nào." />
                    )}
                    {activeTab === 'orders' && (
                        <RelatedTable group={related?.orders} columns={ORDER_COLUMNS} module="order"
                            emptyText="Cơ hội chưa có đơn hàng nào." />
                    )}
                    {activeTab === 'invoices' && (
                        <RelatedTable group={related?.invoices} columns={INVOICE_COLUMNS} module="invoice"
                            emptyText="Cơ hội chưa có hóa đơn nào." />
                    )}
                    {activeTab === 'activities' && <Timeline group={related?.activities} />}
                </div>
            </div>

            {editOpen && <OpportunityEditModal item={opportunity} onClose={() => setEditOpen(false)} />}
        </div>
    );
};

export default OpportunityDetailPage;
