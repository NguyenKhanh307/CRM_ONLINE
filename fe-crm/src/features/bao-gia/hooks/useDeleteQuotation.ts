import { useMutation, useQueryClient } from '@tanstack/react-query';
import { quotationService } from '../services/quotationService';

export function useDeleteQuotation() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (id: number) => quotationService.remove(id),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['quotations'] }),
    });
}
