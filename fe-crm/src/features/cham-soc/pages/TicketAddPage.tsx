import { useMemo, useRef, useState } from 'react';
import { useConfirm } from '@/shared/confirm/useConfirm';
import { collectErrors } from '@/shared/utils/validators';
import { useNavigate } from 'react-router-dom';
import { useFormKeyboardNav } from '@/shared/keyboard/useFormKeyboardNav';
import { SearchableSelect } from '@/shared/components/SearchableSelect';
import { FormPageHeader } from '@/shared/components/form/FormPageHeader';
import { FormSection } from '@/shared/components/form/FormSection';
import { FieldRow } from '@/shared/components/form/FieldRow';
import { PrefillHint } from '@/shared/components/form/PrefillHint';
import { fillEmpty, hasFilled } from '@/shared/utils/prefill';
import { fetchPrimaryContactId } from '@/shared/lookup/recordPrefill';
import { RecordPicker } from '@/shared/components/form/RecordPicker';
import { invoiceService } from '@/features/hoa-don/services/invoiceService';
import { inputCls } from '@/shared/components/form/formStyles';
import { useAlert } from '@/shared/alert/useAlert';
import { useAuth } from '@/core/auth/useAuth';
import { useActiveUsers } from '@/features/users/hooks/useActiveUsers';
import { useCustomerList } from '@/features/khach-hang/hooks/useCustomerList';
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
    const { confirmCreate } = useConfirm();
    const { user } = useAuth();
    const defaultUserId = user ? String(user.id) : '';
    const [form, setForm] = useState<HeaderState>(() => initialState(defaultUserId));
    const [returnRows, setReturnRows] = useState<ReturnRow[]>([emptyReturnRow()]);
    const { mutate, isPending } = useCreateTicket();

    const { data: users = [] } = useActiveUsers();
    const { data: customers = [] } = useCustomerList();
    const { data: products = [] } = useProductList();

    const userOptions = useMemo(() => users.map((u) => ({ value: String(u.id), label: u.fullName })), [users]);
    const customerOptions = useMemo(() => customers.map((c) => ({ value: String(c.id), label: c.name })), [customers]);
    const productOptions = useMemo(() => products.map((p) => ({ value: String(p.id), label: `${p.sku} — ${p.name}` })), [products]);

    const isReturn = form.type === 'return' || form.type === 'exchange';
    const [errors, setErrors] = useState<Record<string, string>>({});

    /** Cập nhật form và xóa lỗi của đúng những field vừa gõ. */
    const set = (patch: Partial<HeaderState>) => {
        setForm((p) => ({ ...p, ...patch }));
        setErrors((e) => {
            const next = { ...e };
            Object.keys(patch).forEach((k) => delete next[k]);
            return next;
        });
    };
    const reset = () => { setForm(initialState(defaultUserId)); setReturnRows([emptyReturnRow()]); setPrefillFrom(null); };

    /** Tên khách hàng vừa kéo dữ liệu về — hiện dòng gợi ý dưới ô Khách hàng. */
    const [prefillFrom, setPrefillFrom] = useState<string | null>(null);

    /** Chọn khách hàng → tự điền liên hệ chính (chỉ khi ô liên hệ còn trống). */
    const onPickCustomer = async (v: string) => {
        // Đổi khách thì bỏ liên hệ cũ — liên hệ của khách khác gắn vào đây là dữ liệu sai.
        const base = { ...form, customerId: v, contactId: '' };
        set({ customerId: v, contactId: '' });
        setPrefillFrom(null);
        const customer = customers.find((c) => String(c.id) === v);
        if (!customer) return;
        const patch = fillEmpty(base, { contactId: await fetchPrimaryContactId(customer.id) });
        if (hasFilled(patch)) { set(patch); setPrefillFrom(`khách hàng «${customer.name}»`); }
    };

    /** Chọn hóa đơn → tự điền khách hàng + liên hệ của hóa đơn đó (chỉ ô còn trống). */
    const onPickInvoice = async (v: string) => {
        set({ invoiceId: v });
        setPrefillFrom(null);
        if (!v) return;
        const inv = (await invoiceService.getById(Number(v))).data.data;
        const patch = fillEmpty({ ...form, invoiceId: v }, {
            customerId: inv.customerId ? String(inv.customerId) : '',
            contactId: inv.contactId ? String(inv.contactId) : '',
        });
        if (hasFilled(patch)) { set(patch); setPrefillFrom(`hóa đơn «${inv.code}»`); }
    };

    /** Kiem tra bat buoc + bien (khop rang buoc backend) - tra map field->loi. */
    const validate = (): Record<string, string> =>
        collectErrors({
            code: !form.code.trim() ? 'Mã phiếu không được để trống' : null,
            subject: !form.subject.trim() ? 'Tiêu đề không được để trống' : null,
        });

    const submit = async (andNew: boolean) => {
        // Loi nhap lieu hien do duoi o; popup xac nhan chi mo khi du lieu da hop le.
        const errs = validate();
        setErrors(errs);
        if (Object.keys(errs).length > 0) return;

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
        if (!(await confirmCreate('phiếu hỗ trợ'))) return;
        mutate(payload, {
            onSuccess: () => {
                if (andNew) { reset(); showAlert('Đã tạo phiếu thành công'); }
                else navigate('/cham-soc');
            },
            onError: (err: unknown) => showAlert((err as { response?: { data?: { message?: string } } })?.response?.data?.message ?? 'Có lỗi xảy ra khi lưu phiếu'),
        });
    };

    const formRef = useRef<HTMLDivElement>(null);
    useFormKeyboardNav(formRef, { onSubmit: () => submit(false) });

    return (
        <div ref={formRef} className="p-6 bg-bg-main min-h-[calc(100vh-50px)]">
            <FormPageHeader title="Thêm phiếu hỗ trợ" saving={isPending}
                onCancel={() => navigate(-1)} onSave={() => submit(false)} onSaveAndNew={() => submit(true)} />

            <div className="bg-white rounded-card shadow-sm p-6 space-y-8">
                <FormSection title="Thông tin phiếu">
                    <div className="grid grid-cols-2 gap-x-10 gap-y-4">
                        <div className="space-y-4">
                            <FieldRow label="Mã phiếu" required error={errors.code}>
                                <input type="text" value={form.code} onChange={(e) => set({ code: e.target.value })} className={inputCls} />
                            </FieldRow>
                            <FieldRow label="Tiêu đề" required error={errors.subject}>
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
                                <SearchableSelect value={form.customerId} onChange={onPickCustomer} options={customerOptions} />
                                <PrefillHint source={prefillFrom} />
                            </FieldRow>
                            <FieldRow label="Liên hệ">
                                <RecordPicker module="contact" value={form.contactId} onChange={(v) => set({ contactId: v })}
                                    customerId={form.customerId ? Number(form.customerId) : undefined} />
                            </FieldRow>
                            <FieldRow label="Sản phẩm">
                                <SearchableSelect value={form.productId} onChange={(v) => set({ productId: v })} options={productOptions} />
                            </FieldRow>
                            <FieldRow label="Hóa đơn">
                                <RecordPicker module="invoice" value={form.invoiceId} onChange={onPickInvoice} />
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
