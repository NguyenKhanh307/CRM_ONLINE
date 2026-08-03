import { FiPlus, FiTrash2 } from 'react-icons/fi';
import { SearchableSelect, type SelectOption } from '@/shared/components/SearchableSelect';
import { inputCls } from '@/shared/components/form/formStyles';
import type { ReturnReason, TicketReturnItemPayload } from '../types/ticketTypes';
import { REASON_OPTIONS } from '../config/ticketEnums';

// một dòng hàng trả/đổi ở phía FE (state form)
export interface ReturnRow {
    key: string;
    productId: string;
    quantity: number;
    unitPrice: number;
    amount: number;
    reason: string;
    conditionNote: string;
}

let seq = 0;
// tạo dòng trống mới
export const emptyReturnRow = (): ReturnRow => ({
    key: `r${++seq}`, productId: '', quantity: 1, unitPrice: 0, amount: 0, reason: '', conditionNote: '',
});

// chuyển các dòng form sang payload gửi BE (bỏ dòng chưa chọn sản phẩm)
export const toReturnItemPayloads = (rows: ReturnRow[]): TicketReturnItemPayload[] =>
    rows.filter(r => r.productId).map(r => ({
        invoiceItemId: null,
        productId: Number(r.productId),
        quantity: r.quantity,
        unitPrice: r.unitPrice,
        amount: r.amount || r.quantity * r.unitPrice,
        reason: (r.reason || null) as ReturnReason | null,
        conditionNote: r.conditionNote || null,
    }));

interface Props {
    rows: ReturnRow[];
    onChange: (rows: ReturnRow[]) => void;
    productOptions: SelectOption[];
}

// bảng nhập dòng hàng trả/đổi (product/quantity/unitPrice/amount/reason/conditionNote)
export function ReturnItemsTable({ rows, onChange, productOptions }: Props) {
    const patch = (key: string, p: Partial<ReturnRow>) =>
        onChange(rows.map(r => {
            if (r.key !== key) return r;
            const next = { ...r, ...p };
            // tự tính thành tiền = số lượng x đơn giá khi hai giá trị này đổi
            if (p.quantity != null || p.unitPrice != null) next.amount = next.quantity * next.unitPrice;
            return next;
        }));

    return (
        <div className="space-y-2">
            <table className="w-full text-md">
                <thead>
                    <tr className="text-left text-gray-500 border-b border-gray-200">
                        <th className="py-2 pr-2 font-medium">Sản phẩm</th>
                        <th className="py-2 px-2 font-medium w-24">Số lượng</th>
                        <th className="py-2 px-2 font-medium w-32">Đơn giá</th>
                        <th className="py-2 px-2 font-medium w-32">Thành tiền</th>
                        <th className="py-2 px-2 font-medium w-40">Lý do</th>
                        <th className="py-2 px-2 font-medium">Tình trạng</th>
                        <th className="w-10" />
                    </tr>
                </thead>
                <tbody>
                    {rows.map(r => (
                        <tr key={r.key} className="border-b border-gray-100">
                            <td className="py-1.5 pr-2 min-w-[180px]">
                                <SearchableSelect value={r.productId} onChange={(v) => patch(r.key, { productId: v })} options={productOptions} />
                            </td>
                            <td className="py-1.5 px-2">
                                <input type="number" min={0} value={r.quantity} onChange={(e) => patch(r.key, { quantity: Number(e.target.value) })} className={inputCls} />
                            </td>
                            <td className="py-1.5 px-2">
                                <input type="number" min={0} value={r.unitPrice} onChange={(e) => patch(r.key, { unitPrice: Number(e.target.value) })} className={inputCls} />
                            </td>
                            <td className="py-1.5 px-2">
                                <input type="number" min={0} value={r.amount} onChange={(e) => patch(r.key, { amount: Number(e.target.value) })} className={inputCls} />
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
                        <tr><td colSpan={7} className="py-3 text-center text-gray-400">Chưa có dòng hàng nào</td></tr>
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
