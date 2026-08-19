import { useRef, useState, type FormEvent, useEffect, useMemo } from 'react';
import { notify } from '@/core/data/dataBus';
import { collectErrors, dateRangeError } from '@/shared/utils/validators';
import { FieldError } from '@/shared/components/form/FormField';
import { ModalFooter } from '@/shared/components/ModalFooter';
import { useConfirm } from '@/shared/confirm/useConfirm';
import { useFormKeyboardNav } from '@/shared/keyboard/useFormKeyboardNav';
import { FiX } from 'react-icons/fi';
import type { InvoiceResult, UpdateInvoicePayload } from '../types/invoiceTypes';
import { useUpdateInvoice } from '../hooks/useUpdateInvoice';
import { invoiceService } from '../services/invoiceService';
import { orderService } from '@/features/don-hang/services/orderService';
import { quotationService } from '@/features/bao-gia/services/quotationService';
import type { OrderResult } from '@/features/don-hang/types/orderTypes';
import type { QuotationResult } from '@/features/bao-gia/types/quotationTypes';
import { PaymentSchedulesTable } from './PaymentSchedulesTable';
import { useProductList } from '@/features/san-pham/hooks/useProductList';
import { useActiveUsers } from '@/features/users/hooks/useActiveUsers';
import { SearchableSelect } from '@/shared/components/SearchableSelect';
import { RecordPicker } from '@/shared/components/form/RecordPicker';
import { DerivedContextBox } from '@/shared/components/form/DerivedContextBox';
import { ProductLineItemsTable } from '@/shared/components/form/ProductLineItemsTable';
import { DateInput } from '@/shared/components/form/DateInput';
import {
    type LineItemRow,
    type ProductOption,
    fromItemResult,
    diffLineItems,
    toItemPayload, validateLineItems } from '@/shared/components/form/productLineItem';

interface Props {
    item: InvoiceResult | null;
    onClose: () => void;
}

const INVOICE_STATUS_LABELS: Record<string, string> = {
    draft: 'Nháp', sent: 'Đã gửi', partially_paid: 'Thanh toán một phần',
    paid: 'Đã thanh toán', cancelled: 'Đã hủy',
};
const INVOICE_STATUS_COLORS: Record<string, string> = {
    draft: 'bg-gray-100 text-gray-600', sent: 'bg-blue-100 text-blue-700', partially_paid: 'bg-yellow-100 text-yellow-700',
    paid: 'bg-green-100 text-green-700', cancelled: 'bg-red-100 text-red-600',
};
const PAYMENT_STATUS_LABELS: Record<string, string> = { unpaid: 'Chưa thanh toán', partial: 'Một phần', paid: 'Đã thanh toán' };
const PAYMENT_STATUS_COLORS: Record<string, string> = {
    unpaid: 'bg-red-100 text-red-600', partial: 'bg-yellow-100 text-yellow-700', paid: 'bg-green-100 text-green-700',
};

// Hóa đơn chỉ còn 1 khóa ngoại chính (orderId) — khách hàng/liên hệ/báo giá tra qua đơn hàng ->
// báo giá, hiển thị read-only để giữ ngữ cảnh
export function InvoiceEditModal({ item, onClose }: Props) {
    const { mutate, isPending } = useUpdateInvoice();
    const { data: products = [] } = useProductList();
    const { data: users = [] } = useActiveUsers();
    const userOptions = useMemo(() => users.map((u) => ({ value: String(u.id), label: u.fullName })), [users]);
    const [form, setForm] = useState<UpdateInvoicePayload>({
        orderId: null, ownerId: null, invoiceDate: null, dueDate: null, note: null,
    });
    const [rows, setRows] = useState<LineItemRow[]>([]);
    const [originalRows, setOriginalRows] = useState<LineItemRow[]>([]);
    const [saving, setSaving] = useState(false);
    const [order, setOrder] = useState<OrderResult | null>(null);
    const [quotation, setQuotation] = useState<QuotationResult | null>(null);

    const productOptions = useMemo<ProductOption[]>(
        () => products.map((p) => ({ value: String(p.id), label: `${p.sku} — ${p.name}`, unit: p.unit ?? '', price: p.basePrice ?? 0, vatRate: p.vatRate ?? 0 })),
        [products],
    );

    useEffect(() => {
        if (!item) return;
        setForm({
            orderId: item.orderId, ownerId: item.ownerId,
            invoiceDate: item.invoiceDate, dueDate: item.dueDate, note: item.note,
        });
        setOrder(null);
        setQuotation(null);
        if (item.orderId != null) loadOrderContext(item.orderId);
        invoiceService.getItems(item.id).then((r) => {
            const loaded = r.data.data.map(fromItemResult);
            setRows(loaded);
            setOriginalRows(loaded);
        });
    }, [item]);

    const [errors, setErrors] = useState<Record<string, string>>({});
    // xóa lỗi của một ô ngay khi người dùng gõ lại
    const clearError = (key: string) =>
        setErrors((prev) => (prev[key] ? { ...prev, [key]: '' } : prev));

    // tải chuỗi Đơn hàng -> Báo giá để hiển thị khách hàng/liên hệ read-only
    const loadOrderContext = async (orderId: number) => {
        const o = (await orderService.getById(orderId)).data.data;
        setOrder(o);
        if (o.quotationId != null) {
            const q = (await quotationService.getById(o.quotationId)).data.data;
            setQuotation(q);
        } else {
            setQuotation(null);
        }
    };

    // đổi đơn hàng -> fetch chi tiết chuỗi order -> quotation để hiển thị read-only
    const onPickOrder = async (v: string) => {
        setForm(f => ({ ...f, orderId: v ? Number(v) : null }));
        setOrder(null);
        setQuotation(null);
        if (!v) return;
        await loadOrderContext(Number(v));
    };

    const { confirmSave } = useConfirm();
    const formRef = useRef<HTMLFormElement>(null);
    useFormKeyboardNav(formRef, {
        onSubmit: () => formRef.current?.requestSubmit(),
        onCancel: onClose,
        enabled: !!item,
    });

    if (!item) return null;

    const handleSubmit = async (e: FormEvent) => {
        e.preventDefault();
        // lỗi nhập liệu hiện đỏ dưới ô; popup xác nhận chỉ mở khi dữ liệu đã hợp lệ
        const errs = collectErrors({
            dueDate: dateRangeError(form.invoiceDate, form.dueDate, 'ngày hóa đơn', 'Hạn thanh toán'),
            items: validateLineItems(rows),
        });
        setErrors(errs);
        if (Object.keys(errs).length > 0) return;

        if (!(await confirmSave('hóa đơn'))) return;
        setSaving(true);
        try {
            await mutate({ id: item.id, payload: form });
            const { toCreate, toUpdate, toDelete } = diffLineItems(originalRows, rows);
            await Promise.all([
                ...toCreate.map((r) => invoiceService.createItem(item.id, toItemPayload(r))),
                ...toUpdate.map((r) => invoiceService.updateItem(item.id, r.backendId as number, toItemPayload(r))),
                ...toDelete.map((id) => invoiceService.deleteItem(item.id, id)),
            ]);
            notify(`invoice-items:${item.id}`);
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
                    <h2 className="text-lg font-semibold text-text-main">Chỉnh sửa Hóa đơn</h2>
                    <button onClick={onClose} className="p-1 rounded hover:bg-gray-100 text-gray-500"><FiX size={18} /></button>
                </div>
                <form ref={formRef} onSubmit={handleSubmit} noValidate className="px-5 py-4 space-y-3">
                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <label className={lbl}>Trạng thái (đổi qua hành động)</label>
                            <span className={`inline-block px-2 py-1.5 rounded text-sm font-medium ${INVOICE_STATUS_COLORS[item.status] ?? 'bg-gray-100 text-gray-600'}`}>
                                {INVOICE_STATUS_LABELS[item.status] ?? item.status}
                            </span>
                        </div>
                        <div>
                            <label className={lbl}>Trạng thái thanh toán (theo đợt thanh toán)</label>
                            <span className={`inline-block px-2 py-1.5 rounded text-sm font-medium ${PAYMENT_STATUS_COLORS[item.paymentStatus] ?? 'bg-gray-100 text-gray-600'}`}>
                                {PAYMENT_STATUS_LABELS[item.paymentStatus] ?? item.paymentStatus}
                            </span>
                        </div>
                    </div>
                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <label className={lbl}>Ngày hóa đơn</label>
                            <DateInput value={form.invoiceDate ?? ''} onChange={v => setForm(f => ({ ...f, invoiceDate: v || null }))} />
                        </div>
                        <div>
                            <label className={lbl}>Hạn thanh toán</label>
                            <FieldError error={errors.dueDate}>
                                <DateInput value={form.dueDate ?? ''} onChange={v => { setForm(f => ({ ...f, dueDate: v || null })); clearError('dueDate'); }} />
                            </FieldError>
                        </div>
                    </div>
                    <div>
                        <label className={lbl}>Đơn hàng</label>
                        <RecordPicker module="order"
                            value={form.orderId != null ? String(form.orderId) : ''}
                            onChange={onPickOrder}
                            fallbackLabel={item.orderCode} />
                    </div>
                    <DerivedContextBox rows={[
                        { label: 'Khách hàng', value: quotation?.customerName },
                        { label: 'Liên hệ', value: quotation?.contactName },
                        { label: 'Báo giá nguồn', value: order?.quotationCode },
                    ]} />
                    <div>
                        <label className={lbl}>Người phụ trách</label>
                        <SearchableSelect
                            value={form.ownerId != null ? String(form.ownerId) : ''}
                            onChange={(v) => setForm(f => ({ ...f, ownerId: v ? Number(v) : null }))}
                            options={userOptions}
                            fallbackLabel={item.ownerName}
                        />
                    </div>
                    <div>
                        <label className={lbl}>Hàng hóa</label>
                        <ProductLineItemsTable rows={rows} onChange={setRows} productOptions={productOptions} showUnit showTax />
                        {errors.items && <p className="text-xs text-danger mt-1">{errors.items}</p>}
                    </div>
                    <div>
                        <label className={lbl}>Đợt thanh toán</label>
                        <PaymentSchedulesTable invoiceId={item.id} invoiceTotal={item.total ?? 0} />
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
