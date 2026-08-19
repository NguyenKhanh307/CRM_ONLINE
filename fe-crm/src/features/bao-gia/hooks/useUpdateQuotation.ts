import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { quotationService } from '../services/quotationService';
import type { UpdateQuotationPayload } from '../types/quotationTypes';

// cập nhật báo giá — báo danh sách VÀ trang chi tiết đang mở làm mới sau khi thành công
export function useUpdateQuotation() {
    const { mutate: run, isPending } = useLiveMutation(
        ({ id, payload }: { id: number; payload: UpdateQuotationPayload }) => quotationService.update(id, payload));

    const mutate: typeof run = (input, callbacks) =>
        run(input, {
            ...callbacks,
            onSuccess: (data) => { notify('quotations'); notify(`quotation:${input.id}`); callbacks?.onSuccess?.(data); },
        });

    return { mutate, isPending };
}
