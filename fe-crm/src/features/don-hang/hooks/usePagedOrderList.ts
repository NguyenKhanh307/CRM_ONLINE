import { keepPreviousData, useQuery } from '@tanstack/react-query';
import type { PageParams } from '@/shared/types/api';
import { orderService } from '../services/orderService';

// danh sách đơn hàng phân trang server-side (search `q` + tag lọc `status` trong PageParams)
export function usePagedOrderList(params: PageParams) {
    return useQuery({
        queryKey: ['orders', 'paged', params],
        queryFn: () => orderService.getList(params).then(r => r.data.data),
        placeholderData: keepPreviousData,
    });
}
