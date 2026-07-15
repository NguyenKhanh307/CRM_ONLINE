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
import {
    INVOICE_COLUMNS, OPPORTUNITY_COLUMNS, ORDER_COLUMNS, QUOTATION_COLUMNS, TICKET_COLUMNS,
} from '@/shared/components/detail/relatedColumns';
import { formatNumber } from '@/shared/utils/number';
import { useContactDetail } from '../hooks/useContactDetail';
import { useContactRelated } from '../hooks/useContactRelated';
import { ContactInfoPanel } from '../components/ContactInfoPanel';
import { ContactEditModal } from '../components/ContactEditModal';

type TabKey = 'opportunities' | 'quotations' | 'orders' | 'invoices' | 'tickets' | 'activities';

/** Trang chi tiết Liên hệ — 2 cột: thông tin + cơ hội/báo giá/đơn/hóa đơn/phiếu CS/hoạt động. */
const ContactDetailPage = () => {
    const { id } = useParams<{ id: string }>();
    const contactId = Number(id);
    const { data: contact, isLoading } = useContactDetail(contactId);
    const { data: related } = useContactRelated(contactId);
    const [activeTab, setActiveTab] = useState<TabKey>('opportunities');
    const [editOpen, setEditOpen] = useState(false);

    if (isLoading) return <div className="p-6 text-gray-400">Đang tải...</div>;
    if (!contact) return <div className="p-6 text-gray-500">Không tìm thấy liên hệ.</div>;

    const tabs: TabDef<TabKey>[] = [
        { key: 'opportunities', label: 'Cơ hội', count: related?.opportunities.total },
        { key: 'quotations', label: 'Báo giá', count: related?.quotations.total },
        { key: 'orders', label: 'Đơn hàng', count: related?.orders.total },
        { key: 'invoices', label: 'Hóa đơn', count: related?.invoices.total },
        { key: 'tickets', label: 'Chăm sóc', count: related?.tickets.total },
        { key: 'activities', label: 'Hoạt động', count: related?.activities.total },
    ];

    const stats: StatCard[] = [
        { label: 'Số cơ hội', value: formatNumber(related?.opportunities.total ?? 0) },
        { label: 'Số báo giá', value: formatNumber(related?.quotations.total ?? 0) },
        { label: 'Số phiếu CS', value: formatNumber(related?.tickets.total ?? 0) },
    ];

    return (
        <div className="p-6 bg-bg-main min-h-screen">
            <DetailHeader
                backTo="/lien-he"
                title={contact.fullName}
                badge={contact.isPrimary
                    ? <span className="inline-block px-2 py-0.5 rounded text-sm font-medium bg-primary/10 text-primary">Liên hệ chính</span>
                    : undefined}
                meta={[
                    { label: 'Chức danh', value: contact.title },
                    { label: 'Khách hàng', value: contact.customerName },
                    { label: 'Email', value: contact.email },
                    { label: 'Người phụ trách', value: contact.assignedUserName },
                ]}
                actions={
                    <ActionButton variant="outline" icon={FiEdit2} onClick={() => setEditOpen(true)}>Sửa</ActionButton>
                }
            />

            <div className="grid grid-cols-1 lg:grid-cols-3 gap-4">
                <aside className="lg:col-span-1">
                    <InfoCard><ContactInfoPanel contact={contact} /></InfoCard>
                </aside>
                <main className="lg:col-span-2">
                    <StatCards stats={stats} />
                    <div className="bg-white rounded-card shadow-sm">
                        <Tabs tabs={tabs} active={activeTab} onChange={setActiveTab} />
                        <div className="p-4">
                            {activeTab === 'opportunities' && (
                                <RelatedTable group={related?.opportunities} columns={OPPORTUNITY_COLUMNS} module="opportunity"
                                    emptyText="Liên hệ chưa có cơ hội nào." />
                            )}
                            {activeTab === 'quotations' && (
                                <RelatedTable group={related?.quotations} columns={QUOTATION_COLUMNS} module="quotation"
                                    emptyText="Liên hệ chưa có báo giá nào." />
                            )}
                            {activeTab === 'orders' && (
                                <RelatedTable group={related?.orders} columns={ORDER_COLUMNS} module="order"
                                    emptyText="Liên hệ chưa có đơn hàng nào." />
                            )}
                            {activeTab === 'invoices' && (
                                <RelatedTable group={related?.invoices} columns={INVOICE_COLUMNS} module="invoice"
                                    emptyText="Liên hệ chưa có hóa đơn nào." />
                            )}
                            {activeTab === 'tickets' && (
                                <RelatedTable group={related?.tickets} columns={TICKET_COLUMNS} module="ticket"
                                    emptyText="Liên hệ chưa có phiếu chăm sóc nào." />
                            )}
                            {activeTab === 'activities' && <Timeline group={related?.activities} />}
                        </div>
                    </div>
                </main>
            </div>

            {editOpen && <ContactEditModal item={contact} onClose={() => setEditOpen(false)} />}
        </div>
    );
};

export default ContactDetailPage;
