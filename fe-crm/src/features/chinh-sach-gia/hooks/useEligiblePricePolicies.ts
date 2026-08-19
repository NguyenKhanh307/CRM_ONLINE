import { useLiveQuery } from '@/core/data/useLiveQuery';
import { pricingService } from '../services/pricingService';

// chính sách giá mà người dùng hiện tại (và khách hàng đang chọn, nếu có) được phép sử dụng
// dùng cho dropdown "Chính sách giá" ở form Cơ hội/Báo giá — KHÁC `usePricePolicyList` (danh sách
// đầy đủ, dùng ở trang quản trị `/chinh-sach-gia`)
export function useEligiblePricePolicies(customerId?: number) {
    return useLiveQuery(`price-policies:eligible:${customerId ?? 'none'}`,
        () => pricingService.getEligibleList({ customerId }).then(r => r.data.data));
}
