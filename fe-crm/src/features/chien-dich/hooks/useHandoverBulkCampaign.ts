import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { campaignService } from '../services/campaignService';

// bàn giao hàng loạt Chiến dịch cho nhân viên khác — báo danh sách làm mới sau khi thành công
export function useHandoverBulkCampaign() {
    const { mutate: run, isPending } = useLiveMutation(
        (payload: { ids: number[]; toUserId: number; reason?: string }) => campaignService.handoverBulk(payload));

    const mutate: typeof run = (payload, callbacks) =>
        run(payload, { ...callbacks, onSuccess: (data) => { notify('campaigns'); callbacks?.onSuccess?.(data); } });

    return { mutate, isPending };
}
