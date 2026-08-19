import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { campaignService } from '../services/campaignService';

// loại hành động chuyển trạng thái Chiến dịch
export type CampaignAction = 'schedule' | 'start' | 'pause' | 'complete' | 'cancel';

interface WorkflowInput {
    id: number;
    action: CampaignAction;
}

// hook thực hiện hành động chuyển trạng thái Chiến dịch: schedule / start / pause / complete / cancel
export function useCampaignWorkflow() {
    const { mutate: run, isPending } = useLiveMutation(
        ({ id, action }: WorkflowInput) => campaignService[action](id));

    const mutate: typeof run = (input, callbacks) =>
        run(input, {
            ...callbacks,
            onSuccess: (data) => {
                notify('campaigns');
                notify(`campaign:${input.id}`);
                callbacks?.onSuccess?.(data);
            },
        });

    return { mutate, isPending };
}
