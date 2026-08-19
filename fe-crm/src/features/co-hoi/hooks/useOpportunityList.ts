import { useLiveQuery } from '@/core/data/useLiveQuery';
import { opportunityService } from '../services/opportunityService';

// lấy toàn bộ cơ hội (tối đa 500 dòng) — dùng cho dropdown chọn cơ hội
export function useOpportunityList() {
    return useLiveQuery('opportunities', () =>
        opportunityService.getList({ page: 0, size: 500, sortBy: 'createdAt', sortDir: 'desc' }).then(r => r.data.data.items));
}
