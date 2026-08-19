import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { invoiceService } from '../services/invoiceService';

// bàn giao hàng loạt Hóa đơn cho nhân viên khác — báo danh sách làm mới sau khi thành công
export function useHandoverBulkInvoice() {
    const { mutate: run, isPending } = useLiveMutation(
        (payload: { ids: number[]; toUserId: number; reason?: string }) => invoiceService.handoverBulk(payload));

    const mutate: typeof run = (payload, callbacks) =>
        run(payload, { ...callbacks, onSuccess: (data) => { notify('invoices'); callbacks?.onSuccess?.(data); } });

    return { mutate, isPending };
}
