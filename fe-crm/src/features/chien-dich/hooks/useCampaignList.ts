import { useLiveQuery } from '@/core/data/useLiveQuery';
import { campaignService } from '../services/campaignService';

// lấy danh sách Chiến dịch (phân trang)
export function useCampaignList() {
    return useLiveQuery('campaigns', () =>
        campaignService.getList({ page: 0, size: 500, sortBy: 'createdAt', sortDir: 'desc' }).then(r => r.data.data.items));
}
