import { useMutation, useQueryClient } from '@tanstack/react-query';
import { quotationService } from '../services/quotationService';

// bàn giao hàng loạt báo giá cho nhân viên khác
export function useHandoverBulkQuotation() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (payload: { ids: number[]; toUserId: number; reason?: string }) =>
            quotationService.handoverBulk(payload),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['quotations'] }),
    });
}
