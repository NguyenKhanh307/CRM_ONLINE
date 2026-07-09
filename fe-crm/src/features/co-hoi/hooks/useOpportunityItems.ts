import { useQuery } from '@tanstack/react-query';
import { opportunityService } from '../services/opportunityService';

/** Lấy danh sách dòng hàng của một cơ hội (bảng dưới ở bố cục 2 bảng). */
export function useOpportunityItems(opportunityId: number | null) {
    return useQuery({
        queryKey: ['opportunity-items', opportunityId],
        queryFn: () => opportunityService.getItems(opportunityId as number).then(r => r.data.data),
        enabled: opportunityId != null,
    });
}
