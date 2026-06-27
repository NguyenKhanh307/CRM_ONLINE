import { useMutation, useQueryClient } from '@tanstack/react-query';
import { invoiceService } from '../services/invoiceService';
import type { CreateInvoicePayload } from '../types/invoiceTypes';

/** Tạo mới Hóa đơn — invalidate danh sách sau khi thành công. */
export function useCreateInvoice() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (payload: CreateInvoicePayload) => invoiceService.create(payload),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['invoices'] }),
    });
}
