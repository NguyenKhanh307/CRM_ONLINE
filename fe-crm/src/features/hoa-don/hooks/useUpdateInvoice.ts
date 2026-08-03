import { useMutation, useQueryClient } from '@tanstack/react-query';
import { invoiceService } from '../services/invoiceService';
import type { UpdateInvoicePayload } from '../types/invoiceTypes';

// cập nhật Hóa đơn — invalidate danh sách sau khi thành công
export function useUpdateInvoice() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: ({ id, payload }: { id: number; payload: UpdateInvoicePayload }) =>
            invoiceService.update(id, payload),
        onSuccess: (_d, v) => {
            qc.invalidateQueries({ queryKey: ['invoices'] });
            qc.invalidateQueries({ queryKey: ['invoice', v.id] });
        },
    });
}
