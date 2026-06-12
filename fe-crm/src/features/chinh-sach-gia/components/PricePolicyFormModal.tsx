import { useState, type FormEvent, useEffect } from 'react';
import { FiX } from 'react-icons/fi';
import type { PricePolicyResult, CreatePricePolicyPayload, UpdatePricePolicyPayload, PricePolicyStatus } from '../types/pricingTypes';
import { useCreatePricePolicy } from '../hooks/useCreatePricePolicy';
import { useUpdatePricePolicy } from '../hooks/useUpdatePricePolicy';

interface Props {
    item: PricePolicyResult | null;
    open: boolean;
    onClose: () => void;
}

const EMPTY: CreatePricePolicyPayload = {
    code: '', name: '', type: null, priority: null,
    startDate: null, endDate: null, status: 'active',
};

export function PricePolicyFormModal({ item, open, onClose }: Props) {
    const { mutate: createFn, isPending: isCreating } = useCreatePricePolicy();
    const { mutate: updateFn, isPending: isUpdating } = useUpdatePricePolicy();
    const isPending = isCreating || isUpdating;

    const [form, setForm] = useState<CreatePricePolicyPayload>(EMPTY);

    useEffect(() => {
        if (!open) return;
        if (item) {
            setForm({
                code: item.code,
                name: item.name,
                type: item.type,
                priority: item.priority,
                startDate: item.startDate,
                endDate: item.endDate,
                status: item.status,
            });
        } else {
            setForm(EMPTY);
        }
    }, [open, item]);

    if (!open) return null;

    const handleSubmit = (e: FormEvent) => {
        e.preventDefault();
        if (item) {
            const payload: UpdatePricePolicyPayload = {
                name: form.name, type: form.type, priority: form.priority,
                startDate: form.startDate, endDate: form.endDate, status: form.status,
            };
            updateFn({ id: item.id, payload }, { onSuccess: onClose });
        } else {
            createFn(form, { onSuccess: onClose });
        }
    };

    const inp = 'w-full border border-gray-300 rounded-btn px-3 py-1.5 text-md text-text-main focus:outline-none focus:border-primary';
    const lbl = 'block text-sm font-medium text-gray-700 mb-1';

    return (
        <div className="fixed inset-0 z-[9999] flex items-center justify-center bg-black/40" onClick={onClose}>
            <div className="bg-white rounded-card shadow-lg w-full max-w-lg mx-4 max-h-[90vh] overflow-y-auto" onClick={e => e.stopPropagation()}>
                <div className="flex items-center justify-between px-5 py-4 border-b border-gray-200">
                    <h2 className="text-lg font-semibold text-text-main">
                        {item ? 'Chỉnh sửa chính sách giá' : 'Tạo chính sách giá'}
                    </h2>
                    <button onClick={onClose} className="p-1 rounded hover:bg-gray-100 text-gray-500"><FiX size={18} /></button>
                </div>
                <form onSubmit={handleSubmit} className="px-5 py-4 space-y-3">
                    <div>
                        <label className={lbl}>Mã chính sách <span className="text-danger">*</span></label>
                        <input
                            className={inp} required maxLength={20}
                            value={form.code}
                            onChange={e => setForm(f => ({ ...f, code: e.target.value }))}
                            disabled={!!item}
                        />
                    </div>
                    <div>
                        <label className={lbl}>Tên chính sách <span className="text-danger">*</span></label>
                        <input
                            className={inp} required maxLength={40}
                            value={form.name}
                            onChange={e => setForm(f => ({ ...f, name: e.target.value }))}
                        />
                    </div>
                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <label className={lbl}>Loại</label>
                            <input
                                className={inp} maxLength={20}
                                value={form.type ?? ''}
                                onChange={e => setForm(f => ({ ...f, type: e.target.value || null }))}
                            />
                        </div>
                        <div>
                            <label className={lbl}>Độ ưu tiên</label>
                            <input
                                className={inp} type="number" min={0}
                                value={form.priority ?? ''}
                                onChange={e => setForm(f => ({ ...f, priority: e.target.value ? Number(e.target.value) : null }))}
                            />
                        </div>
                    </div>
                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <label className={lbl}>Ngày bắt đầu</label>
                            <input
                                className={inp} type="date"
                                value={form.startDate ?? ''}
                                onChange={e => setForm(f => ({ ...f, startDate: e.target.value || null }))}
                            />
                        </div>
                        <div>
                            <label className={lbl}>Ngày kết thúc</label>
                            <input
                                className={inp} type="date"
                                value={form.endDate ?? ''}
                                onChange={e => setForm(f => ({ ...f, endDate: e.target.value || null }))}
                            />
                        </div>
                    </div>
                    <div>
                        <label className={lbl}>Trạng thái</label>
                        <select
                            className={inp}
                            value={form.status}
                            onChange={e => setForm(f => ({ ...f, status: e.target.value as PricePolicyStatus }))}
                        >
                            <option value="active">Đang áp dụng</option>
                            <option value="inactive">Ngừng áp dụng</option>
                            <option value="expired">Hết hạn</option>
                        </select>
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
