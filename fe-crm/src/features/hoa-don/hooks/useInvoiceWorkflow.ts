import { useMutation, useQueryClient } from '@tanstack/react-query';
import { invoiceService } from '../services/invoiceService';

/** Loại hành động chuyển trạng thái Hóa đơn. */
export type InvoiceAction = 'issue' | 'cancel';

/**
 * Hook thực hiện hành động chuyển trạng thái Hóa đơn:
 * issue (phát hành) / cancel (hủy).
 */
export function useInvoiceWorkflow() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: ({ id, action }: { id: number; action: InvoiceAction }) => invoiceService[action](id),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['invoices'] }),
    });
}
