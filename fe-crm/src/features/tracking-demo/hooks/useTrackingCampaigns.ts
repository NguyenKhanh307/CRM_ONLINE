import { useLiveQuery } from '@/core/data/useLiveQuery';
import { trackingService } from '../services/trackingService';

// danh sách chiến dịch đang chạy để landing page gắn nguồn (endpoint công khai)
// bỏ staleTime: dữ liệu chiến dịch ít đổi nên không polling, chỉ tải khi mount
export function useTrackingCampaigns() {
    return useLiveQuery('tracking:campaigns', () => trackingService.getCampaigns().then((r) => r.data.data));
}
