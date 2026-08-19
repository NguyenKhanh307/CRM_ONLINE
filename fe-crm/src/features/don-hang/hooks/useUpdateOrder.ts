import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { orderService } from '../services/orderService';
import type { UpdateOrderPayload } from '../types/orderTypes';

// cập nhật Đơn hàng — báo danh sách VÀ trang chi tiết đang mở làm mới sau khi thành công
export function useUpdateOrder() {
    const { mutate: run, isPending } = useLiveMutation(
        ({ id, payload }: { id: number; payload: UpdateOrderPayload }) => orderService.update(id, payload));

    const mutate: typeof run = (input, callbacks) =>
        run(input, {
            ...callbacks,
            onSuccess: (data) => { notify('orders'); notify(`order:${input.id}`); callbacks?.onSuccess?.(data); },
        });

    return { mutate, isPending };
}
