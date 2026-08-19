import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { campaignService } from '../services/campaignService';
import type { UpdateCampaignPayload } from '../types/campaignTypes';

// cập nhật Chiến dịch — báo danh sách VÀ trang chi tiết đang mở làm mới sau khi thành công
export function useUpdateCampaign() {
    const { mutate: run, isPending } = useLiveMutation(
        ({ id, payload }: { id: number; payload: UpdateCampaignPayload }) => campaignService.update(id, payload));

    const mutate: typeof run = (input, callbacks) =>
        run(input, {
            ...callbacks,
            onSuccess: (data) => { notify('campaigns'); notify(`campaign:${input.id}`); callbacks?.onSuccess?.(data); },
        });

    return { mutate, isPending };
}
