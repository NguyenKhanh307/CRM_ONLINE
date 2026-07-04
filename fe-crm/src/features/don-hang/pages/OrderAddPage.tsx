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
    computeTotals,
    toItemPayloads,
} from '@/shared/components/form/productLineItem';
import { useAlert } from '@/shared/alert/useAlert';
import { useAuth } from '@/core/auth/useAuth';
import { useActiveUsers } from '@/features/users/hooks/useActiveUsers';
import { useCustomerList } from '@/features/khach-hang/hooks/useCustomerList';
import { useContactList } from '@/features/lien-he/hooks/useContactList';
import { useProductList } from '@/features/san-pham/hooks/useProductList';
import { useCampaignList } from '@/features/chien-dich/hooks/useCampaignList';
import { useCreateOrder } from '../hooks/useCreateOrder';
import type { CreateOrderPayload } from '../types/orderTypes';

interface HeaderState {
    code: string; orderDate: string; deliveryDate: string;
    customerId: string; contactId: string; campaignId: string; ownerId: string;
    currency: string; exchangeRate: string;
    billingAddress: string; taxCode: string; note: string;
}

/** State khởi tạo — người phụ trách mặc định là user đang đăng nhập. */
const initialState = (ownerId: string): HeaderState => ({
    code: '', orderDate: '', deliveryDate: '', customerId: '', contactId: '', campaignId: '',
    ownerId, currency: 'VND', exchangeRate: '1',
    billingAddress: '', taxCode: '', note: '',
});

/** Trang thêm Đơn hàng mới — header + bảng hàng hóa (layout AMIS). */
const OrderAddPage = () => {
    const navigate = useNavigate();
    const { showAlert } = useAlert();
    const { user } = useAuth();
    const defaultOwnerId = user ? String(user.id) : '';
    const [form, setForm] = useState<HeaderState>(() => initialState(defaultOwnerId));
    const [rows, setRows] = useState<LineItemRow[]>([emptyLineItem()]);
    const { mutate, isPending } = useCreateOrder();

    const { data: users = [] } = useActiveUsers();
    const { data: customers = [] } = useCustomerList();
    const { data: contacts = [] } = useContactList();
    const { data: products = [] } = useProductList();
    const { data: campaigns = [] } = useCampaignList();

    const userOptions = useMemo(() => users.map((u) => ({ value: String(u.id), label: u.fullName })), [users]);
    const customerOptions = useMemo(() => customers.map((c) => ({ value: String(c.id), label: c.name })), [customers]);
    const contactOptions = useMemo(() => contacts.map((c) => ({ value: String(c.id), label: c.fullName })), [contacts]);
    const campaignOptions = useMemo(() => campaigns.map((c) => ({ value: String(c.id), label: c.name })), [campaigns]);
    const productOptions = useMemo<ProductOption[]>(
        () => products.map((p) => ({ value: String(p.id), label: `${p.sku} — ${p.name}`, unit: p.unit ?? '', price: p.basePrice ?? 0, vatRate: p.vatRate ?? 0 })),
        [products],
    );

    const set = (patch: Partial<HeaderState>) => setForm((p) => ({ ...p, ...patch }));
    const reset = () => { setForm(initialState(defaultOwnerId)); setRows([emptyLineItem()]); };

    const submit = (andNew: boolean) => {
        if (!form.code.trim()) { showAlert('Mã Đơn hàng không được để trống'); return; }
        const totals = computeTotals(rows);
        const payload: CreateOrderPayload = {
            code: form.code.trim(),
            customerId: form.customerId ? Number(form.customerId) : null,
            contactId: form.contactId ? Number(form.contactId) : null,
            quotationId: null,
            opportunityId: null,
            campaignId: form.campaignId ? Number(form.campaignId) : null,
            ownerId: form.ownerId ? Number(form.ownerId) : null,
            orderDate: form.orderDate || null,
            deliveryDate: form.deliveryDate || null,
            currency: form.currency || 'VND',
            exchangeRate: Number(form.exchangeRate) || 1,
            billingAddress: form.billingAddress || null,
            taxCode: form.taxCode || null,
            subtotal: totals.subtotal,
            discount: totals.discount,
            tax: totals.tax,
            total: totals.total,
            note: form.note || null,
            items: toItemPayloads(rows),
        };
        mutate(payload, {
            onSuccess: () => {
                if (andNew) { reset(); showAlert('Đã lưu Đơn hàng thành công'); }
                else navigate('/don-hang');
            },
            onError: (err: unknown) => {
                const msg = (err as { response?: { data?: { message?: string } } })?.response?.data?.message
                    ?? 'Có lỗi xảy ra khi lưu Đơn hàng';
                showAlert(msg);
            },
        });
    };

    return (
        <div className="p-6 bg-bg-main min-h-[calc(100vh-50px)]">
            <FormPageHeader title="Thêm Đơn hàng" saving={isPending}
                onCancel={() => navigate(-1)} onSave={() => submit(false)} onSaveAndNew={() => submit(true)} />

            <div className="bg-white rounded-card shadow-sm p-6 space-y-8">
                <FormSection title="Thông tin chung">
                    <div className="grid grid-cols-2 gap-x-10 gap-y-4">
                        <div className="space-y-4">
                            <FieldRow label="Mã Đơn hàng" required>
                                <input type="text" value={form.code} onChange={(e) => set({ code: e.target.value })} className={inputCls} />
                            </FieldRow>
                            <FieldRow label="Ngày đơn hàng">
                                <DateInput value={form.orderDate} onChange={(v) => set({ orderDate: v })} />
                            </FieldRow>
                            <FieldRow label="Ngày giao dự kiến">
                                <DateInput value={form.deliveryDate} onChange={(v) => set({ deliveryDate: v })} />
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
                            <FieldRow label="Chiến dịch">
                                <SearchableSelect value={form.campaignId} onChange={(v) => set({ campaignId: v })} options={campaignOptions} />
                            </FieldRow>
                            <FieldRow label="Tiền tệ">
                                <input type="text" value={form.currency} onChange={(e) => set({ currency: e.target.value })} className={inputCls} />
                            </FieldRow>
                            <FieldRow label="Mã số thuế">
                                <input type="text" value={form.taxCode} onChange={(e) => set({ taxCode: e.target.value })} className={inputCls} />
                            </FieldRow>
                            <FieldRow label="Địa chỉ xuất HĐ">
                                <input type="text" value={form.billingAddress} onChange={(e) => set({ billingAddress: e.target.value })} className={inputCls} />
                            </FieldRow>
                        </div>
                    </div>
                </FormSection>

                <FormSection title="Hàng hóa">
                    <ProductLineItemsTable rows={rows} onChange={setRows} productOptions={productOptions} showUnit showTax />
                </FormSection>

                <FormSection title="Thông tin mô tả">
                    <FieldRow label="Ghi chú" alignTop>
                        <textarea rows={3} value={form.note} onChange={(e) => set({ note: e.target.value })} className={`${inputCls} resize-none`} />
                    </FieldRow>
                </FormSection>
            </div>
        </div>
    );
};

export default OrderAddPage;
