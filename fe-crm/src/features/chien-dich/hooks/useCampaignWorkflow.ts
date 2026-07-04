import { useMutation, useQueryClient } from '@tanstack/react-query';
import { campaignService } from '../services/campaignService';

/** Loại hành động chuyển trạng thái Chiến dịch. */
export type CampaignAction = 'schedule' | 'start' | 'pause' | 'complete' | 'cancel';

/**
 * Hook thực hiện hành động chuyển trạng thái Chiến dịch:
 * schedule / start / pause / complete / cancel.
 */
export function useCampaignWorkflow() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: ({ id, action }: { id: number; action: CampaignAction }) => campaignService[action](id),
        onSuccess: (_d, v) => {
            qc.invalidateQueries({ queryKey: ['campaigns'] });
            qc.invalidateQueries({ queryKey: ['campaign', v.id] });
        },
    });
}
