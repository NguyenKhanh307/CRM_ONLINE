import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { opportunityService } from '../services/opportunityService';

// bàn giao hàng loạt cơ hội cho nhân viên khác — báo danh sách làm mới sau khi thành công
export function useHandoverBulkOpportunity() {
    const { mutate: run, isPending } = useLiveMutation(
        (payload: { ids: number[]; toUserId: number; reason?: string }) => opportunityService.handoverBulk(payload));

    const mutate: typeof run = (payload, callbacks) =>
        run(payload, { ...callbacks, onSuccess: (data) => { notify('opportunities'); callbacks?.onSuccess?.(data); } });

    return { mutate, isPending };
}
