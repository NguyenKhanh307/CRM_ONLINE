import { keepPreviousData, useQuery } from '@tanstack/react-query';
import type { PageParams } from '@/shared/types/api';
import { leadService } from '../services/leadService';

/** Danh sách tiềm năng phân trang server-side (search `q` + tag lọc `status` trong PageParams). */
export function usePagedLeadList(params: PageParams) {
    return useQuery({
        queryKey: ['leads', 'paged', params],
        queryFn: () => leadService.getList(params).then(r => r.data.data),
        placeholderData: keepPreviousData,
    });
}
