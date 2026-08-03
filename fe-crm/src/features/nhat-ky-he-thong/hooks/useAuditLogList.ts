import { keepPreviousData, useQuery } from '@tanstack/react-query';
import { auditLogService } from '../services/auditLogService';

// danh sách nhật ký sự kiện phân trang server-side, lọc theo nguồn + tìm kiếm
export function useAuditLogList(params: { source: string | null; q: string; page: number; size: number }) {
    return useQuery({
        queryKey: ['audit-log', params],
        queryFn: () => auditLogService.getList(params).then(r => r.data.data),
        placeholderData: keepPreviousData,
    });
}
