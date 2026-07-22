import { useMemo, useRef, useState } from 'react';
import { dateRangeError, pastDateError } from '@/shared/utils/validators';
import { useConfirm } from '@/shared/confirm/useConfirm';
import { useNavigate } from 'react-router-dom';
import { useFormKeyboardNav } from '@/shared/keyboard/useFormKeyboardNav';
import { SearchableSelect } from '@/shared/components/SearchableSelect';
import { FormPageHeader } from '@/shared/components/form/FormPageHeader';
import { FormSection } from '@/shared/components/form/FormSection';
import { FieldRow } from '@/shared/components/form/FieldRow';
import { PrefillHint } from '@/shared/components/form/PrefillHint';
import { fillEmpty, hasFilled, primaryContactOf } from '@/shared/utils/prefill';
import { inputCls } from '@/shared/components/form/formStyles';
import { DateInput } from '@/shared/components/form/DateInput';
import { ProductLineItemsTable } from '@/shared/components/form/ProductLineItemsTable';
import {
    type LineItemRow,
    type ProductOption,
    emptyLineItem,
    computeTotals,
    toItemPayloads, validateLineItems } from '@/shared/components/form/productLineItem';
import { useAlert } from '@/shared/alert/useAlert';
import { useAuth } from '@/core/auth/useAuth';
import { useActiveUsers } from '@/features/users/hooks/useActiveUsers';
import { useCustomerList } from '@/features/khach-hang/hooks/useCustomerList';
import { useContactList } from '@/features/lien-he/hooks/useContactList';
import { useProductList } from '@/features/san-pham/hooks/useProductList';
import { useCampaignList } from '@/features/chien-dich/hooks/useCampaignList';
import { useCreateInvoice } from '../hooks/useCreateInvoice';
import type { CreateInvoicePayload } from '../types/invoiceTypes';

interface HeaderState {
    code: string; invoiceDate: string; dueDate: string;
    customerId: string; contactId: string; campaignId: string; ownerId: string;
    currency: string; exchangeRate: string;
    billingAddress: string; taxCode: string; note: string;
}

/** State khởi tạo — người phụ trách mặc định là user đang đăng nhập. */
const initialState = (ownerId: string): HeaderState => ({
    code: '', invoiceDate: '', dueDate: '', customerId: '', contactId: '', campaignId: '',
    ownerId, currency: 'VND', exchangeRate: '1',
    billingAddress: '', taxCode: '', note: '',
});

/** Trang thêm Hóa đơn mới — header + bảng hàng hóa (layout AMIS). */
const InvoiceAddPage = () => {
    const navigate = useNavigate();
    const { showAlert } = useAlert();
    const { confirmCreate } = useConfirm();
    const { user } = useAuth();
    const defaultOwnerId = user ? String(user.id) : '';
    const [form, setForm] = useState<HeaderState>(() => initialState(defaultOwnerId));
    const [rows, setRows] = useState<LineItemRow[]>([emptyLineItem()]);
    const { mutate, isPending } = useCreateInvoice();

    const { data: users = [] } = useActiveUsers();
    const { data: customers = [] } = useCustomerList();
    const { data: contacts = [] } = useContactList();
    const { data: products = [] } = useProductList();
    const { data: campaigns = [] } = useCampaignList();

    const userOptions = useMemo(() => users.map((u) => ({ value: String(u.id), label: u.fullName })), [users]);
    const customerOptions = useMemo(() => customers.map((c) => ({ value: String(c.id), label: c.name })), [customers]);
    const contactOptions = useMemo(() => contacts.map((c) => ({ value: String(c.id), label: c.fullName })), [contacts]);
    const campaignOptions = useMemo(() => campaigns.map((c) => ({ value: String(c.id), label: c.name })), [campaigns]);
    const productOptions = useMemo<ProductOption[]>(
        () => products.map((p) => ({ value: String(p.id), label: `${p.sku} — ${p.name}`, unit: p.unit ?? '', price: p.basePrice ?? 0, vatRate: p.vatRate ?? 0 })),
        [products],
    );

    const set = (patch: Partial<HeaderState>) => setForm((p) => ({ ...p, ...patch }));
    const reset = () => { setForm(initialState(defaultOwnerId)); setRows([emptyLineItem()]); setPrefillFrom(null); };

    /** Tên khách hàng vừa kéo dữ liệu về — hiện dòng gợi ý dưới ô Khách hàng. */
    const [prefillFrom, setPrefillFrom] = useState<string | null>(null);

    /** Chọn khách hàng → tự điền liên hệ chính, MST, địa chỉ xuất HĐ, người phụ trách (chỉ ô còn trống). */
    const onPickCustomer = (v: string) => {
        set({ customerId: v });
        setPrefillFrom(null);
        const customer = customers.find((c) => String(c.id) === v);
        if (!customer) return;
        const contact = primaryContactOf(contacts, customer.id);
        const patch = fillEmpty(form, {
            contactId: contact ? String(contact.id) : '',
            ownerId: customer.ownerId ? String(customer.ownerId) : '',
            taxCode: customer.taxCode ?? '',
            billingAddress: customer.address ?? '',
        });
        if (hasFilled(patch)) { set(patch); setPrefillFrom(`khách hàng «${customer.name}»`); }
    };

    const submit = async (andNew: boolean) => {
        if (!form.code.trim()) { showAlert('Mã Hóa đơn không được để trống'); return; }
        // Kiểm tra biên (khớp ràng buộc backend) — chặn submit nếu dữ liệu không hợp lệ
        const vErr = pastDateError(form.invoiceDate, 'Ngày hóa đơn') ?? pastDateError(form.dueDate, 'Hạn thanh toán')
            ?? dateRangeError(form.invoiceDate, form.dueDate, 'ngày hóa đơn', 'Hạn thanh toán') ?? validateLineItems(rows);
        if (vErr) { showAlert(vErr); return; }
        const totals = computeTotals(rows);
        const payload: CreateInvoicePayload = {
            code: form.code.trim(),
            customerId: form.customerId ? Number(form.customerId) : null,
            contactId: form.contactId ? Number(form.contactId) : null,
            quotationId: null,
            opportunityId: null,
            campaignId: form.campaignId ? Number(form.campaignId) : null,
            ownerId: form.ownerId ? Number(form.ownerId) : null,
            invoiceDate: form.invoiceDate || null,
            dueDate: form.dueDate || null,
            currency: form.currency || 'VND',
            exchangeRate: Number(form.exchangeRate) || 1,
            billingAddress: form.billingAddress || null,
            taxCode: form.taxCode || null,
            subtotal: totals.subtotal,
            discount: totals.discount,
            tax: totals.tax,
            total: totals.total,
            note: form.note || null,
            items: toItemPayloads(rows),
        };
        if (!(await confirmCreate('hóa đơn'))) return;
        mutate(payload, {
            onSuccess: () => {
                if (andNew) { reset(); showAlert('Đã lưu Hóa đơn thành công'); }
                else navigate('/hoa-don');
            },
            onError: (err: unknown) => {
                const msg = (err as { response?: { data?: { message?: string } } })?.response?.data?.message
                    ?? 'Có lỗi xảy ra khi lưu Hóa đơn';
                showAlert(msg);
            },
        });
    };

    const formRef = useRef<HTMLDivElement>(null);
    useFormKeyboardNav(formRef, { onSubmit: () => submit(false) });

    return (
        <div ref={formRef} className="p-6 bg-bg-main min-h-[calc(100vh-50px)]">
            <FormPageHeader title="Thêm Hóa đơn" saving={isPending}
                onCancel={() => navigate(-1)} onSave={() => submit(false)} onSaveAndNew={() => submit(true)} />

            <div className="bg-white rounded-card shadow-sm p-6 space-y-8">
                <FormSection title="Thông tin chung">
                    <div className="grid grid-cols-2 gap-x-10 gap-y-4">
                        <div className="space-y-4">
                            <FieldRow label="Mã Hóa đơn" required>
                                <input type="text" value={form.code} onChange={(e) => set({ code: e.target.value })} className={inputCls} />
                            </FieldRow>
                            <FieldRow label="Ngày hóa đơn">
                                <DateInput value={form.invoiceDate} onChange={(v) => set({ invoiceDate: v })} />
                            </FieldRow>
                            <FieldRow label="Hạn thanh toán">
                                <DateInput value={form.dueDate} onChange={(v) => set({ dueDate: v })} />
                            </FieldRow>
                            <FieldRow label="Khách hàng">
                                <SearchableSelect value={form.customerId} onChange={onPickCustomer} options={customerOptions} />
                                <PrefillHint source={prefillFrom} />
                            </FieldRow>
                            <FieldRow label="Liên hệ">
                                <SearchableSelect value={form.contactId} onChange={(v) => set({ contactId: v })} options={contactOptions} />
                            </FieldRow>
                        </div>
                        <div className="space-y-4">
                            <FieldRow label="Người phụ trách">
                                <SearchableSelect value={form.ownerId} onChange={(v) => set({ ownerId: v })} options={userOptions} />
                            </FieldRow>
                            <FieldRow label="Tiền tệ">
                                <input type="text" value={form.currency} onChange={(e) => set({ currency: e.target.value })} className={inputCls} />
                            </FieldRow>
                            <FieldRow label="Tỷ giá">
                                <input type="number" value={form.exchangeRate} onChange={(e) => set({ exchangeRate: e.target.value })} className={inputCls} />
                            </FieldRow>
                            <FieldRow label="Mã số thuế">
                                <input type="text" value={form.taxCode} onChange={(e) => set({ taxCode: e.target.value })} className={inputCls} />
                            </FieldRow>
                            <FieldRow label="Địa chỉ xuất HĐ">
                                <input type="text" value={form.billingAddress} onChange={(e) => set({ billingAddress: e.target.value })} className={inputCls} />
                            </FieldRow>
                            <FieldRow label="Chiến dịch">
                                <SearchableSelect value={form.campaignId} onChange={(v) => set({ campaignId: v })} options={campaignOptions} />
                            </FieldRow>
                        </div>
                    </div>
                </FormSection>

                <FormSection title="Hàng hóa">
                    <ProductLineItemsTable rows={rows} onChange={setRows} productOptions={productOptions} showUnit showTax />
                </FormSection>

                <FormSection title="Thông tin mô tả">
                    <FieldRow label="Ghi chú" alignTop>
                        <textarea rows={3} value={form.note} onChange={(e) => set({ note: e.target.value })} className={`${inputCls} resize-none`} />
                    </FieldRow>
                </FormSection>
            </div>
        </div>
    );
};

export default InvoiceAddPage;
