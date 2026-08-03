import { useQuery } from '@tanstack/react-query';
import { opportunityService } from '../services/opportunityService';

// lấy chi tiết một cơ hội theo ID (trang 360°)
export function useOpportunityDetail(id: number | undefined) {
    return useQuery({
        queryKey: ['opportunity', id],
        queryFn: () => opportunityService.getById(id as number).then(r => r.data.data),
        enabled: id != null && !Number.isNaN(id),
    });
}
