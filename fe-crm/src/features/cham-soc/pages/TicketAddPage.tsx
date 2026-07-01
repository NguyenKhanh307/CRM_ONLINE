import { useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { SearchableSelect } from '@/shared/components/SearchableSelect';
import { FormPageHeader } from '@/shared/components/form/FormPageHeader';
import { FormSection } from '@/shared/components/form/FormSection';
import { FieldRow } from '@/shared/components/form/FieldRow';
import { inputCls } from '@/shared/components/form/formStyles';
import { useAlert } from '@/shared/alert/useAlert';
import { useAuth } from '@/core/auth/useAuth';
import { useActiveUsers } from '@/features/users/hooks/useActiveUsers';
import { useCustomerList } from '@/features/khach-hang/hooks/useCustomerList';
import { useContactList } from '@/features/lien-he/hooks/useContactList';
import { useProductList } from '@/features/san-pham/hooks/useProductList';
import { useCreateTicket } from '../hooks/useCreateTicket';
import { ReturnItemsTable, type ReturnRow, emptyReturnRow, toReturnItemPayloads } from '../components/ReturnItemsTable';
import { TYPE_OPTIONS, CHANNEL_OPTIONS, PRIORITY_OPTIONS, REASON_OPTIONS } from '../config/ticketEnums';
import type { CreateTicketPayload, TicketType, TicketChannel, TicketPriority, ReturnReason } from '../types/ticketTypes';

interface HeaderState {
    code: string; type: string; subject: string; priority: string; channel: string; reason: string;
    customerId: string; contactId: string; productId: string; invoiceId: string; assignedUserId: string; description: string;
}

const initialState = (assignedUserId: string): HeaderState => ({
    code: '', type: 'support', subject: '', priority: 'medium', channel: 'web', reason: '',
    customerId: '', contactId: '', productId: '', invoiceId: '', assignedUserId, description: '',
});

/** Trang thêm phiếu hỗ trợ mới — hiện bảng dòng hàng trả/đổi khi loại là trả/đổi. */
const TicketAddPage = () => {
    const navigate = useNavigate();
    const { showAlert } = useAlert();
    const { user } = useAuth();
    const defaultUserId = user ? String(user.id) : '';
    const [form, setForm] = useState<HeaderState>(() => initialState(defaultUserId));
    const [returnRows, setReturnRows] = useState<ReturnRow[]>([emptyReturnRow()]);
    const { mutate, isPending } = useCreateTicket();

    const { data: users = [] } = useActiveUsers();
    const { data: customers = [] } = useCustomerList();
    const { data: contacts = [] } = useContactList();
    const { data: products = [] } = useProductList();

    const userOptions = useMemo(() => users.map((u) => ({ value: String(u.id), label: u.fullName })), [users]);
    const customerOptions = useMemo(() => customers.map((c) => ({ value: String(c.id), label: c.name })), [customers]);
    const contactOptions = useMemo(() => contacts.map((c) => ({ value: String(c.id), label: c.fullName })), [contacts]);
    const productOptions = useMemo(() => products.map((p) => ({ value: String(p.id), label: `${p.sku} — ${p.name}` })), [products]);

    const isReturn = form.type === 'return' || form.type === 'exchange';
    const set = (patch: Partial<HeaderState>) => setForm((p) => ({ ...p, ...patch }));
    const reset = () => { setForm(initialState(defaultUserId)); setReturnRows([emptyReturnRow()]); };

    const submit = (andNew: boolean) => {
        if (!form.code.trim()) { showAlert('Mã phiếu không được để trống'); return; }
        if (!form.subject.trim()) { showAlert('Tiêu đề không được để trống'); return; }
        const payload: CreateTicketPayload = {
            code: form.code.trim(),
            type: form.type as TicketType,
            subject: form.subject.trim(),
            description: form.description || null,
            customerId: form.customerId ? Number(form.customerId) : null,
            contactId: form.contactId ? Number(form.contactId) : null,
            invoiceId: form.invoiceId ? Number(form.invoiceId) : null,
            productId: form.productId ? Number(form.productId) : null,
            channel: form.channel as TicketChannel,
            priority: form.priority as TicketPriority,
            reason: (form.reason || null) as ReturnReason | null,
            assignedUserId: form.assignedUserId ? Number(form.assignedUserId) : null,
            returnItems: isReturn ? toReturnItemPayloads(returnRows) : [],
        };
        mutate(payload, {
            onSuccess: () => {
                if (andNew) { reset(); showAlert('Đã tạo phiếu thành công'); }
                else navigate('/cham-soc');
            },
            onError: (err: unknown) => showAlert((err as { response?: { data?: { message?: string } } })?.response?.data?.message ?? 'Có lỗi xảy ra khi lưu phiếu'),
        });
    };

    return (
        <div className="p-6 bg-bg-main min-h-[calc(100vh-50px)]">
            <FormPageHeader title="Thêm phiếu hỗ trợ" saving={isPending}
                onCancel={() => navigate(-1)} onSave={() => submit(false)} onSaveAndNew={() => submit(true)} />

            <div className="bg-white rounded-card shadow-sm p-6 space-y-8">
                <FormSection title="Thông tin phiếu">
                    <div className="grid grid-cols-2 gap-x-10 gap-y-4">
                        <div className="space-y-4">
                            <FieldRow label="Mã phiếu" required>
                                <input type="text" value={form.code} onChange={(e) => set({ code: e.target.value })} className={inputCls} />
                            </FieldRow>
                            <FieldRow label="Tiêu đề" required>
                                <input type="text" value={form.subject} onChange={(e) => set({ subject: e.target.value })} className={inputCls} />
                            </FieldRow>
                            <FieldRow label="Loại yêu cầu">
                                <SearchableSelect value={form.type} onChange={(v) => set({ type: v })} options={TYPE_OPTIONS} />
                            </FieldRow>
                            <FieldRow label="Độ ưu tiên">
                                <SearchableSelect value={form.priority} onChange={(v) => set({ priority: v })} options={PRIORITY_OPTIONS} />
                            </FieldRow>
                            <FieldRow label="Kênh tiếp nhận">
                                <SearchableSelect value={form.channel} onChange={(v) => set({ channel: v })} options={CHANNEL_OPTIONS} />
                            </FieldRow>
                            <FieldRow label="Lý do">
                                <SearchableSelect value={form.reason} onChange={(v) => set({ reason: v })} options={REASON_OPTIONS} />
                            </FieldRow>
                        </div>
                        <div className="space-y-4">
                            <FieldRow label="Khách hàng">
                                <SearchableSelect value={form.customerId} onChange={(v) => set({ customerId: v })} options={customerOptions} />
                            </FieldRow>
                            <FieldRow label="Liên hệ">
                                <SearchableSelect value={form.contactId} onChange={(v) => set({ contactId: v })} options={contactOptions} />
                            </FieldRow>
                            <FieldRow label="Sản phẩm">
                                <SearchableSelect value={form.productId} onChange={(v) => set({ productId: v })} options={productOptions} />
                            </FieldRow>
                            <FieldRow label="Hóa đơn (ID)">
                                <input type="number" value={form.invoiceId} onChange={(e) => set({ invoiceId: e.target.value })} className={inputCls} />
                            </FieldRow>
                            <FieldRow label="Người xử lý">
                                <SearchableSelect value={form.assignedUserId} onChange={(v) => set({ assignedUserId: v })} options={userOptions} />
                            </FieldRow>
                        </div>
                    </div>
                </FormSection>

                {isReturn && (
                    <FormSection title="Hàng trả / đổi">
                        <ReturnItemsTable rows={returnRows} onChange={setReturnRows} productOptions={productOptions} />
                    </FormSection>
                )}

                <FormSection title="Mô tả">
                    <FieldRow label="Nội dung" alignTop>
                        <textarea rows={3} value={form.description} onChange={(e) => set({ description: e.target.value })} className={`${inputCls} resize-none`} />
                    </FieldRow>
                </FormSection>
            </div>
        </div>
    );
};

export default TicketAddPage;
