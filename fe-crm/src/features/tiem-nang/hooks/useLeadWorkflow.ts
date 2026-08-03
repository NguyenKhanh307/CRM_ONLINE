import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { leadService } from '../services/leadService';

// loại hành động chuyển trạng thái tiềm năng
export type LeadAction = 'qualify' | 'convert' | 'lose' | 'claim';

interface WorkflowInput {
    id: number;
    action: LeadAction;
    reason?: string;
    customerId?: number | null;
}

// chạy hành động chuyển trạng thái tiềm năng: convert (chuyển đổi) / lose (đánh mất, kèm lý do)
// / claim (nhân viên tự nhận chăm sóc) / qualify (đủ điều kiện thủ công)
export function useLeadWorkflow() {
    const { mutate: run, isPending } = useLiveMutation((input: WorkflowInput) => {
        // bước gọi đúng api theo hành động
        switch (input.action) {
            case 'qualify': return leadService.qualify(input.id);
            case 'convert': return leadService.convert(input.id, input.customerId);
            case 'lose': return leadService.lose(input.id, input.reason);
            case 'claim': return leadService.claim(input.id);
        }
    });

    const mutate: typeof run = (input, callbacks) =>
        run(input, {
            ...callbacks,
            onSuccess: (data) => {
                // convert tạo Khách hàng + Liên hệ + Cơ hội -> làm mới cả các danh sách đó
                notify('leads'); notify('customers'); notify('contacts'); notify('opportunities');
                notify(`lead:${input.id}`);
                callbacks?.onSuccess?.(data);
            },
        });

    return { mutate, isPending };
}
