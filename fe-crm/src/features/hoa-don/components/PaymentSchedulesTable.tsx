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
    // tổng tiền hóa đơn — dùng để tính "Còn lại" và chặn thu vượt quá
    invoiceTotal: number;
}

const STATUS_LABELS: Record<PaymentScheduleStatus, string> = {
    pending: 'Chờ thu', partial: 'Một phần', paid: 'Đã thu', overdue: 'Quá hạn',
};
const STATUS_OPTIONS: PaymentScheduleStatus[] = ['pending', 'partial', 'paid', 'overdue'];

const inp = 'w-full border border-gray-300 rounded-btn px-2 py-1 text-sm text-text-main focus:outline-none focus:border-primary';

// lỗi khi tổng đã thu (các đợt khác + giá trị đang nhập) vượt quá tổng hóa đơn
function overpayError(otherPaid: number, value: number | null, invoiceTotal: number): string | null {
    if (value == null) return null;
    if (otherPaid + value > invoiceTotal) return 'Vượt quá tổng hóa đơn';
    return null;
}

// một dòng đợt thanh toán đã tồn tại — chỉnh sửa tại chỗ rồi lưu
function ScheduleRow({ invoiceId, s, otherPaid, invoiceTotal }: { invoiceId: number; s: InvoicePaymentScheduleResult; otherPaid: number; invoiceTotal: number }) {
    const { update, remove } = useInvoicePayments(invoiceId);
    const { confirmSave, confirmDelete } = useConfirm();
    const [form, setForm] = useState<PaymentSchedulePayload>({
        installmentNo: s.installmentNo, dueDate: s.dueDate, amount: null,
        paidAmount: s.paidAmount, status: s.status, bankName: s.bankName, bankAccount: s.bankAccount, note: s.note,
    });
    const [error, setError] = useState<string | null>(null);

    const handleSave = async () => {
        // lỗi nhập liệu hiện đỏ ngay dưới ô, không dùng popup
        const err = nonNegativeError(form.paidAmount, 'Đã thu') ?? overpayError(otherPaid, form.paidAmount, invoiceTotal);
        setError(err);
        if (err) return;
        if (!(await confirmSave(`đợt thanh toán ${s.installmentNo ?? ''}`.trim()))) return;
        update.mutate({ id: s.id, payload: { ...form, amount: null } });
    };

    const handleDelete = async () => {
        if (!(await confirmDelete(`đợt thanh toán ${s.installmentNo ?? ''}`.trim()))) return;
        remove.mutate(s.id);
    };

    return (
        <tr>
            <td className="px-2 py-1 border-b border-gray-100 text-center text-sm">{s.installmentNo ?? '—'}</td>
            <td className="px-2 py-1 border-b border-gray-100" style={{ minWidth: 160 }}>
                <DateInput value={form.dueDate ?? ''} onChange={(v) => setForm((f) => ({ ...f, dueDate: v || null }))} />
            </td>
            <td className="px-2 py-1 border-b border-gray-100">
                <input type="number" min={0} className={`${inp} text-right ${error ? 'border-danger' : ''}`} value={form.paidAmount ?? ''} onChange={(e) => { setForm((f) => ({ ...f, paidAmount: e.target.value ? +e.target.value : null })); setError(null); }} />
                {error && <p className="text-xs text-danger mt-1">{error}</p>}
            </td>
            <td className="px-2 py-1 border-b border-gray-100">
                <select className={inp} value={form.status} onChange={(e) => setForm((f) => ({ ...f, status: e.target.value as PaymentScheduleStatus }))}>
                    {STATUS_OPTIONS.map((o) => <option key={o} value={o}>{STATUS_LABELS[o]}</option>)}
                </select>
            </td>
            <td className="px-2 py-1 border-b border-gray-100">
                <input className={inp} placeholder="Ngân hàng" value={form.bankName ?? ''} onChange={(e) => setForm((f) => ({ ...f, bankName: e.target.value || null }))} />
            </td>
            <td className="px-2 py-1 border-b border-gray-100">
                <input className={inp} placeholder="Số TK" value={form.bankAccount ?? ''} onChange={(e) => setForm((f) => ({ ...f, bankAccount: e.target.value || null }))} />
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

// bảng đợt thanh toán của hóa đơn — thêm/sửa/xóa; be tự suy ra paymentStatus. chỉ nhập "Đã thu",
// không còn ô "Số tiền" (đợt dự kiến) — "Còn lại" tính thẳng từ tổng hóa đơn trừ đã thu
export function PaymentSchedulesTable({ invoiceId, invoiceTotal }: Props) {
    const { list, add } = useInvoicePayments(invoiceId);
    const { confirmCreate } = useConfirm();
    const schedules = list.data ?? [];
    const [draft, setDraft] = useState<PaymentSchedulePayload>({
        installmentNo: null, dueDate: null, amount: null, paidAmount: null, status: 'pending', bankName: null, bankAccount: null, note: null,
    });
    const [draftError, setDraftError] = useState<string | null>(null);

    const totalPaid = schedules.reduce((s, x) => s + (x.paidAmount ?? 0), 0);
    const remaining = Math.max(invoiceTotal - totalPaid, 0);

    const submitAdd = async () => {
        // lỗi nhập liệu hiện đỏ ngay dưới ô, không dùng popup
        const err = nonNegativeError(draft.paidAmount, 'Đã thu') ?? overpayError(totalPaid, draft.paidAmount, invoiceTotal);
        setDraftError(err);
        if (err) return;

        if (!(await confirmCreate('đợt thanh toán'))) return;

        const nextNo = draft.installmentNo ?? (schedules.length + 1);
        add.mutate({ ...draft, amount: null, installmentNo: nextNo }, {
            onSuccess: () => setDraft({ installmentNo: null, dueDate: null, amount: null, paidAmount: null, status: 'pending', bankName: null, bankAccount: null, note: null }),
        });
    };

    return (
        <div className="space-y-2">
            <div className="overflow-x-auto border border-gray-200 rounded-section">
                <table className="w-full border-collapse" style={{ minWidth: 560 }}>
                    <thead>
                        <tr className="bg-gray-100 text-sm text-text-main">
                            <th className="px-2 py-2 text-center" style={{ width: 44 }}>Đợt</th>
                            <th className="px-2 py-2 text-left">Hạn thu</th>
                            <th className="px-2 py-2 text-right">Đã thu</th>
                            <th className="px-2 py-2 text-left" style={{ width: 120 }}>Trạng thái</th>
                            <th className="px-2 py-2 text-left" style={{ width: 120 }}>Ngân hàng</th>
                            <th className="px-2 py-2 text-left" style={{ width: 120 }}>Số TK</th>
                            <th className="px-2 py-2 text-left">Ghi chú</th>
                            <th className="px-2 py-2 text-center" style={{ width: 80 }} />
                        </tr>
                    </thead>
                    <tbody>
                        {schedules.map((s) => (
                            <ScheduleRow key={s.id} invoiceId={invoiceId} s={s} invoiceTotal={invoiceTotal}
                                otherPaid={totalPaid - (s.paidAmount ?? 0)} />
                        ))}
                        {schedules.length === 0 && (
                            <tr><td colSpan={8} className="px-2 py-3 text-center text-sm text-gray-400">Chưa có đợt thanh toán nào</td></tr>
                        )}
                        {/* Dòng thêm mới */}
                        <tr className="bg-gray-50">
                            <td className="px-2 py-1 text-center text-sm text-gray-400">{schedules.length + 1}</td>
                            <td className="px-2 py-1"><DateInput value={draft.dueDate ?? ''} onChange={(v) => setDraft((f) => ({ ...f, dueDate: v || null }))} /></td>
                            <td className="px-2 py-1">
                                <input type="number" min={0} className={`${inp} text-right ${draftError ? 'border-danger' : ''}`} value={draft.paidAmount ?? ''} onChange={(e) => { setDraft((f) => ({ ...f, paidAmount: e.target.value ? +e.target.value : null })); setDraftError(null); }} />
                                {draftError && <p className="text-xs text-danger mt-1">{draftError}</p>}
                            </td>
                            <td className="px-2 py-1">
                                <select className={inp} value={draft.status} onChange={(e) => setDraft((f) => ({ ...f, status: e.target.value as PaymentScheduleStatus }))}>
                                    {STATUS_OPTIONS.map((o) => <option key={o} value={o}>{STATUS_LABELS[o]}</option>)}
                                </select>
                            </td>
                            <td className="px-2 py-1"><input className={inp} placeholder="Ngân hàng" value={draft.bankName ?? ''} onChange={(e) => setDraft((f) => ({ ...f, bankName: e.target.value || null }))} /></td>
                            <td className="px-2 py-1"><input className={inp} placeholder="Số TK" value={draft.bankAccount ?? ''} onChange={(e) => setDraft((f) => ({ ...f, bankAccount: e.target.value || null }))} /></td>
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
                <span>Tổng hóa đơn: <b>{formatNumber(invoiceTotal)}</b></span>
                <span>Đã thu: <b className="text-primary">{formatNumber(totalPaid)}</b></span>
                <span>Còn lại: <b>{formatNumber(remaining)}</b></span>
            </div>
        </div>
    );
}
