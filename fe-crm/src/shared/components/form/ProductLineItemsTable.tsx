import { useMemo, type Dispatch, type SetStateAction } from 'react';
import { FiPlus, FiTrash2 } from 'react-icons/fi';
import { SearchableSelect } from '@/shared/components/SearchableSelect';
import { formatNumber } from '@/shared/utils/number';
import { usePolicyPricing } from './usePolicyPricing';
import {
    type LineItemRow,
    type ProductOption,
    emptyLineItem,
    rowSubtotal,
    rowDiscount,
    rowTotal,
    computeTotals,
} from './productLineItem';

interface Props {
    rows: LineItemRow[];
    /**
     * Setter của danh sách dòng hàng — dạng `useState` setter để patch bất đồng bộ (tra giá theo
     * chính sách) luôn đọc được trạng thái mới nhất, không ghi đè mất thao tác vừa làm.
     */
    onChange: Dispatch<SetStateAction<LineItemRow[]>>;
    productOptions: ProductOption[];
    /** Hiện cột Đơn vị tính (báo giá/đơn hàng). */
    showUnit?: boolean;
    /** Hiện cột Thuế suất (báo giá/đơn hàng). */
    showTax?: boolean;
    /** Chính sách giá đang chọn — nếu có, đơn giá/CK% dòng hàng tra qua /api/pricing/resolve. */
    pricePolicyId?: number | null;
}

const fmt = (n: number) => formatNumber(n);

const thBase = 'text-table font-semibold text-text-main border-b-2 border-gray-300 px-2 py-2 whitespace-nowrap bg-gray-100';
const tdBase = 'border-b border-gray-200 px-2 py-1';
const numIn = 'w-full bg-transparent text-table text-text-main text-right focus:outline-none';
const txtIn = 'w-full bg-transparent text-table text-text-main focus:outline-none';

/**
 * Bảng hàng hóa controlled — dùng chung cho form báo giá / đơn hàng / cơ hội.
 * Chọn sản phẩm tự điền đơn vị / đơn giá / thuế suất từ ProductOption.
 */
export const ProductLineItemsTable = ({
    rows,
    onChange,
    productOptions,
    showUnit = false,
    showTax = false,
    pricePolicyId,
}: Props) => {
    const totals = useMemo(() => computeTotals(rows), [rows]);

    const { priceRow, priceRowDebounced, hints } = usePolicyPricing(pricePolicyId, rows, productOptions, onChange);

    const changeRow = (id: string, patch: Partial<LineItemRow>) =>
        onChange((prev) => prev.map((r) => (r.id === id ? { ...r, ...patch } : r)));

    const selectProduct = (id: string, productId: string) => {
        const opt = productOptions.find((o) => o.value === productId);
        // Điền ngay từ sản phẩm để giao diện phản hồi tức thì...
        changeRow(id, {
            productId,
            unit: opt?.unit ?? '',
            unitPrice: opt?.price ?? 0,
            taxRate: opt?.vatRate ?? 0,
        });
        // ...rồi để chính sách giá ghi đè đơn giá/CK nếu sản phẩm nằm trong chính sách
        priceRow(id, productId, rows.find((r) => r.id === id)?.quantity ?? 1);
    };

    /** Đổi số lượng có thể vượt/tụt qua ngưỡng min_qty của chính sách → tra lại giá. */
    const changeQuantity = (row: LineItemRow, quantity: number) => {
        changeRow(row.id, { quantity });
        if (row.productId) priceRowDebounced(row.id, row.productId, quantity);
    };

    const addRow = () => onChange((prev) => [...prev, emptyLineItem()]);
    const removeRow = (id: string) => onChange((prev) => prev.filter((r) => r.id !== id));

    return (
        <div className="space-y-3">
            <div className="overflow-x-auto border border-gray-300 rounded-section">
                <table className="border-collapse w-full" style={{ minWidth: 980 }}>
                    <thead>
                        <tr>
                            <th className={`${thBase} text-center`} style={{ width: 44 }}>STT</th>
                            <th className={`${thBase} text-left`} style={{ width: 220 }}>Hàng hóa</th>
                            {showUnit && <th className={`${thBase} text-left`} style={{ width: 90 }}>ĐVT</th>}
                            <th className={`${thBase} text-right`} style={{ width: 80 }}>Số lượng</th>
                            <th className={`${thBase} text-right`} style={{ width: 110 }}>Đơn giá</th>
                            <th className={`${thBase} text-right`} style={{ width: 80 }}>CK%</th>
                            {showTax && <th className={`${thBase} text-right`} style={{ width: 80 }}>Thuế%</th>}
                            <th className={`${thBase} text-right`} style={{ width: 120 }}>Thành tiền</th>
                            <th className={`${thBase} text-right`} style={{ width: 120 }}>Tổng tiền</th>
                            <th className={`${thBase} text-center`} style={{ width: 44 }} />
                        </tr>
                    </thead>
                    <tbody>
                        {rows.map((row, idx) => (
                            <tr key={row.id}>
                                <td className={`${tdBase} text-center text-table text-gray-500`}>{idx + 1}</td>
                                <td className={tdBase}>
                                    <SearchableSelect
                                        value={row.productId}
                                        onChange={(v) => selectProduct(row.id, v)}
                                        options={productOptions}
                                        placeholder="Chọn hàng hóa"
                                    />
                                    {/* Vì sao giá chính sách chưa áp — gợi ý nhẹ, không dùng modal chặn thao tác */}
                                    {hints[row.id] && (
                                        <p className="text-[11px] text-amber-600 mt-0.5">{hints[row.id]}</p>
                                    )}
                                </td>
                                {showUnit && (
                                    <td className={tdBase}>
                                        <input type="text" value={row.unit} onChange={(e) => changeRow(row.id, { unit: e.target.value })} className={txtIn} />
                                    </td>
                                )}
                                <td className={tdBase}>
                                    <input type="number" min={0} value={row.quantity} onChange={(e) => changeQuantity(row, parseFloat(e.target.value) || 0)} className={numIn} />
                                </td>
                                <td className={tdBase}>
                                    <input type="number" min={0} value={row.unitPrice} onChange={(e) => changeRow(row.id, { unitPrice: parseFloat(e.target.value) || 0 })} className={numIn} />
                                </td>
                                <td className={tdBase}>
                                    <input type="number" min={0} max={100} value={row.discountPct} onChange={(e) => changeRow(row.id, { discountPct: parseFloat(e.target.value) || 0 })} className={numIn} />
                                </td>
                                {showTax && (
                                    <td className={tdBase}>
                                        <input type="number" min={0} max={100} value={row.taxRate} onChange={(e) => changeRow(row.id, { taxRate: parseFloat(e.target.value) || 0 })} className={numIn} />
                                    </td>
                                )}
                                <td className={`${tdBase} text-right text-table text-text-main`}>{fmt(rowSubtotal(row) - rowDiscount(row))}</td>
                                <td className={`${tdBase} text-right text-table font-medium text-text-main`}>{fmt(rowTotal(row))}</td>
                                <td className={`${tdBase} text-center`}>
                                    <button type="button" onClick={() => removeRow(row.id)} className="p-1 rounded hover:bg-red-50 text-gray-400 hover:text-danger" title="Xóa dòng">
                                        <FiTrash2 size={13} />
                                    </button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>

            <div className="flex items-center justify-between flex-wrap gap-2">
                <button type="button" onClick={addRow} className="border border-gray-300 text-text-main rounded-btn px-3 py-1.5 text-sm hover:bg-gray-50 transition-colors flex items-center gap-1">
                    <FiPlus size={13} /> Thêm dòng
                </button>
                <div className="text-md text-text-main space-x-6">
                    <span>Tạm tính: <b>{fmt(totals.subtotal)}</b></span>
                    <span>Chiết khấu: <b>{fmt(totals.discount)}</b></span>
                    <span>Thuế: <b>{fmt(totals.tax)}</b></span>
                    <span>Tổng tiền: <b className="text-primary">{fmt(totals.total)}</b></span>
                </div>
            </div>
        </div>
    );
};
