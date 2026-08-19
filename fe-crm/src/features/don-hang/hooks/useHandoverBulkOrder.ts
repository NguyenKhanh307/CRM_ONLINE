import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { orderService } from '../services/orderService';

// bàn giao hàng loạt Đơn hàng cho nhân viên khác — báo danh sách làm mới sau khi thành công
export function useHandoverBulkOrder() {
    const { mutate: run, isPending } = useLiveMutation(
        (payload: { ids: number[]; toUserId: number; reason?: string }) => orderService.handoverBulk(payload));

    const mutate: typeof run = (payload, callbacks) =>
        run(payload, { ...callbacks, onSuccess: (data) => { notify('orders'); callbacks?.onSuccess?.(data); } });

    return { mutate, isPending };
}
