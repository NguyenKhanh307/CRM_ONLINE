import { useMutation, useQueryClient } from '@tanstack/react-query';
import { campaignService } from '../services/campaignService';
import type { UpdateCampaignPayload } from '../types/campaignTypes';

/** Cập nhật Chiến dịch — invalidate danh sách sau khi thành công. */
export function useUpdateCampaign() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: ({ id, payload }: { id: number; payload: UpdateCampaignPayload }) =>
            campaignService.update(id, payload),
        onSuccess: (_d, v) => {
            qc.invalidateQueries({ queryKey: ['campaigns'] });
            qc.invalidateQueries({ queryKey: ['campaign', v.id] });
        },
    });
}
