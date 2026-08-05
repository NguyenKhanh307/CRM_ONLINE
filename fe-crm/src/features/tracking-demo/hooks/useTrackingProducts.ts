import { useQuery } from '@tanstack/react-query';
import { trackingService } from '../services/trackingService';

// danh sách sản phẩm đang hoạt động để khách duyệt trên landing page (endpoint công khai)
export function useTrackingProducts() {
    return useQuery({
        queryKey: ['tracking', 'products'],
        queryFn: () => trackingService.getProducts().then((r) => r.data.data),
        staleTime: 5 * 60 * 1000,
    });
}
