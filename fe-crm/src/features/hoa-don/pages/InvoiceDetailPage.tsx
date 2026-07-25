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
import { TICKET_COLUMNS } from '@/shared/components/detail/relatedColumns';
import { formatCurrency, formatNumber } from '@/shared/utils/number';
import { useInvoiceDetail } from '../hooks/useInvoiceDetail';
import { useInvoiceRelated } from '../hooks/useInvoiceRelated';
import { InvoiceInfoPanel } from '../components/InvoiceInfoPanel';
import { InvoiceEditModal } from '../components/InvoiceEditModal';
import { PaymentSchedulesTable } from '../components/PaymentSchedulesTable';

const STATUS_LABELS: Record<string, string> = {
    draft: 'Nháp', sent: 'Đã gửi', partially_paid: 'Thanh toán một phần', paid: 'Đã thanh toán', cancelled: 'Đã hủy',
};
const STATUS_COLORS: Record<string, string> = {
    draft: 'bg-gray-100 text-gray-600', sent: 'bg-blue-100 text-blue-700', partially_paid: 'bg-yellow-100 text-yellow-700',
    paid: 'bg-green-100 text-green-700', cancelled: 'bg-red-100 text-red-600',
};
const PAYMENT_LABELS: Record<string, string> = {
    unpaid: 'Chưa thanh toán', partial: 'Thanh toán một phần', paid: 'Đã thanh toán', overdue: 'Quá hạn',
};

type TabKey = 'payments' | 'tickets' | 'activities';

/** Trang chi tiết Hóa đơn — 2 cột: thông tin + phiếu chăm sóc + hoạt động. */
const InvoiceDetailPage = () => {
    const { id } = useParams<{ id: string }>();
    const invoiceId = Number(id);
    const { data: invoice, isLoading } = useInvoiceDetail(invoiceId);
    const { data: related } = useInvoiceRelated(invoiceId);
    const [activeTab, setActiveTab] = useState<TabKey>('payments');
    const [editOpen, setEditOpen] = useState(false);

    if (isLoading) return <div className="p-6 text-gray-400">Đang tải...</div>;
    if (!invoice) return <div className="p-6 text-gray-500">Không tìm thấy hóa đơn.</div>;

    const tabs: TabDef<TabKey>[] = [
        // Đợt thanh toán để ở đây (không phải trong modal sửa) vì phát hành xong hóa đơn bị khóa,
        // mà status sent → partially_paid → paid chỉ suy ra được từ các đợt thanh toán.
        { key: 'payments', label: 'Đợt thanh toán' },
        { key: 'tickets', label: 'Chăm sóc', count: related?.tickets.total },
        { key: 'activities', label: 'Hoạt động', count: related?.activities.total },
    ];

    const paid = invoice.paymentStatus === 'paid';
    const stats: StatCard[] = [
        { label: 'Tổng tiền', value: formatCurrency(invoice.total ?? 0), tone: 'success' },
        { label: 'Thanh toán', value: PAYMENT_LABELS[invoice.paymentStatus] ?? invoice.paymentStatus, tone: paid ? 'success' : 'warning' },
        { label: 'Số phiếu CS', value: formatNumber(related?.tickets.total ?? 0) },
    ];

    return (
        <div className="p-6 bg-bg-main">
            <DetailHeader
                backTo="/hoa-don"
                title={invoice.code}
                badge={
                    <span className={`inline-block px-2 py-0.5 rounded text-sm font-medium ${STATUS_COLORS[invoice.status] ?? 'bg-gray-100 text-gray-600'}`}>
                        {STATUS_LABELS[invoice.status] ?? invoice.status}
                    </span>
                }
                meta={[
                    { label: 'Khách hàng', value: invoice.customerName },
                    { label: 'Báo giá nguồn', value: invoice.quotationCode },
                    { label: 'Tổng tiền', value: invoice.total != null ? formatCurrency(invoice.total) : null },
                    { label: 'Người phụ trách', value: invoice.ownerName },
                ]}
                actions={
                    <ActionButton variant="outline" icon={FiEdit2} onClick={() => setEditOpen(true)}>Sửa</ActionButton>
                }
            />

            <div className="grid grid-cols-1 lg:grid-cols-3 gap-4">
                <aside className="lg:col-span-1">
                    <InfoCard><InvoiceInfoPanel invoice={invoice} /></InfoCard>
                </aside>
                <main className="lg:col-span-2">
                    <StatCards stats={stats} />
                    <div className="bg-white rounded-card shadow-sm">
                        <Tabs tabs={tabs} active={activeTab} onChange={setActiveTab} />
                        <div className="p-4">
                            {activeTab === 'payments' && <PaymentSchedulesTable invoiceId={invoiceId} />}
                            {activeTab === 'tickets' && (
                                <RelatedTable group={related?.tickets} columns={TICKET_COLUMNS} module="ticket"
                                    emptyText="Hóa đơn chưa có phiếu chăm sóc nào." />
                            )}
                            {activeTab === 'activities' && <Timeline group={related?.activities} />}
                        </div>
                    </div>
                </main>
            </div>

            {editOpen && <InvoiceEditModal item={invoice} onClose={() => setEditOpen(false)} />}
        </div>
    );
};

export default InvoiceDetailPage;
