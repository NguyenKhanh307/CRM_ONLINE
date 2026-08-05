// một dòng hàng hóa trong form báo giá/đơn hàng/cơ hội
export interface LineItemRow {
    id: string;
    // ID dòng đã lưu ở backend (chỉ có khi đang sửa); dòng mới để undefined
    backendId?: number;
    productId: string;
    unit: string;
    quantity: number;
    unitPrice: number;
    discountPct: number;
    taxRate: number;
    note: string;
}

// option sản phẩm cho dropdown — kèm unit/price/vatRate để autofill
export interface ProductOption {
    value: string;
    label: string;
    unit?: string;
    price?: number;
    vatRate?: number;
}

// tạo một dòng trống mới
export const emptyLineItem = (): LineItemRow => ({
    id: crypto.randomUUID(),
    productId: '',
    unit: '',
    quantity: 1,
    unitPrice: 0,
    discountPct: 0,
    taxRate: 0,
    note: '',
});

// thành tiền của một dòng (trước chiết khấu/thuế)
export const rowSubtotal = (r: LineItemRow): number => r.quantity * r.unitPrice;
// tiền chiết khấu của một dòng
export const rowDiscount = (r: LineItemRow): number => (rowSubtotal(r) * r.discountPct) / 100;
// tiền thuế của một dòng (sau chiết khấu)
export const rowTax = (r: LineItemRow): number =>
    ((rowSubtotal(r) - rowDiscount(r)) * r.taxRate) / 100;
// tổng tiền của một dòng
export const rowTotal = (r: LineItemRow): number =>
    rowSubtotal(r) - rowDiscount(r) + rowTax(r);

// tổng hợp subtotal/discount/tax/total của toàn bộ dòng
export const computeTotals = (rows: LineItemRow[]) =>
    rows.reduce(
        (acc, r) => ({
            subtotal: acc.subtotal + rowSubtotal(r),
            discount: acc.discount + rowDiscount(r),
            tax: acc.tax + rowTax(r),
            total: acc.total + rowTotal(r),
        }),
        { subtotal: 0, discount: 0, tax: 0, total: 0 },
    );

// chuyển một dòng UI sang payload item gửi API — không gửi amount, backend tự tính từ quantity/unitPrice/discount/taxRate
export const toItemPayload = (r: LineItemRow) => ({
    productId: Number(r.productId),
    unit: r.unit || null,
    quantity: r.quantity,
    unitPrice: r.unitPrice,
    discount: rowDiscount(r),
    taxRate: r.taxRate,
    note: r.note || null,
});

// chuyển các dòng UI sang payload item gửi API (lọc dòng chưa chọn sản phẩm)
export const toItemPayloads = (rows: LineItemRow[]) =>
    rows.filter((r) => r.productId).map(toItemPayload);

// dòng item trả về từ backend (GET) — đủ field để dựng lại LineItemRow
export interface ItemResultLike {
    id: number;
    productId: number | null;
    unit?: string | null;
    quantity: number;
    unitPrice: number;
    discount: number;
    taxRate?: number;
    note: string | null;
}

// dựng LineItemRow từ item backend (suy ngược discountPct từ tiền chiết khấu)
export const fromItemResult = (it: ItemResultLike): LineItemRow => {
    const base = it.quantity * it.unitPrice;
    return {
        id: crypto.randomUUID(),
        backendId: it.id,
        productId: it.productId != null ? String(it.productId) : '',
        unit: it.unit ?? '',
        quantity: it.quantity,
        unitPrice: it.unitPrice,
        discountPct: base > 0 ? (it.discount / base) * 100 : 0,
        taxRate: it.taxRate ?? 0,
        note: it.note ?? '',
    };
};

// so sánh dòng gốc (backend) với dòng hiện tại để biết cần tạo/sửa/xóa item nào
export const diffLineItems = (original: LineItemRow[], current: LineItemRow[]) => {
    const curIds = new Set(current.filter((r) => r.backendId).map((r) => r.backendId));
    const toDelete = original.filter((r) => r.backendId && !curIds.has(r.backendId)).map((r) => r.backendId as number);
    const toCreate = current.filter((r) => !r.backendId && r.productId);
    const toUpdate = current.filter((r) => r.backendId && r.productId);
    return { toCreate, toUpdate, toDelete };
};

// kiểm tra biên các dòng hàng (khớp ràng buộc backend): SL > 0, đơn giá >= 0, CK% và Thuế% trong [0,100]
// trả về thông báo lỗi đầu tiên (kèm số thứ tự dòng), hoặc null nếu tất cả hợp lệ
export const validateLineItems = (rows: LineItemRow[]): string | null => {
    for (let i = 0; i < rows.length; i++) {
        const r = rows[i];
        const n = i + 1;
        if (!r.productId) return `Dòng ${n}: chưa chọn hàng hóa`;
        if (!(r.quantity > 0)) return `Dòng ${n}: số lượng phải lớn hơn 0`;
        if (r.unitPrice < 0) return `Dòng ${n}: đơn giá không được âm`;
        if (r.discountPct < 0 || r.discountPct > 100) return `Dòng ${n}: chiết khấu phải từ 0 đến 100%`;
        if (r.taxRate < 0 || r.taxRate > 100) return `Dòng ${n}: thuế suất phải từ 0 đến 100%`;
    }
    return null;
};
