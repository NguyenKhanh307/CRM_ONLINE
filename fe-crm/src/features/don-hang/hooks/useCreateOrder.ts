import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { orderService } from '../services/orderService';
import type { CreateOrderPayload } from '../types/orderTypes';

// tạo mới Đơn hàng — báo danh sách làm mới sau khi thành công
export function useCreateOrder() {
    const { mutate: run, isPending } = useLiveMutation((payload: CreateOrderPayload) => orderService.create(payload));

    const mutate: typeof run = (payload, callbacks) =>
        run(payload, { ...callbacks, onSuccess: (data) => { notify('orders'); callbacks?.onSuccess?.(data); } });

    return { mutate, isPending };
}
