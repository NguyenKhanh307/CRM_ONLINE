import { useQuery } from '@tanstack/react-query';
import { opportunityService } from '../services/opportunityService';

export function useOpportunityList() {
    return useQuery({
        queryKey: ['opportunities'],
        queryFn: () => opportunityService.getList({ page: 0, size: 100 }).then(r => r.data.data.items),
    });
}
