import { formatISODate } from '@/shared/utils/date';
import type { ExportColumn } from '@/shared/components/export/exportTypes';
import type { ProductResult } from '../types/productTypes';

const STATUS_LABELS: Record<string, string> = {
    active: 'Đang kinh doanh', inactive: 'Ngừng hoạt động', discontinued: 'Ngừng kinh doanh',
};

const TYPE_LABELS: Record<string, string> = {
    goods: 'Vật tư hàng hóa', service: 'Dịch vụ',
};

/** Các cột khả dụng khi xuất file phân hệ Sản phẩm. */
export const productExportColumns: ExportColumn<ProductResult>[] = [
    { key: 'sku', label: 'Mã SKU' },
    { key: 'name', label: 'Tên sản phẩm' },
    { key: 'categoryName', label: 'Danh mục', format: r => r.categoryName ?? '' },
    { key: 'type', label: 'Loại', format: r => TYPE_LABELS[r.type] ?? r.type },
    { key: 'unit', label: 'Đơn vị' },
    { key: 'basePrice', label: 'Giá bán', format: r => r.basePrice ?? '' },
    { key: 'costPrice', label: 'Giá vốn', format: r => r.costPrice ?? '' },
    { key: 'vatRate', label: 'Thuế VAT (%)', format: r => r.vatRate ?? '' },
    { key: 'description', label: 'Mô tả', format: r => r.description ?? '' },
    { key: 'status', label: 'Trạng thái', format: r => STATUS_LABELS[r.status] ?? r.status },
    { key: 'createdAt', label: 'Ngày tạo', format: r => formatISODate(r.createdAt) },
];
