import { useLiveQuery } from '@/core/data/useLiveQuery';
import { campaignService } from '../services/campaignService';

// lấy toàn bộ bản ghi quy về một chiến dịch (tiềm năng / cơ hội / đơn hàng / hóa đơn)
export function useCampaignRelated(id: number | undefined) {
    const enabled = id != null && !Number.isNaN(id);
    return useLiveQuery(`campaign:${id}:related`, () => campaignService.getRelated(id as number).then(r => r.data.data), enabled);
}
