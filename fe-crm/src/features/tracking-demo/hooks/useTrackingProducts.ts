import { useLiveQuery } from '@/core/data/useLiveQuery';
import { trackingService } from '../services/trackingService';

// danh sách sản phẩm đang hoạt động để khách duyệt trên landing page (endpoint công khai)
// bỏ staleTime: dữ liệu sản phẩm ít đổi nên không polling, chỉ tải khi mount
export function useTrackingProducts() {
    return useLiveQuery('tracking:products', () => trackingService.getProducts().then((r) => r.data.data));
}
