import { useMutation, useQueryClient } from '@tanstack/react-query';
import { quotationService } from '../services/quotationService';
import type { UpdateQuotationPayload } from '../types/quotationTypes';

export function useUpdateQuotation() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: ({ id, payload }: { id: number; payload: UpdateQuotationPayload }) =>
            quotationService.update(id, payload),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['quotations'] }),
    });
}
