import { useMutation, useQueryClient } from '@tanstack/react-query';
import { campaignService } from '../services/campaignService';

/** Bàn giao hàng loạt Chiến dịch cho nhân viên khác. */
export function useHandoverBulkCampaign() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (payload: { ids: number[]; toUserId: number; reason?: string }) =>
            campaignService.handoverBulk(payload),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['campaigns'] }),
    });
}
