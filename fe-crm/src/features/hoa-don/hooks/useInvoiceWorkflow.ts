import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { invoiceService } from '../services/invoiceService';

// loại hành động chuyển trạng thái Hóa đơn
export type InvoiceAction = 'issue' | 'cancel' | 'reopen';

// hook thực hiện hành động chuyển trạng thái Hóa đơn: issue (phát hành) / cancel (hủy) / reopen (mở lại)
export function useInvoiceWorkflow() {
    const { mutate: run, isPending } = useLiveMutation(
        ({ id, action }: { id: number; action: InvoiceAction }) => invoiceService[action](id));

    const mutate: typeof run = (input, callbacks) =>
        run(input, {
            ...callbacks,
            onSuccess: (data) => { notify('invoices'); notify(`invoice:${input.id}`); callbacks?.onSuccess?.(data); },
        });

    return { mutate, isPending };
}
