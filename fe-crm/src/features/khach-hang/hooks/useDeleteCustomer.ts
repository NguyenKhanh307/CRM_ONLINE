import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { customerService } from '../services/customerService';

// xóa khách hàng — báo danh sách làm mới sau khi thành công
export function useDeleteCustomer() {
    const { mutate: run, isPending } = useLiveMutation((id: number) => customerService.remove(id));

    const mutate: typeof run = (id, callbacks) =>
        run(id, { ...callbacks, onSuccess: (data) => { notify('customers'); callbacks?.onSuccess?.(data); } });

    return { mutate, isPending };
}
