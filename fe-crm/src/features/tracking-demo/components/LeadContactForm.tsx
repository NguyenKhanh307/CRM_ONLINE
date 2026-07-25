import { useRef, useState } from 'react';
import { FieldError } from '@/shared/components/form/FormField';
import { useFormKeyboardNav } from '@/shared/keyboard/useFormKeyboardNav';
import { collectErrors, emailError, phoneError } from '@/shared/utils/validators';
import { EMPTY_FORM, SUBMIT_POINTS } from '../config/trackingDemoConfig';
import type { LeadFormState } from '../types/trackingTypes';

interface Props {
    busy: boolean;
    onSubmit: (form: LeadFormState) => void;
}

const inputCls = 'w-full rounded border border-gray-300 px-3 py-2 text-sm focus:border-blue-500 focus:outline-none';

/**
 * Bước 4 — form liên hệ: biến khách ẩn danh thành tiềm năng có tên/email/SĐT.
 *
 * Đây là form khách tự điền trên website, KHÔNG phải thao tác thêm/sửa trong CRM,
 * nên cố ý chỉ báo lỗi đỏ dưới ô — không có popup xác nhận.
 */
export const LeadContactForm = ({ busy, onSubmit }: Props) => {
    const [form, setForm] = useState<LeadFormState>(EMPTY_FORM);
    const [errors, setErrors] = useState<Record<string, string>>({});
    const formRef = useRef<HTMLFormElement>(null);

    // autoFocus tắt: trang mô phỏng website, không cướp focus của khách khi vừa mở.
    useFormKeyboardNav(formRef, { onSubmit: () => formRef.current?.requestSubmit(), autoFocus: false });

    /** Cập nhật form và xóa lỗi của đúng những ô vừa gõ. */
    const set = (patch: Partial<LeadFormState>) => {
        setForm((f) => ({ ...f, ...patch }));
        setErrors((e) => {
            const next = { ...e };
            Object.keys(patch).forEach((k) => delete next[k]);
            return next;
        });
    };

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        const errs = collectErrors({
            name: !form.name.trim() ? 'Vui lòng nhập họ tên' : null,
            email: !form.email.trim() ? 'Vui lòng nhập email' : emailError(form.email),
            phone: phoneError(form.phone),
        });
        setErrors(errs);
        if (Object.keys(errs).length > 0) return;
        onSubmit(form);
    };

    return (
        <form ref={formRef} onSubmit={handleSubmit} noValidate className="bg-white rounded-xl shadow-sm border border-gray-200 p-5">
            <div className="flex items-baseline gap-2 mb-1">
                <span className="flex h-6 w-6 items-center justify-center rounded-full bg-blue-600 text-white text-xs font-bold">4</span>
                <h2 className="font-semibold text-gray-800">Để lại thông tin liên hệ</h2>
            </div>
            <p className="ml-8 mb-3 text-sm text-gray-500">
                Nộp form là tín hiệu mạnh nhất (+{SUBMIT_POINTS} điểm): khách ẩn danh trở thành tiềm năng có tên,
                email và số điện thoại thật trong CRM.
            </p>

            <div className="ml-8 space-y-3">
                <div className="grid grid-cols-1 gap-3 sm:grid-cols-2">
                    <FieldError error={errors.name}>
                        <input className={inputCls} placeholder="Họ tên *" value={form.name} onChange={(e) => set({ name: e.target.value })} />
                    </FieldError>
                    <FieldError>
                        <input className={inputCls} placeholder="Công ty" value={form.companyName} onChange={(e) => set({ companyName: e.target.value })} />
                    </FieldError>
                    <FieldError error={errors.email}>
                        <input className={inputCls} placeholder="Email *" value={form.email} onChange={(e) => set({ email: e.target.value })} />
                    </FieldError>
                    <FieldError error={errors.phone}>
                        <input className={inputCls} placeholder="Số điện thoại" value={form.phone} onChange={(e) => set({ phone: e.target.value })} />
                    </FieldError>
                </div>
                <textarea
                    className={`${inputCls} resize-none`} rows={2} placeholder="Nhu cầu của bạn"
                    value={form.note} onChange={(e) => set({ note: e.target.value })}
                />
                <button
                    type="submit"
                    disabled={busy}
                    className="w-full rounded-lg bg-blue-600 py-2.5 text-sm font-semibold text-white hover:bg-blue-700 disabled:opacity-50"
                >
                    Gửi thông tin (+{SUBMIT_POINTS} điểm)
                </button>
            </div>
        </form>
    );
};
