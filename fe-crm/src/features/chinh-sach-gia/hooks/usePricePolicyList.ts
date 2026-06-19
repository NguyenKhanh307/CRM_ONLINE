import { useQuery } from '@tanstack/react-query';
import { pricingService } from '../services/pricingService';

/** Lấy danh sách chính sách giá (phân trang). */
export function usePricePolicyList() {
    return useQuery({
        queryKey: ['price-policies'],
        queryFn: () => pricingService.getList({ page: 0, size: 100 }).then(r => r.data.data.items),
    });
}
