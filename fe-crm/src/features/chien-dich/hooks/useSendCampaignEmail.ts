import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { campaignService } from '../services/campaignService';
import type { SendCampaignEmailPayload } from '../types/campaignTypes';

// gửi email hàng loạt cho thành viên Chiến dịch — trả về số email đã gửi
export function useSendCampaignEmail(campaignId: number) {
    const { mutate: run, isPending } = useLiveMutation(
        (payload: SendCampaignEmailPayload) => campaignService.sendEmail(campaignId, payload).then(r => r.data.data));

    const mutate: typeof run = (payload, callbacks) =>
        run(payload, {
            ...callbacks,
            onSuccess: (data) => {
                notify(`campaign-members:${campaignId}`);
                notify(`campaign-stats:${campaignId}`);
                callbacks?.onSuccess?.(data);
            },
        });

    return { mutate, isPending };
}
