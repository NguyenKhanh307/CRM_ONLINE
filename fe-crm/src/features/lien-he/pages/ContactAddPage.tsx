import { useEffect, useRef, useState } from 'react';
import { collectErrors, emailError, phoneError, validateOrWarn } from '@/shared/utils/validators';
import { useConfirm } from '@/shared/confirm/useConfirm';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { useFormKeyboardNav } from '@/shared/keyboard/useFormKeyboardNav';
import { FormPageHeader } from '@/shared/components/form/FormPageHeader';
import { DuplicateWarning } from '@/shared/components/DuplicateWarning';
import { useDuplicateCheck } from '@/shared/hooks/useDuplicateCheck';
import { FormSection } from '@/shared/components/form/FormSection';
import { PrefillHint } from '@/shared/components/form/PrefillHint';
import { fillEmpty, hasFilled } from '@/shared/utils/prefill';
import { useAlert } from '@/shared/alert/useAlert';
import { useAuth } from '@/core/auth/useAuth';
import { leadService } from '@/features/tiem-nang/services/leadService';
import type { LeadResult } from '@/features/tiem-nang/types/leadTypes';
import { useCreateContact } from '../hooks/useCreateContact';
import { ContactGeneralSection } from '../components/ContactGeneralSection';
import { ContactContactSection } from '../components/ContactContactSection';
import { ContactOtherSection } from '../components/ContactOtherSection';
import { INITIAL_CONTACT_FORM, type ContactFormState } from '../components/contactFormTypes';
import type { CreateContactPayload } from '../types/contactTypes';

/** Chuyển form state sang payload API. */
const toPayload = (f: ContactFormState): CreateContactPayload => ({
    customerId: f.customerId ? Number(f.customerId) : null,
    assignedUserId: f.assignedUserId ? Number(f.assignedUserId) : null,
    salutation: f.salutation || null,
    fullName: f.hoTen.trim(),
    title: f.title || null,
    department: f.department || null,
    email: f.email || null,
    zalo: f.zalo || null,
    phone: f.phone || null,
    source: f.source || null,
    gender: f.gender || null,
    dateOfBirth: f.dateOfBirth || null,
    isPrimary: f.isPrimary,
});

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

    // vào trang qua nút "Tạo liên hệ" (chuột phải/trang chi tiết Tiềm năng, ?fromLead=<id>) -> tự
    // điền tên/SĐT/email/nguồn/người phụ trách (chỉ ô còn trống), chạy đúng 1 lần lúc mount.
    // Lưu xong sẽ gán ngược lead.contactId = liên hệ vừa tạo (xem submit()).
    const [searchParams] = useSearchParams();
    const [pickedLead, setPickedLead] = useState<LeadResult | null>(null);
    const [prefillFrom, setPrefillFrom] = useState<string | null>(null);
    const autoPickedRef = useRef(false);
    useEffect(() => {
        const fromLead = searchParams.get('fromLead');
        if (!fromLead || autoPickedRef.current) return;
        autoPickedRef.current = true;
        leadService.getById(Number(fromLead)).then((r) => {
            const lead = r.data.data;
            setPickedLead(lead);
            const patch = fillEmpty(form, {
                hoTen: lead.name,
                phone: lead.phone ?? '',
                email: lead.email ?? '',
                source: lead.source ?? '',
                assignedUserId: lead.ownerId ? String(lead.ownerId) : '',
            });
            if (hasFilled(patch)) { onChange(patch); setPrefillFrom(`tiềm năng «${lead.name}»`); }
        });
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [searchParams]);

    /** Kiểm tra bắt buộc + biên (khớp ràng buộc backend) — trả map field→lỗi. */
    const validate = (): Record<string, string> =>
        collectErrors({
            hoTen: !form.hoTen.trim() ? 'Họ và tên không được để trống' : null,
            email: emailError(form.email),
            phone: phoneError(form.phone),
        });

    const submit = async () => {
        // Lỗi nhập liệu hiện đỏ dưới ô; popup xác nhận chỉ mở khi dữ liệu đã hợp lệ.
        const errs = validate();
        setErrors(errs);
        if (!validateOrWarn(errs, showAlert)) return;

        if (!(await confirmCreate('liên hệ'))) return;

        mutate(toPayload(form), {
            onSuccess: async (res) => {
                // tạo từ Tiềm năng có sẵn -> gán ngược lead.contactId để biết tiềm năng đã có liên hệ
                // chính thức. Không rollback nếu bước này lỗi — liên hệ đã tạo vẫn giữ nguyên, gán tay
                // lại sau qua LeadEditModal (khớp cách OpportunityAddPage xử lý convertedOpportunityId)
                if (pickedLead) {
                    try {
                        await leadService.update(pickedLead.id, {
                            name: pickedLead.name,
                            companyName: pickedLead.companyName,
                            leadType: pickedLead.leadType,
                            ownerId: pickedLead.ownerId,
                            contactId: res.data.data.id,
                            convertedOpportunityId: pickedLead.convertedOpportunityId,
                            campaignId: pickedLead.campaignId,
                            taxCode: pickedLead.taxCode,
                            website: pickedLead.website,
                            industry: pickedLead.industry,
                            source: pickedLead.source,
                            status: pickedLead.status,
                            phone: pickedLead.phone,
                            email: pickedLead.email,
                            note: pickedLead.note,
                        });
                    } catch { /* bỏ qua — không chặn luồng tạo liên hệ */ }
                }
                navigate('/lien-he');
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
    useFormKeyboardNav(formRef, { onSubmit: () => submit() });

    // Cảnh báo (không chặn) khi email/SĐT trùng bản ghi đã có
    const { data: duplicates } = useDuplicateCheck({ email: form.email, phone: form.phone });

    return (
        <div ref={formRef} className="p-6 bg-bg-main min-h-[calc(100vh-50px)]">
            <FormPageHeader
                title="Thêm Liên hệ"
                saving={isPending}
                onCancel={() => navigate(-1)}
                onSave={() => submit()}
            />

            <PrefillHint source={prefillFrom} />
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
