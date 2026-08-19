import { useRef, useState, type FormEvent, useEffect, useMemo } from 'react';
import { collectErrors, dateRangeError } from '@/shared/utils/validators';
import { FieldError } from '@/shared/components/form/FormField';
import { ModalFooter } from '@/shared/components/ModalFooter';
import { useConfirm } from '@/shared/confirm/useConfirm';
import { useFormKeyboardNav } from '@/shared/keyboard/useFormKeyboardNav';
import { notify } from '@/core/data/dataBus';
import { FiX, FiRefreshCw } from 'react-icons/fi';
import type { QuotationResult, UpdateQuotationPayload } from '../types/quotationTypes';
import { useUpdateQuotation } from '../hooks/useUpdateQuotation';
import { quotationService } from '../services/quotationService';
import { useAlert } from '@/shared/alert/useAlert';
import { useProductList } from '@/features/san-pham/hooks/useProductList';
import { useCustomerList } from '@/features/khach-hang/hooks/useCustomerList';
import { useActiveUsers } from '@/features/users/hooks/useActiveUsers';
import { useEligiblePricePolicies } from '@/features/chinh-sach-gia/hooks/useEligiblePricePolicies';
import { SearchableSelect } from '@/shared/components/SearchableSelect';
import { RecordPicker } from '@/shared/components/form/RecordPicker';
import { ProductLineItemsTable } from '@/shared/components/form/ProductLineItemsTable';
import { DateInput } from '@/shared/components/form/DateInput';
import { PrefillHint } from '@/shared/components/form/PrefillHint';
import { fillEmpty, hasFilled } from '@/shared/utils/prefill';
import { opportunityService } from '@/features/co-hoi/services/opportunityService';
import {
    type LineItemRow,
    type ProductOption,
    fromItemResult,
    diffLineItems,
    toItemPayload, validateLineItems } from '@/shared/components/form/productLineItem';

interface Props {
    item: QuotationResult | null;
    onClose: () => void;
}

const QUOTATION_STATUS_LABELS: Record<string, string> = {
    draft: 'Nháp', pending: 'Chờ duyệt', approved: 'Đã duyệt', rejected: 'Từ chối', sent: 'Đã gửi', expired: 'Hết hạn',
};
const QUOTATION_STATUS_COLORS: Record<string, string> = {
    draft: 'bg-gray-100 text-gray-600', pending: 'bg-yellow-100 text-yellow-700', approved: 'bg-green-100 text-green-700',
    rejected: 'bg-red-100 text-red-600', sent: 'bg-blue-100 text-blue-700', expired: 'bg-orange-100 text-orange-700',
};

export function QuotationEditModal({ item, onClose }: Props) {
    const { showAlert } = useAlert();
    const { mutate, isPending } = useUpdateQuotation();
    const { data: products = [] } = useProductList();
    const { data: customers = [] } = useCustomerList();
    const { data: users = [] } = useActiveUsers();
    const [pulling, setPulling] = useState(false);
    const [form, setForm] = useState<UpdateQuotationPayload>({
        customerId: null, contactId: null, pricePolicyId: null, ownerId: null, quoteDate: null,
        validUntil: null, note: null,
    });
    const [rows, setRows] = useState<LineItemRow[]>([]);
    const [originalRows, setOriginalRows] = useState<LineItemRow[]>([]);
    const [saving, setSaving] = useState(false);
    const { data: pricePolicies = [] } = useEligiblePricePolicies(form.customerId ?? undefined);

    const productOptions = useMemo<ProductOption[]>(
        () => products.map((p) => ({ value: String(p.id), label: `${p.sku} — ${p.name}`, unit: p.unit ?? '', price: p.basePrice ?? 0, vatRate: p.vatRate ?? 0 })),
        [products],
    );
    const pricePolicyOptions = useMemo(() => pricePolicies.map((p) => ({ value: String(p.id), label: p.name })), [pricePolicies]);
    const customerOptions = useMemo(() => customers.map((c) => ({ value: String(c.id), label: c.name })), [customers]);
    const userOptions = useMemo(() => users.map((u) => ({ value: String(u.id), label: u.fullName })), [users]);

    useEffect(() => {
        if (!item) return;
        setForm({
            customerId: item.customerId, contactId: item.contactId, opportunityId: item.opportunityId,
            pricePolicyId: item.pricePolicyId,
            ownerId: item.ownerId, quoteDate: item.quoteDate, validUntil: item.validUntil,
            note: item.note,
        });
        quotationService.getItems(item.id).then((r) => {
            const loaded = r.data.data.map(fromItemResult);
            setRows(loaded);
            setOriginalRows(loaded);
        });
    }, [item]);

    const [errors, setErrors] = useState<Record<string, string>>({});
    // xóa lỗi của một ô ngay khi người dùng gõ lại
    const clearError = (key: string) =>
        setErrors((prev) => (prev[key] ? { ...prev, [key]: '' } : prev));

    // tên bản ghi vừa kéo dữ liệu về — hiện dòng gợi ý dưới ô Cơ hội
    const [prefillFrom, setPrefillFrom] = useState<string | null>(null);

    // đổi cơ hội -> tự điền chính sách giá còn trống (không đè giá trị đã có)
    const onPickOpportunity = async (v: string) => {
        setForm(f => ({ ...f, opportunityId: v ? Number(v) : null }));
        setPrefillFrom(null);
        if (!v) return;
        const o = (await opportunityService.getById(Number(v))).data.data;
        const patch = fillEmpty({ ...form, opportunityId: Number(v) }, {
            pricePolicyId: o.pricePolicyId ?? null,
        });
        if (hasFilled(patch)) { setForm(f => ({ ...f, ...patch })); setPrefillFrom(`cơ hội «${o.code}»`); }
    };

    const { confirmSave, confirm } = useConfirm();
    const [creatingRevision, setCreatingRevision] = useState(false);
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

    // nhân viên bấm "Tạo báo giá mới theo yêu cầu khách" — sinh báo giá mới từ đề xuất, khóa báo giá này
    const handleCreateRevision = async () => {
        const ok = await confirm({
            message: 'Tạo báo giá mới theo đề xuất của khách? Báo giá hiện tại sẽ chuyển sang trạng thái khóa.',
            confirmLabel: 'Tạo báo giá mới',
        });
        if (!ok) return;
        setCreatingRevision(true);
        try {
            const res = await quotationService.createRevision(item.id);
            notify('quotations');
            showAlert(`Đã tạo báo giá mới: ${res.data.data.code}`);
            onClose();
        } catch (err) {
            const msg = (err as { response?: { data?: { message?: string } } })?.response?.data?.message
                ?? 'Không tạo được báo giá mới';
            showAlert(msg);
        } finally {
            setCreatingRevision(false);
        }
    };

    // lưu form + diff dòng hàng — lỗi hiện đỏ dưới ô, popup xác nhận chỉ mở khi dữ liệu đã hợp lệ
    const handleSubmit = async (e: FormEvent) => {
        e.preventDefault();
        // bước kiểm tra dữ liệu
        const errs = collectErrors({
            validUntil: dateRangeError(form.quoteDate, form.validUntil, 'ngày báo giá', 'Ngày hiệu lực'),
            items: validateLineItems(rows),
        });
        setErrors(errs);
        if (Object.keys(errs).length > 0) return;

        // bước hỏi xác nhận rồi lưu header + diff dòng hàng
        if (!(await confirmSave('báo giá'))) return;
        setSaving(true);
        try {
            await mutate({ id: item.id, payload: form });
            const { toCreate, toUpdate, toDelete } = diffLineItems(originalRows, rows);
            await Promise.all([
                ...toCreate.map((r) => quotationService.createItem(item.id, toItemPayload(r))),
                ...toUpdate.map((r) => quotationService.updateItem(item.id, r.backendId as number, toItemPayload(r))),
                ...toDelete.map((id) => quotationService.deleteItem(item.id, id)),
            ]);
            // sửa dòng hàng báo giá primary đồng bộ ngược về cơ hội (amount roll-up) -> làm mới cơ hội + báo giá
            notify('quotations');
            notify('opportunities');
            notify(`quotation:${item.id}`);
            notify(`quotation-items:${item.id}`);
            if (item.opportunityId != null) notify(`opportunity:${item.opportunityId}`);
            onClose();
        } finally {
            setSaving(false);
        }
    };

    // cập nhật lại danh sách dòng hàng theo cơ hội nguồn (áp dụng ngay ở backend, giữ liên kết)
    const handleSyncFromOpportunity = async () => {
        if (!item) return;
        setPulling(true);
        try {
            await quotationService.syncItemsFromOpportunity(item.id);
            const r = await quotationService.getItems(item.id);
            const loaded = r.data.data.map(fromItemResult);
            setRows(loaded);
            setOriginalRows(loaded);
            notify('quotations');
            notify('opportunities');
            notify(`quotation:${item.id}`);
            notify(`quotation-items:${item.id}`);
            if (item.opportunityId != null) notify(`opportunity:${item.opportunityId}`);
            showAlert('Đã cập nhật dòng hàng từ cơ hội');
        } catch (err) {
            const msg = (err as { response?: { data?: { message?: string } } })?.response?.data?.message
                ?? 'Không cập nhật được dòng hàng từ cơ hội';
            showAlert(msg);
        } finally {
            setPulling(false);
        }
    };

    const inp = 'w-full border border-gray-300 rounded-btn px-3 py-1.5 text-md text-text-main focus:outline-none focus:border-primary';
    const lbl = 'block text-sm font-medium text-gray-700 mb-1';
    const busy = isPending || saving;

    return (
        <div className="fixed inset-0 z-[9999] flex items-center justify-center bg-black/40" onClick={onClose}>
            <div className="bg-white rounded-card shadow-lg w-full max-w-3xl mx-4 max-h-[90vh] overflow-y-auto" onClick={(e) => e.stopPropagation()}>
                <div className="flex items-center justify-between px-5 py-4 border-b border-gray-200">
                    <h2 className="text-lg font-semibold text-text-main">Chỉnh sửa báo giá</h2>
                    <button onClick={onClose} className="p-1 rounded hover:bg-gray-100 text-gray-500"><FiX size={18} /></button>
                </div>
                <form ref={formRef} onSubmit={handleSubmit} noValidate className="px-5 py-4 space-y-3">
                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <label className={lbl}>Ngày báo giá</label>
                            <DateInput value={form.quoteDate ?? ''} onChange={v => setForm(f => ({ ...f, quoteDate: v || null }))} />
                        </div>
                        <div>
                            <label className={lbl}>Hiệu lực đến</label>
                            <FieldError error={errors.validUntil}>
                                <DateInput value={form.validUntil ?? ''} onChange={v => { setForm(f => ({ ...f, validUntil: v || null })); clearError('validUntil'); }} />
                            </FieldError>
                        </div>
                    </div>
                    <div>
                        <label className={lbl}>Trạng thái (đổi qua hành động)</label>
                        <span className={`inline-block px-2 py-1.5 rounded text-sm font-medium ${QUOTATION_STATUS_COLORS[item.status] ?? 'bg-gray-100 text-gray-600'}`}>
                            {QUOTATION_STATUS_LABELS[item.status] ?? item.status}
                        </span>
                    </div>
                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <label className={lbl}>Cơ hội</label>
                            <RecordPicker module="opportunity"
                                value={form.opportunityId != null ? String(form.opportunityId) : ''}
                                onChange={onPickOpportunity}
                                fallbackLabel={item.opportunityName} />
                            <PrefillHint source={prefillFrom} />
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
                        <div className="flex items-center justify-between mb-1">
                            <label className={lbl + ' mb-0'}>Hàng hóa</label>
                            {item.opportunityId != null && (
                                <button
                                    type="button"
                                    onClick={handleSyncFromOpportunity}
                                    disabled={pulling || busy}
                                    className="flex items-center gap-1.5 px-2.5 py-1 rounded-btn border border-gray-300 text-sm text-gray-600 hover:bg-gray-50 disabled:opacity-50"
                                    title="Lấy lại toàn bộ dòng hàng từ cơ hội nguồn"
                                >
                                    <FiRefreshCw size={13} className={pulling ? 'animate-spin' : ''} />
                                    {pulling ? 'Đang cập nhật...' : 'Cập nhật dòng hàng từ cơ hội'}
                                </button>
                            )}
                        </div>
                        <ProductLineItemsTable rows={rows} onChange={setRows} productOptions={productOptions} showUnit showTax
                            pricePolicyId={form.pricePolicyId} customerId={form.customerId} />
                        {errors.items && <p className="text-xs text-danger mt-1">{errors.items}</p>}
                    </div>
                    {item.customerResponse && (
                        <div className="rounded-btn border border-gray-200 bg-gray-50 p-3">
                            <div className="text-sm font-medium text-gray-700 mb-1">Phản hồi khách hàng</div>
                            <span className={`inline-block px-2 py-0.5 rounded text-sm font-medium ${
                                item.customerResponse === 'accepted' ? 'bg-green-100 text-green-700'
                                    : item.customerResponse === 'adjust' ? 'bg-yellow-100 text-yellow-700'
                                        : 'bg-red-100 text-red-600'
                            }`}>
                                {item.customerResponse === 'accepted' ? 'Đồng ý'
                                    : item.customerResponse === 'adjust' ? 'Đề nghị chỉnh sửa dòng hàng' : 'Không đồng ý'}
                            </span>
                            {/* customerResponseNote khi adjust là JSON đề xuất (id/số lượng), không phải văn bản — không hiện thô */}
                            {item.customerResponseNote && item.customerResponse !== 'adjust' && (
                                <div className="text-md text-gray-600 mt-1">Nội dung: {item.customerResponseNote}</div>
                            )}
                            {item.customerResponse === 'adjust' && !item.isLocked && (
                                <button type="button" onClick={handleCreateRevision} disabled={creatingRevision}
                                    className="mt-2 px-3 py-1.5 rounded-btn bg-primary text-white text-sm font-medium hover:opacity-90 disabled:opacity-50">
                                    {creatingRevision ? 'Đang tạo...' : 'Tạo báo giá mới theo yêu cầu khách'}
                                </button>
                            )}
                        </div>
                    )}
                    <div>
                        <label className={lbl}>Ghi chú</label>
                        <textarea className={inp} rows={2} value={form.note ?? ''} onChange={e => setForm(f => ({ ...f, note: e.target.value || null }))} />
                    </div>
                    <ModalFooter onCancel={onClose} saving={busy} />
                </form>
            </div>
        </div>
    );
}
