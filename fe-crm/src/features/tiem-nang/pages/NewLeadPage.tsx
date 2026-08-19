import { useMemo, useRef, useState } from 'react';
import { collectErrors, emailError, phoneError, requiredError, taxCodeError, validateOrWarn } from '@/shared/utils/validators';
import { useConfirm } from '@/shared/confirm/useConfirm';
import { useNavigate } from 'react-router-dom';
import { FiPlus, FiTrash2, FiUserPlus } from 'react-icons/fi';
import { SearchableSelect } from '@/shared/components/SearchableSelect';
import { ActionButton } from '@/shared/components/ActionButton';
import { useFormKeyboardNav } from '@/shared/keyboard/useFormKeyboardNav';
import { FormPageHeader } from '@/shared/components/form/FormPageHeader';
import { DuplicateWarning } from '@/shared/components/DuplicateWarning';
import { useDuplicateCheck } from '@/shared/hooks/useDuplicateCheck';
import { FormSection } from '@/shared/components/form/FormSection';
import { FieldRow } from '@/shared/components/form/FieldRow';
import { RecordPicker } from '@/shared/components/form/RecordPicker';
import { inputCls } from '@/shared/components/form/formStyles';
import { useAlert } from '@/shared/alert/useAlert';
import { useAuth } from '@/core/auth/useAuth';
import { useActiveUsers } from '@/features/users/hooks/useActiveUsers';
import { useCampaignList } from '@/features/chien-dich/hooks/useCampaignList';
import { useProductList } from '@/features/san-pham/hooks/useProductList';
import { leadService } from '../services/leadService';
import { useCreateLead } from '../hooks/useCreateLead';
import type { CreateLeadItemPayload, CreateLeadPayload } from '../types/leadTypes';
import { LEAD_TYPE_OPTIONS, SOURCE_OPTIONS } from '../config/leadOptions';

// dòng nháp "sản phẩm quan tâm" gom ở client — chỉ gửi lên BE sau khi tiềm năng đã có id
interface InterestDraftRow {
    id: string;
    productId: string;
    interestType: 'viewed' | 'requested_quote';
    note: string;
}

const INTEREST_TYPE_OPTIONS = [
    { value: 'viewed', label: 'Đã xem' },
    { value: 'requested_quote', label: 'Yêu cầu báo giá' },
];

interface FormState {
    code: string;
    name: string;
    leadType: string;
    phone: string;
    email: string;
    source: string;
    companyName: string;
    taxCode: string;
    website: string;
    industry: string;
    ownerId: string;
    contactId: string;
    campaignId: string;
    note: string;
}

// state khởi tạo — người phụ trách mặc định là user đang đăng nhập
const initialState = (ownerId: string): FormState => ({
    code: '', name: '', leadType: '', phone: '', email: '',
    source: '', companyName: '', taxCode: '', website: '', industry: '',
    ownerId, contactId: '', campaignId: '', note: '',
});

const toPayload = (f: FormState): CreateLeadPayload => ({
    code: f.code.trim(),
    name: f.name.trim(),
    companyName: f.companyName || null,
    leadType: f.leadType || null,
    ownerId: f.ownerId ? Number(f.ownerId) : null,
    contactId: f.contactId ? Number(f.contactId) : null,
    campaignId: f.campaignId ? Number(f.campaignId) : null,
    taxCode: f.taxCode || null,
    website: f.website || null,
    industry: f.industry || null,
    source: f.source || null,
    phone: f.phone || null,
    email: f.email || null,
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
    const { data: campaigns = [] } = useCampaignList();
    const { data: products = [] } = useProductList();

    const userOptions = useMemo(() => users.map((u) => ({ value: String(u.id), label: u.fullName })), [users]);
    const campaignOptions = useMemo(() => (campaigns ?? []).map((c) => ({ value: String(c.id), label: c.name })), [campaigns]);
    const productOptions = useMemo(() => products.map((p) => ({ value: String(p.id), label: `${p.sku} — ${p.name}` })), [products]);

    // "Sản phẩm quan tâm" — chưa có leadId nên chỉ gom nháp ở client, gửi từng dòng sau khi tạo lead
    const [interestRows, setInterestRows] = useState<InterestDraftRow[]>([]);
    const [draftProductId, setDraftProductId] = useState('');
    const [draftInterestType, setDraftInterestType] = useState<'viewed' | 'requested_quote'>('viewed');
    const [draftNote, setDraftNote] = useState('');

    const addInterestRow = () => {
        if (!draftProductId) return;
        setInterestRows((rows) => [
            ...rows,
            { id: crypto.randomUUID(), productId: draftProductId, interestType: draftInterestType, note: draftNote },
        ]);
        setDraftProductId('');
        setDraftInterestType('viewed');
        setDraftNote('');
    };
    const removeInterestRow = (id: string) => setInterestRows((rows) => rows.filter((r) => r.id !== id));

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

    // hàm kiểm tra bắt buộc + biên (khớp ràng buộc backend) — trả map field->lỗi
    const validate = (): Record<string, string> =>
        collectErrors({
            code: requiredError(form.code, 'Mã tiềm năng'),
            name: requiredError(form.name, 'Tên tiềm năng'),
            email: emailError(form.email),
            phone: phoneError(form.phone),
            taxCode: taxCodeError(form.taxCode),
        });

    // hàm lưu — lỗi hiện đỏ dưới ô, popup xác nhận chỉ mở khi dữ liệu đã hợp lệ
    const submit = async () => {
        // bước kiểm tra dữ liệu
        const errs = validate();
        setErrors(errs);
        if (!validateOrWarn(errs, showAlert)) return;

        // bước hỏi xác nhận
        if (!(await confirmCreate('tiềm năng'))) return;

        // bước gọi api lưu
        mutate(toPayload(form), {
            onSuccess: async (res) => {
                const newLeadId = res.data.data.id;
                // gửi từng dòng "sản phẩm quan tâm" đã gom nháp — chỉ gửi được sau khi lead có id
                if (interestRows.length > 0) {
                    await Promise.all(interestRows.map((r) => {
                        const payload: CreateLeadItemPayload = {
                            productId: Number(r.productId),
                            interestType: r.interestType,
                            note: r.note || null,
                        };
                        return leadService.createItem(newLeadId, payload);
                    }));
                }
                navigate('/tiem-nang');
            },
            onError: (err: unknown) => {
                const msg = (err as { response?: { data?: { message?: string } } })?.response?.data?.message
                    ?? 'Có lỗi xảy ra khi lưu tiềm năng';
                showAlert(msg);
            },
        });
    };

    const formRef = useRef<HTMLDivElement>(null);
    useFormKeyboardNav(formRef, { onSubmit: () => submit() });

    // cảnh báo (không chặn) khi email/SĐT/MST trùng bản ghi đã có
    const { data: duplicates } = useDuplicateCheck({ email: form.email, phone: form.phone, taxCode: form.taxCode });

    return (
        <div ref={formRef} className="p-6 bg-bg-main min-h-[calc(100vh-50px)]">
            <FormPageHeader
                title="Thêm Tiềm năng"
                saving={isPending}
                onCancel={() => navigate(-1)}
                onSave={() => submit()}
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
                            <FieldRow label="Liên hệ">
                                <div className="flex items-center gap-1.5">
                                    <div className="flex-1">
                                        <RecordPicker module="contact" value={form.contactId} onChange={(v) => set({ contactId: v })} />
                                    </div>
                                    <button
                                        type="button"
                                        title="Tạo liên hệ mới (mở tab mới)"
                                        onClick={() => window.open('/lien-he/them-moi', '_blank')}
                                        className="shrink-0 p-2 rounded-btn border border-gray-300 text-gray-500 hover:border-primary hover:text-primary"
                                    >
                                        <FiUserPlus size={14} />
                                    </button>
                                </div>
                            </FieldRow>
                        </div>
                        <div className="space-y-4">
                            <FieldRow label="Chiến dịch nguồn">
                                <SearchableSelect value={form.campaignId} onChange={(v) => set({ campaignId: v })} options={campaignOptions} />
                            </FieldRow>
                        </div>
                    </div>
                </FormSection>

                <FormSection title="Sản phẩm quan tâm">
                    <div className="space-y-3">
                        <div className="grid grid-cols-3 gap-2 items-start">
                            <SearchableSelect
                                value={draftProductId}
                                onChange={setDraftProductId}
                                options={productOptions}
                                placeholder="Chọn sản phẩm"
                            />
                            <SearchableSelect
                                value={draftInterestType}
                                onChange={(v) => setDraftInterestType((v || 'viewed') as 'viewed' | 'requested_quote')}
                                options={INTEREST_TYPE_OPTIONS}
                            />
                            <div className="flex items-center gap-1.5">
                                <input
                                    type="text"
                                    placeholder="Ghi chú (tùy chọn)"
                                    value={draftNote}
                                    onChange={(e) => setDraftNote(e.target.value)}
                                    className={inputCls}
                                />
                                <ActionButton icon={FiPlus} disabled={!draftProductId} onClick={addInterestRow}>
                                    Thêm
                                </ActionButton>
                            </div>
                        </div>

                        {interestRows.length > 0 && (
                            <table className="w-full text-sm">
                                <thead>
                                    <tr className="text-left text-gray-500 border-b border-gray-100">
                                        <th className="py-1.5 pr-2 font-medium">Sản phẩm</th>
                                        <th className="py-1.5 pr-2 font-medium">Mức độ quan tâm</th>
                                        <th className="py-1.5 pr-2 font-medium">Ghi chú</th>
                                        <th className="py-1.5 pr-2 font-medium w-8" />
                                    </tr>
                                </thead>
                                <tbody>
                                    {interestRows.map((r) => (
                                        <tr key={r.id} className="border-b border-gray-50">
                                            <td className="py-1.5 pr-2 text-text-main">
                                                {productOptions.find((o) => o.value === r.productId)?.label ?? r.productId}
                                            </td>
                                            <td className="py-1.5 pr-2 text-text-main">
                                                {INTEREST_TYPE_OPTIONS.find((o) => o.value === r.interestType)?.label}
                                            </td>
                                            <td className="py-1.5 pr-2 text-gray-500">{r.note || '—'}</td>
                                            <td className="py-1.5 pr-2 text-right">
                                                <button
                                                    type="button"
                                                    title="Xóa dòng"
                                                    onClick={() => removeInterestRow(r.id)}
                                                    className="p-1 rounded hover:bg-red-50 text-gray-400 hover:text-danger"
                                                >
                                                    <FiTrash2 size={13} />
                                                </button>
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        )}
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
