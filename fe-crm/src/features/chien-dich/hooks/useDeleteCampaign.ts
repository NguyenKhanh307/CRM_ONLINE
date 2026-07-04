import { useMutation, useQueryClient } from '@tanstack/react-query';
import { campaignService } from '../services/campaignService';

/** Xóa Chiến dịch — invalidate danh sách sau khi thành công. */
export function useDeleteCampaign() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (id: number) => campaignService.remove(id),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['campaigns'] }),
    });
}
