import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { quotationService } from '../services/quotationService';

// bàn giao hàng loạt báo giá cho nhân viên khác — báo danh sách làm mới sau khi thành công
export function useHandoverBulkQuotation() {
    const { mutate: run, isPending } = useLiveMutation(
        (payload: { ids: number[]; toUserId: number; reason?: string }) => quotationService.handoverBulk(payload));

    const mutate: typeof run = (payload, callbacks) =>
        run(payload, { ...callbacks, onSuccess: (data) => { notify('quotations'); callbacks?.onSuccess?.(data); } });

    return { mutate, isPending };
}
