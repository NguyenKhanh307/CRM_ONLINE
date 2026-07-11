import { keepPreviousData, useQuery } from '@tanstack/react-query';
import type { PageParams } from '@/shared/types/api';
import { opportunityService } from '../services/opportunityService';

/** Danh sách cơ hội phân trang server-side (search `q` + tag lọc `status` trong PageParams). */
export function usePagedOpportunityList(params: PageParams) {
    return useQuery({
        queryKey: ['opportunities', 'paged', params],
        queryFn: () => opportunityService.getList(params).then(r => r.data.data),
        placeholderData: keepPreviousData,
    });
}
