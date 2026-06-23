import { useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { SearchableSelect } from '@/shared/components/SearchableSelect';
import { FormPageHeader } from '@/shared/components/form/FormPageHeader';
import { FormSection } from '@/shared/components/form/FormSection';
import { FieldRow } from '@/shared/components/form/FieldRow';
import { inputCls } from '@/shared/components/form/formStyles';
import { ProductLineItemsTable } from '@/shared/components/form/ProductLineItemsTable';
import {
    type LineItemRow,
    type ProductOption,
    emptyLineItem,
    computeTotals,
    toItemPayloads,
} from '@/shared/components/form/productLineItem';
import { useAlert } from '@/shared/alert/useAlert';
import { useActiveUsers } from '@/features/users/hooks/useActiveUsers';
import { useOrgUnits } from '@/features/users/hooks/useOrgUnits';
import { useCustomerList } from '@/features/khach-hang/hooks/useCustomerList';
import { useContactList } from '@/features/lien-he/hooks/useContactList';
import { useProductList } from '@/features/san-pham/hooks/useProductList';
import { useCreateOrder } from '../hooks/useCreateOrder';
import type { CreateOrderPayload } from '../types/orderTypes';

const ORDER_TYPE_OPTIONS = [
    { value: 'standard', label: 'Tiêu chuẩn' },
    { value: 'parent', label: 'Đơn cha' },
    { value: 'child', label: 'Đơn con' },
];
const STATUS_OPTIONS = [
    { value: 'draft', label: 'Nháp' },
    { value: 'confirmed', label: 'Đã xác nhận' },
    { value: 'processing', label: 'Đang xử lý' },
    { value: 'completed', label: 'Hoàn thành' },
    { value: 'cancelled', label: 'Đã hủy' },
];
const PAYMENT_STATUS_OPTIONS = [
    { value: 'unpaid', label: 'Chưa thanh toán' },
    { value: 'partial', label: 'Thanh toán một phần' },
    { value: 'paid', label: 'Đã thanh toán' },
];

interface HeaderState {
    code: string; orderDate: string; orderType: string; status: string;
    customerId: string; contactId: string; ownerId: string; executorUnitId: string;
    currency: string; exchangeRate: string; paymentStatus: string;
    creditDays: string; paymentDueDate: string; isInvoiced: boolean;
    receiverName: string; receiverPhone: string; note: string;
}

const INITIAL: HeaderState = {
    code: '', orderDate: '', orderType: 'standard', status: 'draft', customerId: '', contactId: '',
    ownerId: '', executorUnitId: '', currency: 'VND', exchangeRate: '1',
    paymentStatus: 'unpaid', creditDays: '', paymentDueDate: '', isInvoiced: false,
    receiverName: '', receiverPhone: '', note: '',
};

/** Trang thêm đơn hàng mới — header + bảng hàng hóa (layout AMIS). */
const OrderAddPage = () => {
    const navigate = useNavigate();
    const { showAlert } = useAlert();
    const [form, setForm] = useState<HeaderState>(INITIAL);
    const [rows, setRows] = useState<LineItemRow[]>([emptyLineItem()]);
    const { mutate, isPending } = useCreateOrder();

    const { data: users = [] } = useActiveUsers();
    const { data: units = [] } = useOrgUnits();
    const { data: customers = [] } = useCustomerList();
    const { data: contacts = [] } = useContactList();
    const { data: products = [] } = useProductList();

    const userOptions = useMemo(() => users.map((u) => ({ value: String(u.id), label: u.fullName })), [users]);
    const unitOptions = useMemo(() => units.map((u) => ({ value: String(u.id), label: u.name })), [units]);
    const customerOptions = useMemo(() => customers.map((c) => ({ value: String(c.id), label: c.name })), [customers]);
    const contactOptions = useMemo(() => contacts.map((c) => ({ value: String(c.id), label: c.fullName })), [contacts]);
    const productOptions = useMemo<ProductOption[]>(
        () => products.map((p) => ({ value: String(p.id), label: `${p.sku} — ${p.name}`, unit: p.unit ?? '', price: p.basePrice ?? 0, vatRate: p.vatRate ?? 0 })),
        [products],
    );

    const set = (patch: Partial<HeaderState>) => setForm((p) => ({ ...p, ...patch }));
    const reset = () => { setForm(INITIAL); setRows([emptyLineItem()]); };
    const num = (s: string): number | null => (s.trim() ? Number(s) : null);

    const submit = (andNew: boolean) => {
        if (!form.code.trim()) { showAlert('Mã đơn hàng không được để trống'); return; }
        const totals = computeTotals(rows);
        const payload: CreateOrderPayload = {
            code: form.code.trim(),
            customerId: form.customerId ? Number(form.customerId) : null,
            contactId: form.contactId ? Number(form.contactId) : null,
            quotationId: null,
            opportunityId: null,
            ownerId: form.ownerId ? Number(form.ownerId) : null,
            executorUnitId: form.executorUnitId ? Number(form.executorUnitId) : null,
            parentOrderId: null,
            orderType: form.orderType,
            orderDate: form.orderDate || null,
            currency: form.currency || 'VND',
            exchangeRate: Number(form.exchangeRate) || 1,
            status: form.status,
            paymentStatus: form.paymentStatus,
            creditDays: num(form.creditDays),
            paymentDueDate: form.paymentDueDate || null,
            isInvoiced: form.isInvoiced,
            receiverName: form.receiverName || null,
            receiverPhone: form.receiverPhone || null,
            subtotal: totals.subtotal,
            discount: totals.discount,
            tax: totals.tax,
            total: totals.total,
            note: form.note || null,
            items: toItemPayloads(rows),
        };
        mutate(payload, {
            onSuccess: () => {
                if (andNew) { reset(); showAlert('Đã lưu đơn hàng thành công'); }
                else navigate('/don-hang');
            },
            onError: (err: unknown) => {
                const msg = (err as { response?: { data?: { message?: string } } })?.response?.data?.message
                    ?? 'Có lỗi xảy ra khi lưu đơn hàng';
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
                            <FieldRow label="Mã đơn hàng" required>
                                <input type="text" value={form.code} onChange={(e) => set({ code: e.target.value })} className={inputCls} />
                            </FieldRow>
                            <FieldRow label="Ngày đặt hàng">
                                <input type="date" value={form.orderDate} onChange={(e) => set({ orderDate: e.target.value })} className={inputCls} />
                            </FieldRow>
                            <FieldRow label="Loại đơn">
                                <SearchableSelect value={form.orderType} onChange={(v) => set({ orderType: v })} options={ORDER_TYPE_OPTIONS} />
                            </FieldRow>
                            <FieldRow label="Khách hàng">
                                <SearchableSelect value={form.customerId} onChange={(v) => set({ customerId: v })} options={customerOptions} />
                            </FieldRow>
                            <FieldRow label="Liên hệ">
                                <SearchableSelect value={form.contactId} onChange={(v) => set({ contactId: v })} options={contactOptions} />
                            </FieldRow>
                        </div>
                        <div className="space-y-4">
                            <FieldRow label="Trạng thái">
                                <SearchableSelect value={form.status} onChange={(v) => set({ status: v })} options={STATUS_OPTIONS} />
                            </FieldRow>
                            <FieldRow label="Người phụ trách">
                                <SearchableSelect value={form.ownerId} onChange={(v) => set({ ownerId: v })} options={userOptions} />
                            </FieldRow>
                            <FieldRow label="Đơn vị thực hiện">
                                <SearchableSelect value={form.executorUnitId} onChange={(v) => set({ executorUnitId: v })} options={unitOptions} />
                            </FieldRow>
                        </div>
                    </div>
                </FormSection>

                <FormSection title="Thanh toán">
                    <div className="grid grid-cols-2 gap-x-10 gap-y-4">
                        <div className="space-y-4">
                            <FieldRow label="Tiền tệ">
                                <input type="text" value={form.currency} onChange={(e) => set({ currency: e.target.value })} className={inputCls} />
                            </FieldRow>
                            <FieldRow label="Trạng thái TT">
                                <SearchableSelect value={form.paymentStatus} onChange={(v) => set({ paymentStatus: v })} options={PAYMENT_STATUS_OPTIONS} />
                            </FieldRow>
                            <FieldRow label="Số ngày được nợ">
                                <input type="number" value={form.creditDays} onChange={(e) => set({ creditDays: e.target.value })} className={inputCls} />
                            </FieldRow>
                        </div>
                        <div className="space-y-4">
                            <FieldRow label="Tỷ giá">
                                <input type="number" value={form.exchangeRate} onChange={(e) => set({ exchangeRate: e.target.value })} className={inputCls} />
                            </FieldRow>
                            <FieldRow label="Hạn thanh toán">
                                <input type="date" value={form.paymentDueDate} onChange={(e) => set({ paymentDueDate: e.target.value })} className={inputCls} />
                            </FieldRow>
                            <label className="flex items-center gap-2 cursor-pointer w-fit ml-[160px]">
                                <input type="checkbox" checked={form.isInvoiced} onChange={(e) => set({ isInvoiced: e.target.checked })} className="w-4 h-4 accent-primary" />
                                <span className="text-md text-text-main">Đã xuất hóa đơn</span>
                            </label>
                        </div>
                    </div>
                </FormSection>

                <FormSection title="Giao hàng">
                    <div className="grid grid-cols-2 gap-x-10 gap-y-4">
                        <FieldRow label="Người nhận">
                            <input type="text" value={form.receiverName} onChange={(e) => set({ receiverName: e.target.value })} className={inputCls} />
                        </FieldRow>
                        <FieldRow label="ĐT người nhận">
                            <input type="text" value={form.receiverPhone} onChange={(e) => set({ receiverPhone: e.target.value })} className={inputCls} />
                        </FieldRow>
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
