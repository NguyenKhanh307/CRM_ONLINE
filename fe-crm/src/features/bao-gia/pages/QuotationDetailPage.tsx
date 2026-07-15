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
import { INVOICE_COLUMNS, ORDER_COLUMNS } from '@/shared/components/detail/relatedColumns';
import { formatCurrency, formatNumber } from '@/shared/utils/number';
import { useQuotationDetail } from '../hooks/useQuotationDetail';
import { useQuotationRelated } from '../hooks/useQuotationRelated';
import { QuotationInfoPanel } from '../components/QuotationInfoPanel';
import { QuotationEditModal } from '../components/QuotationEditModal';

const STATUS_LABELS: Record<string, string> = {
    draft: 'Nháp', pending: 'Chờ duyệt', approved: 'Đã duyệt', rejected: 'Từ chối',
    sent: 'Đã gửi', accepted: 'Khách chấp nhận', expired: 'Hết hạn',
};
const STATUS_COLORS: Record<string, string> = {
    draft: 'bg-gray-100 text-gray-600', pending: 'bg-yellow-100 text-yellow-700', approved: 'bg-green-100 text-green-700',
    accepted: 'bg-green-100 text-green-700', rejected: 'bg-red-100 text-red-600', sent: 'bg-blue-100 text-blue-700',
    expired: 'bg-orange-100 text-orange-700',
};

type TabKey = 'orders' | 'invoices' | 'activities';

/** Trang chi tiết Báo giá — 2 cột: thông tin + đơn hàng/hóa đơn phát sinh + hoạt động. */
const QuotationDetailPage = () => {
    const { id } = useParams<{ id: string }>();
    const quotationId = Number(id);
    const { data: quotation, isLoading } = useQuotationDetail(quotationId);
    const { data: related } = useQuotationRelated(quotationId);
    const [activeTab, setActiveTab] = useState<TabKey>('orders');
    const [editOpen, setEditOpen] = useState(false);

    if (isLoading) return <div className="p-6 text-gray-400">Đang tải...</div>;
    if (!quotation) return <div className="p-6 text-gray-500">Không tìm thấy báo giá.</div>;

    const tabs: TabDef<TabKey>[] = [
        { key: 'orders', label: 'Đơn hàng', count: related?.orders.total },
        { key: 'invoices', label: 'Hóa đơn', count: related?.invoices.total },
        { key: 'activities', label: 'Hoạt động', count: related?.activities.total },
    ];

    const stats: StatCard[] = [
        { label: 'Tổng tiền', value: formatCurrency(quotation.total ?? 0), tone: 'success' },
        { label: 'Số đơn hàng', value: formatNumber(related?.orders.total ?? 0) },
        { label: 'Số hóa đơn', value: formatNumber(related?.invoices.total ?? 0) },
    ];

    return (
        <div className="p-6 bg-bg-main min-h-screen">
            <DetailHeader
                backTo="/bao-gia"
                title={quotation.code}
                badge={
                    <span className={`inline-block px-2 py-0.5 rounded text-sm font-medium ${STATUS_COLORS[quotation.status] ?? 'bg-gray-100 text-gray-600'}`}>
                        {STATUS_LABELS[quotation.status] ?? quotation.status}
                    </span>
                }
                meta={[
                    { label: 'Khách hàng', value: quotation.customerName },
                    { label: 'Cơ hội', value: quotation.opportunityName },
                    { label: 'Tổng tiền', value: quotation.total != null ? formatCurrency(quotation.total) : null },
                    { label: 'Người phụ trách', value: quotation.ownerName },
                ]}
                actions={
                    <ActionButton variant="outline" icon={FiEdit2} onClick={() => setEditOpen(true)}>Sửa</ActionButton>
                }
            />

            <div className="grid grid-cols-1 lg:grid-cols-3 gap-4">
                <aside className="lg:col-span-1">
                    <InfoCard><QuotationInfoPanel quotation={quotation} /></InfoCard>
                </aside>
                <main className="lg:col-span-2">
                    <StatCards stats={stats} />
                    <div className="bg-white rounded-card shadow-sm">
                        <Tabs tabs={tabs} active={activeTab} onChange={setActiveTab} />
                        <div className="p-4">
                            {activeTab === 'orders' && (
                                <RelatedTable group={related?.orders} columns={ORDER_COLUMNS} module="order"
                                    emptyText="Báo giá chưa phát sinh đơn hàng." />
                            )}
                            {activeTab === 'invoices' && (
                                <RelatedTable group={related?.invoices} columns={INVOICE_COLUMNS} module="invoice"
                                    emptyText="Báo giá chưa phát sinh hóa đơn." />
                            )}
                            {activeTab === 'activities' && <Timeline group={related?.activities} />}
                        </div>
                    </div>
                </main>
            </div>

            {editOpen && <QuotationEditModal item={quotation} onClose={() => setEditOpen(false)} />}
        </div>
    );
};

export default QuotationDetailPage;
