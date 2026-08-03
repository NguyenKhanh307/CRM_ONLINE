import { keepPreviousData, useQuery } from '@tanstack/react-query';
import type { PageParams } from '@/shared/types/api';
import { pricingService } from '../services/pricingService';

// danh sách chính sách giá phân trang server-side (search `q` + tag lọc `status` trong PageParams)
export function usePagedPricePolicyList(params: PageParams) {
    return useQuery({
        queryKey: ['price-policies', 'paged', params],
        queryFn: () => pricingService.getList(params).then(r => r.data.data),
        placeholderData: keepPreviousData,
    });
}
