import { formatISODate } from '@/shared/utils/date';
import type { ExportColumn } from '@/shared/components/export/exportTypes';
import type { WarehouseResult } from '../types/warehouseTypes';

/** Các cột khả dụng khi xuất file phân hệ Kho hàng. */
export const warehouseExportColumns: ExportColumn<WarehouseResult>[] = [
    { key: 'code', label: 'Mã kho' },
    { key: 'name', label: 'Tên kho hàng' },
    { key: 'address', label: 'Địa chỉ' },
    { key: 'isActive', label: 'Trạng thái', format: r => (r.isActive ? 'Đang hoạt động' : 'Ngừng hoạt động') },
    { key: 'createdAt', label: 'Ngày tạo', format: r => formatISODate(r.createdAt) },
];
