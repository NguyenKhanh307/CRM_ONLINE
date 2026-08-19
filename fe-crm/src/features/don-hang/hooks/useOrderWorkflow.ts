import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { orderService } from '../services/orderService';

// loại hành động chuyển trạng thái Đơn hàng
export type OrderAction = 'confirm' | 'process' | 'complete' | 'cancel' | 'reopen';

// hook thực hiện hành động chuyển trạng thái Đơn hàng: confirm/process/complete/cancel/reopen.
// Xuất hóa đơn không còn ở đây — xem InvoiceAddPage?fromOrder= (tạo hóa đơn) +
// orderService.markConverted (khóa nguồn)
export function useOrderWorkflow() {
    const { mutate: run, isPending } = useLiveMutation(
        ({ id, action }: { id: number; action: OrderAction }) => orderService[action](id));

    const mutate: typeof run = (input, callbacks) =>
        run(input, {
            ...callbacks,
            onSuccess: (data) => { notify('orders'); notify(`order:${input.id}`); callbacks?.onSuccess?.(data); },
        });

    return { mutate, isPending };
}
