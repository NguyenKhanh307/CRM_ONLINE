import { useState } from 'react';
import { useParams } from 'react-router-dom';
import { FiEdit2 } from 'react-icons/fi';
import { ActionButton } from '@/shared/components/ActionButton';
import { DetailHeader } from '@/shared/components/detail/DetailHeader';
import { Tabs, type TabDef } from '@/shared/components/detail/Tabs';
import { RelatedTable } from '@/shared/components/detail/RelatedTable';
import { Timeline } from '@/shared/components/detail/Timeline';
import {
    CONTACT_COLUMNS, INVOICE_COLUMNS, OPPORTUNITY_COLUMNS,
    ORDER_COLUMNS, QUOTATION_COLUMNS, TICKET_COLUMNS,
} from '@/shared/components/detail/relatedColumns';
import { useCustomerDetail } from '../hooks/useCustomerDetail';
import { useCustomerRelated } from '../hooks/useCustomerRelated';
import { CustomerInfoPanel } from '../components/CustomerInfoPanel';
import { CustomerEditModal } from '../components/CustomerEditModal';
import { CustomerSharesTab } from '../components/tabs/CustomerSharesTab';

const STATUS_LABELS: Record<string, string> = { active: 'Hoạt động', inactive: 'Ngừng', potential: 'Tiềm năng' };
const STATUS_COLORS: Record<string, string> = {
    active: 'bg-green-100 text-green-700', inactive: 'bg-red-100 text-red-600', potential: 'bg-yellow-100 text-yellow-700',
};

type TabKey = 'overview' | 'contacts' | 'opportunities' | 'quotations' | 'orders' | 'invoices' | 'tickets' | 'activities' | 'shares';

// trang chi tiết 360° Khách hàng — thông tin + toàn bộ bản ghi liên quan + dòng thời gian
const CustomerDetailPage = () => {
    const { id } = useParams<{ id: string }>();
    const customerId = Number(id);
    const { data: customer, isLoading } = useCustomerDetail(customerId);
    const { data: related } = useCustomerRelated(customerId);
    const [activeTab, setActiveTab] = useState<TabKey>('overview');
    const [editOpen, setEditOpen] = useState(false);

    if (isLoading) return <div className="p-6 text-gray-400">Đang tải...</div>;
    if (!customer) return <div className="p-6 text-gray-500">Không tìm thấy khách hàng.</div>;

    const tabs: TabDef<TabKey>[] = [
        { key: 'overview', label: 'Tổng quan' },
        { key: 'contacts', label: 'Liên hệ', count: related?.contacts.total },
        { key: 'opportunities', label: 'Cơ hội', count: related?.opportunities.total },
        { key: 'quotations', label: 'Báo giá', count: related?.quotations.total },
        { key: 'orders', label: 'Đơn hàng', count: related?.orders.total },
        { key: 'invoices', label: 'Hóa đơn', count: related?.invoices.total },
        { key: 'tickets', label: 'Chăm sóc', count: related?.tickets.total },
        { key: 'activities', label: 'Hoạt động', count: related?.activities.total },
        { key: 'shares', label: 'Chia sẻ' },
    ];

    return (
        <div className="p-6 bg-bg-main">
            <DetailHeader
                backTo="/khach-hang"
                title={customer.name}
                badge={
                    <span className={`inline-block px-2 py-0.5 rounded text-sm font-medium ${STATUS_COLORS[customer.status] ?? 'bg-gray-100 text-gray-600'}`}>
                        {STATUS_LABELS[customer.status] ?? customer.status}
                    </span>
                }
                meta={[
                    { label: 'Mã', value: customer.code },
                    { label: 'Mã số thuế', value: customer.taxCode },
                    { label: 'Điện thoại', value: customer.phone },
                    { label: 'Người phụ trách', value: customer.ownerName },
                ]}
                actions={
                    <ActionButton variant="outline" icon={FiEdit2} onClick={() => setEditOpen(true)}>
                        Sửa
                    </ActionButton>
                }
            />

            <div className="bg-white rounded-card shadow-sm">
                <Tabs tabs={tabs} active={activeTab} onChange={setActiveTab} />
                <div className="p-4">
                    {activeTab === 'overview' && <CustomerInfoPanel customer={customer} />}
                    {activeTab === 'contacts' && (
                        <RelatedTable group={related?.contacts} columns={CONTACT_COLUMNS} module="contact"
                            emptyText="Khách hàng chưa có liên hệ nào." />
                    )}
                    {activeTab === 'opportunities' && (
                        <RelatedTable group={related?.opportunities} columns={OPPORTUNITY_COLUMNS} module="opportunity"
                            emptyText="Khách hàng chưa có cơ hội nào." />
                    )}
                    {activeTab === 'quotations' && (
                        <RelatedTable group={related?.quotations} columns={QUOTATION_COLUMNS} module="quotation"
                            emptyText="Khách hàng chưa có báo giá nào." />
                    )}
                    {activeTab === 'orders' && (
                        <RelatedTable group={related?.orders} columns={ORDER_COLUMNS} module="order"
                            emptyText="Khách hàng chưa có đơn hàng nào." />
                    )}
                    {activeTab === 'invoices' && (
                        <RelatedTable group={related?.invoices} columns={INVOICE_COLUMNS} module="invoice"
                            emptyText="Khách hàng chưa có hóa đơn nào." />
                    )}
                    {activeTab === 'tickets' && (
                        <RelatedTable group={related?.tickets} columns={TICKET_COLUMNS} module="ticket"
                            emptyText="Khách hàng chưa có phiếu chăm sóc nào." />
                    )}
                    {activeTab === 'activities' && <Timeline group={related?.activities} />}
                    {activeTab === 'shares' && <CustomerSharesTab customerId={customerId} />}
                </div>
            </div>

            {editOpen && <CustomerEditModal item={customer} onClose={() => setEditOpen(false)} />}
        </div>
    );
};

export default CustomerDetailPage;
