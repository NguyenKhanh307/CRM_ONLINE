import { useLiveQuery } from '@/core/data/useLiveQuery';
import { opportunityService } from '../services/opportunityService';

// lấy chi tiết một cơ hội theo ID (trang 360°)
export function useOpportunityDetail(id: number | undefined) {
    const enabled = id != null && !Number.isNaN(id);
    return useLiveQuery(`opportunity:${id}`, () => opportunityService.getById(id as number).then(r => r.data.data), enabled);
}
