import { useState } from 'react';
import { FiPlus, FiTrash2, FiSave } from 'react-icons/fi';
import { DateInput } from '@/shared/components/form/DateInput';
import { useConfirm } from '@/shared/confirm/useConfirm';
import { formatNumber } from '@/shared/utils/number';
import { nonNegativeError } from '@/shared/utils/validators';
import { useInvoicePayments } from '../hooks/useInvoicePayments';
import type { InvoicePaymentScheduleResult, PaymentSchedulePayload, PaymentScheduleStatus } from '../types/invoiceTypes';

interface Props {
    invoiceId: number;
}

const STATUS_LABELS: Record<PaymentScheduleStatus, string> = {
    pending: 'Chờ thu', partial: 'Một phần', paid: 'Đã thu', overdue: 'Quá hạn',
};
const STATUS_OPTIONS: PaymentScheduleStatus[] = ['pending', 'partial', 'paid', 'overdue'];

const inp = 'w-full border border-gray-300 rounded-btn px-2 py-1 text-sm text-text-main focus:outline-none focus:border-primary';

/** Một dòng đợt thanh toán đã tồn tại — chỉnh sửa tại chỗ rồi lưu. */
function ScheduleRow({ invoiceId, s }: { invoiceId: number; s: InvoicePaymentScheduleResult }) {
    const { update, remove } = useInvoicePayments(invoiceId);
    const { confirmSave, confirmDelete } = useConfirm();
    const [form, setForm] = useState<PaymentSchedulePayload>({
        installmentNo: s.installmentNo, dueDate: s.dueDate, amount: s.amount,
        paidAmount: s.paidAmount, status: s.status, paidAt: s.paidAt, note: s.note,
    });
    const [error, setError] = useState<string | null>(null);

    const handleSave = async () => {
        // Lỗi nhập liệu hiện đỏ ngay dưới ô số tiền, không dùng popup.
        const err = nonNegativeError(form.amount, 'Số tiền') ?? nonNegativeError(form.paidAmount, 'Đã thu');
        setError(err);
        if (err) return;
        if (!(await confirmSave(`đợt thanh toán ${s.installmentNo ?? ''}`.trim()))) return;
        update.mutate({ id: s.id, payload: form });
    };

    const handleDelete = async () => {
        if (!(await confirmDelete(`đợt thanh toán ${s.installmentNo ?? ''}`.trim()))) return;
        remove.mutate(s.id);
    };

    return (
        <tr>
            <td className="px-2 py-1 border-b border-gray-100 text-center text-sm">{s.installmentNo ?? '—'}</td>
            <td className="px-2 py-1 border-b border-gray-100" style={{ minWidth: 140 }}>
                <DateInput value={form.dueDate ?? ''} onChange={(v) => setForm((f) => ({ ...f, dueDate: v || null }))} />
            </td>
            <td className="px-2 py-1 border-b border-gray-100">
                <input type="number" min={0} className={`${inp} text-right ${error ? 'border-danger' : ''}`} value={form.amount ?? ''} onChange={(e) => { setForm((f) => ({ ...f, amount: e.target.value ? +e.target.value : null })); setError(null); }} />
                {error && <p className="text-xs text-danger mt-1">{error}</p>}
            </td>
            <td className="px-2 py-1 border-b border-gray-100">
                <input type="number" min={0} className={`${inp} text-right`} value={form.paidAmount ?? ''} onChange={(e) => { setForm((f) => ({ ...f, paidAmount: e.target.value ? +e.target.value : null })); setError(null); }} />
            </td>
            <td className="px-2 py-1 border-b border-gray-100">
                <select className={inp} value={form.status} onChange={(e) => setForm((f) => ({ ...f, status: e.target.value as PaymentScheduleStatus }))}>
                    {STATUS_OPTIONS.map((o) => <option key={o} value={o}>{STATUS_LABELS[o]}</option>)}
                </select>
            </td>
            <td className="px-2 py-1 border-b border-gray-100">
                <input className={inp} value={form.note ?? ''} onChange={(e) => setForm((f) => ({ ...f, note: e.target.value || null }))} />
            </td>
            <td className="px-2 py-1 border-b border-gray-100 text-center whitespace-nowrap">
                <button type="button" title="Lưu đợt" disabled={update.isPending}
                    className="p-1.5 rounded hover:bg-blue-50 text-gray-400 hover:text-primary disabled:opacity-50"
                    onClick={handleSave}>
                    <FiSave size={13} />
                </button>
                <button type="button" title="Xóa đợt" disabled={remove.isPending}
                    className="p-1.5 rounded hover:bg-red-50 text-gray-400 hover:text-danger disabled:opacity-50"
                    onClick={handleDelete}>
                    <FiTrash2 size={13} />
                </button>
            </td>
        </tr>
    );
}

/** Bảng đợt thanh toán của hóa đơn — thêm/sửa/xóa; BE tự suy ra paymentStatus. */
export function PaymentSchedulesTable({ invoiceId }: Props) {
    const { list, add } = useInvoicePayments(invoiceId);
    const { confirmCreate } = useConfirm();
    const schedules = list.data ?? [];
    const [draft, setDraft] = useState<PaymentSchedulePayload>({
        installmentNo: null, dueDate: null, amount: null, paidAmount: null, status: 'pending', paidAt: null, note: null,
    });
    const [draftError, setDraftError] = useState<string | null>(null);

    const totalAmount = schedules.reduce((s, x) => s + (x.amount ?? 0), 0);
    const totalPaid = schedules.reduce((s, x) => s + (x.paidAmount ?? 0), 0);

    const submitAdd = async () => {
        // Lỗi nhập liệu hiện đỏ ngay dưới ô số tiền, không dùng popup.
        const err = !draft.amount
            ? 'Vui lòng nhập số tiền'
            : nonNegativeError(draft.amount, 'Số tiền') ?? nonNegativeError(draft.paidAmount, 'Đã thu');
        setDraftError(err);
        if (err) return;

        if (!(await confirmCreate('đợt thanh toán'))) return;

        const nextNo = draft.installmentNo ?? (schedules.length + 1);
        add.mutate({ ...draft, installmentNo: nextNo }, {
            onSuccess: () => setDraft({ installmentNo: null, dueDate: null, amount: null, paidAmount: null, status: 'pending', paidAt: null, note: null }),
        });
    };

    return (
        <div className="space-y-2">
            <div className="overflow-x-auto border border-gray-200 rounded-section">
                <table className="w-full border-collapse" style={{ minWidth: 620 }}>
                    <thead>
                        <tr className="bg-gray-100 text-sm text-text-main">
                            <th className="px-2 py-2 text-center" style={{ width: 44 }}>Đợt</th>
                            <th className="px-2 py-2 text-left">Hạn thu</th>
                            <th className="px-2 py-2 text-right">Số tiền</th>
                            <th className="px-2 py-2 text-right">Đã thu</th>
                            <th className="px-2 py-2 text-left" style={{ width: 120 }}>Trạng thái</th>
                            <th className="px-2 py-2 text-left">Ghi chú</th>
                            <th className="px-2 py-2 text-center" style={{ width: 80 }} />
                        </tr>
                    </thead>
                    <tbody>
                        {schedules.map((s) => <ScheduleRow key={s.id} invoiceId={invoiceId} s={s} />)}
                        {schedules.length === 0 && (
                            <tr><td colSpan={7} className="px-2 py-3 text-center text-sm text-gray-400">Chưa có đợt thanh toán nào</td></tr>
                        )}
                        {/* Dòng thêm mới */}
                        <tr className="bg-gray-50">
                            <td className="px-2 py-1 text-center text-sm text-gray-400">{schedules.length + 1}</td>
                            <td className="px-2 py-1"><DateInput value={draft.dueDate ?? ''} onChange={(v) => setDraft((f) => ({ ...f, dueDate: v || null }))} /></td>
                            <td className="px-2 py-1">
                                <input type="number" min={0} className={`${inp} text-right ${draftError ? 'border-danger' : ''}`} value={draft.amount ?? ''} onChange={(e) => { setDraft((f) => ({ ...f, amount: e.target.value ? +e.target.value : null })); setDraftError(null); }} />
                                {draftError && <p className="text-xs text-danger mt-1">{draftError}</p>}
                            </td>
                            <td className="px-2 py-1"><input type="number" min={0} className={`${inp} text-right`} value={draft.paidAmount ?? ''} onChange={(e) => { setDraft((f) => ({ ...f, paidAmount: e.target.value ? +e.target.value : null })); setDraftError(null); }} /></td>
                            <td className="px-2 py-1">
                                <select className={inp} value={draft.status} onChange={(e) => setDraft((f) => ({ ...f, status: e.target.value as PaymentScheduleStatus }))}>
                                    {STATUS_OPTIONS.map((o) => <option key={o} value={o}>{STATUS_LABELS[o]}</option>)}
                                </select>
                            </td>
                            <td className="px-2 py-1"><input className={inp} value={draft.note ?? ''} onChange={(e) => setDraft((f) => ({ ...f, note: e.target.value || null }))} /></td>
                            <td className="px-2 py-1 text-center">
                                <button type="button" title="Thêm đợt" disabled={add.isPending}
                                    className="p-1.5 rounded hover:bg-green-50 text-gray-400 hover:text-success disabled:opacity-50"
                                    onClick={submitAdd}>
                                    <FiPlus size={14} />
                                </button>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
            <div className="text-sm text-text-main text-right space-x-6">
                <span>Tổng đợt: <b>{formatNumber(totalAmount)}</b></span>
                <span>Đã thu: <b className="text-primary">{formatNumber(totalPaid)}</b></span>
                <span>Còn lại: <b>{formatNumber(Math.max(totalAmount - totalPaid, 0))}</b></span>
            </div>
        </div>
    );
}
