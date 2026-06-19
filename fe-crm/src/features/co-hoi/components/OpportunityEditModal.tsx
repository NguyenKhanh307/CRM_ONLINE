import { useState, type FormEvent, useEffect, useMemo } from 'react';
import { FiX } from 'react-icons/fi';
import type { OpportunityResult, UpdateOpportunityPayload } from '../types/opportunityTypes';
import { useUpdateOpportunity } from '../hooks/useUpdateOpportunity';
import { opportunityService } from '../services/opportunityService';
import { useProductList } from '@/features/san-pham/hooks/useProductList';
import { ProductLineItemsTable } from '@/shared/components/form/ProductLineItemsTable';
import {
    type LineItemRow,
    type ProductOption,
    fromItemResult,
    diffLineItems,
    toItemPayload,
} from '@/shared/components/form/productLineItem';

interface Props {
    item: OpportunityResult | null;
    onClose: () => void;
}

const OPP_STATUSES = ['open', 'won', 'lost'];
const OPP_STATUS_LABELS: Record<string, string> = { open: 'Đang mở', won: 'Thắng', lost: 'Thua' };

export function OpportunityEditModal({ item, onClose }: Props) {
    const { mutateAsync, isPending } = useUpdateOpportunity();
    const { data: products = [] } = useProductList();
    const [form, setForm] = useState<UpdateOpportunityPayload>({
        name: '', opportunityType: null, customerId: null, contactId: null, ownerId: null,
        stageId: null, amount: null, expectedRevenue: null, probability: null, expectedCloseDate: null,
        source: null, winLossReason: null, description: null, status: 'open',
    });
    const [rows, setRows] = useState<LineItemRow[]>([]);
    const [originalRows, setOriginalRows] = useState<LineItemRow[]>([]);
    const [saving, setSaving] = useState(false);

    const productOptions = useMemo<ProductOption[]>(
        () => products.map((p) => ({ value: String(p.id), label: `${p.sku} — ${p.name}`, unit: p.unit ?? '', price: p.basePrice ?? 0 })),
        [products],
    );

    useEffect(() => {
        if (!item) return;
        setForm({
            name: item.name, opportunityType: item.opportunityType, customerId: item.customerId,
            contactId: item.contactId, ownerId: item.ownerId, stageId: item.stageId, amount: item.amount,
            expectedRevenue: item.expectedRevenue, probability: item.probability,
            expectedCloseDate: item.expectedCloseDate, source: item.source,
            winLossReason: item.winLossReason, description: item.description, status: item.status,
        });
        opportunityService.getItems(item.id).then((r) => {
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
            await mutateAsync({ id: item.id, payload: form });
            const { toCreate, toUpdate, toDelete } = diffLineItems(originalRows, rows);
            await Promise.all([
                ...toCreate.map((r) => opportunityService.createItem(item.id, toItemPayload(r))),
                ...toUpdate.map((r) => opportunityService.updateItem(item.id, r.backendId as number, toItemPayload(r))),
                ...toDelete.map((id) => opportunityService.deleteItem(item.id, id)),
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
                    <h2 className="text-lg font-semibold text-text-main">Chỉnh sửa cơ hội</h2>
                    <button onClick={onClose} className="p-1 rounded hover:bg-gray-100 text-gray-500"><FiX size={18} /></button>
                </div>
                <form onSubmit={handleSubmit} className="px-5 py-4 space-y-3">
                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <label className={lbl}>Tên cơ hội <span className="text-danger">*</span></label>
                            <input className={inp} required value={form.name} onChange={e => setForm(f => ({ ...f, name: e.target.value }))} />
                        </div>
                        <div>
                            <label className={lbl}>Loại cơ hội</label>
                            <input className={inp} value={form.opportunityType ?? ''} onChange={e => setForm(f => ({ ...f, opportunityType: e.target.value || null }))} />
                        </div>
                    </div>
                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <label className={lbl}>Trạng thái</label>
                            <select className={inp} value={form.status} onChange={e => setForm(f => ({ ...f, status: e.target.value }))}>
                                {OPP_STATUSES.map(s => <option key={s} value={s}>{OPP_STATUS_LABELS[s]}</option>)}
                            </select>
                        </div>
                        <div>
                            <label className={lbl}>Xác suất (%)</label>
                            <input type="number" min="0" max="100" className={inp} value={form.probability ?? ''} onChange={e => setForm(f => ({ ...f, probability: e.target.value ? +e.target.value : null }))} />
                        </div>
                    </div>
                    <div className="grid grid-cols-3 gap-3">
                        <div>
                            <label className={lbl}>Giá trị (đ)</label>
                            <input type="number" className={inp} value={form.amount ?? ''} onChange={e => setForm(f => ({ ...f, amount: e.target.value ? +e.target.value : null }))} />
                        </div>
                        <div>
                            <label className={lbl}>Doanh số kỳ vọng</label>
                            <input type="number" className={inp} value={form.expectedRevenue ?? ''} onChange={e => setForm(f => ({ ...f, expectedRevenue: e.target.value ? +e.target.value : null }))} />
                        </div>
                        <div>
                            <label className={lbl}>Ngày đóng dự kiến</label>
                            <input type="date" className={inp} value={form.expectedCloseDate ?? ''} onChange={e => setForm(f => ({ ...f, expectedCloseDate: e.target.value || null }))} />
                        </div>
                    </div>
                    <div>
                        <label className={lbl}>Hàng hóa</label>
                        <ProductLineItemsTable rows={rows} onChange={setRows} productOptions={productOptions} />
                    </div>
                    <div>
                        <label className={lbl}>Mô tả</label>
                        <textarea className={inp} rows={2} value={form.description ?? ''} onChange={e => setForm(f => ({ ...f, description: e.target.value || null }))} />
                    </div>
                    <div>
                        <label className={lbl}>Lý do thắng/thua</label>
                        <input className={inp} value={form.winLossReason ?? ''} onChange={e => setForm(f => ({ ...f, winLossReason: e.target.value || null }))} />
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
