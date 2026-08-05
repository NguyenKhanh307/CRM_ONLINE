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
import { INVOICE_COLUMNS } from '@/shared/components/detail/relatedColumns';
import { formatCurrency, formatNumber } from '@/shared/utils/number';
import { useOrderDetail } from '../hooks/useOrderDetail';
import { useOrderRelated } from '../hooks/useOrderRelated';
import { OrderInfoPanel } from '../components/OrderInfoPanel';
import { OrderEditModal } from '../components/OrderEditModal';

const STATUS_LABELS: Record<string, string> = {
    draft: 'Nháp', confirmed: 'Đã xác nhận', processing: 'Đang xử lý', completed: 'Hoàn tất', cancelled: 'Đã hủy',
};
const STATUS_COLORS: Record<string, string> = {
    draft: 'bg-gray-100 text-gray-600', confirmed: 'bg-cyan-100 text-cyan-700', processing: 'bg-yellow-100 text-yellow-700',
    completed: 'bg-green-100 text-green-700', cancelled: 'bg-red-100 text-red-600',
};

type TabKey = 'invoices' | 'activities';

// trang chi tiết Đơn hàng — 2 cột: thông tin + hóa đơn xuất ra + hoạt động
const OrderDetailPage = () => {
    const { id } = useParams<{ id: string }>();
    const orderId = Number(id);
    const { data: order, isLoading } = useOrderDetail(orderId);
    const { data: related } = useOrderRelated(orderId);
    const [activeTab, setActiveTab] = useState<TabKey>('invoices');
    const [editOpen, setEditOpen] = useState(false);

    if (isLoading) return <div className="p-6 text-gray-400">Đang tải...</div>;
    if (!order) return <div className="p-6 text-gray-500">Không tìm thấy đơn hàng.</div>;

    const tabs: TabDef<TabKey>[] = [
        { key: 'invoices', label: 'Hóa đơn', count: related?.invoices.total },
        { key: 'activities', label: 'Hoạt động', count: related?.activities.total },
    ];

    const stats: StatCard[] = [
        { label: 'Tổng tiền', value: formatCurrency(order.total ?? 0), tone: 'success' },
        { label: 'Hóa đơn', value: (related?.invoices.total ?? 0) > 0 ? 'Đã xuất' : 'Chưa xuất', tone: (related?.invoices.total ?? 0) > 0 ? 'success' : 'warning' },
        { label: 'Số hoạt động', value: formatNumber(related?.activities.total ?? 0) },
    ];

    return (
        <div className="p-6 bg-bg-main">
            <DetailHeader
                backTo="/don-hang"
                title={order.code}
                badge={
                    <span className={`inline-block px-2 py-0.5 rounded text-sm font-medium ${STATUS_COLORS[order.status] ?? 'bg-gray-100 text-gray-600'}`}>
                        {STATUS_LABELS[order.status] ?? order.status}
                    </span>
                }
                meta={[
                    { label: 'Báo giá nguồn', value: order.quotationCode },
                    { label: 'Tổng tiền', value: order.total != null ? formatCurrency(order.total) : null },
                    { label: 'Người phụ trách', value: order.ownerName },
                ]}
                actions={
                    <ActionButton variant="outline" icon={FiEdit2} onClick={() => setEditOpen(true)}>Sửa</ActionButton>
                }
            />

            <div className="grid grid-cols-1 lg:grid-cols-3 gap-4">
                <aside className="lg:col-span-1">
                    <InfoCard><OrderInfoPanel order={order} /></InfoCard>
                </aside>
                <main className="lg:col-span-2">
                    <StatCards stats={stats} />
                    <div className="bg-white rounded-card shadow-sm">
                        <Tabs tabs={tabs} active={activeTab} onChange={setActiveTab} />
                        <div className="p-4">
                            {activeTab === 'invoices' && (
                                <RelatedTable group={related?.invoices} columns={INVOICE_COLUMNS} module="invoice"
                                    emptyText="Đơn hàng chưa xuất hóa đơn." />
                            )}
                            {activeTab === 'activities' && <Timeline group={related?.activities} />}
                        </div>
                    </div>
                </main>
            </div>

            {editOpen && <OrderEditModal item={order} onClose={() => setEditOpen(false)} />}
        </div>
    );
};

export default OrderDetailPage;
