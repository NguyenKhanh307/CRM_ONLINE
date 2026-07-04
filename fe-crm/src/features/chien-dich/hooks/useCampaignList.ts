import { useQuery } from '@tanstack/react-query';
import { campaignService } from '../services/campaignService';

/** Lấy danh sách Chiến dịch (phân trang). */
export function useCampaignList() {
    return useQuery({
        queryKey: ['campaigns'],
        queryFn: () => campaignService.getList({ page: 0, size: 500, sortBy: 'createdAt', sortDir: 'desc' }).then(r => r.data.data.items),
    });
}
