import { useMemo, useRef, useState, type FormEvent, useEffect } from 'react';
import { collectErrors, emailError, phoneError } from '@/shared/utils/validators';
import { FieldError } from '@/shared/components/form/FormField';
import { ModalFooter } from '@/shared/components/ModalFooter';
import { useConfirm } from '@/shared/confirm/useConfirm';
import { useFormKeyboardNav } from '@/shared/keyboard/useFormKeyboardNav';
import { FiX } from 'react-icons/fi';
import type { ContactResult, UpdateContactPayload } from '../types/contactTypes';
import { useUpdateContact } from '../hooks/useUpdateContact';
import { DateInput } from '@/shared/components/form/DateInput';
import { SearchableSelect } from '@/shared/components/SearchableSelect';
import { useCustomerList } from '@/features/khach-hang/hooks/useCustomerList';
import { SALUTATION_OPTIONS, SOURCE_OPTIONS } from './contactFormTypes';

interface Props {
    item: ContactResult | null;
    onClose: () => void;
}

export function ContactEditModal({ item, onClose }: Props) {
    const { mutate, isPending } = useUpdateContact();
    const { data: customers = [] } = useCustomerList();
    const customerOptions = useMemo(
        () => customers.map((c) => ({ value: String(c.id), label: c.name })),
        [customers],
    );
    const [form, setForm] = useState<UpdateContactPayload>({
        customerId: null, assignedUserId: null, fullName: '',
        email: null, gender: null, dateOfBirth: null, isPrimary: false,
        salutation: null, title: null, department: null,
        zalo: null, phone: null, source: null,
    });
    const [saving, setSaving] = useState(false);

    useEffect(() => {
        if (!item) return;
        setForm({
            customerId: item.customerId, assignedUserId: item.assignedUserId, fullName: item.fullName,
            email: item.email, gender: item.gender,
            dateOfBirth: item.dateOfBirth, isPrimary: item.isPrimary,
            salutation: item.salutation, title: item.title, department: item.department,
            zalo: item.zalo, phone: item.phone, source: item.source,
        });
    }, [item]);

    const [errors, setErrors] = useState<Record<string, string>>({});
    /** Xoa loi cua mot o ngay khi nguoi dung go lai. */
    const clearError = (key: string) =>
        setErrors((prev) => (prev[key] ? { ...prev, [key]: '' } : prev));

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
        // Lỗi nhập liệu hiện đỏ dưới ô; popup xác nhận chỉ mở khi dữ liệu đã hợp lệ.
        const errs = collectErrors({
            email: emailError(form.email),
            phone: phoneError(form.phone),
        });
        setErrors(errs);
        if (Object.keys(errs).length > 0) return;

        if (!(await confirmSave('liên hệ'))) return;
        setSaving(true);
        try {
            await mutate({ id: item.id, payload: form });
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
            <div className="bg-white rounded-card shadow-lg w-full max-w-2xl mx-4 max-h-[90vh] overflow-y-auto" onClick={(e) => e.stopPropagation()}>
                <div className="flex items-center justify-between px-5 py-4 border-b border-gray-200">
                    <h2 className="text-lg font-semibold text-text-main">Chỉnh sửa liên hệ</h2>
                    <button onClick={onClose} className="p-1 rounded hover:bg-gray-100 text-gray-500"><FiX size={18} /></button>
                </div>
                <form ref={formRef} onSubmit={handleSubmit} noValidate className="px-5 py-4 space-y-3">
                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <label className={lbl}>Họ tên <span className="text-danger">*</span></label>
                            <input className={inp} required value={form.fullName} onChange={e => setForm(f => ({ ...f, fullName: e.target.value }))} />
                        </div>
                        <div>
                            <label className={lbl}>Xưng hô</label>
                            <SearchableSelect options={SALUTATION_OPTIONS} value={form.salutation ?? ''}
                                onChange={v => setForm(f => ({ ...f, salutation: v || null }))} />
                        </div>
                    </div>
                    <div className="grid grid-cols-3 gap-3">
                        <div>
                            <label className={lbl}>Tổ chức</label>
                            <SearchableSelect options={customerOptions} value={form.customerId != null ? String(form.customerId) : ''}
                                onChange={v => setForm(f => ({ ...f, customerId: v ? Number(v) : null }))}
                                fallbackLabel={item.customerName} />
                        </div>
                        <div>
                            <label className={lbl}>Chức danh</label>
                            <input className={inp} value={form.title ?? ''} onChange={e => setForm(f => ({ ...f, title: e.target.value || null }))} />
                        </div>
                        <div>
                            <label className={lbl}>Phòng ban</label>
                            <input className={inp} value={form.department ?? ''} onChange={e => setForm(f => ({ ...f, department: e.target.value || null }))} />
                        </div>
                    </div>
                    <div className="grid grid-cols-3 gap-3">
                        <div>
                            <label className={lbl}>Email</label>
                            <FieldError error={errors.email}>
                                <input type="email" className={inp} value={form.email ?? ''} onChange={e => { setForm(f => ({ ...f, email: e.target.value || null })); clearError('email'); }} />
                            </FieldError>
                        </div>
                        <div>
                            <label className={lbl}>Số điện thoại</label>
                            <FieldError error={errors.phone}>
                                <input className={inp} value={form.phone ?? ''} onChange={e => { setForm(f => ({ ...f, phone: e.target.value || null })); clearError('phone'); }} />
                            </FieldError>
                        </div>
                        <div>
                            <label className={lbl}>Zalo</label>
                            <input className={inp} value={form.zalo ?? ''} onChange={e => setForm(f => ({ ...f, zalo: e.target.value || null }))} />
                        </div>
                    </div>
                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <label className={lbl}>Nguồn gốc</label>
                            <SearchableSelect options={SOURCE_OPTIONS} value={form.source ?? ''}
                                onChange={v => setForm(f => ({ ...f, source: v || null }))} />
                        </div>
                        <div>
                            <label className={lbl}>Giới tính</label>
                            <select className={inp} value={form.gender ?? ''} onChange={e => setForm(f => ({ ...f, gender: e.target.value || null }))}>
                                <option value="">-- Chọn --</option>
                                <option value="male">Nam</option>
                                <option value="female">Nữ</option>
                                <option value="other">Khác</option>
                            </select>
                        </div>
                    </div>
                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <label className={lbl}>Ngày sinh</label>
                            <DateInput value={form.dateOfBirth ?? ''} onChange={v => setForm(f => ({ ...f, dateOfBirth: v || null }))} />
                        </div>
                    </div>

                    <div className="flex items-center gap-6">
                        <label className="flex items-center gap-2 text-md text-text-main cursor-pointer">
                            <input type="checkbox" className="w-4 h-4 accent-primary" checked={form.isPrimary} onChange={e => setForm(f => ({ ...f, isPrimary: e.target.checked }))} />
                            Liên hệ chính
                        </label>
                    </div>
                    <ModalFooter onCancel={onClose} saving={busy} />
                </form>
            </div>
        </div>
    );
}
