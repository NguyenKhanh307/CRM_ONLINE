import { useEffect, useMemo, useRef, useState } from 'react';
import { collectErrors } from '@/shared/utils/validators';
import { formatNumber } from '@/shared/utils/number';
import { useConfirm } from '@/shared/confirm/useConfirm';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { useFormKeyboardNav } from '@/shared/keyboard/useFormKeyboardNav';
import { SearchableSelect } from '@/shared/components/SearchableSelect';
import { FormPageHeader } from '@/shared/components/form/FormPageHeader';
import { FormSection } from '@/shared/components/form/FormSection';
import { FieldRow } from '@/shared/components/form/FieldRow';
import { PrefillHint } from '@/shared/components/form/PrefillHint';
import { fillEmpty, hasFilled } from '@/shared/utils/prefill';
import { fetchPrimaryContactId } from '@/shared/lookup/recordPrefill';
import { RecordPicker } from '@/shared/components/form/RecordPicker';
import { inputCls } from '@/shared/components/form/formStyles';
import { ProductLineItemsTable } from '@/shared/components/form/ProductLineItemsTable';
import {
    type LineItemRow,
    type ProductOption,
    computeTotals,
    emptyLineItem,
    toItemPayloads, validateLineItems } from '@/shared/components/form/productLineItem';
import { useAlert } from '@/shared/alert/useAlert';
import { useAuth } from '@/core/auth/useAuth';
import { useActiveUsers } from '@/features/users/hooks/useActiveUsers';
import { useCustomerList } from '@/features/khach-hang/hooks/useCustomerList';
import { useProductList } from '@/features/san-pham/hooks/useProductList';
import { useEligiblePricePolicies } from '@/features/chinh-sach-gia/hooks/useEligiblePricePolicies';
import { useCampaignList } from '@/features/chien-dich/hooks/useCampaignList';
import { leadService } from '@/features/tiem-nang/services/leadService';
import { useCreateOpportunity } from '../hooks/useCreateOpportunity';
import { useOpportunityStages } from '../hooks/useOpportunityStages';
import type { CreateOpportunityPayload } from '../types/opportunityTypes';
import type { LeadResult } from '@/features/tiem-nang/types/leadTypes';

const SOURCE_OPTIONS = [
    { value: 'website', label: 'Website' },
    { value: 'gioi-thieu', label: 'Giới thiệu' },
    { value: 'dien-thoai', label: 'Điện thoại' },
    { value: 'email', label: 'Email' },
    { value: 'khac', label: 'Khác' },
];

interface HeaderState {
    code: string; name: string; opportunityType: string; customerId: string; contactId: string;
    ownerId: string; stageId: string; campaignId: string; pricePolicyId: string; source: string;
    description: string; winLossReason: string;
}

// state khởi tạo — người phụ trách mặc định là user đang đăng nhập
const initialState = (ownerId: string): HeaderState => ({
    code: '', name: '', opportunityType: '', customerId: '', contactId: '', ownerId, stageId: '',
    campaignId: '', pricePolicyId: '', source: '',
    description: '', winLossReason: '',
});

// trang thêm cơ hội mới — header + bảng hàng hóa (layout AMIS)
const OpportunityAddPage = () => {
    const navigate = useNavigate();
    const { showAlert } = useAlert();
    const { confirmCreate } = useConfirm();
    const { user } = useAuth();
    const defaultOwnerId = user ? String(user.id) : '';
    const [form, setForm] = useState<HeaderState>(() => initialState(defaultOwnerId));
    const [rows, setRows] = useState<LineItemRow[]>([emptyLineItem()]);
    const { mutate, isPending } = useCreateOpportunity();

    const { data: users = [] } = useActiveUsers();
    const { data: customers = [] } = useCustomerList();
    const { data: products = [] } = useProductList();
    const { data: stages = [] } = useOpportunityStages();
    const { data: pricePolicies = [] } = useEligiblePricePolicies(form.customerId ? Number(form.customerId) : undefined);
    const { data: campaigns = [] } = useCampaignList();

    const userOptions = useMemo(() => users.map((u) => ({ value: String(u.id), label: u.fullName })), [users]);
    const campaignOptions = useMemo(() => campaigns.map((c) => ({ value: String(c.id), label: c.name })), [campaigns]);
    const customerOptions = useMemo(() => customers.map((c) => ({ value: String(c.id), label: c.name })), [customers]);
    const stageOptions = useMemo(() => stages.map((s) => ({ value: String(s.id), label: s.name })), [stages]);
    // giai đoạn đang chọn — nguồn của xác suất thắng (BE cũng suy ra từ đúng bản ghi này)
    const selectedStage = useMemo(() => stages.find((s) => String(s.id) === form.stageId), [stages, form.stageId]);
    const pricePolicyOptions = useMemo(() => pricePolicies.map((p) => ({ value: String(p.id), label: p.name })), [pricePolicies]);
    const productOptions = useMemo<ProductOption[]>(
        () => products.map((p) => ({ value: String(p.id), label: `${p.sku} — ${p.name}`, unit: p.unit ?? '', price: p.basePrice ?? 0 })),
        [products],
    );
    // giá trị cơ hội KHÔNG gõ tay — luôn suy ra từ tổng dòng hàng (khớp công thức BE tự tính lại)
    const amount = useMemo(() => computeTotals(rows).total, [rows]);

    // nguồn tạo cơ hội: Khách hàng có sẵn (mặc định, giữ nguyên luồng cũ) hoặc Tiềm năng có sẵn
    // (chỉ tự điền vài ô — KHÔNG tự tạo Khách hàng/Liên hệ, người dùng vẫn tự chọn như bình thường)
    const [originKind, setOriginKind] = useState<'customer' | 'lead'>('customer');
    const [pickedLeadId, setPickedLeadId] = useState('');
    const [pickedLead, setPickedLead] = useState<LeadResult | null>(null);
    const [leadPrefillFrom, setLeadPrefillFrom] = useState<string | null>(null);

    const [errors, setErrors] = useState<Record<string, string>>({});

    // hàm cập nhật form và xóa lỗi của đúng những field vừa gõ
    const set = (patch: Partial<HeaderState>) => {
        setForm((p) => ({ ...p, ...patch }));
        setErrors((e) => {
            const next = { ...e };
            Object.keys(patch).forEach((k) => delete next[k]);
            return next;
        });
    };
    // tên khách hàng vừa kéo dữ liệu về — hiện dòng gợi ý dưới ô Khách hàng
    const [prefillFrom, setPrefillFrom] = useState<string | null>(null);

    // hàm chọn khách hàng -> tự điền liên hệ chính, người phụ trách, nguồn (chỉ ô còn trống)
    const onPickCustomer = async (v: string) => {
        // đổi khách thì bỏ liên hệ cũ — liên hệ của khách khác gắn vào đây là dữ liệu sai
        const base = { ...form, customerId: v, contactId: '' };
        set({ customerId: v, contactId: '' });
        setPrefillFrom(null);
        const customer = customers.find((c) => String(c.id) === v);
        if (!customer) return;
        const patch = fillEmpty(base, {
            contactId: await fetchPrimaryContactId(customer.id),
            ownerId: customer.ownerId ? String(customer.ownerId) : '',
            source: customer.source ?? '',
        });
        if (hasFilled(patch)) { set(patch); setPrefillFrom(`khách hàng «${customer.name}»`); }
    };

    // hàm chọn tiềm năng nguồn -> chỉ tự điền tên/nguồn/chiến dịch/người phụ trách (ô còn trống),
    // KHÔNG đụng customerId/contactId — người dùng vẫn tự chọn/tạo Khách hàng+Liên hệ như bình thường
    const onPickLead = async (v: string) => {
        setPickedLeadId(v);
        setPickedLead(null);
        setLeadPrefillFrom(null);
        if (!v) return;
        const lead = (await leadService.getById(Number(v))).data.data;
        setPickedLead(lead);
        const patch = fillEmpty(form, {
            name: lead.companyName || lead.name,
            source: lead.source ?? '',
            campaignId: lead.campaignId ? String(lead.campaignId) : '',
            ownerId: lead.ownerId ? String(lead.ownerId) : '',
        });
        if (hasFilled(patch)) { set(patch); setLeadPrefillFrom(`tiềm năng «${lead.name}»`); }
    };

    // vào trang qua nút "Tạo cơ hội" (chuột phải ở danh sách Tiềm năng, ?fromLead=<id>) -> tự
    // chọn nguồn Tiềm năng + tự điền, chạy đúng 1 lần lúc mount
    const [searchParams] = useSearchParams();
    const autoPickedRef = useRef(false);
    useEffect(() => {
        const fromLead = searchParams.get('fromLead');
        if (!fromLead || autoPickedRef.current) return;
        autoPickedRef.current = true;
        setOriginKind('lead');
        void onPickLead(fromLead);
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [searchParams]);

    // hàm kiểm tra bắt buộc + biên (khớp ràng buộc backend) — trả map field->lỗi
    const validate = (): Record<string, string> =>
        collectErrors({
            code: !form.code.trim() ? 'Mã cơ hội không được để trống' : null,
            name: !form.name.trim() ? 'Tên cơ hội không được để trống' : null,
            items: validateLineItems(rows),
        });

    // hàm lưu — lỗi hiện đỏ dưới ô, popup xác nhận chỉ mở khi dữ liệu đã hợp lệ
    const submit = async () => {
        // bước kiểm tra dữ liệu
        const errs = validate();
        setErrors(errs);
        if (Object.keys(errs).length > 0) return;

        const payload: CreateOpportunityPayload = {
            code: form.code.trim(),
            name: form.name.trim(),
            opportunityType: form.opportunityType || null,
            customerId: form.customerId ? Number(form.customerId) : null,
            contactId: form.contactId ? Number(form.contactId) : null,
            ownerId: form.ownerId ? Number(form.ownerId) : null,
            stageId: form.stageId ? Number(form.stageId) : null,
            campaignId: form.campaignId ? Number(form.campaignId) : null,
            pricePolicyId: form.pricePolicyId ? Number(form.pricePolicyId) : null,
            amount,
            source: form.source || null,
            winLossReason: form.winLossReason || null,
            description: form.description || null,
            items: toItemPayloads(rows),
        };
        // bước hỏi xác nhận rồi mới gọi api lưu
        if (!(await confirmCreate('cơ hội'))) return;
        mutate(payload, {
            onSuccess: async (res) => {
                // tạo từ Tiềm năng có sẵn -> chỉ gán ngược converted_opportunity_id để truy vết
                // "cơ hội này bắt nguồn từ tiềm năng nào" (SELECT ... WHERE converted_opportunity_id = ?).
                // KHÔNG tự đổi status='converted' — quy định mới chỉ cho chuyển đổi khi đã có đơn
                // hàng đầu tiên (guard ở UpdateLeadUseCase), việc đó xảy ra sau và vẫn là thao tác tay.
                // Không rollback nếu bước này lỗi — cơ hội đã tạo vẫn giữ nguyên, gán tay lại sau qua LeadEditModal
                if (pickedLead) {
                    try {
                        await leadService.update(pickedLead.id, {
                            name: pickedLead.name,
                            companyName: pickedLead.companyName,
                            leadType: pickedLead.leadType,
                            ownerId: pickedLead.ownerId,
                            contactId: pickedLead.contactId,
                            convertedOpportunityId: res.data.data.id,
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
                    } catch {
                        // im lặng — không chặn luồng tạo cơ hội vì liên kết truy vết chỉ là phụ trợ
                    }
                }
                navigate('/co-hoi');
            },
            onError: (err: unknown) => {
                const msg = (err as { response?: { data?: { message?: string } } })?.response?.data?.message
                    ?? 'Có lỗi xảy ra khi lưu cơ hội';
                showAlert(msg);
            },
        });
    };

    const formRef = useRef<HTMLDivElement>(null);
    useFormKeyboardNav(formRef, { onSubmit: () => submit() });

    return (
        <div ref={formRef} className="p-6 bg-bg-main min-h-[calc(100vh-50px)]">
            <FormPageHeader title="Thêm Cơ hội" saving={isPending}
                onCancel={() => navigate(-1)} onSave={() => submit()} />

            <div className="bg-white rounded-card shadow-sm p-6 space-y-8">
                <FormSection title="Thông tin chung">
                    <div className="grid grid-cols-2 gap-x-10 gap-y-4">
                        <div className="space-y-4">
                            <FieldRow label="Mã cơ hội" required error={errors.code}>
                                <input type="text" value={form.code} onChange={(e) => set({ code: e.target.value })} className={inputCls} />
                            </FieldRow>
                            <FieldRow label="Tên cơ hội" required error={errors.name}>
                                <input type="text" value={form.name} onChange={(e) => set({ name: e.target.value })} className={inputCls} />
                            </FieldRow>
                            <FieldRow label="Loại cơ hội">
                                <input type="text" value={form.opportunityType} onChange={(e) => set({ opportunityType: e.target.value })} className={inputCls} />
                            </FieldRow>
                            <FieldRow label="Nguồn tạo cơ hội">
                                <div className="flex gap-2">
                                    <button
                                        type="button"
                                        onClick={() => setOriginKind('customer')}
                                        className={`px-3 py-1.5 text-sm rounded-btn border ${originKind === 'customer' ? 'border-primary bg-primary/10 text-primary' : 'border-gray-300 text-gray-600'}`}
                                    >
                                        Khách hàng có sẵn
                                    </button>
                                    <button
                                        type="button"
                                        onClick={() => setOriginKind('lead')}
                                        className={`px-3 py-1.5 text-sm rounded-btn border ${originKind === 'lead' ? 'border-primary bg-primary/10 text-primary' : 'border-gray-300 text-gray-600'}`}
                                    >
                                        Tiềm năng có sẵn
                                    </button>
                                </div>
                            </FieldRow>
                            {originKind === 'lead' && (
                                <FieldRow label="Tiềm năng nguồn">
                                    <RecordPicker module="lead" value={pickedLeadId} onChange={onPickLead} />
                                    <PrefillHint source={leadPrefillFrom} />
                                    <p className="text-xs text-gray-400 mt-1">
                                        Chỉ tự điền Tên/Nguồn/Chiến dịch/Người phụ trách — Khách hàng &amp; Liên hệ vẫn tự chọn/tạo bên dưới.
                                    </p>
                                </FieldRow>
                            )}
                            <FieldRow label="Khách hàng">
                                <SearchableSelect value={form.customerId} onChange={onPickCustomer} options={customerOptions} />
                                <PrefillHint source={prefillFrom} />
                            </FieldRow>
                            <FieldRow label="Liên hệ">
                                <RecordPicker module="contact" value={form.contactId} onChange={(v) => set({ contactId: v })}
                                    customerId={form.customerId ? Number(form.customerId) : undefined} />
                            </FieldRow>
                        </div>
                        <div className="space-y-4">
                            <FieldRow label="Người phụ trách">
                                <SearchableSelect value={form.ownerId} onChange={(v) => set({ ownerId: v })} options={userOptions} />
                            </FieldRow>
                            <FieldRow label="Giai đoạn">
                                <SearchableSelect value={form.stageId} onChange={(v) => set({ stageId: v })} options={stageOptions} />
                            </FieldRow>
                            <FieldRow label="Chính sách giá">
                                <SearchableSelect value={form.pricePolicyId} onChange={(v) => set({ pricePolicyId: v })} options={pricePolicyOptions} />
                            </FieldRow>
                            <FieldRow label="Nguồn gốc">
                                <SearchableSelect value={form.source} onChange={(v) => set({ source: v })} options={SOURCE_OPTIONS} />
                            </FieldRow>
                            <FieldRow label="Chiến dịch">
                                <SearchableSelect value={form.campaignId} onChange={(v) => set({ campaignId: v })} options={campaignOptions} />
                            </FieldRow>
                        </div>
                    </div>
                </FormSection>

                <FormSection title="Giá trị">
                    <div className="grid grid-cols-2 gap-x-10 gap-y-4">
                        <div className="space-y-4">
                            {/* giá trị KHÔNG gõ tay — luôn suy từ tổng dòng hàng bên dưới, khớp cách backend tự tính lại */}
                            <FieldRow label="Giá trị">
                                <div className="flex items-center gap-2">
                                    <span className="text-md font-medium text-text-main">{formatNumber(amount)}</span>
                                    <span className="text-sm text-gray-400">(tự tính theo dòng hàng)</span>
                                </div>
                            </FieldRow>
                        </div>
                        <div className="space-y-4">
                            {/* xác suất do giai đoạn pipeline định nghĩa (opportunity_stages.probability) — chỉ hiển thị */}
                            <FieldRow label="Xác suất (%)">
                                <div className="flex items-center gap-2">
                                    <span className="text-md font-medium text-text-main">
                                        {selectedStage ? `${formatNumber(selectedStage.probability)}%` : '—'}
                                    </span>
                                    <span className="text-sm text-gray-400">(tự động theo giai đoạn)</span>
                                </div>
                            </FieldRow>
                        </div>
                    </div>
                </FormSection>

                <FormSection title="Hàng hóa">
                    <ProductLineItemsTable rows={rows} onChange={setRows} productOptions={productOptions} pricePolicyId={form.pricePolicyId ? Number(form.pricePolicyId) : null} customerId={form.customerId ? Number(form.customerId) : null} />
                    {errors.items && <p className="text-xs text-danger mt-1">{errors.items}</p>}
                </FormSection>

                <FormSection title="Thông tin mô tả">
                    <div className="space-y-4">
                        <FieldRow label="Mô tả" alignTop>
                            <textarea rows={3} value={form.description} onChange={(e) => set({ description: e.target.value })} className={`${inputCls} resize-none`} />
                        </FieldRow>
                        <FieldRow label="Lý do thắng/thua" alignTop>
                            <textarea rows={2} value={form.winLossReason} onChange={(e) => set({ winLossReason: e.target.value })} className={`${inputCls} resize-none`} />
                        </FieldRow>
                    </div>
                </FormSection>
            </div>
        </div>
    );
};

export default OpportunityAddPage;
