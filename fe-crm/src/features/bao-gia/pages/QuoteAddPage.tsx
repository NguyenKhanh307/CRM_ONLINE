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
import { useCreateQuotation } from '../hooks/useCreateQuotation';
import type { CreateQuotationPayload } from '../types/quotationTypes';

interface HeaderState {
    code: string; customerId: string; contactId: string; campaignId: string; ownerId: string;
    quoteDate: string; validUntil: string; currency: string; exchangeRate: string;
    note: string;
}

/** State khởi tạo — người phụ trách mặc định là user đang đăng nhập. */
const initialState = (ownerId: string): HeaderState => ({
    code: '', customerId: '', contactId: '', campaignId: '', ownerId, quoteDate: '', validUntil: '',
    currency: 'VND', exchangeRate: '1', note: '',
});

/** Trang thêm báo giá mới — header + bảng hàng hóa (layout AMIS). */
const QuoteAddPage = () => {
    const navigate = useNavigate();
    const { showAlert } = useAlert();
    const { confirmCreate } = useConfirm();
    const { user } = useAuth();
    const defaultOwnerId = user ? String(user.id) : '';
    const [form, setForm] = useState<HeaderState>(() => initialState(defaultOwnerId));
    const [rows, setRows] = useState<LineItemRow[]>([emptyLineItem()]);
    const { mutate, isPending } = useCreateQuotation();

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

    /** Chọn khách hàng → tự điền liên hệ chính và người phụ trách (chỉ ô còn trống). */
    const onPickCustomer = (v: string) => {
        set({ customerId: v });
        setPrefillFrom(null);
        const customer = customers.find((c) => String(c.id) === v);
        if (!customer) return;
        const contact = primaryContactOf(contacts, customer.id);
        const patch = fillEmpty(form, {
            contactId: contact ? String(contact.id) : '',
            ownerId: customer.ownerId ? String(customer.ownerId) : '',
        });
        if (hasFilled(patch)) { set(patch); setPrefillFrom(`khách hàng «${customer.name}»`); }
    };

    const submit = async (andNew: boolean) => {
        if (!form.code.trim()) { showAlert('Mã báo giá không được để trống'); return; }
        // Kiểm tra biên (khớp ràng buộc backend) — chặn submit nếu dữ liệu không hợp lệ
        const vErr = pastDateError(form.quoteDate, 'Ngày báo giá') ?? pastDateError(form.validUntil, 'Ngày hiệu lực')
            ?? dateRangeError(form.quoteDate, form.validUntil, 'ngày báo giá', 'Ngày hiệu lực') ?? validateLineItems(rows);
        if (vErr) { showAlert(vErr); return; }
        const totals = computeTotals(rows);
        const payload: CreateQuotationPayload = {
            code: form.code.trim(),
            customerId: form.customerId ? Number(form.customerId) : null,
            contactId: form.contactId ? Number(form.contactId) : null,
            opportunityId: null,
            campaignId: form.campaignId ? Number(form.campaignId) : null,
            ownerId: form.ownerId ? Number(form.ownerId) : null,
            quoteDate: form.quoteDate || null,
            validUntil: form.validUntil || null,
            currency: form.currency || 'VND',
            exchangeRate: Number(form.exchangeRate) || 1,
            subtotal: totals.subtotal,
            discount: totals.discount,
            tax: totals.tax,
            total: totals.total,
            note: form.note || null,
            items: toItemPayloads(rows),
        };
        if (!(await confirmCreate('báo giá'))) return;
        mutate(payload, {
            onSuccess: () => {
                if (andNew) { reset(); showAlert('Đã lưu báo giá thành công'); }
                else navigate('/bao-gia');
            },
            onError: (err: unknown) => {
                const msg = (err as { response?: { data?: { message?: string } } })?.response?.data?.message
                    ?? 'Có lỗi xảy ra khi lưu báo giá';
                showAlert(msg);
            },
        });
    };

    const formRef = useRef<HTMLDivElement>(null);
    useFormKeyboardNav(formRef, { onSubmit: () => submit(false) });

    return (
        <div ref={formRef} className="p-6 bg-bg-main min-h-[calc(100vh-50px)]">
            <FormPageHeader title="Thêm Báo giá" saving={isPending}
                onCancel={() => navigate(-1)} onSave={() => submit(false)} onSaveAndNew={() => submit(true)} />

            <div className="bg-white rounded-card shadow-sm p-6 space-y-8">
                <FormSection title="Thông tin chi tiết">
                    <div className="grid grid-cols-2 gap-x-10 gap-y-4">
                        <div className="space-y-4">
                            <FieldRow label="Số báo giá" required>
                                <input type="text" value={form.code} onChange={(e) => set({ code: e.target.value })} className={inputCls} />
                            </FieldRow>
                            <FieldRow label="Khách hàng">
                                <SearchableSelect value={form.customerId} onChange={onPickCustomer} options={customerOptions} />
                                <PrefillHint source={prefillFrom} />
                            </FieldRow>
                            <FieldRow label="Liên hệ">
                                <SearchableSelect value={form.contactId} onChange={(v) => set({ contactId: v })} options={contactOptions} />
                            </FieldRow>
                            <FieldRow label="Người phụ trách">
                                <SearchableSelect value={form.ownerId} onChange={(v) => set({ ownerId: v })} options={userOptions} />
                            </FieldRow>
                        </div>
                        <div className="space-y-4">
                            <FieldRow label="Ngày báo giá">
                                <DateInput value={form.quoteDate} onChange={(v) => set({ quoteDate: v })} />
                            </FieldRow>
                            <FieldRow label="Hiệu lực đến">
                                <DateInput value={form.validUntil} onChange={(v) => set({ validUntil: v })} />
                            </FieldRow>
                            <FieldRow label="Tiền tệ">
                                <input type="text" value={form.currency} onChange={(e) => set({ currency: e.target.value })} className={inputCls} />
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

export default QuoteAddPage;
