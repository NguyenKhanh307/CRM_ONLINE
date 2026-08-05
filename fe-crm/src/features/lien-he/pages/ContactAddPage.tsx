import { useRef, useState } from 'react';
import { collectErrors, emailError, phoneError } from '@/shared/utils/validators';
import { useConfirm } from '@/shared/confirm/useConfirm';
import { useNavigate } from 'react-router-dom';
import { useFormKeyboardNav } from '@/shared/keyboard/useFormKeyboardNav';
import { FormPageHeader } from '@/shared/components/form/FormPageHeader';
import { DuplicateWarning } from '@/shared/components/DuplicateWarning';
import { useDuplicateCheck } from '@/shared/hooks/useDuplicateCheck';
import { FormSection } from '@/shared/components/form/FormSection';
import { useAlert } from '@/shared/alert/useAlert';
import { useAuth } from '@/core/auth/useAuth';
import { useCreateContact } from '../hooks/useCreateContact';
import { ContactGeneralSection } from '../components/ContactGeneralSection';
import { ContactContactSection } from '../components/ContactContactSection';
import { ContactOtherSection } from '../components/ContactOtherSection';
import { INITIAL_CONTACT_FORM, type ContactFormState } from '../components/contactFormTypes';
import type { ContactPhonePayload, CreateContactPayload } from '../types/contactTypes';

/** Chuyển form state sang payload API. */
const toPayload = (f: ContactFormState): CreateContactPayload => {
    const phones: ContactPhonePayload[] = [];
    if (f.mobilePhone.trim())
        phones.push({ phone: f.mobilePhone.trim(), phoneType: 'mobile', isPrimary: true });
    if (f.officePhone.trim())
        phones.push({ phone: f.officePhone.trim(), phoneType: 'office', isPrimary: false });
    return {
        customerId: f.customerId ? Number(f.customerId) : null,
        assignedUserId: f.assignedUserId ? Number(f.assignedUserId) : null,
        salutation: f.salutation || null,
        fullName: `${f.hoDem} ${f.ten}`.trim(),
        title: f.title || null,
        department: f.department || null,
        email: f.email || null,
        zalo: f.zalo || null,
        source: f.source || null,
        gender: f.gender || null,
        dateOfBirth: f.dateOfBirth || null,
        isPrimary: f.isPrimary,
        phones,
    };
};

/**
 * Trang thêm liên hệ mới — form full-page nhiều section (layout AMIS).
 */
const ContactAddPage = () => {
    const navigate = useNavigate();
    const { showAlert } = useAlert();
    const { confirmCreate } = useConfirm();
    const { user } = useAuth();
    // Người phụ trách mặc định là user đang đăng nhập.
    const initialForm: ContactFormState = { ...INITIAL_CONTACT_FORM, assignedUserId: user ? String(user.id) : '' };
    const [form, setForm] = useState<ContactFormState>(initialForm);
    const [errors, setErrors] = useState<Record<string, string>>({});
    const { mutate, isPending } = useCreateContact();

    /** Cập nhật form và xóa lỗi của đúng những field vừa gõ. */
    const onChange = (patch: Partial<ContactFormState>) => {
        setForm((prev) => ({ ...prev, ...patch }));
        setErrors((e) => {
            const next = { ...e };
            Object.keys(patch).forEach((k) => delete next[k]);
            return next;
        });
    };

    /** Kiểm tra bắt buộc + biên (khớp ràng buộc backend) — trả map field→lỗi. */
    const validate = (): Record<string, string> =>
        collectErrors({
            ten: !form.ten.trim() ? 'Tên không được để trống' : null,
            email: emailError(form.email),
            mobilePhone: phoneError(form.mobilePhone),
            officePhone: phoneError(form.officePhone),
        });

    const submit = async () => {
        // Lỗi nhập liệu hiện đỏ dưới ô; popup xác nhận chỉ mở khi dữ liệu đã hợp lệ.
        const errs = validate();
        setErrors(errs);
        if (Object.keys(errs).length > 0) return;

        if (!(await confirmCreate('liên hệ'))) return;

        mutate(toPayload(form), {
            onSuccess: () => navigate('/lien-he'),
            onError: (err: unknown) => {
                const msg =
                    (err as { response?: { data?: { message?: string } } })?.response?.data
                        ?.message ?? 'Có lỗi xảy ra khi lưu liên hệ';
                showAlert(msg);
            },
        });
    };

    const formRef = useRef<HTMLDivElement>(null);
    useFormKeyboardNav(formRef, { onSubmit: () => submit() });

    // Cảnh báo (không chặn) khi email/SĐT trùng bản ghi đã có
    const { data: duplicates } = useDuplicateCheck({ email: form.email, phone: form.mobilePhone });

    return (
        <div ref={formRef} className="p-6 bg-bg-main min-h-[calc(100vh-50px)]">
            <FormPageHeader
                title="Thêm Liên hệ"
                saving={isPending}
                onCancel={() => navigate(-1)}
                onSave={() => submit()}
            />

            <DuplicateWarning matches={duplicates} />

            <div className="bg-white rounded-card shadow-sm p-6 space-y-8">
                <FormSection title="Thông tin chung">
                    <ContactGeneralSection value={form} onChange={onChange} errors={errors} />
                </FormSection>

                <FormSection title="Thông tin liên lạc">
                    <ContactContactSection value={form} onChange={onChange} errors={errors} />
                </FormSection>

                <FormSection title="Thông tin khác">
                    <ContactOtherSection value={form} onChange={onChange} />
                </FormSection>
            </div>
        </div>
    );
};

export default ContactAddPage;
