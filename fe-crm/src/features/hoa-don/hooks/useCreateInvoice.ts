import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { invoiceService } from '../services/invoiceService';
import type { CreateInvoicePayload } from '../types/invoiceTypes';

// tạo mới Hóa đơn — báo danh sách làm mới sau khi thành công
export function useCreateInvoice() {
    const { mutate: run, isPending } = useLiveMutation((payload: CreateInvoicePayload) => invoiceService.create(payload));

    const mutate: typeof run = (payload, callbacks) =>
        run(payload, { ...callbacks, onSuccess: (data) => { notify('invoices'); callbacks?.onSuccess?.(data); } });

    return { mutate, isPending };
}
