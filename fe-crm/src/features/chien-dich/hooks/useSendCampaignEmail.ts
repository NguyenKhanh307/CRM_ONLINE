import { useMutation, useQueryClient } from '@tanstack/react-query';
import { campaignService } from '../services/campaignService';
import type { SendCampaignEmailPayload } from '../types/campaignTypes';

/** Gửi email hàng loạt cho thành viên Chiến dịch — trả về số email đã gửi. */
export function useSendCampaignEmail(campaignId: number) {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (payload: SendCampaignEmailPayload) => campaignService.sendEmail(campaignId, payload).then(r => r.data.data),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['campaign-members', campaignId] }),
    });
}
