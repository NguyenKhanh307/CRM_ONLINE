import { useQuery } from '@tanstack/react-query';
import { opportunityService } from '../services/opportunityService';

/** Lấy toàn bộ bản ghi liên quan của một cơ hội (trang 360°). */
export function useOpportunityRelated(id: number | undefined) {
    return useQuery({
        queryKey: ['opportunity', id, 'related'],
        queryFn: () => opportunityService.getRelated(id as number).then(r => r.data.data),
        enabled: id != null && !Number.isNaN(id),
    });
}
