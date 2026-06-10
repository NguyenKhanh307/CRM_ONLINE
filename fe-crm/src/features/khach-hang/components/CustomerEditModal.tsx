import { useState, type FormEvent, useEffect } from 'react';
import { FiX } from 'react-icons/fi';
import type { CustomerResult, UpdateCustomerPayload } from '../types/customerTypes';
import { useUpdateCustomer } from '../hooks/useUpdateCustomer';

interface Props {
    item: CustomerResult | null;
    onClose: () => void;
}

const CUSTOMER_TYPES = ['individual', 'company'];
const CUSTOMER_TYPE_LABELS: Record<string, string> = { individual: 'Cá nhân', company: 'Doanh nghiệp' };
const CUSTOMER_STATUSES = ['active', 'inactive', 'potential'];
const CUSTOMER_STATUS_LABELS: Record<string, string> = { active: 'Hoạt động', inactive: 'Ngừng', potential: 'Tiềm năng' };

export function CustomerEditModal({ item, onClose }: Props) {
    const { mutate, isPending } = useUpdateCustomer();
    const [form, setForm] = useState<UpdateCustomerPayload>({
        name: '', type: 'individual', taxCode: null, phone: null,
        email: null, address: null, source: null, status: 'active', ownerId: null, unitId: null,
    });

    useEffect(() => {
        if (!item) return;
        setForm({
            name: item.name, type: item.type, taxCode: item.taxCode, phone: item.phone,
            email: item.email, address: item.address, source: item.source, status: item.status,
            ownerId: item.ownerId, unitId: item.unitId,
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
                    <h2 className="text-lg font-semibold text-text-main">Chỉnh sửa khách hàng</h2>
                    <button onClick={onClose} className="p-1 rounded hover:bg-gray-100 text-gray-500"><FiX size={18} /></button>
                </div>
                <form onSubmit={handleSubmit} className="px-5 py-4 space-y-3">
                    <div>
                        <label className={lbl}>Tên khách hàng <span className="text-danger">*</span></label>
                        <input className={inp} required value={form.name} onChange={e => setForm(f => ({ ...f, name: e.target.value }))} />
                    </div>
                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <label className={lbl}>Loại</label>
                            <select className={inp} value={form.type} onChange={e => setForm(f => ({ ...f, type: e.target.value }))}>
                                {CUSTOMER_TYPES.map(t => <option key={t} value={t}>{CUSTOMER_TYPE_LABELS[t]}</option>)}
                            </select>
                        </div>
                        <div>
                            <label className={lbl}>Trạng thái</label>
                            <select className={inp} value={form.status} onChange={e => setForm(f => ({ ...f, status: e.target.value }))}>
                                {CUSTOMER_STATUSES.map(s => <option key={s} value={s}>{CUSTOMER_STATUS_LABELS[s]}</option>)}
                            </select>
                        </div>
                    </div>
                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <label className={lbl}>Điện thoại</label>
                            <input className={inp} value={form.phone ?? ''} onChange={e => setForm(f => ({ ...f, phone: e.target.value || null }))} />
                        </div>
                        <div>
                            <label className={lbl}>Email</label>
                            <input type="email" className={inp} value={form.email ?? ''} onChange={e => setForm(f => ({ ...f, email: e.target.value || null }))} />
                        </div>
                    </div>
                    <div>
                        <label className={lbl}>Mã số thuế</label>
                        <input className={inp} value={form.taxCode ?? ''} onChange={e => setForm(f => ({ ...f, taxCode: e.target.value || null }))} />
                    </div>
                    <div>
                        <label className={lbl}>Địa chỉ</label>
                        <input className={inp} value={form.address ?? ''} onChange={e => setForm(f => ({ ...f, address: e.target.value || null }))} />
                    </div>
                    <div>
                        <label className={lbl}>Nguồn</label>
                        <input className={inp} value={form.source ?? ''} onChange={e => setForm(f => ({ ...f, source: e.target.value || null }))} />
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
