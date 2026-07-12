import { useRef, useState, type FormEvent, useEffect, useMemo } from 'react';
import { dateRangeError } from '@/shared/utils/validators';
import { ModalFooter } from '@/shared/components/ModalFooter';
import { useConfirm } from '@/shared/confirm/useConfirm';
import { useFormKeyboardNav } from '@/shared/keyboard/useFormKeyboardNav';
import { useQueryClient } from '@tanstack/react-query';
import { FiX, FiRefreshCw } from 'react-icons/fi';
import type { QuotationResult, UpdateQuotationPayload } from '../types/quotationTypes';
import { useUpdateQuotation } from '../hooks/useUpdateQuotation';
import { quotationService } from '../services/quotationService';
import { useAlert } from '@/shared/alert/useAlert';
import { useProductList } from '@/features/san-pham/hooks/useProductList';
import { ProductLineItemsTable } from '@/shared/components/form/ProductLineItemsTable';
import { DateInput } from '@/shared/components/form/DateInput';
import {
    type LineItemRow,
    type ProductOption,
    fromItemResult,
    diffLineItems,
    computeTotals,
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
    const qc = useQueryClient();
    const { showAlert } = useAlert();
    const { mutateAsync, isPending } = useUpdateQuotation();
    const { data: products = [] } = useProductList();
    const [pulling, setPulling] = useState(false);
    const [form, setForm] = useState<UpdateQuotationPayload>({
        customerId: null, contactId: null, ownerId: null, quoteDate: null,
        validUntil: null, currency: 'VND', exchangeRate: 1,
        subtotal: null, discount: null, tax: null, total: null, note: null,
    });
    const [rows, setRows] = useState<LineItemRow[]>([]);
    const [originalRows, setOriginalRows] = useState<LineItemRow[]>([]);
    const [saving, setSaving] = useState(false);

    const productOptions = useMemo<ProductOption[]>(
        () => products.map((p) => ({ value: String(p.id), label: `${p.sku} — ${p.name}`, unit: p.unit ?? '', price: p.basePrice ?? 0, vatRate: p.vatRate ?? 0 })),
        [products],
    );

    useEffect(() => {
        if (!item) return;
        setForm({
            customerId: item.customerId, contactId: item.contactId, opportunityId: item.opportunityId,
            ownerId: item.ownerId, quoteDate: item.quoteDate, validUntil: item.validUntil,
            currency: item.currency, exchangeRate: item.exchangeRate,
            subtotal: item.subtotal, discount: item.discount, tax: item.tax, total: item.total, note: item.note,
        });
        quotationService.getItems(item.id).then((r) => {
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

    const handleSubmit = async (e: FormEvent) => {
        e.preventDefault();
        // Kiểm tra biên (khớp ràng buộc backend) — chặn submit nếu dữ liệu không hợp lệ
        const vErr = dateRangeError(form.quoteDate, form.validUntil, 'ngày báo giá', 'Ngày hiệu lực') ?? validateLineItems(rows);
        if (vErr) { showAlert(vErr); return; }
        if (!(await confirmSave('báo giá'))) return;
        setSaving(true);
        try {
            const totals = computeTotals(rows);
            await mutateAsync({
                id: item.id,
                payload: { ...form, subtotal: totals.subtotal, discount: totals.discount, tax: totals.tax, total: totals.total },
            });
            const { toCreate, toUpdate, toDelete } = diffLineItems(originalRows, rows);
            await Promise.all([
                ...toCreate.map((r) => quotationService.createItem(item.id, toItemPayload(r))),
                ...toUpdate.map((r) => quotationService.updateItem(item.id, r.backendId as number, toItemPayload(r))),
                ...toDelete.map((id) => quotationService.deleteItem(item.id, id)),
            ]);
            // Sửa dòng hàng báo giá primary đồng bộ ngược về cơ hội (amount roll-up) → làm mới cơ hội + báo giá
            ['quotations', 'opportunities'].forEach((key) => qc.invalidateQueries({ queryKey: [key] }));
            onClose();
        } finally {
            setSaving(false);
        }
    };

    /** Cập nhật lại danh sách dòng hàng theo cơ hội nguồn (áp dụng ngay ở backend, giữ liên kết). */
    const handleSyncFromOpportunity = async () => {
        if (!item) return;
        setPulling(true);
        try {
            await quotationService.syncItemsFromOpportunity(item.id);
            const r = await quotationService.getItems(item.id);
            const loaded = r.data.data.map(fromItemResult);
            setRows(loaded);
            setOriginalRows(loaded);
            ['quotations', 'opportunities'].forEach((key) => qc.invalidateQueries({ queryKey: [key] }));
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
                <form ref={formRef} onSubmit={handleSubmit} className="px-5 py-4 space-y-3">
                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <label className={lbl}>Ngày báo giá</label>
                            <DateInput value={form.quoteDate ?? ''} onChange={v => setForm(f => ({ ...f, quoteDate: v || null }))} />
                        </div>
                        <div>
                            <label className={lbl}>Hiệu lực đến</label>
                            <DateInput value={form.validUntil ?? ''} onChange={v => setForm(f => ({ ...f, validUntil: v || null }))} />
                        </div>
                    </div>
                    <div className="grid grid-cols-3 gap-3">
                        <div>
                            <label className={lbl}>Trạng thái (đổi qua hành động)</label>
                            <span className={`inline-block px-2 py-1.5 rounded text-sm font-medium ${QUOTATION_STATUS_COLORS[item.status] ?? 'bg-gray-100 text-gray-600'}`}>
                                {QUOTATION_STATUS_LABELS[item.status] ?? item.status}
                            </span>
                        </div>
                        <div>
                            <label className={lbl}>Tiền tệ</label>
                            <input className={inp} value={form.currency ?? ''} onChange={e => setForm(f => ({ ...f, currency: e.target.value || null }))} />
                        </div>
                        <div>
                            <label className={lbl}>Tỷ giá</label>
                            <input type="number" className={inp} value={form.exchangeRate ?? ''} onChange={e => setForm(f => ({ ...f, exchangeRate: e.target.value ? +e.target.value : null }))} />
                        </div>
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
                        <ProductLineItemsTable rows={rows} onChange={setRows} productOptions={productOptions} showUnit showTax />
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
                                    : item.customerResponse === 'adjust' ? 'Yêu cầu điều chỉnh' : 'Không đồng ý'}
                            </span>
                            {item.customerResponseNote && <div className="text-md text-gray-600 mt-1">Nội dung: {item.customerResponseNote}</div>}
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
