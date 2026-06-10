import { useMutation, useQueryClient } from '@tanstack/react-query';
import { quotationService } from '../services/quotationService';

export function useHandoverBulkQuotation() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (payload: { ids: number[]; toUserId: number; reason?: string }) =>
            quotationService.handoverBulk(payload),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['quotations'] }),
    });
}
