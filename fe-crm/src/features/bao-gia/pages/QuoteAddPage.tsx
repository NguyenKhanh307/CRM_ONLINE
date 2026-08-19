import { useEffect, useMemo, useRef, useState } from 'react';
import { collectErrors, dateRangeError, pastDateError, validateOrWarn } from '@/shared/utils/validators';
import { useConfirm } from '@/shared/confirm/useConfirm';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { useFormKeyboardNav } from '@/shared/keyboard/useFormKeyboardNav';
import { SearchableSelect } from '@/shared/components/SearchableSelect';
import { FormPageHeader } from '@/shared/components/form/FormPageHeader';
import { FormSection } from '@/shared/components/form/FormSection';
import { FieldRow } from '@/shared/components/form/FieldRow';
import { PrefillHint } from '@/shared/components/form/PrefillHint';
import { fillEmpty, hasFilled } from '@/shared/utils/prefill';
import { fetchPrimaryContactId } from '@/shared/lookup/recordPrefill';
import { RecordPicker } from '@/shared/components/form/RecordPicker';
import { opportunityService } from '@/features/co-hoi/services/opportunityService';
import { inputCls } from '@/shared/components/form/formStyles';
import { DateInput } from '@/shared/components/form/DateInput';
import { ProductLineItemsTable } from '@/shared/components/form/ProductLineItemsTable';
import {
    type LineItemRow,
    type ProductOption,
    emptyLineItem,
    fromItemResult,
    toItemPayloads, validateLineItems } from '@/shared/components/form/productLineItem';
import { quotationService } from '../services/quotationService';
import { useAlert } from '@/shared/alert/useAlert';
import { useAuth } from '@/core/auth/useAuth';
import { useActiveUsers } from '@/features/users/hooks/useActiveUsers';
import { useCustomerList } from '@/features/khach-hang/hooks/useCustomerList';
import { useProductList } from '@/features/san-pham/hooks/useProductList';
import { useEligiblePricePolicies } from '@/features/chinh-sach-gia/hooks/useEligiblePricePolicies';
import { useCreateQuotation } from '../hooks/useCreateQuotation';
import type { CreateQuotationPayload } from '../types/quotationTypes';

interface HeaderState {
    code: string; customerId: string; contactId: string; pricePolicyId: string; ownerId: string;
    opportunityId: string;
    quoteDate: string; validUntil: string;
    note: string;
}

// state khởi tạo — người phụ trách mặc định là user đang đăng nhập
const initialState = (ownerId: string): HeaderState => ({
    code: '', customerId: '', contactId: '', pricePolicyId: '', opportunityId: '', ownerId, quoteDate: '', validUntil: '',
    note: '',
});

// trang thêm báo giá mới — header + bảng hàng hóa (layout AMIS)
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
    const { data: products = [] } = useProductList();
    const { data: pricePolicies = [] } = useEligiblePricePolicies(form.customerId ? Number(form.customerId) : undefined);

    const userOptions = useMemo(() => users.map((u) => ({ value: String(u.id), label: u.fullName })), [users]);
    const customerOptions = useMemo(() => customers.map((c) => ({ value: String(c.id), label: c.name })), [customers]);
    const pricePolicyOptions = useMemo(() => pricePolicies.map((p) => ({ value: String(p.id), label: p.name })), [pricePolicies]);
    const productOptions = useMemo<ProductOption[]>(
        () => products.map((p) => ({ value: String(p.id), label: `${p.sku} — ${p.name}`, unit: p.unit ?? '', price: p.basePrice ?? 0, vatRate: p.vatRate ?? 0 })),
        [products],
    );

    const [errors, setErrors] = useState<Record<string, string>>({});

    // cập nhật form và xóa lỗi của đúng những field vừa gõ
    const set = (patch: Partial<HeaderState>) => {
        setForm((p) => ({ ...p, ...patch }));
        setErrors((e) => {
            const next = { ...e };
            Object.keys(patch).forEach((k) => delete next[k]);
            return next;
        });
    };

    // tên khách hàng vừa kéo dữ liệu về — hiện dòng gợi ý dưới ô Khách hàng
    const [prefillFrom, setPrefillFrom] = useState<string | null>(null);

    // chọn khách hàng -> tự điền liên hệ chính và người phụ trách (chỉ ô còn trống)
    const onPickCustomer = async (v: string) => {
        // đổi khách thì bỏ liên hệ cũ — liên hệ của khách khác gắn vào đây là dữ liệu sai
        const base = { ...form, customerId: v, contactId: '' };
        set({ customerId: v, contactId: '' });
        setPrefillFrom(null);
        const customer = customers.find((c) => String(c.id) === v);
        if (!customer) return;
        const patch = fillEmpty(base, {
            contactId: await fetchPrimaryContactId(customer.id),
            ownerId: customer.ownerId ? String(customer.ownerId) : '',
        });
        if (hasFilled(patch)) { set(patch); setPrefillFrom(`khách hàng «${customer.name}»`); }
    };

    // báo giá đầu tiên của cơ hội đang chọn -> tự đặt làm báo giá đồng bộ (primary) sau khi lưu
    const isFirstQuoteRef = useRef(false);

    // chọn cơ hội nguồn -> tự điền bên mua, chiến dịch, chính sách giá + chép dòng hàng (chỉ khi
    // bảng còn trống, để không xóa thứ đang gõ dở) — đồng nhất với InvoiceAddPage?fromOrder=
    const onPickOpportunity = async (v: string) => {
        set({ opportunityId: v });
        setPrefillFrom(null);
        isFirstQuoteRef.current = false;
        if (!v) return;
        const o = (await opportunityService.getById(Number(v))).data.data;
        const patch = fillEmpty({ ...form, opportunityId: v }, {
            customerId: o.customerId ? String(o.customerId) : '',
            contactId: o.contactId ? String(o.contactId) : '',
            pricePolicyId: o.pricePolicyId ? String(o.pricePolicyId) : '',
            ownerId: o.ownerId ? String(o.ownerId) : '',
        });
        if (hasFilled(patch)) set(patch);

        // báo giá đầu tiên của cơ hội -> đặt đồng bộ ngay sau khi tạo (khớp hành vi clone cũ)
        const related = (await opportunityService.getRelated(Number(v))).data.data;
        isFirstQuoteRef.current = related.quotations.total === 0;

        // chép dòng hàng của cơ hội — CHỈ khi bảng chưa chọn sản phẩm nào. Dòng hàng Cơ hội không
        // lưu ĐVT (opportunity_items không có cột unit) -> tra lại theo sản phẩm đã nạp sẵn
        const emptyTable = rows.every((r) => !r.productId);
        if (emptyTable) {
            const items = (await opportunityService.getItems(Number(v))).data.data;
            setRows(items.map((it) => {
                const p = products.find((pr) => pr.id === it.productId);
                return { ...fromItemResult(it), unit: p?.unit ?? '', backendId: undefined };
            }));
        }
        setPrefillFrom(emptyTable
            ? `cơ hội «${o.code}» (kèm dòng hàng)`
            : `cơ hội «${o.code}» — giữ nguyên dòng hàng bạn đã nhập`);
    };

    // vào trang qua nút "Tạo báo giá" (chuột phải/trang chi tiết Cơ hội, ?fromOpportunity=<id>) ->
    // tự chọn cơ hội nguồn + tự điền, chạy đúng 1 lần lúc mount
    const [searchParams] = useSearchParams();
    const autoPickedRef = useRef(false);
    useEffect(() => {
        const fromOpportunity = searchParams.get('fromOpportunity');
        if (!fromOpportunity || autoPickedRef.current) return;
        autoPickedRef.current = true;
        void onPickOpportunity(fromOpportunity);
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [searchParams]);

    // kiểm tra bắt buộc + biên (khớp ràng buộc backend) - trả map field->lỗi
    const validate = (): Record<string, string> =>
        collectErrors({
            code: !form.code.trim() ? 'Mã báo giá không được để trống' : null,
            quoteDate: pastDateError(form.quoteDate, 'Ngày báo giá'),
            validUntil: pastDateError(form.validUntil, 'Ngày hiệu lực')
                ?? dateRangeError(form.quoteDate, form.validUntil, 'ngày báo giá', 'Ngày hiệu lực'),
            items: validateLineItems(rows),
        });

    // lưu form — lỗi hiện đỏ dưới ô, popup xác nhận chỉ mở khi dữ liệu đã hợp lệ
    const submit = async () => {
        // bước kiểm tra dữ liệu
        const errs = validate();
        setErrors(errs);
        if (!validateOrWarn(errs, showAlert)) return;

        const payload: CreateQuotationPayload = {
            code: form.code.trim(),
            customerId: form.customerId ? Number(form.customerId) : null,
            contactId: form.contactId ? Number(form.contactId) : null,
            opportunityId: form.opportunityId ? Number(form.opportunityId) : null,
            pricePolicyId: form.pricePolicyId ? Number(form.pricePolicyId) : null,
            ownerId: form.ownerId ? Number(form.ownerId) : null,
            quoteDate: form.quoteDate || null,
            validUntil: form.validUntil || null,
            note: form.note || null,
            items: toItemPayloads(rows),
        };
        if (!(await confirmCreate('báo giá'))) return;
        mutate(payload, {
            onSuccess: async (res) => {
                // báo giá đầu tiên của cơ hội -> tự đặt đồng bộ (primary), lỗi thì bỏ qua vì
                // báo giá đã tạo thành công, đặt primary chỉ là bước phụ trợ
                if (isFirstQuoteRef.current) {
                    try { await quotationService.setPrimary(res.data.data.id); } catch { /* im lặng */ }
                }
                navigate('/bao-gia');
            },
            onError: (err: unknown) => {
                const msg = (err as { response?: { data?: { message?: string } } })?.response?.data?.message
                    ?? 'Có lỗi xảy ra khi lưu báo giá';
                showAlert(msg);
            },
        });
    };

    const formRef = useRef<HTMLDivElement>(null);
    useFormKeyboardNav(formRef, { onSubmit: () => submit() });

    return (
        <div ref={formRef} className="p-6 bg-bg-main min-h-[calc(100vh-50px)]">
            <FormPageHeader title="Thêm Báo giá" saving={isPending}
                onCancel={() => navigate(-1)} onSave={() => submit()} />

            <div className="bg-white rounded-card shadow-sm p-6 space-y-8">
                <FormSection title="Thông tin chi tiết">
                    <div className="grid grid-cols-2 gap-x-10 gap-y-4">
                        <div className="space-y-4">
                            <FieldRow label="Số báo giá" required error={errors.code}>
                                <input type="text" value={form.code} onChange={(e) => set({ code: e.target.value })} className={inputCls} />
                            </FieldRow>
                            <FieldRow label="Khách hàng">
                                <SearchableSelect value={form.customerId} onChange={onPickCustomer} options={customerOptions} />
                                <PrefillHint source={prefillFrom} />
                            </FieldRow>
                            <FieldRow label="Liên hệ">
                                <RecordPicker module="contact" value={form.contactId} onChange={(v) => set({ contactId: v })}
                                    customerId={form.customerId ? Number(form.customerId) : undefined} />
                            </FieldRow>
                            <FieldRow label="Người phụ trách">
                                <SearchableSelect value={form.ownerId} onChange={(v) => set({ ownerId: v })} options={userOptions} />
                            </FieldRow>
                        </div>
                        <div className="space-y-4">
                            <FieldRow label="Ngày báo giá" error={errors.quoteDate}>
                                <DateInput value={form.quoteDate} onChange={(v) => set({ quoteDate: v })} />
                            </FieldRow>
                            <FieldRow label="Hiệu lực đến" error={errors.validUntil}>
                                <DateInput value={form.validUntil} onChange={(v) => set({ validUntil: v })} />
                            </FieldRow>
                            <FieldRow label="Chính sách giá">
                                <SearchableSelect value={form.pricePolicyId} onChange={(v) => set({ pricePolicyId: v })} options={pricePolicyOptions} />
                            </FieldRow>
                            <FieldRow label="Cơ hội">
                                <RecordPicker module="opportunity" value={form.opportunityId} onChange={onPickOpportunity} />
                            </FieldRow>
                        </div>
                    </div>
                </FormSection>

                <FormSection title="Hàng hóa">
                    <ProductLineItemsTable rows={rows} onChange={setRows} productOptions={productOptions} showUnit showTax
                        pricePolicyId={form.pricePolicyId ? Number(form.pricePolicyId) : null}
                        customerId={form.customerId ? Number(form.customerId) : null} />
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

export default QuoteAddPage;
