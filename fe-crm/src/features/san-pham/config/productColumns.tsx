import { type ColumnDef } from '@tanstack/react-table';
import { badgeCell, currencyCell, dateCell, labelCell, numberCell, tagCell, textCell } from '@/shared/components/table/cells';
import type { ProductResult } from '../types/productTypes';

const TYPE_LABELS: Record<string, string> = {
    goods: 'Vật tư hàng hóa', service: 'Dịch vụ',
};

const STATUS_LABELS: Record<string, string> = {
    active: 'Đang kinh doanh', inactive: 'Ngừng hoạt động', discontinued: 'Ngừng kinh doanh',
};

const STATUS_COLORS: Record<string, string> = {
    active: 'bg-green-100 text-green-700', inactive: 'bg-gray-100 text-gray-600', discontinued: 'bg-red-100 text-red-600',
};

/** Tạo danh sách cột Sản phẩm — hiển thị đầy đủ trường + tên danh mục (do BE resolve sẵn). */
export const getProductColumns = (): ColumnDef<ProductResult>[] => [
    { accessorKey: 'sku', header: 'Mã SKU', size: 120, enableSorting: true },
    { accessorKey: 'name', header: 'Tên sản phẩm', size: 200, enableSorting: true },
    { accessorKey: 'categoryName', header: 'Danh mục', size: 150, cell: tagCell },
    { accessorKey: 'type', header: 'Loại', size: 120, cell: labelCell(TYPE_LABELS) },
    { accessorKey: 'unit', header: 'Đơn vị', size: 90, cell: textCell },
    { accessorKey: 'basePrice', header: 'Giá bán', size: 140, cell: currencyCell },
    { accessorKey: 'costPrice', header: 'Giá vốn', size: 140, cell: currencyCell },
    { accessorKey: 'vatRate', header: 'Thuế VAT (%)', size: 110, cell: numberCell },
    { accessorKey: 'description', header: 'Mô tả', size: 200, cell: textCell },
    { accessorKey: 'status', header: 'Trạng thái', size: 130, cell: badgeCell(STATUS_LABELS, STATUS_COLORS) },
    { accessorKey: 'createdByName', header: 'Người tạo', size: 160, cell: textCell },
    { accessorKey: 'createdAt', header: 'Ngày tạo', size: 120, enableSorting: true, cell: dateCell },
    { accessorKey: 'updatedByName', header: 'Người sửa cuối', size: 160, cell: textCell },
    { accessorKey: 'updatedAt', header: 'Ngày sửa', size: 120, enableSorting: true, cell: dateCell },
];
