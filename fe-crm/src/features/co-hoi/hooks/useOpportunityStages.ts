import { useQuery } from '@tanstack/react-query';
import { opportunityStageService } from '../services/opportunityStageService';

// lấy danh sách giai đoạn cơ hội — dùng cho dropdown stage
export function useOpportunityStages(enabled = true) {
    return useQuery({
        queryKey: ['opportunity-stages'],
        queryFn: () => opportunityStageService.getList().then((r) => r.data.data.items),
        enabled,
    });
}
