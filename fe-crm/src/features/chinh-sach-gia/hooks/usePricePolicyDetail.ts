import { useLiveQuery } from '@/core/data/useLiveQuery';
import { pricingService } from '../services/pricingService';

// lấy chi tiết chính sách giá
export function usePricePolicyDetail(id: number) {
    return useLiveQuery(`price-policy:${id}`, () => pricingService.getById(id).then(r => r.data.data), id > 0);
}
