import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { leadService } from '../services/leadService';

// bàn giao hàng loạt tiềm năng cho nhân viên khác — báo danh sách làm mới sau khi thành công
export function useHandoverBulkLead() {
    const { mutate: run, isPending } = useLiveMutation(
        (payload: { ids: number[]; toUserId: number; reason?: string }) => leadService.handoverBulk(payload));

    const mutate: typeof run = (payload, callbacks) =>
        run(payload, { ...callbacks, onSuccess: (data) => { notify('leads'); callbacks?.onSuccess?.(data); } });

    return { mutate, isPending };
}
