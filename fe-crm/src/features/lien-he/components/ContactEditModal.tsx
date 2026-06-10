import { useState, type FormEvent, useEffect } from 'react';
import { FiX } from 'react-icons/fi';
import type { ContactResult, UpdateContactPayload } from '../types/contactTypes';
import { useUpdateContact } from '../hooks/useUpdateContact';

interface Props {
    item: ContactResult | null;
    onClose: () => void;
}

export function ContactEditModal({ item, onClose }: Props) {
    const { mutate, isPending } = useUpdateContact();
    const [form, setForm] = useState<UpdateContactPayload>({
        customerId: null, assignedUserId: null, fullName: '', position: null,
        email: null, gender: null, dateOfBirth: null, address: null, isPrimary: false,
    });

    useEffect(() => {
        if (!item) return;
        setForm({
            customerId: item.customerId, assignedUserId: item.assignedUserId, fullName: item.fullName,
            position: item.position, email: item.email, gender: item.gender,
            dateOfBirth: item.dateOfBirth, address: item.address, isPrimary: item.isPrimary,
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
                    <h2 className="text-lg font-semibold text-text-main">Chỉnh sửa liên hệ</h2>
                    <button onClick={onClose} className="p-1 rounded hover:bg-gray-100 text-gray-500"><FiX size={18} /></button>
                </div>
                <form onSubmit={handleSubmit} className="px-5 py-4 space-y-3">
                    <div>
                        <label className={lbl}>Họ tên <span className="text-danger">*</span></label>
                        <input className={inp} required value={form.fullName} onChange={e => setForm(f => ({ ...f, fullName: e.target.value }))} />
                    </div>
                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <label className={lbl}>Chức vụ</label>
                            <input className={inp} value={form.position ?? ''} onChange={e => setForm(f => ({ ...f, position: e.target.value || null }))} />
                        </div>
                        <div>
                            <label className={lbl}>Email</label>
                            <input type="email" className={inp} value={form.email ?? ''} onChange={e => setForm(f => ({ ...f, email: e.target.value || null }))} />
                        </div>
                    </div>
                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <label className={lbl}>Giới tính</label>
                            <select className={inp} value={form.gender ?? ''} onChange={e => setForm(f => ({ ...f, gender: e.target.value || null }))}>
                                <option value="">-- Chọn --</option>
                                <option value="male">Nam</option>
                                <option value="female">Nữ</option>
                                <option value="other">Khác</option>
                            </select>
                        </div>
                        <div>
                            <label className={lbl}>Ngày sinh</label>
                            <input type="date" className={inp} value={form.dateOfBirth ?? ''} onChange={e => setForm(f => ({ ...f, dateOfBirth: e.target.value || null }))} />
                        </div>
                    </div>
                    <div>
                        <label className={lbl}>Địa chỉ</label>
                        <input className={inp} value={form.address ?? ''} onChange={e => setForm(f => ({ ...f, address: e.target.value || null }))} />
                    </div>
                    <div>
                        <label className="flex items-center gap-2 text-md text-text-main cursor-pointer">
                            <input type="checkbox" className="w-4 h-4 accent-primary" checked={form.isPrimary} onChange={e => setForm(f => ({ ...f, isPrimary: e.target.checked }))} />
                            Liên hệ chính
                        </label>
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
