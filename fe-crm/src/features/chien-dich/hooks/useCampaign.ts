import { useQuery } from '@tanstack/react-query';
import { campaignService } from '../services/campaignService';

/** Lấy chi tiết một Chiến dịch theo ID. */
export function useCampaign(id: number) {
    return useQuery({
        queryKey: ['campaign', id],
        queryFn: () => campaignService.getById(id).then(r => r.data.data),
        enabled: !!id,
    });
}
