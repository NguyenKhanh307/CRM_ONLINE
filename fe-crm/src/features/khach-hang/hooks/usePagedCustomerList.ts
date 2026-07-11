import { keepPreviousData, useQuery } from '@tanstack/react-query';
import type { PageParams } from '@/shared/types/api';
import { customerService } from '../services/customerService';

/** Danh sách khách hàng phân trang server-side (search `q` + tag lọc `status` trong PageParams). */
export function usePagedCustomerList(params: PageParams) {
    return useQuery({
        queryKey: ['customers', 'paged', params],
        queryFn: () => customerService.getList(params).then(r => r.data.data),
        placeholderData: keepPreviousData,
    });
}
