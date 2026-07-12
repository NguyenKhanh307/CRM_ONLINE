import { useMemo, useRef, useState } from 'react';
import { emailError, nonNegativeError, phoneError, taxCodeError } from '@/shared/utils/validators';
import { useConfirm } from '@/shared/confirm/useConfirm';
import { useNavigate } from 'react-router-dom';
import { useFormKeyboardNav } from '@/shared/keyboard/useFormKeyboardNav';
import { SearchableSelect } from '@/shared/components/SearchableSelect';
import { FormPageHeader } from '@/shared/components/form/FormPageHeader';
import { DuplicateWarning } from '@/shared/components/DuplicateWarning';
import { useDuplicateCheck } from '@/shared/hooks/useDuplicateCheck';
import { FormSection } from '@/shared/components/form/FormSection';
import { FieldRow } from '@/shared/components/form/FieldRow';
import { inputCls } from '@/shared/components/form/formStyles';
import { useAlert } from '@/shared/alert/useAlert';
import { useAuth } from '@/core/auth/useAuth';
import { useActiveUsers } from '@/features/users/hooks/useActiveUsers';
import { useOrgUnits } from '@/features/users/hooks/useOrgUnits';
import { useCreateCustomer } from '../hooks/useCreateCustomer';
import type { CreateCustomerPayload } from '../types/customerTypes';

const TYPE_OPTIONS = [
    { value: 'company', label: 'Doanh nghiệp' },
    { value: 'individual', label: 'Cá nhân' },
];

const SOURCE_OPTIONS = [
    { value: 'website', label: 'Website' },
    { value: 'gioi-thieu', label: 'Giới thiệu' },
    { value: 'dien-thoai', label: 'Điện thoại' },
    { value: 'email', label: 'Email' },
    { value: 'khac', label: 'Khác' },
];

const RATING_OPTIONS = [
    { value: 'A', label: 'A' },
    { value: 'B', label: 'B' },
    { value: 'C', label: 'C' },
];

interface FormState {
    code: string; name: string; shortName: string; type: string; taxCode: string;
    industry: string; source: string;
    phone: string; email: string; website: string; address: string;
    creditDays: string; creditLimit: string; bankAccount: string; bankName: string;
    rating: string; annualRevenue: string; employeeSize: string; isDistributor: boolean;
    ownerId: string; unitId: string;
}

/** State khởi tạo — người phụ trách mặc định là user đang đăng nhập. */
const initialState = (ownerId: string): FormState => ({
    code: '', name: '', shortName: '', type: 'company', taxCode: '', industry: '', source: '',
    phone: '', email: '', website: '', address: '', creditDays: '', creditLimit: '',
    bankAccount: '', bankName: '', rating: '', annualRevenue: '', employeeSize: '', isDistributor: false,
    ownerId, unitId: '',
});

const num = (s: string): number | null => (s.trim() ? Number(s) : null);

const toPayload = (f: FormState): CreateCustomerPayload => ({
    code: f.code.trim(),
    name: f.name.trim(),
    shortName: f.shortName || null,
    type: f.type,
    taxCode: f.taxCode || null,
    phone: f.phone || null,
    email: f.email || null,
    website: f.website || null,
    address: f.address || null,
    industry: f.industry || null,
    source: f.source || null,
    creditDays: num(f.creditDays),
    creditLimit: num(f.creditLimit),
    bankAccount: f.bankAccount || null,
    bankName: f.bankName || null,
    rating: f.rating || null,
    annualRevenue: num(f.annualRevenue),
    employeeSize: f.employeeSize || null,
    isDistributor: f.isDistributor,
    ownerId: f.ownerId ? Number(f.ownerId) : null,
    unitId: f.unitId ? Number(f.unitId) : null,
});

/** Trang thêm khách hàng mới — form full-page nhiều section (layout AMIS). */
const CustomerAddPage = () => {
    const navigate = useNavigate();
    const { showAlert } = useAlert();
    const { confirmCreate } = useConfirm();
    const { user } = useAuth();
    const defaultOwnerId = user ? String(user.id) : '';
    const [form, setForm] = useState<FormState>(() => initialState(defaultOwnerId));
    const { mutate, isPending } = useCreateCustomer();
    const { data: users = [] } = useActiveUsers();
    const { data: units = [] } = useOrgUnits();

    const userOptions = useMemo(() => users.map((u) => ({ value: String(u.id), label: u.fullName })), [users]);
    const unitOptions = useMemo(() => units.map((u) => ({ value: String(u.id), label: u.name })), [units]);

    const set = (patch: Partial<FormState>) => setForm((p) => ({ ...p, ...patch }));

    const submit = async (andNew: boolean) => {
        if (!form.code.trim()) { showAlert('Mã khách hàng không được để trống'); return; }
        if (!form.name.trim()) { showAlert('Tên khách hàng không được để trống'); return; }
        if (!(await confirmCreate('khách hàng'))) return;
        // Kiểm tra biên (khớp ràng buộc backend) — chặn submit nếu dữ liệu không hợp lệ
        const vErr = emailError(form.email) ?? phoneError(form.phone) ?? taxCodeError(form.taxCode)
            ?? nonNegativeError(form.creditLimit, 'Hạn mức tín dụng') ?? nonNegativeError(form.annualRevenue, 'Doanh thu năm');
        if (vErr) { showAlert(vErr); return; }
        mutate(toPayload(form), {
            onSuccess: () => {
                if (andNew) { setForm(initialState(defaultOwnerId)); showAlert('Đã lưu khách hàng thành công'); }
                else navigate('/khach-hang');
            },
            onError: (err: unknown) => {
                const msg = (err as { response?: { data?: { message?: string } } })?.response?.data?.message
                    ?? 'Có lỗi xảy ra khi lưu khách hàng';
                showAlert(msg);
            },
        });
    };

    const formRef = useRef<HTMLDivElement>(null);
    useFormKeyboardNav(formRef, { onSubmit: () => submit(false) });

    // Cảnh báo (không chặn) khi email/SĐT/MST trùng bản ghi đã có
    const { data: duplicates } = useDuplicateCheck({ email: form.email, phone: form.phone, taxCode: form.taxCode });

    return (
        <div ref={formRef} className="p-6 bg-bg-main min-h-[calc(100vh-50px)]">
            <FormPageHeader title="Thêm Khách hàng" saving={isPending}
                onCancel={() => navigate(-1)} onSave={() => submit(false)} onSaveAndNew={() => submit(true)} />

            <DuplicateWarning matches={duplicates} />

            <div className="bg-white rounded-card shadow-sm p-6 space-y-8">
                <FormSection title="Thông tin chung">
                    <div className="grid grid-cols-2 gap-x-10 gap-y-4">
                        <div className="space-y-4">
                            <FieldRow label="Mã khách hàng" required>
                                <input type="text" value={form.code} onChange={(e) => set({ code: e.target.value })} className={inputCls} />
                            </FieldRow>
                            <FieldRow label="Tên khách hàng" required>
                                <input type="text" value={form.name} onChange={(e) => set({ name: e.target.value })} className={inputCls} />
                            </FieldRow>
                            <FieldRow label="Tên viết tắt">
                                <input type="text" value={form.shortName} onChange={(e) => set({ shortName: e.target.value })} className={inputCls} />
                            </FieldRow>
                            <FieldRow label="Mã số thuế">
                                <input type="text" value={form.taxCode} onChange={(e) => set({ taxCode: e.target.value })} className={inputCls} />
                            </FieldRow>
                        </div>
                        <div className="space-y-4">
                            <FieldRow label="Loại khách hàng">
                                <SearchableSelect value={form.type} onChange={(v) => set({ type: v })} options={TYPE_OPTIONS} />
                            </FieldRow>
                            <FieldRow label="Ngành nghề">
                                <input type="text" value={form.industry} onChange={(e) => set({ industry: e.target.value })} className={inputCls} />
                            </FieldRow>
                            <FieldRow label="Nguồn gốc">
                                <SearchableSelect value={form.source} onChange={(v) => set({ source: v })} options={SOURCE_OPTIONS} />
                            </FieldRow>
                        </div>
                    </div>
                </FormSection>

                <FormSection title="Thông tin liên lạc">
                    <div className="grid grid-cols-2 gap-x-10 gap-y-4">
                        <div className="space-y-4">
                            <FieldRow label="Điện thoại">
                                <input type="text" value={form.phone} onChange={(e) => set({ phone: e.target.value })} className={inputCls} />
                            </FieldRow>
                            <FieldRow label="Email">
                                <input type="text" value={form.email} onChange={(e) => set({ email: e.target.value })} className={inputCls} />
                            </FieldRow>
                        </div>
                        <div className="space-y-4">
                            <FieldRow label="Website">
                                <input type="text" value={form.website} onChange={(e) => set({ website: e.target.value })} className={inputCls} />
                            </FieldRow>
                            <FieldRow label="Địa chỉ" alignTop>
                                <textarea rows={2} value={form.address} onChange={(e) => set({ address: e.target.value })} className={`${inputCls} resize-none`} />
                            </FieldRow>
                        </div>
                    </div>
                </FormSection>

                <FormSection title="Thông tin tài chính">
                    <div className="grid grid-cols-2 gap-x-10 gap-y-4">
                        <div className="space-y-4">
                            <FieldRow label="Số ngày được nợ">
                                <input type="number" value={form.creditDays} onChange={(e) => set({ creditDays: e.target.value })} className={inputCls} />
                            </FieldRow>
                            <FieldRow label="Số tài khoản">
                                <input type="text" value={form.bankAccount} onChange={(e) => set({ bankAccount: e.target.value })} className={inputCls} />
                            </FieldRow>
                        </div>
                        <div className="space-y-4">
                            <FieldRow label="Hạn mức nợ">
                                <input type="number" value={form.creditLimit} onChange={(e) => set({ creditLimit: e.target.value })} className={inputCls} />
                            </FieldRow>
                            <FieldRow label="Ngân hàng">
                                <input type="text" value={form.bankName} onChange={(e) => set({ bankName: e.target.value })} className={inputCls} />
                            </FieldRow>
                        </div>
                    </div>
                </FormSection>

                <FormSection title="Phân loại">
                    <div className="grid grid-cols-2 gap-x-10 gap-y-4">
                        <div className="space-y-4">
                            <FieldRow label="Xếp hạng">
                                <SearchableSelect value={form.rating} onChange={(v) => set({ rating: v })} options={RATING_OPTIONS} />
                            </FieldRow>
                            <FieldRow label="Doanh thu hàng năm">
                                <input type="number" value={form.annualRevenue} onChange={(e) => set({ annualRevenue: e.target.value })} className={inputCls} />
                            </FieldRow>
                        </div>
                        <div className="space-y-4">
                            <FieldRow label="Quy mô nhân sự">
                                <input type="text" value={form.employeeSize} onChange={(e) => set({ employeeSize: e.target.value })} className={inputCls} />
                            </FieldRow>
                            <label className="flex items-center gap-2 cursor-pointer w-fit ml-[160px]">
                                <input type="checkbox" checked={form.isDistributor} onChange={(e) => set({ isDistributor: e.target.checked })} className="w-4 h-4 accent-primary" />
                                <span className="text-md text-text-main">Là nhà phân phối</span>
                            </label>
                        </div>
                    </div>
                </FormSection>

                <FormSection title="Phụ trách">
                    <div className="grid grid-cols-2 gap-x-10 gap-y-4">
                        <FieldRow label="Nhân viên phụ trách">
                            <SearchableSelect value={form.ownerId} onChange={(v) => set({ ownerId: v })} options={userOptions} />
                        </FieldRow>
                        <FieldRow label="Đơn vị">
                            <SearchableSelect value={form.unitId} onChange={(v) => set({ unitId: v })} options={unitOptions} />
                        </FieldRow>
                    </div>
                </FormSection>
            </div>
        </div>
    );
};

export default CustomerAddPage;
