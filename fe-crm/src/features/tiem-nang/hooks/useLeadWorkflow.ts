import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { leadService } from '../services/leadService';

// loại hành động chuyển trạng thái tiềm năng
export type LeadAction = 'claim';

interface WorkflowInput {
    id: number;
    action: LeadAction;
    reason?: string;
}

// chạy hành động chuyển trạng thái tiềm năng: claim (nhân viên tự nhận chăm sóc)
export function useLeadWorkflow() {
    const { mutate: run, isPending } = useLiveMutation((input: WorkflowInput) => {
        switch (input.action) {
            case 'claim': return leadService.claim(input.id);
        }
    });

    const mutate: typeof run = (input, callbacks) =>
        run(input, {
            ...callbacks,
            onSuccess: (data) => {
                notify('leads');
                notify(`lead:${input.id}`);
                callbacks?.onSuccess?.(data);
            },
        });

    return { mutate, isPending };
}
