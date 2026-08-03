import { useMemo, useRef, useState } from 'react';
import { collectErrors, emailError, nonNegativeError, phoneError, requiredError, taxCodeError } from '@/shared/utils/validators';
import { useConfirm } from '@/shared/confirm/useConfirm';
import { useNavigate } from 'react-router-dom';
import { SearchableSelect } from '@/shared/components/SearchableSelect';
import { useFormKeyboardNav } from '@/shared/keyboard/useFormKeyboardNav';
import { FormPageHeader } from '@/shared/components/form/FormPageHeader';
import { DuplicateWarning } from '@/shared/components/DuplicateWarning';
import { useDuplicateCheck } from '@/shared/hooks/useDuplicateCheck';
import { FormSection } from '@/shared/components/form/FormSection';
import { FieldRow } from '@/shared/components/form/FieldRow';
import { PrefillHint } from '@/shared/components/form/PrefillHint';
import { fillEmpty, hasFilled } from '@/shared/utils/prefill';
import { fetchPrimaryContactId } from '@/shared/lookup/recordPrefill';
import { RecordPicker } from '@/shared/components/form/RecordPicker';
import { inputCls } from '@/shared/components/form/formStyles';
import { useAlert } from '@/shared/alert/useAlert';
import { useAuth } from '@/core/auth/useAuth';
import { useActiveUsers } from '@/features/users/hooks/useActiveUsers';
import { useCustomerList } from '@/features/khach-hang/hooks/useCustomerList';
import { useCampaignList } from '@/features/chien-dich/hooks/useCampaignList';
import { useCreateLead } from '../hooks/useCreateLead';
import type { CreateLeadPayload } from '../types/leadTypes';
import { SOURCE_OPTIONS } from '../config/leadOptions';

const LEAD_TYPE_OPTIONS = [
    { value: 'ca-nhan', label: 'Cá nhân' },
    { value: 'doanh-nghiep', label: 'Doanh nghiệp' },
    { value: 'ho-kinh-doanh', label: 'Hộ kinh doanh' },
];

interface FormState {
    code: string;
    name: string;
    leadType: string;
    title: string;
    department: string;
    phone: string;
    email: string;
    source: string;
    companyName: string;
    taxCode: string;
    website: string;
    industry: string;
    ownerId: string;
    customerId: string;
    contactId: string;
    campaignId: string;
    estimatedValue: string;
    doNotCall: boolean;
    doNotEmail: boolean;
    note: string;
}

// state khởi tạo — người phụ trách mặc định là user đang đăng nhập
const initialState = (ownerId: string): FormState => ({
    code: '', name: '', leadType: '', title: '', department: '', phone: '', email: '',
    source: '', companyName: '', taxCode: '', website: '', industry: '',
    ownerId, customerId: '', contactId: '', campaignId: '', estimatedValue: '', doNotCall: false,
    doNotEmail: false, note: '',
});

const toPayload = (f: FormState): CreateLeadPayload => ({
    code: f.code.trim(),
    name: f.name.trim(),
    companyName: f.companyName || null,
    leadType: f.leadType || null,
    ownerId: f.ownerId ? Number(f.ownerId) : null,
    customerId: f.customerId ? Number(f.customerId) : null,
    contactId: f.contactId ? Number(f.contactId) : null,
    campaignId: f.campaignId ? Number(f.campaignId) : null,
    title: f.title || null,
    department: f.department || null,
    taxCode: f.taxCode || null,
    website: f.website || null,
    industry: f.industry || null,
    source: f.source || null,
    estimatedValue: f.estimatedValue ? Number(f.estimatedValue) : null,
    phone: f.phone || null,
    email: f.email || null,
    doNotCall: f.doNotCall,
    doNotEmail: f.doNotEmail,
    note: f.note || null,
});

// trang thêm tiềm năng mới — form full-page nhiều section (layout AMIS)
const NewLeadPage = () => {
    const navigate = useNavigate();
    const { showAlert } = useAlert();
    const { confirmCreate } = useConfirm();
    const { user } = useAuth();
    const defaultOwnerId = user ? String(user.id) : '';
    const [form, setForm] = useState<FormState>(() => initialState(defaultOwnerId));
    const { mutate, isPending } = useCreateLead();

    const { data: users = [] } = useActiveUsers();
    const { data: customers = [] } = useCustomerList();
    const { data: campaigns = [] } = useCampaignList();

    const userOptions = useMemo(() => users.map((u) => ({ value: String(u.id), label: u.fullName })), [users]);
    const customerOptions = useMemo(() => customers.map((c) => ({ value: String(c.id), label: c.name })), [customers]);
    const campaignOptions = useMemo(() => (campaigns ?? []).map((c) => ({ value: String(c.id), label: c.name })), [campaigns]);

    const [errors, setErrors] = useState<Record<string, string>>({});

    // hàm cập nhật form và xóa lỗi của đúng những field vừa gõ
    const set = (patch: Partial<FormState>) => {
        setForm((p) => ({ ...p, ...patch }));
        setErrors((e) => {
            const next = { ...e };
            Object.keys(patch).forEach((k) => delete next[k]);
            return next;
        });
    };

    // tên khách hàng vừa kéo dữ liệu về — hiện dòng gợi ý dưới ô Khách hàng
    const [prefillFrom, setPrefillFrom] = useState<string | null>(null);

    // hàm chọn khách hàng -> tự điền thông tin công ty + liên hệ chính (chỉ ô còn trống)
    const onPickCustomer = async (v: string) => {
        // bước bỏ liên hệ cũ — đổi khách rồi mà giữ liên hệ của khách khác là dữ liệu sai
        const base = { ...form, customerId: v, contactId: '' };
        set({ customerId: v, contactId: '' });
        setPrefillFrom(null);
        const customer = customers.find((c) => String(c.id) === v);
        if (!customer) return;
        // bước điền các ô còn trống từ thông tin khách hàng
        const patch = fillEmpty(base, {
            contactId: await fetchPrimaryContactId(customer.id),
            companyName: customer.name,
            taxCode: customer.taxCode ?? '',
            website: customer.website ?? '',
            industry: customer.industry ?? '',
            phone: customer.phone ?? '',
            email: customer.email ?? '',
        });
        if (hasFilled(patch)) { set(patch); setPrefillFrom(`khách hàng «${customer.name}»`); }
    };

    // hàm kiểm tra bắt buộc + biên (khớp ràng buộc backend) — trả map field->lỗi
    const validate = (): Record<string, string> =>
        collectErrors({
            code: requiredError(form.code, 'Mã tiềm năng'),
            name: requiredError(form.name, 'Tên tiềm năng'),
            email: emailError(form.email),
            phone: phoneError(form.phone),
            taxCode: taxCodeError(form.taxCode),
            estimatedValue: nonNegativeError(form.estimatedValue, 'Giá trị ước tính'),
        });

    // hàm lưu — lỗi hiện đỏ dưới ô, popup xác nhận chỉ mở khi dữ liệu đã hợp lệ
    const submit = async (andNew: boolean) => {
        // bước kiểm tra dữ liệu
        const errs = validate();
        setErrors(errs);
        if (Object.keys(errs).length > 0) return;

        // bước hỏi xác nhận
        if (!(await confirmCreate('tiềm năng'))) return;

        // bước gọi api lưu
        mutate(toPayload(form), {
            onSuccess: () => {
                if (andNew) { setForm(initialState(defaultOwnerId)); setPrefillFrom(null); showAlert('Đã lưu tiềm năng thành công'); }
                else navigate('/tiem-nang');
            },
            onError: (err: unknown) => {
                const msg = (err as { response?: { data?: { message?: string } } })?.response?.data?.message
                    ?? 'Có lỗi xảy ra khi lưu tiềm năng';
                showAlert(msg);
            },
        });
    };

    const formRef = useRef<HTMLDivElement>(null);
    useFormKeyboardNav(formRef, { onSubmit: () => submit(false) });

    // cảnh báo (không chặn) khi email/SĐT/MST trùng bản ghi đã có
    const { data: duplicates } = useDuplicateCheck({ email: form.email, phone: form.phone, taxCode: form.taxCode });

    return (
        <div ref={formRef} className="p-6 bg-bg-main min-h-[calc(100vh-50px)]">
            <FormPageHeader
                title="Thêm Tiềm năng"
                saving={isPending}
                onCancel={() => navigate(-1)}
                onSave={() => submit(false)}
                onSaveAndNew={() => submit(true)}
            />

            <DuplicateWarning matches={duplicates} />

            <div className="bg-white rounded-card shadow-sm p-6 space-y-8">
                <FormSection title="Thông tin chung">
                    <div className="grid grid-cols-2 gap-x-10 gap-y-4">
                        <div className="space-y-4">
                            <FieldRow label="Mã tiềm năng" required error={errors.code}>
                                <input type="text" value={form.code} onChange={(e) => set({ code: e.target.value })} className={inputCls} />
                            </FieldRow>
                            <FieldRow label="Tên tiềm năng" required error={errors.name}>
                                <input type="text" value={form.name} onChange={(e) => set({ name: e.target.value })} className={inputCls} />
                            </FieldRow>
                            <FieldRow label="Chức danh">
                                <input type="text" value={form.title} onChange={(e) => set({ title: e.target.value })} className={inputCls} />
                            </FieldRow>
                            <FieldRow label="ĐT di động">
                                <input type="text" value={form.phone} onChange={(e) => set({ phone: e.target.value })} className={inputCls} />
                            </FieldRow>
                            <FieldRow label="Nguồn gốc">
                                <SearchableSelect value={form.source} onChange={(v) => set({ source: v })} options={SOURCE_OPTIONS} />
                            </FieldRow>
                        </div>
                        <div className="space-y-4">
                            <FieldRow label="Loại tiềm năng">
                                <SearchableSelect value={form.leadType} onChange={(v) => set({ leadType: v })} options={LEAD_TYPE_OPTIONS} />
                            </FieldRow>
                            <FieldRow label="Phòng ban">
                                <input type="text" value={form.department} onChange={(e) => set({ department: e.target.value })} className={inputCls} />
                            </FieldRow>
                            <FieldRow label="Email" error={errors.email}>
                                <input type="text" value={form.email} onChange={(e) => set({ email: e.target.value })} className={inputCls} />
                            </FieldRow>
                        </div>
                    </div>
                </FormSection>

                <FormSection title="Thông tin tổ chức">
                    <div className="grid grid-cols-2 gap-x-10 gap-y-4">
                        <div className="space-y-4">
                            <FieldRow label="Tên tổ chức">
                                <input type="text" value={form.companyName} onChange={(e) => set({ companyName: e.target.value })} className={inputCls} />
                            </FieldRow>
                            <FieldRow label="Website">
                                <input type="text" value={form.website} onChange={(e) => set({ website: e.target.value })} className={inputCls} />
                            </FieldRow>
                        </div>
                        <div className="space-y-4">
                            <FieldRow label="Mã số thuế" error={errors.taxCode}>
                                <input type="text" value={form.taxCode} onChange={(e) => set({ taxCode: e.target.value })} className={inputCls} />
                            </FieldRow>
                            <FieldRow label="Ngành nghề">
                                <input type="text" value={form.industry} onChange={(e) => set({ industry: e.target.value })} className={inputCls} />
                            </FieldRow>
                        </div>
                    </div>
                </FormSection>

                <FormSection title="Thông tin bán hàng">
                    <div className="grid grid-cols-2 gap-x-10 gap-y-4">
                        <div className="space-y-4">
                            <FieldRow label="Người phụ trách">
                                <SearchableSelect value={form.ownerId} onChange={(v) => set({ ownerId: v })} options={userOptions} />
                            </FieldRow>
                            <FieldRow label="Khách hàng">
                                <SearchableSelect value={form.customerId} onChange={onPickCustomer} options={customerOptions} />
                                <PrefillHint source={prefillFrom} />
                            </FieldRow>
                        </div>
                        <div className="space-y-4">
                            <FieldRow label="Liên hệ">
                                <RecordPicker module="contact" value={form.contactId} onChange={(v) => set({ contactId: v })}
                                    customerId={form.customerId ? Number(form.customerId) : undefined} />
                            </FieldRow>
                            <FieldRow label="Giá trị ước tính" error={errors.estimatedValue}>
                                <input type="number" min={0} value={form.estimatedValue} onChange={(e) => set({ estimatedValue: e.target.value })} className={inputCls} />
                            </FieldRow>
                            <FieldRow label="Chiến dịch nguồn">
                                <SearchableSelect value={form.campaignId} onChange={(v) => set({ campaignId: v })} options={campaignOptions} />
                            </FieldRow>
                        </div>
                    </div>
                </FormSection>

                <FormSection title="Tùy chọn liên hệ">
                    <div className="flex items-center gap-8">
                        <label className="flex items-center gap-2 cursor-pointer">
                            <input type="checkbox" checked={form.doNotCall} onChange={(e) => set({ doNotCall: e.target.checked })} className="w-4 h-4 accent-primary" />
                            <span className="text-md text-text-main">Không gọi điện</span>
                        </label>
                        <label className="flex items-center gap-2 cursor-pointer">
                            <input type="checkbox" checked={form.doNotEmail} onChange={(e) => set({ doNotEmail: e.target.checked })} className="w-4 h-4 accent-primary" />
                            <span className="text-md text-text-main">Không gửi Email</span>
                        </label>
                    </div>
                </FormSection>

                <FormSection title="Thông tin mô tả">
                    <FieldRow label="Mô tả" alignTop>
                        <textarea rows={3} value={form.note} onChange={(e) => set({ note: e.target.value })} className={`${inputCls} resize-none`} />
                    </FieldRow>
                </FormSection>
            </div>
        </div>
    );
};

export default NewLeadPage;
