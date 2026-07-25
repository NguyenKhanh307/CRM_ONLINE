import { useRef, useState, type FormEvent, useEffect } from 'react';
import { collectErrors, emailError } from '@/shared/utils/validators';
import { FieldError } from '@/shared/components/form/FormField';
import { ModalFooter } from '@/shared/components/ModalFooter';
import { useConfirm } from '@/shared/confirm/useConfirm';
import { useFormKeyboardNav } from '@/shared/keyboard/useFormKeyboardNav';
import { FiX, FiPlus, FiTrash2 } from 'react-icons/fi';
import type { ContactResult, UpdateContactPayload } from '../types/contactTypes';
import { useUpdateContact } from '../hooks/useUpdateContact';
import { contactService } from '../services/contactService';
import { DateInput } from '@/shared/components/form/DateInput';

interface Props {
    item: ContactResult | null;
    onClose: () => void;
}

type PhoneType = 'mobile' | 'office' | 'home' | 'other';
interface PhoneRow {
    id: string;
    backendId?: number;
    phone: string;
    phoneType: PhoneType;
    isPrimary: boolean;
}

const PHONE_TYPE_LABELS: Record<PhoneType, string> = { mobile: 'Di động', office: 'Cơ quan', home: 'Nhà', other: 'Khác' };

export function ContactEditModal({ item, onClose }: Props) {
    const { mutateAsync, isPending } = useUpdateContact();
    const [form, setForm] = useState<UpdateContactPayload>({
        customerId: null, assignedUserId: null, fullName: '', position: null,
        email: null, gender: null, dateOfBirth: null, address: null, isPrimary: false,
        salutation: null, title: null, department: null, workEmail: null,
        personalEmail: null, zalo: null, source: null, doNotCall: false, doNotEmail: false,
    });
    const [phones, setPhones] = useState<PhoneRow[]>([]);
    const [originalPhones, setOriginalPhones] = useState<PhoneRow[]>([]);
    const [saving, setSaving] = useState(false);

    useEffect(() => {
        if (!item) return;
        setForm({
            customerId: item.customerId, assignedUserId: item.assignedUserId, fullName: item.fullName,
            position: item.position, email: item.email, gender: item.gender,
            dateOfBirth: item.dateOfBirth, address: item.address, isPrimary: item.isPrimary,
            salutation: item.salutation, title: item.title, department: item.department,
            workEmail: item.workEmail, personalEmail: item.personalEmail, zalo: item.zalo,
            source: item.source, doNotCall: item.doNotCall, doNotEmail: item.doNotEmail,
        });
        contactService.getPhones(item.id).then((r) => {
            const loaded: PhoneRow[] = r.data.data.map((p) => ({
                id: crypto.randomUUID(), backendId: p.id, phone: p.phone, phoneType: p.phoneType, isPrimary: p.isPrimary,
            }));
            setPhones(loaded);
            setOriginalPhones(loaded);
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

    const addPhone = () => setPhones((p) => [...p, { id: crypto.randomUUID(), phone: '', phoneType: 'mobile', isPrimary: false }]);
    const changePhone = (id: string, patch: Partial<PhoneRow>) => setPhones((p) => p.map((r) => r.id === id ? { ...r, ...patch } : r));
    const removePhone = (id: string) => setPhones((p) => p.filter((r) => r.id !== id));

    const handleSubmit = async (e: FormEvent) => {
        e.preventDefault();
        // Lỗi nhập liệu hiện đỏ dưới ô; popup xác nhận chỉ mở khi dữ liệu đã hợp lệ.
        const errs = collectErrors({
            email: emailError(form.email),
            workEmail: emailError(form.workEmail),
            personalEmail: emailError(form.personalEmail),
        });
        setErrors(errs);
        if (Object.keys(errs).length > 0) return;

        if (!(await confirmSave('liên hệ'))) return;
        setSaving(true);
        try {
            await mutateAsync({ id: item.id, payload: form });
            const valid = phones.filter((p) => p.phone.trim());
            const curIds = new Set(valid.filter((p) => p.backendId).map((p) => p.backendId));
            const toDelete = originalPhones.filter((p) => p.backendId && !curIds.has(p.backendId)).map((p) => p.backendId as number);
            await Promise.all([
                ...valid.filter((p) => !p.backendId).map((p) =>
                    contactService.createPhone(item.id, { phone: p.phone, phoneType: p.phoneType, isPrimary: p.isPrimary })),
                ...valid.filter((p) => p.backendId).map((p) =>
                    contactService.updatePhone(item.id, p.backendId as number, { phone: p.phone, phoneType: p.phoneType, isPrimary: p.isPrimary })),
                ...toDelete.map((id) => contactService.deletePhone(item.id, id)),
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
                            <input className={inp} value={form.salutation ?? ''} onChange={e => setForm(f => ({ ...f, salutation: e.target.value || null }))} />
                        </div>
                    </div>
                    <div className="grid grid-cols-3 gap-3">
                        <div>
                            <label className={lbl}>Chức danh</label>
                            <input className={inp} value={form.title ?? ''} onChange={e => setForm(f => ({ ...f, title: e.target.value || null }))} />
                        </div>
                        <div>
                            <label className={lbl}>Phòng ban</label>
                            <input className={inp} value={form.department ?? ''} onChange={e => setForm(f => ({ ...f, department: e.target.value || null }))} />
                        </div>
                        <div>
                            <label className={lbl}>Chức vụ</label>
                            <input className={inp} value={form.position ?? ''} onChange={e => setForm(f => ({ ...f, position: e.target.value || null }))} />
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
                            <label className={lbl}>Email cơ quan</label>
                            <FieldError error={errors.workEmail}>
                                <input type="email" className={inp} value={form.workEmail ?? ''} onChange={e => { setForm(f => ({ ...f, workEmail: e.target.value || null })); clearError('workEmail'); }} />
                            </FieldError>
                        </div>
                        <div>
                            <label className={lbl}>Email cá nhân</label>
                            <FieldError error={errors.personalEmail}>
                                <input type="email" className={inp} value={form.personalEmail ?? ''} onChange={e => { setForm(f => ({ ...f, personalEmail: e.target.value || null })); clearError('personalEmail'); }} />
                            </FieldError>
                        </div>
                    </div>
                    <div className="grid grid-cols-3 gap-3">
                        <div>
                            <label className={lbl}>Zalo</label>
                            <input className={inp} value={form.zalo ?? ''} onChange={e => setForm(f => ({ ...f, zalo: e.target.value || null }))} />
                        </div>
                        <div>
                            <label className={lbl}>Nguồn gốc</label>
                            <input className={inp} value={form.source ?? ''} onChange={e => setForm(f => ({ ...f, source: e.target.value || null }))} />
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
                        <div>
                            <label className={lbl}>Địa chỉ</label>
                            <input className={inp} value={form.address ?? ''} onChange={e => setForm(f => ({ ...f, address: e.target.value || null }))} />
                        </div>
                    </div>

                    {/* Số điện thoại */}
                    <div>
                        <div className="flex items-center justify-between mb-1">
                            <label className={lbl}>Số điện thoại</label>
                            <button type="button" onClick={addPhone} className="text-sm text-primary flex items-center gap-1 hover:underline">
                                <FiPlus size={13} /> Thêm số
                            </button>
                        </div>
                        <div className="space-y-2">
                            {phones.length === 0 && <p className="text-sm text-gray-400">Chưa có số điện thoại</p>}
                            {phones.map((p) => (
                                <div key={p.id} className="flex items-center gap-2">
                                    <input className={`${inp} flex-1`} placeholder="Số điện thoại" value={p.phone} onChange={e => changePhone(p.id, { phone: e.target.value })} />
                                    <select className={`${inp} w-32`} value={p.phoneType} onChange={e => changePhone(p.id, { phoneType: e.target.value as PhoneType })}>
                                        {(Object.keys(PHONE_TYPE_LABELS) as PhoneType[]).map(t => <option key={t} value={t}>{PHONE_TYPE_LABELS[t]}</option>)}
                                    </select>
                                    <label className="flex items-center gap-1 text-sm text-text-main whitespace-nowrap">
                                        <input type="checkbox" className="w-4 h-4 accent-primary" checked={p.isPrimary} onChange={e => changePhone(p.id, { isPrimary: e.target.checked })} /> Chính
                                    </label>
                                    <button type="button" onClick={() => removePhone(p.id)} className="p-1 rounded hover:bg-red-50 text-gray-400 hover:text-danger"><FiTrash2 size={13} /></button>
                                </div>
                            ))}
                        </div>
                    </div>

                    <div className="flex items-center gap-6">
                        <label className="flex items-center gap-2 text-md text-text-main cursor-pointer">
                            <input type="checkbox" className="w-4 h-4 accent-primary" checked={form.isPrimary} onChange={e => setForm(f => ({ ...f, isPrimary: e.target.checked }))} />
                            Liên hệ chính
                        </label>
                        <label className="flex items-center gap-2 text-md text-text-main cursor-pointer">
                            <input type="checkbox" className="w-4 h-4 accent-primary" checked={form.doNotCall ?? false} onChange={e => setForm(f => ({ ...f, doNotCall: e.target.checked }))} />
                            Không gọi
                        </label>
                        <label className="flex items-center gap-2 text-md text-text-main cursor-pointer">
                            <input type="checkbox" className="w-4 h-4 accent-primary" checked={form.doNotEmail ?? false} onChange={e => setForm(f => ({ ...f, doNotEmail: e.target.checked }))} />
                            Không email
                        </label>
                    </div>
                    <ModalFooter onCancel={onClose} saving={busy} />
                </form>
            </div>
        </div>
    );
}
