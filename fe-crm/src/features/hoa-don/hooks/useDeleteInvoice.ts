import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { invoiceService } from '../services/invoiceService';

// xóa Hóa đơn — báo danh sách làm mới sau khi thành công
export function useDeleteInvoice() {
    const { mutate: run, isPending } = useLiveMutation((id: number) => invoiceService.remove(id));

    const mutate: typeof run = (id, callbacks) =>
        run(id, { ...callbacks, onSuccess: (data) => { notify('invoices'); callbacks?.onSuccess?.(data); } });

    return { mutate, isPending };
}
