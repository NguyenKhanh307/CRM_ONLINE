import { useQuery } from '@tanstack/react-query';
import { opportunityService } from '../services/opportunityService';

/** Lấy danh sách cơ hội (phân trang). */
export function useOpportunityList() {
    return useQuery({
        queryKey: ['opportunities'],
        queryFn: () => opportunityService.getList({ page: 0, size: 500, sortBy: 'createdAt', sortDir: 'desc' }).then(r => r.data.data.items),
    });
}
