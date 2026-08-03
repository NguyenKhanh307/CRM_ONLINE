import { keepPreviousData, useQuery } from '@tanstack/react-query';
import type { PageParams } from '@/shared/types/api';
import { activityService } from '../services/activityService';

// danh sách hoạt động phân trang server-side (search `q` + tag lọc `status` trong PageParams)
export function usePagedActivityList(params: PageParams) {
    return useQuery({
        queryKey: ['activities', 'paged', params],
        queryFn: () => activityService.getList(params).then(r => r.data.data),
        placeholderData: keepPreviousData,
    });
}
