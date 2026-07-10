import { useRef, useState, type FormEvent, useEffect, useMemo } from 'react';
import { ModalFooter } from '@/shared/components/ModalFooter';
import { useConfirm } from '@/shared/confirm/useConfirm';
import { useFormKeyboardNav } from '@/shared/keyboard/useFormKeyboardNav';
import { FiX } from 'react-icons/fi';
import type { InvoiceResult, UpdateInvoicePayload } from '../types/invoiceTypes';
import { useUpdateInvoice } from '../hooks/useUpdateInvoice';
import { invoiceService } from '../services/invoiceService';
import { PaymentSchedulesTable } from './PaymentSchedulesTable';
import { useProductList } from '@/features/san-pham/hooks/useProductList';
import { ProductLineItemsTable } from '@/shared/components/form/ProductLineItemsTable';
import { DateInput } from '@/shared/components/form/DateInput';
import {
    type LineItemRow,
    type ProductOption,
    fromItemResult,
    diffLineItems,
    computeTotals,
    toItemPayload,
} from '@/shared/components/form/productLineItem';

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

export function InvoiceEditModal({ item, onClose }: Props) {
    const { mutateAsync, isPending } = useUpdateInvoice();
    const { data: products = [] } = useProductList();
    const [form, setForm] = useState<UpdateInvoicePayload>({
        customerId: null, contactId: null, ownerId: null,
        invoiceDate: null, dueDate: null,
        currency: 'VND', exchangeRate: 1, billingAddress: null, taxCode: null,
        subtotal: null, discount: null, tax: null, total: null, note: null,
    });
    const [rows, setRows] = useState<LineItemRow[]>([]);
    const [originalRows, setOriginalRows] = useState<LineItemRow[]>([]);
    const [saving, setSaving] = useState(false);

    const productOptions = useMemo<ProductOption[]>(
        () => products.map((p) => ({ value: String(p.id), label: `${p.sku} — ${p.name}`, unit: p.unit ?? '', price: p.basePrice ?? 0, vatRate: p.vatRate ?? 0 })),
        [products],
    );

    useEffect(() => {
        if (!item) return;
        setForm({
            customerId: item.customerId, contactId: item.contactId, quotationId: item.quotationId,
            opportunityId: item.opportunityId, ownerId: item.ownerId,
            invoiceDate: item.invoiceDate, dueDate: item.dueDate,
            currency: item.currency, exchangeRate: item.exchangeRate,
            billingAddress: item.billingAddress, taxCode: item.taxCode,
            subtotal: item.subtotal, discount: item.discount, tax: item.tax, total: item.total, note: item.note,
        });
        invoiceService.getItems(item.id).then((r) => {
            const loaded = r.data.data.map(fromItemResult);
            setRows(loaded);
            setOriginalRows(loaded);
        });
    }, [item]);

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
        if (!(await confirmSave('hóa đơn'))) return;
        setSaving(true);
        try {
            const totals = computeTotals(rows);
            await mutateAsync({
                id: item.id,
                payload: { ...form, subtotal: totals.subtotal, discount: totals.discount, tax: totals.tax, total: totals.total },
            });
            const { toCreate, toUpdate, toDelete } = diffLineItems(originalRows, rows);
            await Promise.all([
                ...toCreate.map((r) => invoiceService.createItem(item.id, toItemPayload(r))),
                ...toUpdate.map((r) => invoiceService.updateItem(item.id, r.backendId as number, toItemPayload(r))),
                ...toDelete.map((id) => invoiceService.deleteItem(item.id, id)),
            ]);
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
                <form ref={formRef} onSubmit={handleSubmit} className="px-5 py-4 space-y-3">
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
                            <DateInput value={form.dueDate ?? ''} onChange={v => setForm(f => ({ ...f, dueDate: v || null }))} />
                        </div>
                    </div>
                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <label className={lbl}>Mã số thuế</label>
                            <input className={inp} value={form.taxCode ?? ''} onChange={e => setForm(f => ({ ...f, taxCode: e.target.value || null }))} />
                        </div>
                        <div>
                            <label className={lbl}>Địa chỉ xuất HĐ</label>
                            <input className={inp} value={form.billingAddress ?? ''} onChange={e => setForm(f => ({ ...f, billingAddress: e.target.value || null }))} />
                        </div>
                    </div>
                    <div>
                        <label className={lbl}>Hàng hóa</label>
                        <ProductLineItemsTable rows={rows} onChange={setRows} productOptions={productOptions} showUnit showTax />
                    </div>
                    <div>
                        <label className={lbl}>Đợt thanh toán</label>
                        <PaymentSchedulesTable invoiceId={item.id} />
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
