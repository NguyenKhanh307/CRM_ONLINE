import { useMutation, useQueryClient } from '@tanstack/react-query';
import { invoiceService } from '../services/invoiceService';

// xóa Hóa đơn — invalidate danh sách sau khi thành công
export function useDeleteInvoice() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (id: number) => invoiceService.remove(id),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['invoices'] }),
    });
}
