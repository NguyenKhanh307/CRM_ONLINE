import { useMutation, useQueryClient } from '@tanstack/react-query';
import { leadService } from '../services/leadService';

/** Loại hành động chuyển trạng thái tiềm năng. */
export type LeadAction = 'qualify' | 'convert' | 'lose';

/**
 * Hook thực hiện hành động chuyển trạng thái tiềm năng:
 * convert (chuyển đổi) / lose (đánh mất, kèm lý do).
 */
export function useLeadWorkflow() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: ({ id, action, reason, customerId }:
                     { id: number; action: LeadAction; reason?: string; customerId?: number | null }) => {
            switch (action) {
                case 'qualify': return leadService.qualify(id);
                case 'convert': return leadService.convert(id, customerId);
                case 'lose': return leadService.lose(id, reason);
            }
        },
        // convert tạo Khách hàng + Liên hệ + Cơ hội → làm mới cả các danh sách đó
        onSuccess: () => {
            ['leads', 'customers', 'contacts', 'opportunities'].forEach(
                (key) => qc.invalidateQueries({ queryKey: [key] }));
        },
    });
}
