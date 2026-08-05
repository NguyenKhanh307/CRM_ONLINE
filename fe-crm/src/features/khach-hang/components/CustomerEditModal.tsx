import { useRef, useState, type FormEvent, useEffect } from 'react';
import { collectErrors, emailError, nonNegativeError, phoneError, taxCodeError } from '@/shared/utils/validators';
import { FieldError } from '@/shared/components/form/FormField';
import { ModalFooter } from '@/shared/components/ModalFooter';
import { useConfirm } from '@/shared/confirm/useConfirm';
import { useFormKeyboardNav } from '@/shared/keyboard/useFormKeyboardNav';
import { FiX } from 'react-icons/fi';
import type { CustomerResult, UpdateCustomerPayload } from '../types/customerTypes';
import { useUpdateCustomer } from '../hooks/useUpdateCustomer';
import { RATING_OPTIONS } from '../config/customerOptions';

interface Props {
    item: CustomerResult | null;
    onClose: () => void;
}

const CUSTOMER_TYPES = ['individual', 'company'];
const CUSTOMER_TYPE_LABELS: Record<string, string> = { individual: 'Cá nhân', company: 'Doanh nghiệp' };
const CUSTOMER_STATUS_LABELS: Record<string, string> = { active: 'Hoạt động', inactive: 'Ngừng', potential: 'Tiềm năng' };
const CUSTOMER_STATUS_COLORS: Record<string, string> = {
    active: 'bg-green-100 text-green-700', inactive: 'bg-red-100 text-red-600', potential: 'bg-yellow-100 text-yellow-700',
};

export function CustomerEditModal({ item, onClose }: Props) {
    const { mutate, isPending } = useUpdateCustomer();
    const [form, setForm] = useState<UpdateCustomerPayload>({
        name: '', type: 'individual', taxCode: null, phone: null,
        email: null, address: null, source: null, ownerId: null,
        shortName: null, website: null, industry: null, creditDays: null, creditLimit: null,
        rating: null,
        employeeSize: null, isDistributor: false,
    });

    useEffect(() => {
        if (!item) return;
        setForm({
            name: item.name, type: item.type, taxCode: item.taxCode, phone: item.phone,
            email: item.email, address: item.address, source: item.source,
            ownerId: item.ownerId,
            shortName: item.shortName, website: item.website, industry: item.industry,
            creditDays: item.creditDays, creditLimit: item.creditLimit,
            rating: item.rating,
            employeeSize: item.employeeSize, isDistributor: item.isDistributor,
        });
    }, [item]);

    const [errors, setErrors] = useState<Record<string, string>>({});
    // xóa lỗi của một ô ngay khi người dùng gõ lại
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

    // lưu form — lỗi hiện đỏ dưới ô, popup xác nhận chỉ mở khi dữ liệu đã hợp lệ
    const handleSubmit = async (e: FormEvent) => {
        e.preventDefault();
        const errs = collectErrors({
            name: !form.name?.trim() ? 'Tên khách hàng không được để trống' : null,
            email: emailError(form.email),
            phone: phoneError(form.phone),
            taxCode: taxCodeError(form.taxCode),
            creditLimit: nonNegativeError(form.creditLimit, 'Hạn mức tín dụng'),
        });
        setErrors(errs);
        if (Object.keys(errs).length > 0) return;

        if (!(await confirmSave('khách hàng'))) return;
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
                <form ref={formRef} onSubmit={handleSubmit} noValidate className="px-5 py-4 space-y-3">
                    <div>
                        <label className={lbl}>Tên khách hàng <span className="text-danger">*</span></label>
                        <FieldError error={errors.name}>
                            <input className={inp} required value={form.name} onChange={e => { setForm(f => ({ ...f, name: e.target.value })); clearError('name'); }} />
                        </FieldError>
                    </div>
                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <label className={lbl}>Loại</label>
                            <select className={inp} value={form.type} onChange={e => setForm(f => ({ ...f, type: e.target.value }))}>
                                {CUSTOMER_TYPES.map(t => <option key={t} value={t}>{CUSTOMER_TYPE_LABELS[t]}</option>)}
                            </select>
                        </div>
                        <div>
                            <label className={lbl}>Trạng thái (đổi qua hành động)</label>
                            <span className={`inline-block px-2 py-1.5 rounded text-sm font-medium ${CUSTOMER_STATUS_COLORS[item.status] ?? 'bg-gray-100 text-gray-600'}`}>
                                {CUSTOMER_STATUS_LABELS[item.status] ?? item.status}
                            </span>
                        </div>
                    </div>
                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <label className={lbl}>Điện thoại</label>
                            <FieldError error={errors.phone}>
                                <input className={inp} value={form.phone ?? ''} onChange={e => { setForm(f => ({ ...f, phone: e.target.value || null })); clearError('phone'); }} />
                            </FieldError>
                        </div>
                        <div>
                            <label className={lbl}>Email</label>
                            <FieldError error={errors.email}>
                                <input type="email" className={inp} value={form.email ?? ''} onChange={e => { setForm(f => ({ ...f, email: e.target.value || null })); clearError('email'); }} />
                            </FieldError>
                        </div>
                    </div>
                    <div>
                        <label className={lbl}>Mã số thuế</label>
                        <FieldError error={errors.taxCode}>
                            <input className={inp} value={form.taxCode ?? ''} onChange={e => { setForm(f => ({ ...f, taxCode: e.target.value || null })); clearError('taxCode'); }} />
                        </FieldError>
                    </div>
                    <div>
                        <label className={lbl}>Địa chỉ</label>
                        <input className={inp} value={form.address ?? ''} onChange={e => setForm(f => ({ ...f, address: e.target.value || null }))} />
                    </div>
                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <label className={lbl}>Nguồn</label>
                            <input className={inp} value={form.source ?? ''} onChange={e => setForm(f => ({ ...f, source: e.target.value || null }))} />
                        </div>
                        <div>
                            <label className={lbl}>Tên viết tắt</label>
                            <input className={inp} value={form.shortName ?? ''} onChange={e => setForm(f => ({ ...f, shortName: e.target.value || null }))} />
                        </div>
                    </div>
                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <label className={lbl}>Website</label>
                            <input className={inp} value={form.website ?? ''} onChange={e => setForm(f => ({ ...f, website: e.target.value || null }))} />
                        </div>
                        <div>
                            <label className={lbl}>Ngành nghề</label>
                            <input className={inp} value={form.industry ?? ''} onChange={e => setForm(f => ({ ...f, industry: e.target.value || null }))} />
                        </div>
                    </div>
                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <label className={lbl}>Số ngày được nợ</label>
                            <input type="number" min={0} className={inp} value={form.creditDays ?? ''} onChange={e => setForm(f => ({ ...f, creditDays: e.target.value ? +e.target.value : null }))} />
                        </div>
                        <div>
                            <label className={lbl}>Hạn mức nợ</label>
                            <FieldError error={errors.creditLimit}>
                                <input type="number" min={0} className={inp} value={form.creditLimit ?? ''} onChange={e => { setForm(f => ({ ...f, creditLimit: e.target.value ? +e.target.value : null })); clearError('creditLimit'); }} />
                            </FieldError>
                        </div>
                    </div>
                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <label className={lbl}>Xếp hạng</label>
                            <select className={inp} value={form.rating ?? ''} onChange={e => setForm(f => ({ ...f, rating: e.target.value || null }))}>
                                <option value="">-- Không xếp hạng --</option>
                                {RATING_OPTIONS.map(r => <option key={r.value} value={r.value}>{r.label}</option>)}
                            </select>
                        </div>
                        <div>
                            <label className={lbl}>Quy mô nhân sự</label>
                            <input className={inp} value={form.employeeSize ?? ''} onChange={e => setForm(f => ({ ...f, employeeSize: e.target.value || null }))} />
                        </div>
                    </div>
                    <div className="flex items-center pb-1.5">
                        <label className="flex items-center gap-2 cursor-pointer">
                            <input type="checkbox" className="w-4 h-4 accent-primary" checked={form.isDistributor ?? false} onChange={e => setForm(f => ({ ...f, isDistributor: e.target.checked }))} />
                            <span className="text-sm text-text-main">Là nhà phân phối</span>
                        </label>
                    </div>
                    <ModalFooter onCancel={onClose} saving={isPending} />
                </form>
            </div>
        </div>
    );
}
