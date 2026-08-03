import { useMutation, useQueryClient } from '@tanstack/react-query';
import { campaignService } from '../services/campaignService';
import type { CreateCampaignPayload } from '../types/campaignTypes';

// tạo mới Chiến dịch — invalidate danh sách sau khi thành công
export function useCreateCampaign() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (payload: CreateCampaignPayload) => campaignService.create(payload),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['campaigns'] }),
    });
}
