import { useRef, useState, type FormEvent, useEffect, useMemo } from 'react';
import { useQueryClient } from '@tanstack/react-query';
import { collectErrors, dateRangeError } from '@/shared/utils/validators';
import { FieldError } from '@/shared/components/form/FormField';
import { ModalFooter } from '@/shared/components/ModalFooter';
import { useConfirm } from '@/shared/confirm/useConfirm';
import { useFormKeyboardNav } from '@/shared/keyboard/useFormKeyboardNav';
import { FiX } from 'react-icons/fi';
import type { OrderResult, UpdateOrderPayload } from '../types/orderTypes';
import { useUpdateOrder } from '../hooks/useUpdateOrder';
import { orderService } from '../services/orderService';
import { quotationService } from '@/features/bao-gia/services/quotationService';
import type { QuotationResult } from '@/features/bao-gia/types/quotationTypes';
import { useProductList } from '@/features/san-pham/hooks/useProductList';
import { ProductLineItemsTable } from '@/shared/components/form/ProductLineItemsTable';
import { RecordPicker } from '@/shared/components/form/RecordPicker';
import { DerivedContextBox } from '@/shared/components/form/DerivedContextBox';
import { DateInput } from '@/shared/components/form/DateInput';
import {
    type LineItemRow,
    type ProductOption,
    fromItemResult,
    diffLineItems,
    toItemPayload, validateLineItems } from '@/shared/components/form/productLineItem';

interface Props {
    item: OrderResult | null;
    onClose: () => void;
}

const ORDER_STATUS_LABELS: Record<string, string> = {
    draft: 'Nháp', confirmed: 'Đã xác nhận', processing: 'Đang xử lý',
    completed: 'Hoàn tất', cancelled: 'Đã hủy',
};
const ORDER_STATUS_COLORS: Record<string, string> = {
    draft: 'bg-gray-100 text-gray-600', confirmed: 'bg-blue-100 text-blue-700', processing: 'bg-yellow-100 text-yellow-700',
    completed: 'bg-green-100 text-green-700', cancelled: 'bg-red-100 text-red-600',
};

// modal chỉnh sửa Đơn hàng — kèm bảng dòng hàng (diff create/update/delete khi lưu). Đơn hàng chỉ
// còn 1 khóa ngoại chính (quotationId) — khách hàng/liên hệ/cơ hội tra qua báo giá, hiển thị read-only
export function OrderEditModal({ item, onClose }: Props) {
    const qc = useQueryClient();
    const { mutateAsync, isPending } = useUpdateOrder();
    const { data: products = [] } = useProductList();
    const [form, setForm] = useState<UpdateOrderPayload>({
        quotationId: null, ownerId: null, orderDate: null, deliveryDate: null, note: null,
    });
    const [rows, setRows] = useState<LineItemRow[]>([]);
    const [originalRows, setOriginalRows] = useState<LineItemRow[]>([]);
    const [saving, setSaving] = useState(false);
    const [quotation, setQuotation] = useState<QuotationResult | null>(null);

    const productOptions = useMemo<ProductOption[]>(
        () => products.map((p) => ({ value: String(p.id), label: `${p.sku} — ${p.name}`, unit: p.unit ?? '', price: p.basePrice ?? 0, vatRate: p.vatRate ?? 0 })),
        [products],
    );

    useEffect(() => {
        if (!item) return;
        setForm({
            quotationId: item.quotationId, ownerId: item.ownerId,
            orderDate: item.orderDate, deliveryDate: item.deliveryDate, note: item.note,
        });
        setQuotation(null);
        if (item.quotationId != null) {
            quotationService.getById(item.quotationId).then(r => setQuotation(r.data.data));
        }
        orderService.getItems(item.id).then((r) => {
            const loaded = r.data.data.map(fromItemResult);
            setRows(loaded);
            setOriginalRows(loaded);
        });
    }, [item]);

    const [errors, setErrors] = useState<Record<string, string>>({});
    // xóa lỗi của một ô ngay khi người dùng gõ lại
    const clearError = (key: string) =>
        setErrors((prev) => (prev[key] ? { ...prev, [key]: '' } : prev));

    // đổi báo giá nguồn -> fetch chi tiết để hiển thị khách hàng/liên hệ/cơ hội read-only
    const onPickQuotation = async (v: string) => {
        setForm(f => ({ ...f, quotationId: v ? Number(v) : null }));
        setQuotation(null);
        if (!v) return;
        const q = (await quotationService.getById(Number(v))).data.data;
        setQuotation(q);
    };

    const { confirmSave } = useConfirm();
    const formRef = useRef<HTMLFormElement>(null);
    useFormKeyboardNav(formRef, {
        onSubmit: () => formRef.current?.requestSubmit(),
        onCancel: onClose,
        enabled: !!item,
    });

    if (!item) return null;

    // lưu đơn hàng + đồng bộ dòng hàng — lỗi hiện đỏ dưới ô, popup xác nhận chỉ mở khi dữ liệu đã hợp lệ
    const handleSubmit = async (e: FormEvent) => {
        e.preventDefault();

        // bước kiểm tra dữ liệu
        const errs = collectErrors({
            deliveryDate: dateRangeError(form.orderDate, form.deliveryDate, 'ngày đặt hàng', 'Ngày giao hàng'),
            items: validateLineItems(rows),
        });
        setErrors(errs);
        if (Object.keys(errs).length > 0) return;

        // bước hỏi xác nhận
        if (!(await confirmSave('đơn hàng'))) return;
        setSaving(true);
        try {
            // bước lưu header
            await mutateAsync({ id: item.id, payload: form });

            // bước đồng bộ dòng hàng theo diff rồi làm mới cache
            const { toCreate, toUpdate, toDelete } = diffLineItems(originalRows, rows);
            await Promise.all([
                ...toCreate.map((r) => orderService.createItem(item.id, toItemPayload(r))),
                ...toUpdate.map((r) => orderService.updateItem(item.id, r.backendId as number, toItemPayload(r))),
                ...toDelete.map((id) => orderService.deleteItem(item.id, id)),
            ]);
            qc.invalidateQueries({ queryKey: ['order-items', item.id] });
            onClose();
        } finally {
            setSaving(false);
        }
    };

    const inp = 'w-full border border-gray-300 rounded-btn px-3 py-1.5 text-md text-text-main focus:outline-none focus:border-primary';
    const lbl = 'block text-sm font-medium text-gray-700 mb-1';
    const busy = isPending || saving;

    return (
        <div className="fixed inset-0 z-[9999] flex items-center justify-center bg-black/40" onClick={onClose}>
            <div className="bg-white rounded-card shadow-lg w-full max-w-3xl mx-4 max-h-[90vh] overflow-y-auto" onClick={(e) => e.stopPropagation()}>
                <div className="flex items-center justify-between px-5 py-4 border-b border-gray-200">
                    <h2 className="text-lg font-semibold text-text-main">Chỉnh sửa Đơn hàng</h2>
                    <button onClick={onClose} className="p-1 rounded hover:bg-gray-100 text-gray-500"><FiX size={18} /></button>
                </div>
                <form ref={formRef} onSubmit={handleSubmit} noValidate className="px-5 py-4 space-y-3">
                    <div>
                        <label className={lbl}>Trạng thái (đổi qua hành động)</label>
                        <span className={`inline-block px-2 py-1.5 rounded text-sm font-medium ${ORDER_STATUS_COLORS[item.status] ?? 'bg-gray-100 text-gray-600'}`}>
                            {ORDER_STATUS_LABELS[item.status] ?? item.status}
                        </span>
                    </div>
                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <label className={lbl}>Ngày đơn hàng</label>
                            <DateInput value={form.orderDate ?? ''} onChange={v => setForm(f => ({ ...f, orderDate: v || null }))} />
                        </div>
                        <div>
                            <label className={lbl}>Ngày giao dự kiến</label>
                            <FieldError error={errors.deliveryDate}>
                                <DateInput value={form.deliveryDate ?? ''} onChange={v => { setForm(f => ({ ...f, deliveryDate: v || null })); clearError('deliveryDate'); }} />
                            </FieldError>
                        </div>
                    </div>
                    <div>
                        <label className={lbl}>Báo giá nguồn</label>
                        <RecordPicker module="quotation"
                            value={form.quotationId != null ? String(form.quotationId) : ''}
                            onChange={onPickQuotation}
                            fallbackLabel={item.quotationCode} />
                    </div>
                    <DerivedContextBox rows={[
                        { label: 'Khách hàng', value: quotation?.customerName },
                        { label: 'Liên hệ', value: quotation?.contactName },
                        { label: 'Cơ hội', value: quotation?.opportunityName },
                    ]} />
                    <div>
                        <label className={lbl}>Hàng hóa</label>
                        <ProductLineItemsTable rows={rows} onChange={setRows} productOptions={productOptions} showUnit showTax />
                        {errors.items && <p className="text-xs text-danger mt-1">{errors.items}</p>}
                    </div>
                    <div>
                        <label className={lbl}>Ghi chú</label>
                        <textarea className={inp} rows={2} value={form.note ?? ''} onChange={e => setForm(f => ({ ...f, note: e.target.value || null }))} />
                    </div>
                    <ModalFooter onCancel={onClose} saving={busy} />
                </form>
            </div>
        </div>
    );
}
