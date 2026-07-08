import { useMutation, useQueryClient } from '@tanstack/react-query';
import { quotationService } from '../services/quotationService';
import type { SendQuotationPayload } from '../types/quotationTypes';

/** Loại hành động chuyển trạng thái báo giá. */
export type QuotationAction = 'submit' | 'approve' | 'reject' | 'send' | 'accept' | 'setPrimary' | 'convertToOrder';

interface ActionArgs {
    id: number;
    action: QuotationAction;
    comment?: string;
    /** Tiêu đề/nội dung email tùy biến — chỉ dùng cho action 'send'. */
    emailPayload?: SendQuotationPayload;
}

/**
 * Hook thực hiện các hành động chuyển trạng thái báo giá:
 * submit / approve / reject / send / accept (khách chấp nhận) /
 * setPrimary (đặt đồng bộ) / convertToOrder (chuyển thành đơn hàng).
 */
export function useQuotationWorkflow() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: ({ id, action, comment, emailPayload }: ActionArgs) => {
            switch (action) {
                case 'submit': return quotationService.submit(id);
                case 'approve': return quotationService.approve(id, comment);
                case 'reject': return quotationService.reject(id, comment);
                case 'send': return quotationService.send(id, emailPayload);
                case 'accept': return quotationService.accept(id);
                case 'setPrimary': return quotationService.setPrimary(id);
                case 'convertToOrder': return quotationService.convertToOrder(id);
            }
        },
        // convertToOrder tạo đơn hàng + chuyển cơ hội sang won; setPrimary đồng bộ cơ hội
        onSuccess: () => {
            ['quotations', 'invoices', 'orders', 'opportunities'].forEach(
                (key) => qc.invalidateQueries({ queryKey: [key] }));
        },
    });
}
