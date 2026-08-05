import { formatISODate } from '@/shared/utils/date';
import type { ExportColumn } from '@/shared/components/export/exportTypes';
import type { ProductResult } from '../types/productTypes';

const STATUS_LABELS: Record<string, string> = {
    active: 'Đang kinh doanh', inactive: 'Ngừng hoạt động', discontinued: 'Ngừng kinh doanh',
};

/** Các cột khả dụng khi xuất file phân hệ Sản phẩm. */
export const productExportColumns: ExportColumn<ProductResult>[] = [
    { key: 'sku', label: 'Mã SKU' },
    { key: 'name', label: 'Tên sản phẩm' },
    { key: 'type', label: 'Loại' },
    { key: 'unit', label: 'Đơn vị' },
    { key: 'basePrice', label: 'Giá bán', format: r => r.basePrice ?? '' },
    { key: 'status', label: 'Trạng thái', format: r => STATUS_LABELS[r.status] ?? r.status },
    { key: 'createdAt', label: 'Ngày tạo', format: r => formatISODate(r.createdAt) },
];
