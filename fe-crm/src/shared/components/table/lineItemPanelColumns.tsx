import type { ProductResult } from '@/features/san-pham/types/productTypes';
import { formatCurrency, formatNumber } from '@/shared/utils/number';
import type { ItemResultLike } from '@/shared/components/form/productLineItem';
import type { ItemPanelColumn } from './RecordItemsPanel';

/** Dòng hàng hiển thị ở panel dưới — như item backend trả về, kèm thành tiền. */
export type LineItemPanelRow = ItemResultLike & { amount: number };

interface Options {
    /** Hiện cột Thuế (%) — Cơ hội không lưu taxRate trên dòng hàng. */
    showTax?: boolean;
}

/**
 * Dựng cột cho bảng dòng hàng (chỉ-xem) dùng chung ở Cơ hội / Báo giá / Đơn hàng / Hóa đơn.
 * Mã hàng / Tên hàng / ĐVT được tra từ `productMap` vì item backend chỉ trả `productId`.
 * @param productMap Map productId → sản phẩm
 * @param options Bật/tắt cột Thuế (Cơ hội không có taxRate)
 */
export function getLineItemPanelColumns<T extends LineItemPanelRow>(
    productMap: Map<number, ProductResult>,
    options: Options = {},
): ItemPanelColumn<T>[] {
    const product = (row: T) => (row.productId != null ? productMap.get(row.productId) : undefined);

    const columns: ItemPanelColumn<T>[] = [
        { key: 'sku', header: 'Mã hàng', render: (r) => product(r)?.sku ?? '—' },
        { key: 'name', header: 'Tên hàng', render: (r) => product(r)?.name ?? '—' },
        { key: 'unit', header: 'Đơn vị tính', render: (r) => r.unit || product(r)?.unit || '—' },
        { key: 'quantity', header: 'Số lượng', align: 'right', render: (r) => formatNumber(r.quantity) },
        { key: 'unitPrice', header: 'Đơn giá', align: 'right', render: (r) => formatCurrency(r.unitPrice) },
        { key: 'discount', header: 'Chiết khấu', align: 'right', render: (r) => formatCurrency(r.discount) },
    ];

    if (options.showTax) {
        columns.push({
            key: 'taxRate',
            header: 'Thuế (%)',
            align: 'right',
            render: (r) => formatNumber(r.taxRate ?? 0),
        });
    }

    columns.push(
        { key: 'amount', header: 'Thành tiền', align: 'right', render: (r) => formatCurrency(r.amount) },
        { key: 'note', header: 'Ghi chú', render: (r) => r.note || '—' },
    );

    return columns;
}
