import { useState, type FormEvent, useEffect, useMemo } from 'react';
import { FiX } from 'react-icons/fi';
import type { OrderResult, UpdateOrderPayload } from '../types/orderTypes';
import { useUpdateOrder } from '../hooks/useUpdateOrder';
import { orderService } from '../services/orderService';
import { useProductList } from '@/features/san-pham/hooks/useProductList';
import { useWarehouseList } from '@/features/kho-hang/hooks/useWarehouseList';
import { ProductLineItemsTable } from '@/shared/components/form/ProductLineItemsTable';
import {
    type LineItemRow,
    type ProductOption,
    fromItemResult,
    diffLineItems,
    computeTotals,
    toItemPayload,
} from '@/shared/components/form/productLineItem';

interface Props {
    item: OrderResult | null;
    onClose: () => void;
}

const ORDER_STATUSES = ['draft', 'confirmed', 'processing', 'completed', 'cancelled'];
const ORDER_STATUS_LABELS: Record<string, string> = {
    draft: 'Nháp', confirmed: 'Đã xác nhận', processing: 'Đang xử lý',
    completed: 'Hoàn thành', cancelled: 'Đã hủy',
};
const PAYMENT_STATUSES = ['unpaid', 'partial', 'paid'];
const PAYMENT_STATUS_LABELS: Record<string, string> = { unpaid: 'Chưa thanh toán', partial: 'Một phần', paid: 'Đã thanh toán' };
const ORDER_TYPES = ['standard', 'parent', 'child'];
const ORDER_TYPE_LABELS: Record<string, string> = { standard: 'Tiêu chuẩn', parent: 'Đơn cha', child: 'Đơn con' };

export function OrderEditModal({ item, onClose }: Props) {
    const { mutateAsync, isPending } = useUpdateOrder();
    const { data: products = [] } = useProductList();
    const { data: warehouses = [] } = useWarehouseList();
    const [form, setForm] = useState<UpdateOrderPayload>({
        customerId: null, contactId: null, ownerId: null, warehouseId: null,
        orderType: 'standard', orderDate: null, status: 'draft', paymentStatus: 'unpaid',
        currency: 'VND', exchangeRate: 1, creditDays: null, paymentDueDate: null, isInvoiced: false,
        receiverName: null, receiverPhone: null,
        subtotal: null, discount: null, tax: null, total: null, note: null,
    });
    const [rows, setRows] = useState<LineItemRow[]>([]);
    const [originalRows, setOriginalRows] = useState<LineItemRow[]>([]);
    const [saving, setSaving] = useState(false);

    const productOptions = useMemo<ProductOption[]>(
        () => products.map((p) => ({ value: String(p.id), label: `${p.sku} — ${p.name}`, unit: p.unit ?? '', price: p.basePrice ?? 0, vatRate: p.vatRate ?? 0 })),
        [products],
    );
    const warehouseOptions = useMemo(() => warehouses.map((w) => ({ value: String(w.id), label: w.name })), [warehouses]);

    useEffect(() => {
        if (!item) return;
        setForm({
            customerId: item.customerId, contactId: item.contactId, quotationId: item.quotationId,
            opportunityId: item.opportunityId, ownerId: item.ownerId, executorUnitId: item.executorUnitId,
            warehouseId: item.warehouseId, parentOrderId: item.parentOrderId,
            orderType: item.orderType, orderDate: item.orderDate,
            currency: item.currency, exchangeRate: item.exchangeRate,
            status: item.status, paymentStatus: item.paymentStatus,
            creditDays: item.creditDays, paymentDueDate: item.paymentDueDate, isInvoiced: item.isInvoiced,
            receiverName: item.receiverName, receiverPhone: item.receiverPhone,
            subtotal: item.subtotal, discount: item.discount, tax: item.tax, total: item.total, note: item.note,
        });
        orderService.getItems(item.id).then((r) => {
            const loaded = r.data.data.map(fromItemResult);
            setRows(loaded);
            setOriginalRows(loaded);
        });
    }, [item]);

    if (!item) return null;

    const handleSubmit = async (e: FormEvent) => {
        e.preventDefault();
        setSaving(true);
        try {
            const totals = computeTotals(rows);
            await mutateAsync({
                id: item.id,
                payload: { ...form, subtotal: totals.subtotal, discount: totals.discount, tax: totals.tax, total: totals.total },
            });
            const { toCreate, toUpdate, toDelete } = diffLineItems(originalRows, rows);
            await Promise.all([
                ...toCreate.map((r) => orderService.createItem(item.id, toItemPayload(r))),
                ...toUpdate.map((r) => orderService.updateItem(item.id, r.backendId as number, toItemPayload(r))),
                ...toDelete.map((id) => orderService.deleteItem(item.id, id)),
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
                    <h2 className="text-lg font-semibold text-text-main">Chỉnh sửa đơn hàng</h2>
                    <button onClick={onClose} className="p-1 rounded hover:bg-gray-100 text-gray-500"><FiX size={18} /></button>
                </div>
                <form onSubmit={handleSubmit} className="px-5 py-4 space-y-3">
                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <label className={lbl}>Loại đơn</label>
                            <select className={inp} value={form.orderType} onChange={e => setForm(f => ({ ...f, orderType: e.target.value }))}>
                                {ORDER_TYPES.map(t => <option key={t} value={t}>{ORDER_TYPE_LABELS[t]}</option>)}
                            </select>
                        </div>
                        <div>
                            <label className={lbl}>Ngày đặt hàng</label>
                            <input type="date" className={inp} value={form.orderDate ?? ''} onChange={e => setForm(f => ({ ...f, orderDate: e.target.value || null }))} />
                        </div>
                    </div>
                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <label className={lbl}>Trạng thái đơn</label>
                            <select className={inp} value={form.status} onChange={e => setForm(f => ({ ...f, status: e.target.value }))}>
                                {ORDER_STATUSES.map(s => <option key={s} value={s}>{ORDER_STATUS_LABELS[s]}</option>)}
                            </select>
                        </div>
                        <div>
                            <label className={lbl}>Trạng thái thanh toán</label>
                            <select className={inp} value={form.paymentStatus} onChange={e => setForm(f => ({ ...f, paymentStatus: e.target.value }))}>
                                {PAYMENT_STATUSES.map(s => <option key={s} value={s}>{PAYMENT_STATUS_LABELS[s]}</option>)}
                            </select>
                        </div>
                    </div>
                    <div className="grid grid-cols-3 gap-3">
                        <div>
                            <label className={lbl}>Kho hàng</label>
                            <select className={inp} value={form.warehouseId ?? ''} onChange={e => setForm(f => ({ ...f, warehouseId: e.target.value ? +e.target.value : null }))}>
                                <option value="">-- Chọn --</option>
                                {warehouseOptions.map(w => <option key={w.value} value={w.value}>{w.label}</option>)}
                            </select>
                        </div>
                        <div>
                            <label className={lbl}>Hạn thanh toán</label>
                            <input type="date" className={inp} value={form.paymentDueDate ?? ''} onChange={e => setForm(f => ({ ...f, paymentDueDate: e.target.value || null }))} />
                        </div>
                        <div className="flex items-end pb-1.5">
                            <label className="flex items-center gap-2 cursor-pointer">
                                <input type="checkbox" className="w-4 h-4 accent-primary" checked={form.isInvoiced ?? false} onChange={e => setForm(f => ({ ...f, isInvoiced: e.target.checked }))} />
                                <span className="text-sm text-text-main">Đã xuất hóa đơn</span>
                            </label>
                        </div>
                    </div>
                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <label className={lbl}>Người nhận</label>
                            <input className={inp} value={form.receiverName ?? ''} onChange={e => setForm(f => ({ ...f, receiverName: e.target.value || null }))} />
                        </div>
                        <div>
                            <label className={lbl}>ĐT người nhận</label>
                            <input className={inp} value={form.receiverPhone ?? ''} onChange={e => setForm(f => ({ ...f, receiverPhone: e.target.value || null }))} />
                        </div>
                    </div>
                    <div>
                        <label className={lbl}>Hàng hóa</label>
                        <ProductLineItemsTable rows={rows} onChange={setRows} productOptions={productOptions} showUnit showTax showWarehouse warehouseOptions={warehouseOptions} />
                    </div>
                    <div>
                        <label className={lbl}>Ghi chú</label>
                        <textarea className={inp} rows={2} value={form.note ?? ''} onChange={e => setForm(f => ({ ...f, note: e.target.value || null }))} />
                    </div>
                    <div className="flex justify-end gap-3 pt-2 border-t border-gray-100">
                        <button type="button" onClick={onClose} className="px-4 py-1.5 rounded-btn border border-gray-300 text-md text-text-main hover:bg-gray-50">Hủy</button>
                        <button type="submit" disabled={busy} className="px-4 py-1.5 rounded-btn bg-primary text-white text-md hover:opacity-90 disabled:opacity-50">
                            {busy ? 'Đang lưu...' : 'Lưu'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}
