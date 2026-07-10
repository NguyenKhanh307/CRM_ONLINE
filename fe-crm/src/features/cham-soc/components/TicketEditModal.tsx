import { useEffect, useRef, useState, type FormEvent } from 'react';
import { ModalFooter } from '@/shared/components/ModalFooter';
import { FiX } from 'react-icons/fi';
import { useFormKeyboardNav } from '@/shared/keyboard/useFormKeyboardNav';
import { useConfirm } from '@/shared/confirm/useConfirm';
import { SearchableSelect, type SelectOption } from '@/shared/components/SearchableSelect';
import { FieldRow } from '@/shared/components/form/FieldRow';
import { inputCls } from '@/shared/components/form/formStyles';
import { useAlert } from '@/shared/alert/useAlert';
import { useUpdateTicket } from '../hooks/useUpdateTicket';
import { TYPE_OPTIONS, CHANNEL_OPTIONS, PRIORITY_OPTIONS, REASON_OPTIONS } from '../config/ticketEnums';
import type {
    TicketResult, UpdateTicketPayload, TicketType, TicketChannel, TicketPriority, ReturnReason,
} from '../types/ticketTypes';

interface Props {
    ticket: TicketResult | null;
    customerOptions: SelectOption[];
    contactOptions: SelectOption[];
    userOptions: SelectOption[];
    productOptions: SelectOption[];
    onClose: () => void;
}

interface FormState {
    type: string; subject: string; description: string; channel: string; priority: string; reason: string;
    customerId: string; contactId: string; invoiceId: string; productId: string; assignedUserId: string;
}

const toState = (t: TicketResult): FormState => ({
    type: t.type, subject: t.subject, description: t.description ?? '', channel: t.channel, priority: t.priority,
    reason: t.reason ?? '', customerId: t.customerId ? String(t.customerId) : '',
    contactId: t.contactId ? String(t.contactId) : '', invoiceId: t.invoiceId ? String(t.invoiceId) : '',
    productId: t.productId ? String(t.productId) : '', assignedUserId: t.assignedUserId ? String(t.assignedUserId) : '',
});

/** Modal chỉnh sửa thông tin phiếu (KHÔNG đổi trạng thái — trạng thái đổi qua nút hành động). */
export function TicketEditModal({ ticket, customerOptions, contactOptions, userOptions, productOptions, onClose }: Props) {
    const { showAlert } = useAlert();
    const { confirmSave } = useConfirm();
    const { mutate, isPending } = useUpdateTicket();
    const [form, setForm] = useState<FormState | null>(null);

    useEffect(() => { setForm(ticket ? toState(ticket) : null); }, [ticket]);

    const formRef = useRef<HTMLFormElement>(null);
    useFormKeyboardNav(formRef, {
        onSubmit: () => formRef.current?.requestSubmit(),
        onCancel: onClose,
        enabled: !!ticket && !!form,
    });

    if (!ticket || !form) return null;
    const set = (p: Partial<FormState>) => setForm((s) => (s ? { ...s, ...p } : s));

    const submit = async (e: FormEvent) => {
        e.preventDefault();
        if (!form.subject.trim()) { showAlert('Tiêu đề không được để trống'); return; }
        if (!(await confirmSave('phiếu hỗ trợ'))) return;
        const payload: UpdateTicketPayload = {
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
        };
        mutate({ id: ticket.id, payload }, {
            onSuccess: () => { showAlert('Đã cập nhật phiếu'); onClose(); },
            onError: (err: unknown) => showAlert((err as { response?: { data?: { message?: string } } })?.response?.data?.message ?? 'Không lưu được'),
        });
    };

    return (
        <div className="fixed inset-0 z-[9999] flex items-center justify-center bg-black/40" onClick={onClose}>
            <form ref={formRef} onSubmit={submit} className="bg-white rounded-card shadow-lg w-full max-w-2xl mx-4 max-h-[90vh] overflow-y-auto" onClick={(e) => e.stopPropagation()}>
                <div className="flex items-center justify-between px-5 py-4 border-b border-gray-200">
                    <h2 className="text-lg font-semibold text-text-main">Sửa phiếu {ticket.code}</h2>
                    <button type="button" onClick={onClose} className="p-1 rounded hover:bg-gray-100 text-gray-500"><FiX size={18} /></button>
                </div>
                <div className="px-5 py-4 space-y-4">
                    <FieldRow label="Tiêu đề" required>
                        <input type="text" value={form.subject} onChange={(e) => set({ subject: e.target.value })} className={inputCls} />
                    </FieldRow>
                    <FieldRow label="Loại">
                        <SearchableSelect value={form.type} onChange={(v) => set({ type: v })} options={TYPE_OPTIONS} />
                    </FieldRow>
                    <FieldRow label="Kênh">
                        <SearchableSelect value={form.channel} onChange={(v) => set({ channel: v })} options={CHANNEL_OPTIONS} />
                    </FieldRow>
                    <FieldRow label="Ưu tiên">
                        <SearchableSelect value={form.priority} onChange={(v) => set({ priority: v })} options={PRIORITY_OPTIONS} />
                    </FieldRow>
                    <FieldRow label="Lý do">
                        <SearchableSelect value={form.reason} onChange={(v) => set({ reason: v })} options={REASON_OPTIONS} />
                    </FieldRow>
                    <FieldRow label="Khách hàng">
                        <SearchableSelect value={form.customerId} onChange={(v) => set({ customerId: v })} options={customerOptions} />
                    </FieldRow>
                    <FieldRow label="Liên hệ">
                        <SearchableSelect value={form.contactId} onChange={(v) => set({ contactId: v })} options={contactOptions} />
                    </FieldRow>
                    <FieldRow label="Sản phẩm">
                        <SearchableSelect value={form.productId} onChange={(v) => set({ productId: v })} options={productOptions} />
                    </FieldRow>
                    <FieldRow label="Người xử lý">
                        <SearchableSelect value={form.assignedUserId} onChange={(v) => set({ assignedUserId: v })} options={userOptions} />
                    </FieldRow>
                    <FieldRow label="Hóa đơn (ID)">
                        <input type="number" value={form.invoiceId} onChange={(e) => set({ invoiceId: e.target.value })} className={inputCls} />
                    </FieldRow>
                    <FieldRow label="Mô tả" alignTop>
                        <textarea rows={3} value={form.description} onChange={(e) => set({ description: e.target.value })} className={`${inputCls} resize-none`} />
                    </FieldRow>
                </div>
                <ModalFooter onCancel={onClose} saving={isPending} className="flex justify-end gap-1.5 px-5 py-4 border-t border-gray-100" />
            </form>
        </div>
    );
}
