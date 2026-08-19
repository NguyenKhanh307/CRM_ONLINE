import { useRef, useState, type FormEvent, useEffect, useMemo } from 'react';
import { notify } from '@/core/data/dataBus';
import { formatNumber } from '@/shared/utils/number';
import { ModalFooter } from '@/shared/components/ModalFooter';
import { useConfirm } from '@/shared/confirm/useConfirm';
import { useFormKeyboardNav } from '@/shared/keyboard/useFormKeyboardNav';
import { FiX } from 'react-icons/fi';
import type { OpportunityResult, UpdateOpportunityPayload } from '../types/opportunityTypes';
import { useUpdateOpportunity } from '../hooks/useUpdateOpportunity';
import { useOpportunityStages } from '../hooks/useOpportunityStages';
import { useCampaignList } from '@/features/chien-dich/hooks/useCampaignList';
import { useEligiblePricePolicies } from '@/features/chinh-sach-gia/hooks/useEligiblePricePolicies';
import { opportunityService } from '../services/opportunityService';
import { useProductList } from '@/features/san-pham/hooks/useProductList';
import { useCustomerList } from '@/features/khach-hang/hooks/useCustomerList';
import { useActiveUsers } from '@/features/users/hooks/useActiveUsers';
import { SearchableSelect } from '@/shared/components/SearchableSelect';
import { OPPORTUNITY_TYPE_OPTIONS } from '../config/opportunityOptions';
import { RecordPicker } from '@/shared/components/form/RecordPicker';
import { ProductLineItemsTable } from '@/shared/components/form/ProductLineItemsTable';
import {
    type LineItemRow,
    type ProductOption,
    computeTotals,
    fromItemResult,
    diffLineItems,
    toItemPayload,
} from '@/shared/components/form/productLineItem';

interface Props {
    item: OpportunityResult | null;
    onClose: () => void;
}

const OPP_STATUS_LABELS: Record<string, string> = { open: 'Đang mở', won: 'Thắng', lost: 'Thua' };
const OPP_STATUS_COLORS: Record<string, string> = {
    open: 'bg-blue-100 text-blue-700', won: 'bg-green-100 text-green-700', lost: 'bg-red-100 text-red-600',
};
const SOURCE_OPTIONS = [
    { value: 'website', label: 'Website' },
    { value: 'gioi-thieu', label: 'Giới thiệu' },
    { value: 'dien-thoai', label: 'Điện thoại' },
    { value: 'email', label: 'Email' },
    { value: 'khac', label: 'Khác' },
];

export function OpportunityEditModal({ item, onClose }: Props) {
    const { mutate, isPending } = useUpdateOpportunity();
    const { data: products = [] } = useProductList();
    const { data: stages = [] } = useOpportunityStages();
    const { data: campaigns = [] } = useCampaignList();
    const { data: customers = [] } = useCustomerList();
    const { data: users = [] } = useActiveUsers();
    const [form, setForm] = useState<UpdateOpportunityPayload>({
        name: '', opportunityType: null, customerId: null, contactId: null, ownerId: null,
        stageId: null, campaignId: null, pricePolicyId: null, amount: null,
        source: null, winLossReason: null, description: null,
    });
    const [rows, setRows] = useState<LineItemRow[]>([]);
    const [originalRows, setOriginalRows] = useState<LineItemRow[]>([]);
    const [saving, setSaving] = useState(false);
    const { data: pricePolicies = [] } = useEligiblePricePolicies(form.customerId ?? undefined);

    const productOptions = useMemo<ProductOption[]>(
        () => products.map((p) => ({ value: String(p.id), label: `${p.sku} — ${p.name}`, unit: p.unit ?? '', price: p.basePrice ?? 0 })),
        [products],
    );
    const stageOptions = useMemo(() => stages.map((s) => ({ value: String(s.id), label: s.name })), [stages]);
    const campaignOptions = useMemo(() => campaigns.map((c) => ({ value: String(c.id), label: c.name })), [campaigns]);
    const customerOptions = useMemo(() => customers.map((c) => ({ value: String(c.id), label: c.name })), [customers]);
    const userOptions = useMemo(() => users.map((u) => ({ value: String(u.id), label: u.fullName })), [users]);
    const pricePolicyOptions = useMemo(() => pricePolicies.map((p) => ({ value: String(p.id), label: p.name })), [pricePolicies]);
    // giai đoạn đang chọn — nguồn của xác suất thắng; đổi giai đoạn thì số đổi ngay trước cả khi lưu
    const selectedStage = useMemo(() => stages.find((s) => s.id === form.stageId), [stages, form.stageId]);
    // giá trị cơ hội KHÔNG gõ tay — luôn suy từ tổng dòng hàng hiện tại (khớp cách BE tự tính lại)
    const amount = useMemo(() => computeTotals(rows).total, [rows]);

    useEffect(() => {
        if (!item) return;
        setForm({
            name: item.name, opportunityType: item.opportunityType, customerId: item.customerId,
            contactId: item.contactId, ownerId: item.ownerId, stageId: item.stageId, campaignId: item.campaignId,
            pricePolicyId: item.pricePolicyId, amount: item.amount, source: item.source,
            winLossReason: item.winLossReason, description: item.description,
        });
        opportunityService.getItems(item.id).then((r) => {
            const loaded = r.data.data.map(fromItemResult);
            setRows(loaded);
            setOriginalRows(loaded);
        });
    }, [item]);

    const { confirmSave } = useConfirm();
    const formRef = useRef<HTMLFormElement>(null);
    useFormKeyboardNav(formRef, {
        onSubmit: () => formRef.current?.requestSubmit(),
        onCancel: onClose,
        enabled: !!item,
    });

    if (!item) return null;

    // đổi khách hàng thì bỏ liên hệ cũ — liên hệ của khách khác gắn vào đây là dữ liệu sai
    const onPickCustomer = (v: string) => {
        setForm((f) => ({ ...f, customerId: v ? Number(v) : null, contactId: null }));
    };

    const handleSubmit = async (e: FormEvent) => {
        e.preventDefault();
        if (!(await confirmSave('cơ hội'))) return;
        setSaving(true);
        try {
            await mutate({ id: item.id, payload: { ...form, amount } });
            const { toCreate, toUpdate, toDelete } = diffLineItems(originalRows, rows);
            await Promise.all([
                ...toCreate.map((r) => opportunityService.createItem(item.id, toItemPayload(r))),
                ...toUpdate.map((r) => opportunityService.updateItem(item.id, r.backendId as number, toItemPayload(r))),
                ...toDelete.map((id) => opportunityService.deleteItem(item.id, id)),
            ]);
            // dòng hàng đổi -> BE roll-up lại amount cơ hội -> làm mới cả dòng hàng lẫn header
            notify(`opportunity-items:${item.id}`);
            notify('opportunities');
            notify(`opportunity:${item.id}`);
            onClose();
        } finally {
            setSaving(false);
        }
    };

    const inp = 'w-full border border-gray-300 rounded-btn px-3 py-1.5 text-md text-text-main focus:outline-none focus:border-primary';
    const lbl = 'block text-sm font-medium text-gray-700 mb-1';
    const busy = isPending || saving;

    return (
        <div className="fixed inset-0 z-[9999] flex items-center justify-center bg-black/40" onClick={onClose}>
            <div className="bg-white rounded-card shadow-lg w-full max-w-3xl mx-4 max-h-[90vh] overflow-y-auto" onClick={(e) => e.stopPropagation()}>
                <div className="flex items-center justify-between px-5 py-4 border-b border-gray-200">
                    <h2 className="text-lg font-semibold text-text-main">Chỉnh sửa cơ hội</h2>
                    <button onClick={onClose} className="p-1 rounded hover:bg-gray-100 text-gray-500"><FiX size={18} /></button>
                </div>
                <form ref={formRef} onSubmit={handleSubmit} noValidate className="px-5 py-4 space-y-3">
                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <label className={lbl}>Tên cơ hội <span className="text-danger">*</span></label>
                            <input className={inp} required value={form.name} onChange={e => setForm(f => ({ ...f, name: e.target.value }))} />
                        </div>
                        <div>
                            <label className={lbl}>Loại cơ hội</label>
                            <SearchableSelect options={OPPORTUNITY_TYPE_OPTIONS} value={form.opportunityType ?? ''}
                                onChange={v => setForm(f => ({ ...f, opportunityType: v || null }))}
                                fallbackLabel={form.opportunityType} />
                        </div>
                    </div>
                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <label className={lbl}>Khách hàng</label>
                            <SearchableSelect
                                value={form.customerId != null ? String(form.customerId) : ''}
                                onChange={onPickCustomer}
                                options={customerOptions}
                                fallbackLabel={item.customerName}
                            />
                        </div>
                        <div>
                            <label className={lbl}>Liên hệ</label>
                            <RecordPicker module="contact"
                                value={form.contactId != null ? String(form.contactId) : ''}
                                onChange={(v) => setForm(f => ({ ...f, contactId: v ? Number(v) : null }))}
                                customerId={form.customerId ?? undefined}
                                fallbackLabel={item.contactName}
                            />
                        </div>
                    </div>
                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <label className={lbl}>Người phụ trách</label>
                            <SearchableSelect
                                value={form.ownerId != null ? String(form.ownerId) : ''}
                                onChange={(v) => setForm(f => ({ ...f, ownerId: v ? Number(v) : null }))}
                                options={userOptions}
                                fallbackLabel={item.ownerName}
                            />
                        </div>
                        <div>
                            <label className={lbl}>Nguồn gốc</label>
                            <SearchableSelect
                                value={form.source ?? ''}
                                onChange={(v) => setForm(f => ({ ...f, source: v || null }))}
                                options={SOURCE_OPTIONS}
                            />
                        </div>
                    </div>
                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <label className={lbl}>Giai đoạn</label>
                            <SearchableSelect
                                value={form.stageId != null ? String(form.stageId) : ''}
                                onChange={(v) => setForm(f => ({ ...f, stageId: v ? Number(v) : null }))}
                                options={stageOptions}
                                fallbackLabel={item.stageName}
                            />
                        </div>
                        <div>
                            <label className={lbl}>Trạng thái (tự động theo giai đoạn)</label>
                            <span className={`inline-block px-2 py-1 rounded text-sm font-medium ${OPP_STATUS_COLORS[item.status] ?? 'bg-gray-100 text-gray-600'}`}>
                                {OPP_STATUS_LABELS[item.status] ?? item.status}
                            </span>
                        </div>
                    </div>
                    <div className="grid grid-cols-2 gap-3">
                        {/* xác suất do giai đoạn pipeline định nghĩa (opportunity_stages.probability) — chỉ hiển thị */}
                        <div>
                            <label className={lbl}>Xác suất (tự động theo giai đoạn)</label>
                            <span className="inline-block px-2 py-1 rounded text-sm font-medium bg-blue-50 text-primary">
                                {selectedStage ? `${formatNumber(selectedStage.probability)}%` : '—'}
                            </span>
                        </div>
                        <div>
                            <label className={lbl}>Chiến dịch</label>
                            <SearchableSelect
                                value={form.campaignId != null ? String(form.campaignId) : ''}
                                onChange={(v) => setForm(f => ({ ...f, campaignId: v ? Number(v) : null }))}
                                options={campaignOptions}
                                fallbackLabel={item.campaignName}
                            />
                        </div>
                        <div>
                            <label className={lbl}>Chính sách giá</label>
                            <SearchableSelect
                                value={form.pricePolicyId != null ? String(form.pricePolicyId) : ''}
                                onChange={(v) => setForm(f => ({ ...f, pricePolicyId: v ? Number(v) : null }))}
                                options={pricePolicyOptions}
                            />
                        </div>
                    </div>
                    <div>
                        {/* giá trị KHÔNG gõ tay — luôn suy từ tổng dòng hàng bên dưới */}
                        <label className={lbl}>Giá trị (đ)</label>
                        <div className="flex items-center gap-2">
                            <span className="text-md font-medium text-text-main">{formatNumber(amount)}</span>
                            <span className="text-sm text-gray-400">(tự tính theo dòng hàng)</span>
                        </div>
                    </div>
                    <div>
                        <label className={lbl}>Hàng hóa</label>
                        <ProductLineItemsTable rows={rows} onChange={setRows} productOptions={productOptions} pricePolicyId={form.pricePolicyId} customerId={form.customerId} />
                    </div>
                    <div>
                        <label className={lbl}>Mô tả</label>
                        <textarea className={inp} rows={2} value={form.description ?? ''} onChange={e => setForm(f => ({ ...f, description: e.target.value || null }))} />
                    </div>
                    <div>
                        <label className={lbl}>Lý do thắng/thua</label>
                        <input className={inp} value={form.winLossReason ?? ''} onChange={e => setForm(f => ({ ...f, winLossReason: e.target.value || null }))} />
                    </div>
                    <ModalFooter onCancel={onClose} saving={busy} />
                </form>
            </div>
        </div>
    );
}
