import { useQuery } from '@tanstack/react-query';
import { leadService } from '../services/leadService';

/** Lấy danh sách tiềm năng (phân trang). */
export function useLeadList() {
    return useQuery({
        queryKey: ['leads'],
        queryFn: () => leadService.getList({ page: 0, size: 500, sortBy: 'createdAt', sortDir: 'desc' }).then(r => r.data.data.items),
    });
}
