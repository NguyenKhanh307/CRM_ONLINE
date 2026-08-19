import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { invoiceService } from '../services/invoiceService';
import type { UpdateInvoicePayload } from '../types/invoiceTypes';

// cập nhật Hóa đơn — báo danh sách VÀ trang chi tiết đang mở làm mới sau khi thành công
export function useUpdateInvoice() {
    const { mutate: run, isPending } = useLiveMutation(
        ({ id, payload }: { id: number; payload: UpdateInvoicePayload }) => invoiceService.update(id, payload));

    const mutate: typeof run = (input, callbacks) =>
        run(input, {
            ...callbacks,
            onSuccess: (data) => { notify('invoices'); notify(`invoice:${input.id}`); callbacks?.onSuccess?.(data); },
        });

    return { mutate, isPending };
}
