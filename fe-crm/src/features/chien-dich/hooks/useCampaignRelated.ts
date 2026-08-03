import { useQuery } from '@tanstack/react-query';
import { campaignService } from '../services/campaignService';

// lấy toàn bộ bản ghi quy về một chiến dịch (tiềm năng / cơ hội / đơn hàng / hóa đơn)
export function useCampaignRelated(id: number | undefined) {
    return useQuery({
        queryKey: ['campaign', id, 'related'],
        queryFn: () => campaignService.getRelated(id as number).then(r => r.data.data),
        enabled: id != null && !Number.isNaN(id),
    });
}
