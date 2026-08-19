import { useLiveQuery } from '@/core/data/useLiveQuery';
import type { PageParams } from '@/shared/types/api';
import { opportunityService } from '../services/opportunityService';

// danh sách cơ hội phân trang server-side (search `q` + tag lọc `status` trong params)
export function usePagedOpportunityList(params: PageParams) {
    return useLiveQuery(`opportunities:paged:${JSON.stringify(params)}`, () => opportunityService.getList(params).then(r => r.data.data));
}
