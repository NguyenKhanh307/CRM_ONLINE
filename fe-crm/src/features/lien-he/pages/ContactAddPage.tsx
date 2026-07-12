import { useRef, useState } from 'react';
import { emailError } from '@/shared/utils/validators';
import { useConfirm } from '@/shared/confirm/useConfirm';
import { useNavigate } from 'react-router-dom';
import { useFormKeyboardNav } from '@/shared/keyboard/useFormKeyboardNav';
import { FormPageHeader } from '@/shared/components/form/FormPageHeader';
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
        position: f.position || null,
        email: f.email || null,
        workEmail: f.workEmail || null,
        personalEmail: f.personalEmail || null,
        zalo: f.zalo || null,
        source: f.source || null,
        gender: f.gender || null,
        dateOfBirth: f.dateOfBirth || null,
        address: f.address || null,
        doNotCall: f.doNotCall,
        doNotEmail: f.doNotEmail,
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
    const { mutate, isPending } = useCreateContact();

    const onChange = (patch: Partial<ContactFormState>) =>
        setForm((prev) => ({ ...prev, ...patch }));

    const submit = async (andNew: boolean) => {
        if (!form.ten.trim()) {
        // Kiểm tra biên (khớp ràng buộc backend) — chặn submit nếu dữ liệu không hợp lệ
        const vErr = emailError(form.email) ?? emailError(form.workEmail) ?? emailError(form.personalEmail);
        if (vErr) { showAlert(vErr); return; }
            showAlert('Tên không được để trống');
            return;
        }
        if (!(await confirmCreate('liên hệ'))) return;
        mutate(toPayload(form), {
            onSuccess: () => {
                if (andNew) {
                    setForm(initialForm);
                    showAlert('Đã lưu liên hệ thành công');
                } else {
                    navigate('/lien-he');
                }
            },
            onError: (err: unknown) => {
                const msg =
                    (err as { response?: { data?: { message?: string } } })?.response?.data
                        ?.message ?? 'Có lỗi xảy ra khi lưu liên hệ';
                showAlert(msg);
            },
        });
    };

    const formRef = useRef<HTMLDivElement>(null);
    useFormKeyboardNav(formRef, { onSubmit: () => submit(false) });

    return (
        <div ref={formRef} className="p-6 bg-bg-main min-h-[calc(100vh-50px)]">
            <FormPageHeader
                title="Thêm Liên hệ"
                saving={isPending}
                onCancel={() => navigate(-1)}
                onSave={() => submit(false)}
                onSaveAndNew={() => submit(true)}
            />

            <div className="bg-white rounded-card shadow-sm p-6 space-y-8">
                <FormSection title="Thông tin chung">
                    <ContactGeneralSection value={form} onChange={onChange} />
                </FormSection>

                <FormSection title="Thông tin liên lạc">
                    <ContactContactSection value={form} onChange={onChange} />
                </FormSection>

                <FormSection title="Thông tin khác">
                    <ContactOtherSection value={form} onChange={onChange} />
                </FormSection>
            </div>
        </div>
    );
};

export default ContactAddPage;
