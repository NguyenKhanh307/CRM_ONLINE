import { useState, type FormEvent, useEffect } from 'react';
import { FiX } from 'react-icons/fi';
import type { OpportunityResult, UpdateOpportunityPayload } from '../types/opportunityTypes';
import { useUpdateOpportunity } from '../hooks/useUpdateOpportunity';

interface Props {
    item: OpportunityResult | null;
    onClose: () => void;
}

const OPP_STATUSES = ['open', 'won', 'lost'];
const OPP_STATUS_LABELS: Record<string, string> = { open: 'Đang mở', won: 'Thắng', lost: 'Thua' };

export function OpportunityEditModal({ item, onClose }: Props) {
    const { mutate, isPending } = useUpdateOpportunity();
    const [form, setForm] = useState<UpdateOpportunityPayload>({
        name: '', customerId: null, contactId: null, ownerId: null,
        stageId: null, amount: null, probability: null, expectedCloseDate: null, status: 'open',
    });

    useEffect(() => {
        if (!item) return;
        setForm({
            name: item.name, customerId: item.customerId, contactId: item.contactId,
            ownerId: item.ownerId, stageId: item.stageId, amount: item.amount,
            probability: item.probability, expectedCloseDate: item.expectedCloseDate, status: item.status,
        });
    }, [item]);

    if (!item) return null;

    const handleSubmit = (e: FormEvent) => {
        e.preventDefault();
        mutate({ id: item.id, payload: form }, { onSuccess: onClose });
    };

    const inp = 'w-full border border-gray-300 rounded-btn px-3 py-1.5 text-md text-text-main focus:outline-none focus:border-primary';
    const lbl = 'block text-sm font-medium text-gray-700 mb-1';

    return (
        <div className="fixed inset-0 z-[9999] flex items-center justify-center bg-black/40" onClick={onClose}>
            <div className="bg-white rounded-card shadow-lg w-full max-w-lg mx-4 max-h-[90vh] overflow-y-auto" onClick={(e) => e.stopPropagation()}>
                <div className="flex items-center justify-between px-5 py-4 border-b border-gray-200">
                    <h2 className="text-lg font-semibold text-text-main">Chỉnh sửa cơ hội</h2>
                    <button onClick={onClose} className="p-1 rounded hover:bg-gray-100 text-gray-500"><FiX size={18} /></button>
                </div>
                <form onSubmit={handleSubmit} className="px-5 py-4 space-y-3">
                    <div>
                        <label className={lbl}>Tên cơ hội <span className="text-danger">*</span></label>
                        <input className={inp} required value={form.name} onChange={e => setForm(f => ({ ...f, name: e.target.value }))} />
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
                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <label className={lbl}>Giá trị (đ)</label>
                            <input type="number" className={inp} value={form.amount ?? ''} onChange={e => setForm(f => ({ ...f, amount: e.target.value ? +e.target.value : null }))} />
                        </div>
                        <div>
                            <label className={lbl}>Ngày đóng dự kiến</label>
                            <input type="date" className={inp} value={form.expectedCloseDate ?? ''} onChange={e => setForm(f => ({ ...f, expectedCloseDate: e.target.value || null }))} />
                        </div>
                    </div>
                    <div className="flex justify-end gap-3 pt-2 border-t border-gray-100">
                        <button type="button" onClick={onClose} className="px-4 py-1.5 rounded-btn border border-gray-300 text-md text-text-main hover:bg-gray-50">Hủy</button>
                        <button type="submit" disabled={isPending} className="px-4 py-1.5 rounded-btn bg-primary text-white text-md hover:opacity-90 disabled:opacity-50">
                            {isPending ? 'Đang lưu...' : 'Lưu'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}
