import { useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { SearchableSelect } from '@/shared/components/SearchableSelect';
import { FormPageHeader } from '@/shared/components/form/FormPageHeader';
import { FormSection } from '@/shared/components/form/FormSection';
import { FieldRow } from '@/shared/components/form/FieldRow';
import { inputCls } from '@/shared/components/form/formStyles';
import { useAlert } from '@/shared/alert/useAlert';
import { useAuth } from '@/core/auth/useAuth';
import { useActiveUsers } from '@/features/users/hooks/useActiveUsers';
import { useCustomerList } from '@/features/khach-hang/hooks/useCustomerList';
import { useContactList } from '@/features/lien-he/hooks/useContactList';
import { useCreateLead } from '../hooks/useCreateLead';
import type { CreateLeadPayload } from '../types/leadTypes';

const SOURCE_OPTIONS = [
    { value: 'website', label: 'Website' },
    { value: 'gioi-thieu', label: 'Giới thiệu' },
    { value: 'dien-thoai', label: 'Điện thoại' },
    { value: 'email', label: 'Email' },
    { value: 'mxh', label: 'Mạng xã hội' },
    { value: 'khac', label: 'Khác' },
];

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
    estimatedValue: string;
    doNotCall: boolean;
    doNotEmail: boolean;
    note: string;
}

/** State khởi tạo — người phụ trách mặc định là user đang đăng nhập. */
const initialState = (ownerId: string): FormState => ({
    code: '', name: '', leadType: '', title: '', department: '', phone: '', email: '',
    source: '', companyName: '', taxCode: '', website: '', industry: '',
    ownerId, customerId: '', contactId: '', estimatedValue: '', doNotCall: false,
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

/** Trang thêm tiềm năng mới — form full-page nhiều section (layout AMIS). */
const LeadAddPage = () => {
    const navigate = useNavigate();
    const { showAlert } = useAlert();
    const { user } = useAuth();
    const defaultOwnerId = user ? String(user.id) : '';
    const [form, setForm] = useState<FormState>(() => initialState(defaultOwnerId));
    const { mutate, isPending } = useCreateLead();

    const { data: users = [] } = useActiveUsers();
    const { data: customers = [] } = useCustomerList();
    const { data: contacts = [] } = useContactList();

    const userOptions = useMemo(() => users.map((u) => ({ value: String(u.id), label: u.fullName })), [users]);
    const customerOptions = useMemo(() => customers.map((c) => ({ value: String(c.id), label: c.name })), [customers]);
    const contactOptions = useMemo(() => contacts.map((c) => ({ value: String(c.id), label: c.fullName })), [contacts]);

    const set = (patch: Partial<FormState>) => setForm((p) => ({ ...p, ...patch }));

    const submit = (andNew: boolean) => {
        if (!form.code.trim()) { showAlert('Mã tiềm năng không được để trống'); return; }
        if (!form.name.trim()) { showAlert('Tên tiềm năng không được để trống'); return; }
        mutate(toPayload(form), {
            onSuccess: () => {
                if (andNew) { setForm(initialState(defaultOwnerId)); showAlert('Đã lưu tiềm năng thành công'); }
                else navigate('/tiem-nang');
            },
            onError: (err: unknown) => {
                const msg = (err as { response?: { data?: { message?: string } } })?.response?.data?.message
                    ?? 'Có lỗi xảy ra khi lưu tiềm năng';
                showAlert(msg);
            },
        });
    };

    return (
        <div className="p-6 bg-bg-main min-h-[calc(100vh-50px)]">
            <FormPageHeader
                title="Thêm Tiềm năng"
                saving={isPending}
                onCancel={() => navigate(-1)}
                onSave={() => submit(false)}
                onSaveAndNew={() => submit(true)}
            />

            <div className="bg-white rounded-card shadow-sm p-6 space-y-8">
                <FormSection title="Thông tin chung">
                    <div className="grid grid-cols-2 gap-x-10 gap-y-4">
                        <div className="space-y-4">
                            <FieldRow label="Mã tiềm năng" required>
                                <input type="text" value={form.code} onChange={(e) => set({ code: e.target.value })} className={inputCls} />
                            </FieldRow>
                            <FieldRow label="Tên tiềm năng" required>
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
                            <FieldRow label="Email">
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
                            <FieldRow label="Mã số thuế">
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
                                <SearchableSelect value={form.customerId} onChange={(v) => set({ customerId: v })} options={customerOptions} />
                            </FieldRow>
                        </div>
                        <div className="space-y-4">
                            <FieldRow label="Liên hệ">
                                <SearchableSelect value={form.contactId} onChange={(v) => set({ contactId: v })} options={contactOptions} />
                            </FieldRow>
                            <FieldRow label="Giá trị ước tính">
                                <input type="number" value={form.estimatedValue} onChange={(e) => set({ estimatedValue: e.target.value })} className={inputCls} />
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

export default LeadAddPage;
