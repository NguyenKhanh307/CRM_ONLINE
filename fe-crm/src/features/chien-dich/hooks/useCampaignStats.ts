import { useLiveQuery } from '@/core/data/useLiveQuery';
import { campaignService } from '../services/campaignService';

// lấy thống kê hiệu quả (ROI) của một Chiến dịch
export function useCampaignStats(campaignId: number) {
    return useLiveQuery(`campaign-stats:${campaignId}`, () => campaignService.getStats(campaignId).then(r => r.data.data), !!campaignId);
}
