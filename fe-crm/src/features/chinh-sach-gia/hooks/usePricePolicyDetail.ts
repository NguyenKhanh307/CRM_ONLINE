import { useQuery } from '@tanstack/react-query';
import { pricingService } from '../services/pricingService';

/** Lấy chi tiết chính sách giá. */
export function usePricePolicyDetail(id: number) {
    return useQuery({
        queryKey: ['price-policies', id],
        queryFn: () => pricingService.getById(id).then(r => r.data.data),
        enabled: id > 0,
    });
}
