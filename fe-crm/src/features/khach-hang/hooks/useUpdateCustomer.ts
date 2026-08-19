import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { customerService } from '../services/customerService';
import type { UpdateCustomerPayload } from '../types/customerTypes';

// cập nhật khách hàng — báo danh sách VÀ trang chi tiết đang mở làm mới sau khi thành công
export function useUpdateCustomer() {
    const { mutate: run, isPending } = useLiveMutation(
        ({ id, payload }: { id: number; payload: UpdateCustomerPayload }) => customerService.update(id, payload));

    const mutate: typeof run = (input, callbacks) =>
        run(input, {
            ...callbacks,
            onSuccess: (data) => { notify('customers'); notify(`customer:${input.id}`); callbacks?.onSuccess?.(data); },
        });

    return { mutate, isPending };
}
