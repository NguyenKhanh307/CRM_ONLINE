import { useLiveQuery } from '@/core/data/useLiveQuery';
import { campaignService } from '../services/campaignService';

// lấy chi tiết một Chiến dịch theo ID
export function useCampaign(id: number) {
    return useLiveQuery(`campaign:${id}`, () => campaignService.getById(id).then(r => r.data.data), !!id);
}
