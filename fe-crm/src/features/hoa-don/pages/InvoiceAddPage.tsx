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
import { DerivedContextBox } from '@/shared/components/form/DerivedContextBox';
import { inputCls } from '@/shared/components/form/formStyles';
import { DateInput } from '@/shared/components/form/DateInput';
import { ProductLineItemsTable } from '@/shared/components/form/ProductLineItemsTable';
import {
    type LineItemRow,
    type ProductOption,
    emptyLineItem,
    fromItemResult,
    toItemPayloads, validateLineItems } from '@/shared/components/form/productLineItem';
import { useAlert } from '@/shared/alert/useAlert';
import { useAuth } from '@/core/auth/useAuth';
import { useActiveUsers } from '@/features/users/hooks/useActiveUsers';
import { useProductList } from '@/features/san-pham/hooks/useProductList';
import { orderService } from '@/features/don-hang/services/orderService';
import { quotationService } from '@/features/bao-gia/services/quotationService';
import type { QuotationResult } from '@/features/bao-gia/types/quotationTypes';
import { RecordPicker } from '@/shared/components/form/RecordPicker';
import { useCreateInvoice } from '../hooks/useCreateInvoice';
import type { CreateInvoicePayload } from '../types/invoiceTypes';

interface HeaderState {
    code: string; invoiceDate: string; dueDate: string;
    ownerId: string; orderId: string; note: string;
}

// state khởi tạo — người phụ trách mặc định là user đang đăng nhập
const initialState = (ownerId: string): HeaderState => ({
    code: '', invoiceDate: '', dueDate: '', orderId: '', ownerId, note: '',
});

// trang thêm Hóa đơn mới — header + bảng hàng hóa (layout AMIS). Hóa đơn chỉ còn 1 khóa ngoại
// chính (orderId) — khách hàng/liên hệ tra qua chuỗi đơn hàng -> báo giá, hiển thị read-only
const InvoiceAddPage = () => {
    const navigate = useNavigate();
    const { showAlert } = useAlert();
    const { confirmCreate } = useConfirm();
    const { user } = useAuth();
    const defaultOwnerId = user ? String(user.id) : '';
    const [form, setForm] = useState<HeaderState>(() => initialState(defaultOwnerId));
    const [rows, setRows] = useState<LineItemRow[]>([emptyLineItem()]);
    const { mutate, isPending } = useCreateInvoice();
    const [quotation, setQuotation] = useState<QuotationResult | null>(null);

    const { data: users = [] } = useActiveUsers();
    const { data: products = [] } = useProductList();

    const userOptions = useMemo(() => users.map((u) => ({ value: String(u.id), label: u.fullName })), [users]);
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

    // tên bản ghi vừa kéo dữ liệu về — hiện dòng gợi ý dưới ô Đơn hàng
    const [prefillFrom, setPrefillFrom] = useState<string | null>(null);

    // chọn đơn hàng -> tự điền người phụ trách còn trống + chép dòng hàng (nếu bảng còn trống)
    // + fetch chuỗi order -> quotation để hiển thị khách hàng/liên hệ read-only
    const onPickOrder = async (v: string) => {
        set({ orderId: v });
        setPrefillFrom(null);
        setQuotation(null);
        if (!v) return;
        const order = (await orderService.getById(Number(v))).data.data;
        if (!form.ownerId && order.ownerId) set({ ownerId: String(order.ownerId) });
        if (order.quotationId != null) {
            const q = (await quotationService.getById(order.quotationId)).data.data;
            setQuotation(q);
        }

        // chép dòng hàng của đơn — CHỈ khi bảng chưa chọn sản phẩm nào, để không xóa thứ đang gõ dở
        const emptyTable = rows.every((r) => !r.productId);
        if (emptyTable) {
            const items = (await orderService.getItems(order.id)).data.data;
            // bỏ backendId: đó là id dòng hàng của ĐƠN HÀNG, mang sang hóa đơn mới là id lạ
            setRows(items.map((it) => ({ ...fromItemResult(it), backendId: undefined })));
        }
        setPrefillFrom(emptyTable
            ? `đơn hàng «${order.code}» (kèm dòng hàng)`
            : `đơn hàng «${order.code}» — giữ nguyên dòng hàng bạn đã nhập`);
    };

    // vào trang qua nút "Xuất hóa đơn" (chuột phải Đơn hàng, ?fromOrder=<id>) -> tự chọn đơn hàng
    // nguồn + tự điền, chạy đúng 1 lần lúc mount
    const [searchParams] = useSearchParams();
    const autoPickedRef = useRef(false);
    useEffect(() => {
        const fromOrder = searchParams.get('fromOrder');
        if (!fromOrder || autoPickedRef.current) return;
        autoPickedRef.current = true;
        void onPickOrder(fromOrder);
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [searchParams]);

    // kiểm tra bắt buộc + biên (khớp ràng buộc backend) - trả map field->lỗi
    const validate = (): Record<string, string> =>
        collectErrors({
            code: !form.code.trim() ? 'Mã Hóa đơn không được để trống' : null,
            invoiceDate: pastDateError(form.invoiceDate, 'Ngày hóa đơn'),
            dueDate: pastDateError(form.dueDate, 'Hạn thanh toán')
                ?? dateRangeError(form.invoiceDate, form.dueDate, 'ngày hóa đơn', 'Hạn thanh toán'),
            items: validateLineItems(rows),
        });

    const submit = async () => {
        // lỗi nhập liệu hiện đỏ dưới ô; popup xác nhận chỉ mở khi dữ liệu đã hợp lệ
        const errs = validate();
        setErrors(errs);
        if (!validateOrWarn(errs, showAlert)) return;

        const payload: CreateInvoicePayload = {
            code: form.code.trim(),
            orderId: form.orderId ? Number(form.orderId) : null,
            ownerId: form.ownerId ? Number(form.ownerId) : null,
            invoiceDate: form.invoiceDate || null,
            dueDate: form.dueDate || null,
            note: form.note || null,
            items: toItemPayloads(rows),
        };
        if (!(await confirmCreate('hóa đơn'))) return;
        mutate(payload, {
            onSuccess: async () => {
                // mở từ nút "Xuất hóa đơn" -> khóa đơn hàng nguồn + chuyển sang Hoàn tất (lỗi thì
                // bỏ qua vì hóa đơn đã tạo thành công, đây chỉ là bước phụ trợ trên nguồn)
                if (form.orderId && autoPickedRef.current) {
                    try { await orderService.markConverted(Number(form.orderId)); } catch { /* im lặng */ }
                }
                navigate('/hoa-don');
            },
            onError: (err: unknown) => {
                const msg = (err as { response?: { data?: { message?: string } } })?.response?.data?.message
                    ?? 'Có lỗi xảy ra khi lưu Hóa đơn';
                showAlert(msg);
            },
        });
    };

    const formRef = useRef<HTMLDivElement>(null);
    useFormKeyboardNav(formRef, { onSubmit: () => submit() });

    return (
        <div ref={formRef} className="p-6 bg-bg-main min-h-[calc(100vh-50px)]">
            <FormPageHeader title="Thêm Hóa đơn" saving={isPending}
                onCancel={() => navigate(-1)} onSave={() => submit()} />

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
                            <FieldRow label="Người phụ trách">
                                <SearchableSelect value={form.ownerId} onChange={(v) => set({ ownerId: v })} options={userOptions} />
                            </FieldRow>
                        </div>
                        <div className="space-y-4">
                            <FieldRow label="Đơn hàng">
                                <RecordPicker module="order" value={form.orderId} onChange={onPickOrder} />
                                <PrefillHint source={prefillFrom} />
                            </FieldRow>
                            <DerivedContextBox rows={[
                                { label: 'Khách hàng', value: quotation?.customerName },
                                { label: 'Liên hệ', value: quotation?.contactName },
                                { label: 'Báo giá nguồn', value: quotation?.code },
                            ]} />
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
