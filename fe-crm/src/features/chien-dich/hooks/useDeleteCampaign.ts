import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { campaignService } from '../services/campaignService';

// xóa Chiến dịch — báo danh sách làm mới sau khi thành công
export function useDeleteCampaign() {
    const { mutate: run, isPending } = useLiveMutation((id: number) => campaignService.remove(id));

    const mutate: typeof run = (id, callbacks) =>
        run(id, { ...callbacks, onSuccess: (data) => { notify('campaigns'); callbacks?.onSuccess?.(data); } });

    return { mutate, isPending };
}
