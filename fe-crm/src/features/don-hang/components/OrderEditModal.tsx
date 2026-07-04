import { useState, type FormEvent, useEffect, useMemo } from 'react';
import { FiX } from 'react-icons/fi';
import type { OrderResult, UpdateOrderPayload } from '../types/orderTypes';
import { useUpdateOrder } from '../hooks/useUpdateOrder';
import { orderService } from '../services/orderService';
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

export function OrderEditModal({ item, onClose }: Props) {
    const { mutateAsync, isPending } = useUpdateOrder();
    const { data: products = [] } = useProductList();
    const [form, setForm] = useState<UpdateOrderPayload>({
        customerId: null, contactId: null, ownerId: null,
        orderDate: null, deliveryDate: null,
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
            opportunityId: item.opportunityId, campaignId: item.campaignId, ownerId: item.ownerId,
            orderDate: item.orderDate, deliveryDate: item.deliveryDate,
            currency: item.currency, exchangeRate: item.exchangeRate,
            billingAddress: item.billingAddress, taxCode: item.taxCode,
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
                    <h2 className="text-lg font-semibold text-text-main">Chỉnh sửa Đơn hàng</h2>
                    <button onClick={onClose} className="p-1 rounded hover:bg-gray-100 text-gray-500"><FiX size={18} /></button>
                </div>
                <form onSubmit={handleSubmit} className="px-5 py-4 space-y-3">
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
                            <DateInput value={form.deliveryDate ?? ''} onChange={v => setForm(f => ({ ...f, deliveryDate: v || null }))} />
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
