import { useState, type FormEvent, useEffect } from 'react';
import { FiX } from 'react-icons/fi';
import type { QuotationResult, UpdateQuotationPayload } from '../types/quotationTypes';
import { useUpdateQuotation } from '../hooks/useUpdateQuotation';

interface Props {
    item: QuotationResult | null;
    onClose: () => void;
}

const QUOTATION_STATUSES = ['draft', 'sent', 'approved', 'rejected', 'expired'];
const QUOTATION_STATUS_LABELS: Record<string, string> = {
    draft: 'Nháp', sent: 'Đã gửi', approved: 'Đã duyệt', rejected: 'Từ chối', expired: 'Hết hạn',
};

export function QuotationEditModal({ item, onClose }: Props) {
    const { mutate, isPending } = useUpdateQuotation();
    const [form, setForm] = useState<UpdateQuotationPayload>({
        customerId: null, contactId: null, ownerId: null, quoteDate: null,
        validUntil: null, status: 'draft', subtotal: null, discount: null, tax: null, total: null, note: null,
    });

    useEffect(() => {
        if (!item) return;
        setForm({
            customerId: item.customerId, contactId: item.contactId, ownerId: item.ownerId,
            quoteDate: item.quoteDate, validUntil: item.validUntil, status: item.status,
            subtotal: item.subtotal, discount: item.discount, tax: item.tax, total: item.total, note: item.note,
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
                    <div>
                        <label className={lbl}>Trạng thái</label>
                        <select className={inp} value={form.status} onChange={e => setForm(f => ({ ...f, status: e.target.value }))}>
                            {QUOTATION_STATUSES.map(s => <option key={s} value={s}>{QUOTATION_STATUS_LABELS[s]}</option>)}
                        </select>
                    </div>
                    <div className="grid grid-cols-3 gap-3">
                        <div>
                            <label className={lbl}>Chiết khấu</label>
                            <input type="number" className={inp} value={form.discount ?? ''} onChange={e => setForm(f => ({ ...f, discount: e.target.value ? +e.target.value : null }))} />
                        </div>
                        <div>
                            <label className={lbl}>Thuế</label>
                            <input type="number" className={inp} value={form.tax ?? ''} onChange={e => setForm(f => ({ ...f, tax: e.target.value ? +e.target.value : null }))} />
                        </div>
                        <div>
                            <label className={lbl}>Tổng cộng</label>
                            <input type="number" className={inp} value={form.total ?? ''} onChange={e => setForm(f => ({ ...f, total: e.target.value ? +e.target.value : null }))} />
                        </div>
                    </div>
                    <div>
                        <label className={lbl}>Ghi chú</label>
                        <textarea className={inp} rows={2} value={form.note ?? ''} onChange={e => setForm(f => ({ ...f, note: e.target.value || null }))} />
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
