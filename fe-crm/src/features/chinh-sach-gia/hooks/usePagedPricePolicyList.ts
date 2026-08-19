import { useLiveQuery } from '@/core/data/useLiveQuery';
import type { PageParams } from '@/shared/types/api';
import { pricingService } from '../services/pricingService';

// danh sách chính sách giá phân trang server-side (search `q` + tag lọc `status` trong PageParams)
export function usePagedPricePolicyList(params: PageParams) {
    return useLiveQuery(`price-policies:paged:${JSON.stringify(params)}`, () => pricingService.getList(params).then(r => r.data.data));
}
