import { useMutation, useQueryClient } from '@tanstack/react-query';
import { orderService } from '../services/orderService';

/** Bàn giao hàng loạt Đơn hàng cho nhân viên khác. */
export function useHandoverBulkOrder() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (payload: { ids: number[]; toUserId: number; reason?: string }) =>
            orderService.handoverBulk(payload),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['orders'] }),
    });
}
