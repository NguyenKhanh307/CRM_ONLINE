import { useQuery } from '@tanstack/react-query';
import { campaignService } from '../services/campaignService';

/** Lấy thống kê hiệu quả (ROI) của một Chiến dịch. */
export function useCampaignStats(campaignId: number) {
    return useQuery({
        queryKey: ['campaign-stats', campaignId],
        queryFn: () => campaignService.getStats(campaignId).then(r => r.data.data),
        enabled: !!campaignId,
    });
}
