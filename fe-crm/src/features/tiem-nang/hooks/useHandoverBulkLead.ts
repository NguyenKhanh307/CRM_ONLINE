import { useMutation, useQueryClient } from '@tanstack/react-query';
import { leadService } from '../services/leadService';

export function useHandoverBulkLead() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (payload: { ids: number[]; toUserId: number; reason?: string }) =>
            leadService.handoverBulk(payload),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['leads'] }),
    });
}
