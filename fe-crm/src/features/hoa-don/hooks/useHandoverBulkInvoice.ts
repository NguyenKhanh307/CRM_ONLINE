import { useMutation, useQueryClient } from '@tanstack/react-query';
import { invoiceService } from '../services/invoiceService';

/** Bàn giao hàng loạt Hóa đơn cho nhân viên khác. */
export function useHandoverBulkInvoice() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (payload: { ids: number[]; toUserId: number; reason?: string }) =>
            invoiceService.handoverBulk(payload),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['invoices'] }),
    });
}
