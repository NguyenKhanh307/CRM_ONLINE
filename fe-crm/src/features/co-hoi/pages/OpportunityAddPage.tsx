import { useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { SearchableSelect } from '@/shared/components/SearchableSelect';
import { FormPageHeader } from '@/shared/components/form/FormPageHeader';
import { FormSection } from '@/shared/components/form/FormSection';
import { FieldRow } from '@/shared/components/form/FieldRow';
import { inputCls } from '@/shared/components/form/formStyles';
import { DateInput } from '@/shared/components/form/DateInput';
import { ProductLineItemsTable } from '@/shared/components/form/ProductLineItemsTable';
import {
    type LineItemRow,
    type ProductOption,
    emptyLineItem,
    toItemPayloads,
} from '@/shared/components/form/productLineItem';
import { useAlert } from '@/shared/alert/useAlert';
import { useAuth } from '@/core/auth/useAuth';
import { useActiveUsers } from '@/features/users/hooks/useActiveUsers';
import { useCustomerList } from '@/features/khach-hang/hooks/useCustomerList';
import { useContactList } from '@/features/lien-he/hooks/useContactList';
import { useProductList } from '@/features/san-pham/hooks/useProductList';
import { usePricePolicyList } from '@/features/chinh-sach-gia/hooks/usePricePolicyList';
import { useCreateOpportunity } from '../hooks/useCreateOpportunity';
import { useOpportunityStages } from '../hooks/useOpportunityStages';
import type { CreateOpportunityPayload } from '../types/opportunityTypes';

const SOURCE_OPTIONS = [
    { value: 'website', label: 'Website' },
    { value: 'gioi-thieu', label: 'Giới thiệu' },
    { value: 'dien-thoai', label: 'Điện thoại' },
    { value: 'email', label: 'Email' },
    { value: 'khac', label: 'Khác' },
];

interface HeaderState {
    code: string; name: string; opportunityType: string; customerId: string; contactId: string;
    ownerId: string; stageId: string; pricePolicyId: string; source: string;
    amount: string; expectedRevenue: string; probability: string; expectedCloseDate: string;
    description: string; winLossReason: string;
}

/** State khởi tạo — người phụ trách mặc định là user đang đăng nhập. */
const initialState = (ownerId: string): HeaderState => ({
    code: '', name: '', opportunityType: '', customerId: '', contactId: '', ownerId, stageId: '',
    pricePolicyId: '', source: '', amount: '', expectedRevenue: '', probability: '', expectedCloseDate: '',
    description: '', winLossReason: '',
});

const num = (s: string): number | null => (s.trim() ? Number(s) : null);

/** Trang thêm cơ hội mới — header + bảng hàng hóa (layout AMIS). */
const OpportunityAddPage = () => {
    const navigate = useNavigate();
    const { showAlert } = useAlert();
    const { user } = useAuth();
    const defaultOwnerId = user ? String(user.id) : '';
    const [form, setForm] = useState<HeaderState>(() => initialState(defaultOwnerId));
    const [rows, setRows] = useState<LineItemRow[]>([emptyLineItem()]);
    const { mutate, isPending } = useCreateOpportunity();

    const { data: users = [] } = useActiveUsers();
    const { data: customers = [] } = useCustomerList();
    const { data: contacts = [] } = useContactList();
    const { data: products = [] } = useProductList();
    const { data: stages = [] } = useOpportunityStages();
    const { data: pricePolicies = [] } = usePricePolicyList();

    const userOptions = useMemo(() => users.map((u) => ({ value: String(u.id), label: u.fullName })), [users]);
    const customerOptions = useMemo(() => customers.map((c) => ({ value: String(c.id), label: c.name })), [customers]);
    const contactOptions = useMemo(() => contacts.map((c) => ({ value: String(c.id), label: c.fullName })), [contacts]);
    const stageOptions = useMemo(() => stages.map((s) => ({ value: String(s.id), label: s.name })), [stages]);
    const pricePolicyOptions = useMemo(() => pricePolicies.map((p) => ({ value: String(p.id), label: p.name })), [pricePolicies]);
    const productOptions = useMemo<ProductOption[]>(
        () => products.map((p) => ({ value: String(p.id), label: `${p.sku} — ${p.name}`, unit: p.unit ?? '', price: p.basePrice ?? 0 })),
        [products],
    );

    const set = (patch: Partial<HeaderState>) => setForm((p) => ({ ...p, ...patch }));
    const reset = () => { setForm(initialState(defaultOwnerId)); setRows([emptyLineItem()]); };

    const submit = (andNew: boolean) => {
        if (!form.code.trim()) { showAlert('Mã cơ hội không được để trống'); return; }
        if (!form.name.trim()) { showAlert('Tên cơ hội không được để trống'); return; }
        const payload: CreateOpportunityPayload = {
            code: form.code.trim(),
            name: form.name.trim(),
            opportunityType: form.opportunityType || null,
            customerId: form.customerId ? Number(form.customerId) : null,
            contactId: form.contactId ? Number(form.contactId) : null,
            ownerId: form.ownerId ? Number(form.ownerId) : null,
            stageId: form.stageId ? Number(form.stageId) : null,
            pricePolicyId: form.pricePolicyId ? Number(form.pricePolicyId) : null,
            amount: num(form.amount),
            expectedRevenue: num(form.expectedRevenue),
            probability: num(form.probability),
            expectedCloseDate: form.expectedCloseDate || null,
            source: form.source || null,
            winLossReason: form.winLossReason || null,
            description: form.description || null,
            items: toItemPayloads(rows),
        };
        mutate(payload, {
            onSuccess: () => {
                if (andNew) { reset(); showAlert('Đã lưu cơ hội thành công'); }
                else navigate('/co-hoi');
            },
            onError: (err: unknown) => {
                const msg = (err as { response?: { data?: { message?: string } } })?.response?.data?.message
                    ?? 'Có lỗi xảy ra khi lưu cơ hội';
                showAlert(msg);
            },
        });
    };

    return (
        <div className="p-6 bg-bg-main min-h-[calc(100vh-50px)]">
            <FormPageHeader title="Thêm Cơ hội" saving={isPending}
                onCancel={() => navigate(-1)} onSave={() => submit(false)} onSaveAndNew={() => submit(true)} />

            <div className="bg-white rounded-card shadow-sm p-6 space-y-8">
                <FormSection title="Thông tin chung">
                    <div className="grid grid-cols-2 gap-x-10 gap-y-4">
                        <div className="space-y-4">
                            <FieldRow label="Mã cơ hội" required>
                                <input type="text" value={form.code} onChange={(e) => set({ code: e.target.value })} className={inputCls} />
                            </FieldRow>
                            <FieldRow label="Tên cơ hội" required>
                                <input type="text" value={form.name} onChange={(e) => set({ name: e.target.value })} className={inputCls} />
                            </FieldRow>
                            <FieldRow label="Loại cơ hội">
                                <input type="text" value={form.opportunityType} onChange={(e) => set({ opportunityType: e.target.value })} className={inputCls} />
                            </FieldRow>
                            <FieldRow label="Khách hàng">
                                <SearchableSelect value={form.customerId} onChange={(v) => set({ customerId: v })} options={customerOptions} />
                            </FieldRow>
                            <FieldRow label="Liên hệ">
                                <SearchableSelect value={form.contactId} onChange={(v) => set({ contactId: v })} options={contactOptions} />
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
                        </div>
                    </div>
                </FormSection>

                <FormSection title="Giá trị">
                    <div className="grid grid-cols-2 gap-x-10 gap-y-4">
                        <div className="space-y-4">
                            <FieldRow label="Giá trị">
                                <input type="number" value={form.amount} onChange={(e) => set({ amount: e.target.value })} className={inputCls} />
                            </FieldRow>
                            <FieldRow label="Doanh số kỳ vọng">
                                <input type="number" value={form.expectedRevenue} onChange={(e) => set({ expectedRevenue: e.target.value })} className={inputCls} />
                            </FieldRow>
                        </div>
                        <div className="space-y-4">
                            <FieldRow label="Xác suất (%)">
                                <input type="number" value={form.probability} onChange={(e) => set({ probability: e.target.value })} className={inputCls} />
                            </FieldRow>
                            <FieldRow label="Ngày đóng dự kiến">
                                <DateInput value={form.expectedCloseDate} onChange={(v) => set({ expectedCloseDate: v })} />
                            </FieldRow>
                        </div>
                    </div>
                </FormSection>

                <FormSection title="Hàng hóa">
                    <ProductLineItemsTable rows={rows} onChange={setRows} productOptions={productOptions} />
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
