import { formatISODate } from '@/shared/utils/date';
import type { ExportColumn } from '@/shared/components/export/exportTypes';
import type { OpportunityResult } from '../types/opportunityTypes';

const STATUS_LABELS: Record<string, string> = {
    open: 'Đang mở', won: 'Đã thắng', lost: 'Đã thua',
};

// các cột khả dụng khi xuất file phân hệ Cơ hội
export const opportunityExportColumns: ExportColumn<OpportunityResult>[] = [
    { key: 'code', label: 'Mã' },
    { key: 'name', label: 'Tên cơ hội' },
    { key: 'amount', label: 'Giá trị', format: r => r.amount ?? '' },
    { key: 'status', label: 'Trạng thái', format: r => STATUS_LABELS[r.status] ?? r.status },
    { key: 'customerName', label: 'Khách hàng', format: r => r.customerName ?? '' },
    { key: 'contactName', label: 'Liên hệ', format: r => r.contactName ?? '' },
    { key: 'ownerName', label: 'Người phụ trách', format: r => r.ownerName ?? '' },
    { key: 'stageName', label: 'Giai đoạn', format: r => r.stageName ?? '' },
    { key: 'opportunityType', label: 'Loại', format: r => r.opportunityType ?? '' },
    { key: 'source', label: 'Nguồn', format: r => r.source ?? '' },
    { key: 'pricePolicyId', label: 'ID chính sách giá', format: r => r.pricePolicyId ?? '' },
    { key: 'winLossReason', label: 'Lý do thắng/thua', format: r => r.winLossReason ?? '' },
    { key: 'description', label: 'Mô tả', format: r => r.description ?? '' },
    { key: 'campaignName', label: 'Chiến dịch', format: r => r.campaignName ?? '' },
    { key: 'createdAt', label: 'Ngày tạo', format: r => formatISODate(r.createdAt) },
];
