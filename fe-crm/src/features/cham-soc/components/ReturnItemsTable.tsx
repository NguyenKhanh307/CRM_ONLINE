import { FiPlus, FiTrash2 } from 'react-icons/fi';
import { SearchableSelect } from '@/shared/components/SearchableSelect';
import { inputCls } from '@/shared/components/form/formStyles';
import type { ReturnReason, TicketReturnItemPayload } from '../types/ticketTypes';
import { REASON_OPTIONS } from '../config/ticketEnums';

// một dòng hàng trả/đổi ở phía FE (state form) — chọn theo dòng hóa đơn cụ thể (invoiceItemId),
// không còn chọn sản phẩm/đơn giá/thành tiền trực tiếp
export interface ReturnRow {
    key: string;
    invoiceItemId: string;
    quantity: number;
    reason: string;
    conditionNote: string;
}

let seq = 0;
// tạo dòng trống mới
export const emptyReturnRow = (): ReturnRow => ({
    key: `r${++seq}`, invoiceItemId: '', quantity: 1, reason: '', conditionNote: '',
});

// chuyển các dòng form sang payload gửi BE (bỏ dòng chưa chọn dòng hóa đơn)
export const toReturnItemPayloads = (rows: ReturnRow[]): TicketReturnItemPayload[] =>
    rows.filter(r => r.invoiceItemId).map(r => ({
        invoiceItemId: Number(r.invoiceItemId),
        quantity: r.quantity,
        reason: (r.reason || null) as ReturnReason | null,
        conditionNote: r.conditionNote || null,
    }));

interface Props {
    rows: ReturnRow[];
    onChange: (rows: ReturnRow[]) => void;
}

// bảng nhập dòng hàng trả/đổi — chọn theo dòng hóa đơn cụ thể (invoiceItemId)/quantity/reason/conditionNote
export function ReturnItemsTable({ rows, onChange }: Props) {
    const patch = (key: string, p: Partial<ReturnRow>) =>
        onChange(rows.map(r => (r.key === key ? { ...r, ...p } : r)));

    return (
        <div className="space-y-2">
            <table className="w-full text-md">
                <thead>
                    <tr className="text-left text-gray-500 border-b border-gray-200">
                        <th className="py-2 pr-2 font-medium">ID dòng hóa đơn</th>
                        <th className="py-2 px-2 font-medium w-24">Số lượng</th>
                        <th className="py-2 px-2 font-medium w-40">Lý do</th>
                        <th className="py-2 px-2 font-medium">Tình trạng</th>
                        <th className="w-10" />
                    </tr>
                </thead>
                <tbody>
                    {rows.map(r => (
                        <tr key={r.key} className="border-b border-gray-100">
                            <td className="py-1.5 pr-2 min-w-[140px]">
                                <input type="number" min={1} value={r.invoiceItemId} onChange={(e) => patch(r.key, { invoiceItemId: e.target.value })} className={inputCls} />
                            </td>
                            <td className="py-1.5 px-2">
                                <input type="number" min={0} value={r.quantity} onChange={(e) => patch(r.key, { quantity: Number(e.target.value) })} className={inputCls} />
                            </td>
                            <td className="py-1.5 px-2">
                                <SearchableSelect value={r.reason} onChange={(v) => patch(r.key, { reason: v })} options={REASON_OPTIONS} />
                            </td>
                            <td className="py-1.5 px-2">
                                <input type="text" value={r.conditionNote} onChange={(e) => patch(r.key, { conditionNote: e.target.value })} className={inputCls} />
                            </td>
                            <td className="py-1.5 text-center">
                                <button type="button" onClick={() => onChange(rows.filter(x => x.key !== r.key))}
                                    className="p-1.5 rounded hover:bg-red-50 text-gray-400 hover:text-danger" title="Xóa dòng">
                                    <FiTrash2 size={14} />
                                </button>
                            </td>
                        </tr>
                    ))}
                    {rows.length === 0 && (
                        <tr><td colSpan={5} className="py-3 text-center text-gray-400">Chưa có dòng hàng nào</td></tr>
                    )}
                </tbody>
            </table>
            <button type="button" onClick={() => onChange([...rows, emptyReturnRow()])}
                className="flex items-center gap-1.5 px-3 py-1.5 rounded-btn border border-gray-300 text-md text-gray-600 hover:bg-gray-50">
                <FiPlus size={14} /> Thêm dòng
            </button>
        </div>
    );
}
