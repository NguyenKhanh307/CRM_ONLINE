import { useMemo, useRef, useState } from 'react';
import { collectErrors, dateRangeError, pastDateError } from '@/shared/utils/validators';
import { useConfirm } from '@/shared/confirm/useConfirm';
import { useNavigate } from 'react-router-dom';
import { useFormKeyboardNav } from '@/shared/keyboard/useFormKeyboardNav';
import { SearchableSelect } from '@/shared/components/SearchableSelect';
import { FormPageHeader } from '@/shared/components/form/FormPageHeader';
import { FormSection } from '@/shared/components/form/FormSection';
import { FieldRow } from '@/shared/components/form/FieldRow';
import { RecordPicker } from '@/shared/components/form/RecordPicker';
import { DerivedContextBox } from '@/shared/components/form/DerivedContextBox';
import { quotationService } from '@/features/bao-gia/services/quotationService';
import type { QuotationResult } from '@/features/bao-gia/types/quotationTypes';
import { inputCls } from '@/shared/components/form/formStyles';
import { DateInput } from '@/shared/components/form/DateInput';
import { ProductLineItemsTable } from '@/shared/components/form/ProductLineItemsTable';
import {
    type LineItemRow,
    type ProductOption,
    emptyLineItem,
    toItemPayloads, validateLineItems } from '@/shared/components/form/productLineItem';
import { useAlert } from '@/shared/alert/useAlert';
import { useAuth } from '@/core/auth/useAuth';
import { useActiveUsers } from '@/features/users/hooks/useActiveUsers';
import { useProductList } from '@/features/san-pham/hooks/useProductList';
import { useCreateOrder } from '../hooks/useCreateOrder';
import type { CreateOrderPayload } from '../types/orderTypes';

interface HeaderState {
    code: string; orderDate: string; deliveryDate: string;
    ownerId: string; quotationId: string; note: string;
}

// state khởi tạo — người phụ trách mặc định là user đang đăng nhập
const initialState = (ownerId: string): HeaderState => ({
    code: '', orderDate: '', deliveryDate: '', quotationId: '', ownerId, note: '',
});

// trang thêm Đơn hàng mới — header + bảng hàng hóa (layout AMIS). Đơn hàng chỉ còn 1 khóa ngoại
// chính (quotationId) — khách hàng/liên hệ/cơ hội tra qua báo giá, hiển thị read-only để giữ ngữ cảnh
const OrderAddPage = () => {
    const navigate = useNavigate();
    const { showAlert } = useAlert();
    const { confirmCreate } = useConfirm();
    const { user } = useAuth();
    const defaultOwnerId = user ? String(user.id) : '';
    const [form, setForm] = useState<HeaderState>(() => initialState(defaultOwnerId));
    const [rows, setRows] = useState<LineItemRow[]>([emptyLineItem()]);
    const { mutate, isPending } = useCreateOrder();

    const { data: users = [] } = useActiveUsers();
    const { data: products = [] } = useProductList();
    const [quotation, setQuotation] = useState<QuotationResult | null>(null);

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

    // chọn báo giá nguồn -> fetch chi tiết để hiển thị khách hàng/liên hệ/cơ hội read-only
    // (KHÔNG lưu các field này vào Order, chỉ giữ ngữ cảnh cho người dùng)
    const onPickQuotation = async (v: string) => {
        set({ quotationId: v });
        setQuotation(null);
        if (!v) return;
        const q = (await quotationService.getById(Number(v))).data.data;
        setQuotation(q);
        if (!form.ownerId && q.ownerId) set({ ownerId: String(q.ownerId) });
    };

    // kiểm tra bắt buộc + biên (khớp ràng buộc backend) - trả map field->lỗi
    const validate = (): Record<string, string> =>
        collectErrors({
            code: !form.code.trim() ? 'Mã Đơn hàng không được để trống' : null,
            orderDate: pastDateError(form.orderDate, 'Ngày đặt hàng'),
            deliveryDate: pastDateError(form.deliveryDate, 'Ngày giao hàng')
                ?? dateRangeError(form.orderDate, form.deliveryDate, 'ngày đặt hàng', 'Ngày giao hàng'),
            items: validateLineItems(rows),
        });

    // hàm lưu — lỗi hiện đỏ dưới ô, popup xác nhận chỉ mở khi dữ liệu đã hợp lệ
    const submit = async () => {
        // bước kiểm tra dữ liệu
        const errs = validate();
        setErrors(errs);
        if (Object.keys(errs).length > 0) return;

        const payload: CreateOrderPayload = {
            code: form.code.trim(),
            quotationId: form.quotationId ? Number(form.quotationId) : null,
            ownerId: form.ownerId ? Number(form.ownerId) : null,
            orderDate: form.orderDate || null,
            deliveryDate: form.deliveryDate || null,
            note: form.note || null,
            items: toItemPayloads(rows),
        };
        // bước hỏi xác nhận rồi gọi api lưu
        if (!(await confirmCreate('đơn hàng'))) return;
        mutate(payload, {
            onSuccess: () => navigate('/don-hang'),
            onError: (err: unknown) => {
                const msg = (err as { response?: { data?: { message?: string } } })?.response?.data?.message
                    ?? 'Có lỗi xảy ra khi lưu Đơn hàng';
                showAlert(msg);
            },
        });
    };

    const formRef = useRef<HTMLDivElement>(null);
    useFormKeyboardNav(formRef, { onSubmit: () => submit() });

    return (
        <div ref={formRef} className="p-6 bg-bg-main min-h-[calc(100vh-50px)]">
            <FormPageHeader title="Thêm Đơn hàng" saving={isPending}
                onCancel={() => navigate(-1)} onSave={() => submit()} />

            <div className="bg-white rounded-card shadow-sm p-6 space-y-8">
                <FormSection title="Thông tin chung">
                    <div className="grid grid-cols-2 gap-x-10 gap-y-4">
                        <div className="space-y-4">
                            <FieldRow label="Mã Đơn hàng" required error={errors.code}>
                                <input type="text" value={form.code} onChange={(e) => set({ code: e.target.value })} className={inputCls} />
                            </FieldRow>
                            <FieldRow label="Ngày đơn hàng" error={errors.orderDate}>
                                <DateInput value={form.orderDate} onChange={(v) => set({ orderDate: v })} />
                            </FieldRow>
                            <FieldRow label="Ngày giao dự kiến" error={errors.deliveryDate}>
                                <DateInput value={form.deliveryDate} onChange={(v) => set({ deliveryDate: v })} />
                            </FieldRow>
                        </div>
                        <div className="space-y-4">
                            <FieldRow label="Người phụ trách">
                                <SearchableSelect value={form.ownerId} onChange={(v) => set({ ownerId: v })} options={userOptions} />
                            </FieldRow>
                            <FieldRow label="Báo giá nguồn">
                                <RecordPicker module="quotation" value={form.quotationId} onChange={onPickQuotation} />
                            </FieldRow>
                            <DerivedContextBox rows={[
                                { label: 'Khách hàng', value: quotation?.customerName },
                                { label: 'Liên hệ', value: quotation?.contactName },
                                { label: 'Cơ hội', value: quotation?.opportunityName },
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

export default OrderAddPage;
