import { useMutation, useQueryClient } from '@tanstack/react-query';
import { customerService } from '../services/customerService';

// bàn giao hàng loạt khách hàng cho nhân viên khác
export function useHandoverBulkCustomer() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (payload: { ids: number[]; toUserId: number; reason?: string }) =>
            customerService.handoverBulk(payload),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['customers'] }),
    });
}
