import { useMemo, useRef, useState } from 'react';
import { collectErrors, dateRangeError, pastDateError } from '@/shared/utils/validators';
import { useConfirm } from '@/shared/confirm/useConfirm';
import { useNavigate } from 'react-router-dom';
import { useFormKeyboardNav } from '@/shared/keyboard/useFormKeyboardNav';
import { SearchableSelect } from '@/shared/components/SearchableSelect';
import { FormPageHeader } from '@/shared/components/form/FormPageHeader';
import { FormSection } from '@/shared/components/form/FormSection';
import { FieldRow } from '@/shared/components/form/FieldRow';
import { PrefillHint } from '@/shared/components/form/PrefillHint';
import { fillEmpty, hasFilled } from '@/shared/utils/prefill';
import { fetchPrimaryContactId } from '@/shared/lookup/recordPrefill';
import { inputCls } from '@/shared/components/form/formStyles';
import { DateInput } from '@/shared/components/form/DateInput';
import { ProductLineItemsTable } from '@/shared/components/form/ProductLineItemsTable';
import {
    type LineItemRow,
    type ProductOption,
    emptyLineItem,
    computeTotals,
    fromItemResult,
    toItemPayloads, validateLineItems } from '@/shared/components/form/productLineItem';
import { useAlert } from '@/shared/alert/useAlert';
import { useAuth } from '@/core/auth/useAuth';
import { useActiveUsers } from '@/features/users/hooks/useActiveUsers';
import { useCustomerList } from '@/features/khach-hang/hooks/useCustomerList';
import { useProductList } from '@/features/san-pham/hooks/useProductList';
import { useCampaignList } from '@/features/chien-dich/hooks/useCampaignList';
import { orderService } from '@/features/don-hang/services/orderService';
import { quotationService } from '@/features/bao-gia/services/quotationService';
import { opportunityService } from '@/features/co-hoi/services/opportunityService';
import { RecordPicker } from '@/shared/components/form/RecordPicker';
import { useCreateInvoice } from '../hooks/useCreateInvoice';
import type { CreateInvoicePayload } from '../types/invoiceTypes';

interface HeaderState {
    code: string; invoiceDate: string; dueDate: string;
    customerId: string; contactId: string; campaignId: string; ownerId: string;
    orderId: string; quotationId: string; opportunityId: string;
    currency: string; exchangeRate: string;
    billingAddress: string; taxCode: string; note: string;
}

/** State khởi tạo — người phụ trách mặc định là user đang đăng nhập. */
const initialState = (ownerId: string): HeaderState => ({
    code: '', invoiceDate: '', dueDate: '', customerId: '', contactId: '', campaignId: '',
    orderId: '', quotationId: '', opportunityId: '',
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
    const { data: products = [] } = useProductList();
    const { data: campaigns = [] } = useCampaignList();

    const userOptions = useMemo(() => users.map((u) => ({ value: String(u.id), label: u.fullName })), [users]);
    const customerOptions = useMemo(() => customers.map((c) => ({ value: String(c.id), label: c.name })), [customers]);
    const campaignOptions = useMemo(() => campaigns.map((c) => ({ value: String(c.id), label: c.name })), [campaigns]);
    const productOptions = useMemo<ProductOption[]>(
        () => products.map((p) => ({ value: String(p.id), label: `${p.sku} — ${p.name}`, unit: p.unit ?? '', price: p.basePrice ?? 0, vatRate: p.vatRate ?? 0 })),
        [products],
    );

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
    const reset = () => { setForm(initialState(defaultOwnerId)); setRows([emptyLineItem()]); setPrefillFrom(null); };

    /** Tên khách hàng vừa kéo dữ liệu về — hiện dòng gợi ý dưới ô Khách hàng. */
    const [prefillFrom, setPrefillFrom] = useState<string | null>(null);

    /** Chọn khách hàng → tự điền liên hệ chính, MST, địa chỉ xuất HĐ, người phụ trách (chỉ ô còn trống). */
    const onPickCustomer = async (v: string) => {
        // Đổi khách thì bỏ liên hệ cũ — liên hệ của khách khác gắn vào đây là dữ liệu sai.
        const base = { ...form, customerId: v, contactId: '' };
        set({ customerId: v, contactId: '' });
        setPrefillFrom(null);
        const customer = customers.find((c) => String(c.id) === v);
        if (!customer) return;
        const patch = fillEmpty(base, {
            contactId: await fetchPrimaryContactId(customer.id),
            ownerId: customer.ownerId ? String(customer.ownerId) : '',
            taxCode: customer.taxCode ?? '',
            billingAddress: customer.address ?? '',
        });
        if (hasFilled(patch)) { set(patch); setPrefillFrom(`khách hàng «${customer.name}»`); }
    };

    /** Chọn đơn hàng → tự điền thông tin bên mua + chép dòng hàng (nếu bảng còn trống). */
    const onPickOrder = async (v: string) => {
        set({ orderId: v });
        setPrefillFrom(null);
        if (!v) return;
        const order = (await orderService.getById(Number(v))).data.data;
        const patch = fillEmpty({ ...form, orderId: v }, {
            customerId: order.customerId ? String(order.customerId) : '',
            contactId: order.contactId ? String(order.contactId) : '',
            ownerId: order.ownerId ? String(order.ownerId) : '',
            campaignId: order.campaignId ? String(order.campaignId) : '',
            quotationId: order.quotationId ? String(order.quotationId) : '',
            opportunityId: order.opportunityId ? String(order.opportunityId) : '',
            taxCode: order.taxCode ?? '',
            billingAddress: order.billingAddress ?? '',
        });
        const filled = hasFilled(patch);
        if (filled) set(patch);

        // Chép dòng hàng của đơn — CHỈ khi bảng chưa chọn sản phẩm nào, để không xóa thứ đang gõ dở.
        const emptyTable = rows.every((r) => !r.productId);
        if (emptyTable) {
            const items = (await orderService.getItems(order.id)).data.data;
            // Bỏ backendId: đó là id dòng hàng của ĐƠN HÀNG, mang sang hóa đơn mới là id lạ.
            setRows(items.map((it) => ({ ...fromItemResult(it), backendId: undefined })));
        }
        if (filled || emptyTable) {
            setPrefillFrom(emptyTable
                ? `đơn hàng «${order.code}» (kèm dòng hàng)`
                : `đơn hàng «${order.code}» — giữ nguyên dòng hàng bạn đã nhập`);
        }
    };

    /** Chọn báo giá nguồn → tự điền bên mua + cơ hội + chiến dịch. */
    const onPickQuotation = async (v: string) => {
        set({ quotationId: v });
        setPrefillFrom(null);
        if (!v) return;
        const q = (await quotationService.getById(Number(v))).data.data;
        const patch = fillEmpty({ ...form, quotationId: v }, {
            customerId: q.customerId ? String(q.customerId) : '',
            contactId: q.contactId ? String(q.contactId) : '',
            opportunityId: q.opportunityId ? String(q.opportunityId) : '',
            campaignId: q.campaignId ? String(q.campaignId) : '',
            ownerId: q.ownerId ? String(q.ownerId) : '',
        });
        if (hasFilled(patch)) { set(patch); setPrefillFrom(`báo giá «${q.code}»`); }
    };

    /** Chọn cơ hội → tự điền bên mua + chiến dịch. */
    const onPickOpportunity = async (v: string) => {
        set({ opportunityId: v });
        setPrefillFrom(null);
        if (!v) return;
        const o = (await opportunityService.getById(Number(v))).data.data;
        const patch = fillEmpty({ ...form, opportunityId: v }, {
            customerId: o.customerId ? String(o.customerId) : '',
            contactId: o.contactId ? String(o.contactId) : '',
            campaignId: o.campaignId ? String(o.campaignId) : '',
            ownerId: o.ownerId ? String(o.ownerId) : '',
        });
        if (hasFilled(patch)) { set(patch); setPrefillFrom(`cơ hội «${o.code}»`); }
    };

    /** Kiem tra bat buoc + bien (khop rang buoc backend) - tra map field->loi. */
    const validate = (): Record<string, string> =>
        collectErrors({
            code: !form.code.trim() ? 'Mã Hóa đơn không được để trống' : null,
            invoiceDate: pastDateError(form.invoiceDate, 'Ngày hóa đơn'),
            dueDate: pastDateError(form.dueDate, 'Hạn thanh toán')
                ?? dateRangeError(form.invoiceDate, form.dueDate, 'ngày hóa đơn', 'Hạn thanh toán'),
            items: validateLineItems(rows),
        });

    const submit = async (andNew: boolean) => {
        // Loi nhap lieu hien do duoi o; popup xac nhan chi mo khi du lieu da hop le.
        const errs = validate();
        setErrors(errs);
        if (Object.keys(errs).length > 0) return;

        const totals = computeTotals(rows);
        const payload: CreateInvoicePayload = {
            code: form.code.trim(),
            customerId: form.customerId ? Number(form.customerId) : null,
            contactId: form.contactId ? Number(form.contactId) : null,
            quotationId: form.quotationId ? Number(form.quotationId) : null,
            opportunityId: form.opportunityId ? Number(form.opportunityId) : null,
            campaignId: form.campaignId ? Number(form.campaignId) : null,
            orderId: form.orderId ? Number(form.orderId) : null,
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
                            <FieldRow label="Mã Hóa đơn" required error={errors.code}>
                                <input type="text" value={form.code} onChange={(e) => set({ code: e.target.value })} className={inputCls} />
                            </FieldRow>
                            <FieldRow label="Ngày hóa đơn" error={errors.invoiceDate}>
                                <DateInput value={form.invoiceDate} onChange={(v) => set({ invoiceDate: v })} />
                            </FieldRow>
                            <FieldRow label="Hạn thanh toán" error={errors.dueDate}>
                                <DateInput value={form.dueDate} onChange={(v) => set({ dueDate: v })} />
                            </FieldRow>
                            <FieldRow label="Khách hàng">
                                <SearchableSelect value={form.customerId} onChange={onPickCustomer} options={customerOptions} />
                                <PrefillHint source={prefillFrom} />
                            </FieldRow>
                            <FieldRow label="Liên hệ">
                                <RecordPicker module="contact" value={form.contactId} onChange={(v) => set({ contactId: v })}
                                    customerId={form.customerId ? Number(form.customerId) : undefined} />
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
                                <input type="number" min={0} value={form.exchangeRate} onChange={(e) => set({ exchangeRate: e.target.value })} className={inputCls} />
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
                            <FieldRow label="Đơn hàng">
                                <RecordPicker module="order" value={form.orderId} onChange={onPickOrder} />
                            </FieldRow>
                            <FieldRow label="Báo giá nguồn">
                                <RecordPicker module="quotation" value={form.quotationId} onChange={onPickQuotation} />
                            </FieldRow>
                            <FieldRow label="Cơ hội">
                                <RecordPicker module="opportunity" value={form.opportunityId} onChange={onPickOpportunity} />
                            </FieldRow>
                        </div>
                    </div>
                </FormSection>

                <FormSection title="Hàng hóa">
                    <ProductLineItemsTable rows={rows} onChange={setRows} productOptions={productOptions} showUnit showTax />
                    {errors.items && <p className="text-xs text-danger mt-1">{errors.items}</p>}
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
