import { useMutation, useQueryClient } from '@tanstack/react-query';
import { quotationService } from '../services/quotationService';
import type { CreateQuotationPayload } from '../types/quotationTypes';

// tạo mới báo giá — invalidate danh sách sau khi thành công
export function useCreateQuotation() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (payload: CreateQuotationPayload) => quotationService.create(payload),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['quotations'] }),
    });
}
