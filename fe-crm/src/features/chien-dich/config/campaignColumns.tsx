import { type ColumnDef } from '@tanstack/react-table';
import { badgeCell, currencyCell, dateCell, numberCell, textCell } from '@/shared/components/table/cells';
import type { CampaignResult } from '../types/campaignTypes';

export const CAMPAIGN_STATUS_LABELS: Record<string, string> = {
    draft: 'Nháp', scheduled: 'Đã lên lịch', running: 'Đang chạy',
    paused: 'Tạm dừng', completed: 'Hoàn tất', cancelled: 'Đã hủy',
};
const CAMPAIGN_STATUS_COLORS: Record<string, string> = {
    draft: 'bg-gray-100 text-gray-600', scheduled: 'bg-indigo-100 text-indigo-700',
    running: 'bg-blue-100 text-blue-700', paused: 'bg-yellow-100 text-yellow-700',
    completed: 'bg-green-100 text-green-700', cancelled: 'bg-red-100 text-red-600',
};
export const CAMPAIGN_TYPE_LABELS: Record<string, string> = {
    email: 'Email', event: 'Sự kiện', ads: 'Quảng cáo', social: 'Mạng xã hội',
    seo: 'SEO', webinar: 'Webinar', other: 'Khác',
};
const CAMPAIGN_TYPE_COLORS: Record<string, string> = {
    email: 'bg-blue-50 text-blue-700', event: 'bg-purple-50 text-purple-700',
    ads: 'bg-orange-50 text-orange-700', social: 'bg-pink-50 text-pink-700',
    seo: 'bg-teal-50 text-teal-700', webinar: 'bg-indigo-50 text-indigo-700',
    other: 'bg-gray-50 text-gray-600',
};

// tạo danh sách cột Chiến dịch — hiển thị đầy đủ trường + tên người phụ trách (do BE resolve sẵn)
export const getCampaignColumns = (): ColumnDef<CampaignResult>[] => [
    { accessorKey: 'code', header: 'Mã', size: 110, enableSorting: true },
    { accessorKey: 'name', header: 'Tên chiến dịch', size: 220, enableSorting: true, cell: textCell },
    { accessorKey: 'type', header: 'Loại', size: 130, cell: badgeCell(CAMPAIGN_TYPE_LABELS, CAMPAIGN_TYPE_COLORS) },
    { accessorKey: 'status', header: 'Trạng thái', size: 130, cell: badgeCell(CAMPAIGN_STATUS_LABELS, CAMPAIGN_STATUS_COLORS) },
    { accessorKey: 'channel', header: 'Kênh', size: 150, cell: textCell },
    { accessorKey: 'ownerName', header: 'Người phụ trách', size: 160, cell: textCell },
    { accessorKey: 'startDate', header: 'Bắt đầu', size: 120, cell: dateCell },
    { accessorKey: 'endDate', header: 'Kết thúc', size: 120, cell: dateCell },
    { accessorKey: 'budget', header: 'Ngân sách', size: 150, cell: currencyCell },
    { accessorKey: 'actualCost', header: 'Chi phí thực tế', size: 150, cell: currencyCell },
    { accessorKey: 'targetSize', header: 'Quy mô mục tiêu', size: 140, cell: numberCell },
    { accessorKey: 'expectedRevenue', header: 'Doanh số kỳ vọng', size: 160, cell: currencyCell },
    { accessorKey: 'description', header: 'Mô tả', size: 200, cell: textCell },
    { accessorKey: 'createdByName', header: 'Người tạo', size: 160, cell: textCell },
    { accessorKey: 'createdAt', header: 'Ngày tạo', size: 120, enableSorting: true, cell: dateCell },
    { accessorKey: 'updatedByName', header: 'Người sửa cuối', size: 160, cell: textCell },
    { accessorKey: 'updatedAt', header: 'Ngày sửa', size: 120, enableSorting: true, cell: dateCell },
];
