import { useLiveQuery } from '@/core/data/useLiveQuery';
import { opportunityService } from '../services/opportunityService';

// lấy toàn bộ bản ghi liên quan của một cơ hội (trang 360°)
export function useOpportunityRelated(id: number | undefined) {
    const enabled = id != null && !Number.isNaN(id);
    return useLiveQuery(`opportunity:${id}:related`, () => opportunityService.getRelated(id as number).then(r => r.data.data), enabled);
}
