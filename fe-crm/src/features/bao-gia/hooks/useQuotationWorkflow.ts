import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { quotationService } from '../services/quotationService';
import type { SendQuotationPayload } from '../types/quotationTypes';

// loại hành động chuyển trạng thái báo giá
export type QuotationAction = 'submit' | 'approve' | 'reject' | 'send' | 'markSent' | 'accept' | 'reopen' | 'setPrimary';

interface ActionArgs {
    id: number;
    action: QuotationAction;
    comment?: string;
    // tiêu đề/nội dung email tùy biến — chỉ dùng cho action 'send'
    emailPayload?: SendQuotationPayload;
}

// hook thực hiện các hành động chuyển trạng thái báo giá: submit / approve / reject / send /
// accept (khách chấp nhận) / setPrimary (đặt đồng bộ). Chuyển thành đơn hàng không còn ở đây —
// xem OrderAddPage?fromQuotation= (tạo đơn) + quotationService.markConverted (khóa nguồn)
export function useQuotationWorkflow() {
    const { mutate: run, isPending } = useLiveMutation(({ id, action, comment, emailPayload }: ActionArgs) => {
        switch (action) {
            case 'submit': return quotationService.submit(id);
            case 'approve': return quotationService.approve(id, comment);
            case 'reject': return quotationService.reject(id, comment);
            case 'send': return quotationService.send(id, emailPayload);
            case 'markSent': return quotationService.markSent(id);
            case 'accept': return quotationService.accept(id);
            case 'reopen': return quotationService.reopen(id);
            case 'setPrimary': return quotationService.setPrimary(id);
        }
    });

    // setPrimary đồng bộ dòng hàng cơ hội — báo cả 4 module cùng làm mới, khớp bản cũ
    const mutate: typeof run = (input, callbacks) =>
        run(input, {
            ...callbacks,
            onSuccess: (data) => {
                notify('quotations');
                notify('invoices');
                notify('orders');
                notify('opportunities');
                notify(`quotation:${input.id}`);
                callbacks?.onSuccess?.(data);
            },
        });

    return { mutate, isPending };
}
