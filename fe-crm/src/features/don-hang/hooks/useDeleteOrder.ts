import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { orderService } from '../services/orderService';

// xóa Đơn hàng — báo danh sách làm mới sau khi thành công
export function useDeleteOrder() {
    const { mutate: run, isPending } = useLiveMutation((id: number) => orderService.remove(id));

    const mutate: typeof run = (id, callbacks) =>
        run(id, { ...callbacks, onSuccess: (data) => { notify('orders'); callbacks?.onSuccess?.(data); } });

    return { mutate, isPending };
}
