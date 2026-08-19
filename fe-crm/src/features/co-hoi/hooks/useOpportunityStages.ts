import { useLiveQuery } from '@/core/data/useLiveQuery';
import { opportunityStageService } from '../services/opportunityStageService';

// lấy danh sách giai đoạn cơ hội — dùng cho dropdown stage
export function useOpportunityStages(enabled = true) {
    return useLiveQuery(
        'opportunity-stages',
        () => opportunityStageService.getList().then((r) => r.data.data.items),
        enabled,
    );
}
