import { useLiveQuery } from '@/core/data/useLiveQuery';
import { auditLogService } from '../services/auditLogService';

// danh sách nhật ký sự kiện phân trang server-side, lọc theo nguồn + tìm kiếm
export function useAuditLogList(params: { source: string | null; q: string; page: number; size: number }) {
    return useLiveQuery(
        `audit-log:${JSON.stringify(params)}`,
        () => auditLogService.getList(params).then(r => r.data.data),
    );
}
