import { useMutation, useQueryClient } from '@tanstack/react-query';
import { leadService } from '../services/leadService';

/** Loại hành động chuyển trạng thái tiềm năng. */
export type LeadAction = 'convert' | 'lose';

/**
 * Hook thực hiện hành động chuyển trạng thái tiềm năng:
 * convert (chuyển đổi) / lose (đánh mất, kèm lý do).
 */
export function useLeadWorkflow() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: ({ id, action, reason }: { id: number; action: LeadAction; reason?: string }) =>
            action === 'convert' ? leadService.convert(id) : leadService.lose(id, reason),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['leads'] }),
    });
}
