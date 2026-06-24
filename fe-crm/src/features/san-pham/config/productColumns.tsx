import { type ColumnDef } from '@tanstack/react-table';
import { boolBadge, currencyCell, dateCell, fkCell, labelCell, numberCell, textCell, yesNoCell } from '@/shared/components/table/cells';
import type { ProductResult } from '../types/productTypes';

const TYPE_LABELS: Record<string, string> = {
    goods: 'Vật tư hàng hóa', service: 'Dịch vụ',
};

/** Map ID → tên cho cột khóa ngoại của Sản phẩm. */
export interface ProductColumnLookups {
    categories: Map<number, string>;
}

/** Tạo danh sách cột Sản phẩm — hiển thị đầy đủ trường. */
export const getProductColumns = (lk: ProductColumnLookups): ColumnDef<ProductResult>[] => [
    { accessorKey: 'sku', header: 'Mã SKU', size: 120, enableSorting: true },
    { accessorKey: 'name', header: 'Tên sản phẩm', size: 200, enableSorting: true },
    { accessorKey: 'categoryId', header: 'Danh mục', size: 150, cell: fkCell(lk.categories) },
    { accessorKey: 'type', header: 'Loại', size: 120, cell: labelCell(TYPE_LABELS) },
    { accessorKey: 'unit', header: 'Đơn vị', size: 90, cell: textCell },
    { accessorKey: 'basePrice', header: 'Giá bán', size: 140, cell: currencyCell },
    { accessorKey: 'costPrice', header: 'Giá vốn', size: 140, cell: currencyCell },
    { accessorKey: 'vatRate', header: 'Thuế VAT (%)', size: 110, cell: numberCell },
    { accessorKey: 'description', header: 'Mô tả', size: 200, cell: textCell },
    { accessorKey: 'isDiscontinued', header: 'Ngừng KD', size: 100, cell: yesNoCell },
    { accessorKey: 'isActive', header: 'Trạng thái', size: 120, cell: boolBadge('Đang bán', 'Ngừng bán') },
    { accessorKey: 'createdAt', header: 'Ngày tạo', size: 120, enableSorting: true, cell: dateCell },
];
