import { useState } from 'react';
import { FiPlus, FiTrash2, FiSave } from 'react-icons/fi';
import { useConfirm } from '@/shared/confirm/useConfirm';
import { nonNegativeError, percentError } from '@/shared/utils/validators';
import { useActiveUsers } from '@/features/users/hooks/useActiveUsers';
import { toIdNameMap } from '@/shared/utils/lookup';
import { useInvoiceRevenueRecords } from '../hooks/useInvoiceRevenueRecords';
import type { InvoiceRevenueRecordResult, CreateInvoiceRevenueRecordPayload, UpdateInvoiceRevenueRecordPayload } from '../types/invoiceTypes';

interface Props { invoiceId: number; }

const inp = 'w-full border border-gray-300 rounded-btn px-2 py-1 text-sm text-text-main focus:outline-none focus:border-primary';

// một dòng bản ghi doanh số đã tồn tại — sửa số tiền/tỷ lệ/ghi chú tại chỗ (userId không sửa được)
function RevenueRow({ invoiceId, r, userLabel }: { invoiceId: number; r: InvoiceRevenueRecordResult; userLabel: string }) {
    const { update, remove } = useInvoiceRevenueRecords(invoiceId);
    const { confirmSave, confirmDelete } = useConfirm();
    const [form, setForm] = useState<UpdateInvoiceRevenueRecordPayload>({
        revenueAmount: r.revenueAmount, percentage: r.percentage, note: r.note,
    });
    const [errors, setErrors] = useState<{ revenueAmount?: string | null; percentage?: string | null }>({});

    const handleSave = async () => {
        const revenueAmount = nonNegativeError(form.revenueAmount, 'Doanh số');
        const percentage = percentError(form.percentage, 'Tỷ lệ');
        setErrors({ revenueAmount, percentage });
        if (revenueAmount || percentage) return;
        if (!(await confirmSave(`bản ghi doanh số của ${userLabel}`))) return;
        update.mutate({ id: r.id, payload: form });
    };

    const handleDelete = async () => {
        if (!(await confirmDelete(`bản ghi doanh số của ${userLabel}`))) return;
        remove.mutate(r.id);
    };

    return (
        <tr>
            <td className="px-2 py-1 border-b border-gray-100 text-sm">{userLabel}</td>
            <td className="px-2 py-1 border-b border-gray-100">
                <input type="number" min={0} className={`${inp} text-right ${errors.revenueAmount ? 'border-danger' : ''}`}
                    value={form.revenueAmount ?? ''}
                    onChange={(e) => { setForm((f) => ({ ...f, revenueAmount: e.target.value ? +e.target.value : null })); setErrors((s) => ({ ...s, revenueAmount: null })); }} />
                {errors.revenueAmount && <p className="text-xs text-danger mt-1">{errors.revenueAmount}</p>}
            </td>
            <td className="px-2 py-1 border-b border-gray-100">
                <input type="number" min={0} max={100} className={`${inp} text-right ${errors.percentage ? 'border-danger' : ''}`}
                    value={form.percentage ?? ''}
                    onChange={(e) => { setForm((f) => ({ ...f, percentage: e.target.value ? +e.target.value : null })); setErrors((s) => ({ ...s, percentage: null })); }} />
                {errors.percentage && <p className="text-xs text-danger mt-1">{errors.percentage}</p>}
            </td>
            <td className="px-2 py-1 border-b border-gray-100">
                <input className={inp} value={form.note ?? ''} onChange={(e) => setForm((f) => ({ ...f, note: e.target.value || null }))} />
            </td>
            <td className="px-2 py-1 border-b border-gray-100 text-center whitespace-nowrap">
                <button type="button" title="Lưu" disabled={update.isPending}
                    className="p-1.5 rounded hover:bg-blue-50 text-gray-400 hover:text-primary disabled:opacity-50" onClick={handleSave}>
                    <FiSave size={13} />
                </button>
                <button type="button" title="Xóa" disabled={remove.isPending}
                    className="p-1.5 rounded hover:bg-red-50 text-gray-400 hover:text-danger disabled:opacity-50" onClick={handleDelete}>
                    <FiTrash2 size={13} />
                </button>
            </td>
        </tr>
    );
}

// bảng ghi nhận doanh số + % chia hoa hồng theo nhân viên trên hóa đơn — thêm/sửa/xóa
export function RevenueRecordsTable({ invoiceId }: Props) {
    const { list, add } = useInvoiceRevenueRecords(invoiceId);
    const { data: users = [] } = useActiveUsers();
    const { confirmCreate } = useConfirm();
    const records = list.data ?? [];
    const nameMap = toIdNameMap(users, 'id', 'fullName');

    const [draft, setDraft] = useState<CreateInvoiceRevenueRecordPayload>({ userId: 0, revenueAmount: null, percentage: null, note: null });
    const [errors, setErrors] = useState<{ userId?: string | null; revenueAmount?: string | null; percentage?: string | null }>({});

    const submitAdd = async () => {
        const userId = draft.userId ? null : 'Vui lòng chọn nhân viên';
        const revenueAmount = nonNegativeError(draft.revenueAmount, 'Doanh số');
        const percentage = percentError(draft.percentage, 'Tỷ lệ');
        setErrors({ userId, revenueAmount, percentage });
        if (userId || revenueAmount || percentage) return;

        if (!(await confirmCreate('bản ghi doanh số'))) return;

        add.mutate(draft, {
            onSuccess: () => setDraft({ userId: 0, revenueAmount: null, percentage: null, note: null }),
        });
    };

    return (
        <div className="space-y-2">
            <div className="overflow-x-auto border border-gray-200 rounded-section">
                <table className="w-full border-collapse" style={{ minWidth: 560 }}>
                    <thead>
                        <tr className="bg-gray-100 text-sm text-text-main">
                            <th className="px-2 py-2 text-left">Nhân viên</th>
                            <th className="px-2 py-2 text-right">Doanh số</th>
                            <th className="px-2 py-2 text-right" style={{ width: 100 }}>Tỷ lệ (%)</th>
                            <th className="px-2 py-2 text-left">Ghi chú</th>
                            <th className="px-2 py-2 text-center" style={{ width: 80 }} />
                        </tr>
                    </thead>
                    <tbody>
                        {records.map((r) => (
                            <RevenueRow key={r.id} invoiceId={invoiceId} r={r} userLabel={nameMap.get(r.userId) ?? '—'} />
                        ))}
                        {records.length === 0 && (
                            <tr><td colSpan={5} className="px-2 py-3 text-center text-sm text-gray-400">Chưa có bản ghi doanh số nào</td></tr>
                        )}
                        <tr className="bg-gray-50">
                            <td className="px-2 py-1">
                                <select className={`${inp} ${errors.userId ? 'border-danger' : ''}`}
                                    value={draft.userId || ''}
                                    onChange={(e) => { setDraft((f) => ({ ...f, userId: +e.target.value })); setErrors((s) => ({ ...s, userId: null })); }}>
                                    <option value="">— Chọn nhân viên —</option>
                                    {users.map((u) => <option key={u.id} value={u.id}>{u.fullName}</option>)}
                                </select>
                                {errors.userId && <p className="text-xs text-danger mt-1">{errors.userId}</p>}
                            </td>
                            <td className="px-2 py-1">
                                <input type="number" min={0} className={`${inp} text-right ${errors.revenueAmount ? 'border-danger' : ''}`}
                                    value={draft.revenueAmount ?? ''}
                                    onChange={(e) => { setDraft((f) => ({ ...f, revenueAmount: e.target.value ? +e.target.value : null })); setErrors((s) => ({ ...s, revenueAmount: null })); }} />
                                {errors.revenueAmount && <p className="text-xs text-danger mt-1">{errors.revenueAmount}</p>}
                            </td>
                            <td className="px-2 py-1">
                                <input type="number" min={0} max={100} className={`${inp} text-right ${errors.percentage ? 'border-danger' : ''}`}
                                    value={draft.percentage ?? ''}
                                    onChange={(e) => { setDraft((f) => ({ ...f, percentage: e.target.value ? +e.target.value : null })); setErrors((s) => ({ ...s, percentage: null })); }} />
                                {errors.percentage && <p className="text-xs text-danger mt-1">{errors.percentage}</p>}
                            </td>
                            <td className="px-2 py-1"><input className={inp} value={draft.note ?? ''} onChange={(e) => setDraft((f) => ({ ...f, note: e.target.value || null }))} /></td>
                            <td className="px-2 py-1 text-center">
                                <button type="button" title="Thêm" disabled={add.isPending}
                                    className="p-1.5 rounded hover:bg-green-50 text-gray-400 hover:text-success disabled:opacity-50" onClick={submitAdd}>
                                    <FiPlus size={14} />
                                </button>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    );
}
