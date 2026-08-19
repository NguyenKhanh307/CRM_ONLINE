import { formatISODate } from '@/shared/utils/date';
import type { ExportColumn } from '@/shared/components/export/exportTypes';
import type { CustomerResult } from '../types/customerTypes';

const TYPE_LABELS: Record<string, string> = {
    individual: 'Cá nhân', company: 'Công ty',
};

const STATUS_LABELS: Record<string, string> = {
    active: 'Hoạt động', inactive: 'Không hoạt động', potential: 'Tiềm năng',
};

// các cột khả dụng khi xuất file phân hệ Khách hàng
export const customerExportColumns: ExportColumn<CustomerResult>[] = [
    { key: 'code', label: 'Mã KH' },
    { key: 'name', label: 'Tên khách hàng' },
    { key: 'type', label: 'Loại', format: r => TYPE_LABELS[r.type] ?? r.type },
    { key: 'phone', label: 'Điện thoại' },
    { key: 'email', label: 'Email' },
    { key: 'status', label: 'Trạng thái', format: r => STATUS_LABELS[r.status] ?? r.status },
    { key: 'shortName', label: 'Tên viết tắt', format: r => r.shortName ?? '' },
    { key: 'taxCode', label: 'Mã số thuế', format: r => r.taxCode ?? '' },
    { key: 'website', label: 'Website', format: r => r.website ?? '' },
    { key: 'address', label: 'Địa chỉ', format: r => r.address ?? '' },
    { key: 'industry', label: 'Ngành nghề', format: r => r.industry ?? '' },
    { key: 'source', label: 'Nguồn', format: r => r.source ?? '' },
    { key: 'creditDays', label: 'Số ngày được nợ', format: r => r.creditDays ?? '' },
    { key: 'creditLimit', label: 'Hạn mức nợ', format: r => r.creditLimit ?? '' },
    { key: 'rating', label: 'Xếp hạng', format: r => r.rating ?? '' },
    { key: 'employeeSize', label: 'Quy mô nhân sự', format: r => r.employeeSize ?? '' },
    { key: 'isDistributor', label: 'Nhà phân phối', format: r => (r.isDistributor ? 'Có' : 'Không') },
    { key: 'ownerName', label: 'Người phụ trách', format: r => r.ownerName ?? '' },
    { key: 'createdAt', label: 'Ngày tạo', format: r => formatISODate(r.createdAt) },
];
