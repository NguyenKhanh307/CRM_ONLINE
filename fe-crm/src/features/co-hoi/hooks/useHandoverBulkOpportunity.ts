import { useMutation, useQueryClient } from '@tanstack/react-query';
import { opportunityService } from '../services/opportunityService';

export function useHandoverBulkOpportunity() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (payload: { ids: number[]; toUserId: number; reason?: string }) =>
            opportunityService.handoverBulk(payload),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['opportunities'] }),
    });
}
