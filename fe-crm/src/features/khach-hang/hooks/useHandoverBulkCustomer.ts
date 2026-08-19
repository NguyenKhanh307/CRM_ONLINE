import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { customerService } from '../services/customerService';

// bàn giao hàng loạt khách hàng cho nhân viên khác — báo danh sách làm mới sau khi thành công
export function useHandoverBulkCustomer() {
    const { mutate: run, isPending } = useLiveMutation(
        (payload: { ids: number[]; toUserId: number; reason?: string }) => customerService.handoverBulk(payload));

    const mutate: typeof run = (payload, callbacks) =>
        run(payload, { ...callbacks, onSuccess: (data) => { notify('customers'); callbacks?.onSuccess?.(data); } });

    return { mutate, isPending };
}
