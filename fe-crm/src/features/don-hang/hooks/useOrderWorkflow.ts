import { useMutation, useQueryClient } from '@tanstack/react-query';
import { orderService } from '../services/orderService';

/** Loại hành động chuyển trạng thái Đơn hàng + xuất hóa đơn. */
export type OrderAction = 'confirm' | 'process' | 'complete' | 'cancel' | 'createInvoice';

/**
 * Hook thực hiện hành động trên Đơn hàng:
 * confirm / process / complete / cancel (chuyển trạng thái) và createInvoice (xuất hóa đơn 1-1).
 */
export function useOrderWorkflow() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: ({ id, action }: { id: number; action: OrderAction }) => orderService[action](id),
        onSuccess: (_d, v) => {
            qc.invalidateQueries({ queryKey: ['orders'] });
            qc.invalidateQueries({ queryKey: ['order', v.id] });
            qc.invalidateQueries({ queryKey: ['invoices'] });
        },
    });
}
