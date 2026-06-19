import { useState, type FormEvent, useEffect, useMemo } from 'react';
import { FiX } from 'react-icons/fi';
import type { QuotationResult, UpdateQuotationPayload } from '../types/quotationTypes';
import { useUpdateQuotation } from '../hooks/useUpdateQuotation';
import { quotationService } from '../services/quotationService';
import { useProductList } from '@/features/san-pham/hooks/useProductList';
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
    item: QuotationResult | null;
    onClose: () => void;
}

const QUOTATION_STATUSES = ['draft', 'sent', 'approved', 'rejected', 'expired'];
const QUOTATION_STATUS_LABELS: Record<string, string> = {
    draft: 'Nháp', sent: 'Đã gửi', approved: 'Đã duyệt', rejected: 'Từ chối', expired: 'Hết hạn',
};

export function QuotationEditModal({ item, onClose }: Props) {
    const { mutateAsync, isPending } = useUpdateQuotation();
    const { data: products = [] } = useProductList();
    const [form, setForm] = useState<UpdateQuotationPayload>({
        customerId: null, contactId: null, ownerId: null, quoteDate: null,
        validUntil: null, currency: 'VND', exchangeRate: 1, status: 'draft',
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
            customerId: item.customerId, contactId: item.contactId, opportunityId: item.opportunityId,
            ownerId: item.ownerId, quoteDate: item.quoteDate, validUntil: item.validUntil,
            currency: item.currency, exchangeRate: item.exchangeRate, status: item.status,
            subtotal: item.subtotal, discount: item.discount, tax: item.tax, total: item.total, note: item.note,
        });
        quotationService.getItems(item.id).then((r) => {
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
                ...toCreate.map((r) => quotationService.createItem(item.id, toItemPayload(r))),
                ...toUpdate.map((r) => quotationService.updateItem(item.id, r.backendId as number, toItemPayload(r))),
                ...toDelete.map((id) => quotationService.deleteItem(item.id, id)),
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
                    <h2 className="text-lg font-semibold text-text-main">Chỉnh sửa báo giá</h2>
                    <button onClick={onClose} className="p-1 rounded hover:bg-gray-100 text-gray-500"><FiX size={18} /></button>
                </div>
                <form onSubmit={handleSubmit} className="px-5 py-4 space-y-3">
                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <label className={lbl}>Ngày báo giá</label>
                            <input type="date" className={inp} value={form.quoteDate ?? ''} onChange={e => setForm(f => ({ ...f, quoteDate: e.target.value || null }))} />
                        </div>
                        <div>
                            <label className={lbl}>Hiệu lực đến</label>
                            <input type="date" className={inp} value={form.validUntil ?? ''} onChange={e => setForm(f => ({ ...f, validUntil: e.target.value || null }))} />
                        </div>
                    </div>
                    <div className="grid grid-cols-3 gap-3">
                        <div>
                            <label className={lbl}>Trạng thái</label>
                            <select className={inp} value={form.status} onChange={e => setForm(f => ({ ...f, status: e.target.value }))}>
                                {QUOTATION_STATUSES.map(s => <option key={s} value={s}>{QUOTATION_STATUS_LABELS[s]}</option>)}
                            </select>
                        </div>
                        <div>
                            <label className={lbl}>Tiền tệ</label>
                            <input className={inp} value={form.currency ?? ''} onChange={e => setForm(f => ({ ...f, currency: e.target.value || null }))} />
                        </div>
                        <div>
                            <label className={lbl}>Tỷ giá</label>
                            <input type="number" className={inp} value={form.exchangeRate ?? ''} onChange={e => setForm(f => ({ ...f, exchangeRate: e.target.value ? +e.target.value : null }))} />
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
