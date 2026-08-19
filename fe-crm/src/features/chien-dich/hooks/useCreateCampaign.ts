import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { campaignService } from '../services/campaignService';
import type { CreateCampaignPayload } from '../types/campaignTypes';

// tạo mới Chiến dịch — báo danh sách làm mới sau khi thành công
export function useCreateCampaign() {
    const { mutate: run, isPending } = useLiveMutation((payload: CreateCampaignPayload) => campaignService.create(payload));

    const mutate: typeof run = (payload, callbacks) =>
        run(payload, { ...callbacks, onSuccess: (data) => { notify('campaigns'); callbacks?.onSuccess?.(data); } });

    return { mutate, isPending };
}
