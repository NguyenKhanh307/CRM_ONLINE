import { useQuery } from '@tanstack/react-query';
import { trackingService } from '../services/trackingService';

// danh sách chiến dịch đang chạy để landing page gắn nguồn (endpoint công khai)
export function useTrackingCampaigns() {
    return useQuery({
        queryKey: ['tracking', 'campaigns'],
        queryFn: () => trackingService.getCampaigns().then((r) => r.data.data),
        staleTime: 5 * 60 * 1000,
    });
}
