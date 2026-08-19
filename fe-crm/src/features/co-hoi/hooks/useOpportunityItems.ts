import { useLiveQuery } from '@/core/data/useLiveQuery';
import { opportunityService } from '../services/opportunityService';

// lấy danh sách dòng hàng của một cơ hội (bảng dưới ở bố cục 2 bảng)
export function useOpportunityItems(opportunityId: number | null) {
    return useLiveQuery(
        `opportunity-items:${opportunityId}`,
        () => opportunityService.getItems(opportunityId as number).then(r => r.data.data),
        opportunityId != null,
    );
}
