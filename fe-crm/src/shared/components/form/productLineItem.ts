/** Một dòng hàng hóa trong form báo giá/đơn hàng/cơ hội. */
export interface LineItemRow {
    id: string;
    productId: string;
    unit: string;
    quantity: number;
    unitPrice: number;
    discountPct: number;
    taxRate: number;
    note: string;
}

/** Option sản phẩm cho dropdown — kèm unit/price/vatRate để autofill. */
export interface ProductOption {
    value: string;
    label: string;
    unit?: string;
    price?: number;
    vatRate?: number;
}

/** Tạo một dòng trống mới. */
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

/** Thành tiền của một dòng (trước chiết khấu/thuế). */
export const rowSubtotal = (r: LineItemRow): number => r.quantity * r.unitPrice;
/** Tiền chiết khấu của một dòng. */
export const rowDiscount = (r: LineItemRow): number => (rowSubtotal(r) * r.discountPct) / 100;
/** Tiền thuế của một dòng (sau chiết khấu). */
export const rowTax = (r: LineItemRow): number =>
    ((rowSubtotal(r) - rowDiscount(r)) * r.taxRate) / 100;
/** Tổng tiền của một dòng. */
export const rowTotal = (r: LineItemRow): number =>
    rowSubtotal(r) - rowDiscount(r) + rowTax(r);

/** Tổng hợp subtotal/discount/tax/total của toàn bộ dòng. */
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

/** Chuyển các dòng UI sang payload item gửi API (lọc dòng chưa chọn sản phẩm). */
export const toItemPayloads = (rows: LineItemRow[]) =>
    rows
        .filter((r) => r.productId)
        .map((r) => ({
            productId: Number(r.productId),
            unit: r.unit || null,
            quantity: r.quantity,
            unitPrice: r.unitPrice,
            discount: rowDiscount(r),
            taxRate: r.taxRate,
            amount: rowTotal(r),
            note: r.note || null,
        }));
