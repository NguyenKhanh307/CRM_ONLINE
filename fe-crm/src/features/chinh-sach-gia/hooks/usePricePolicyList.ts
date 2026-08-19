import { useLiveQuery } from '@/core/data/useLiveQuery';
import { pricingService } from '../services/pricingService';

// lấy danh sách chính sách giá (phân trang)
export function usePricePolicyList() {
    return useLiveQuery('price-policies', () => pricingService.getList({ page: 0, size: 100 }).then(r => r.data.data.items));
}
