import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { quotationService } from '../services/quotationService';
import type { CreateQuotationPayload } from '../types/quotationTypes';

// tạo mới báo giá — báo danh sách làm mới sau khi thành công
export function useCreateQuotation() {
    const { mutate: run, isPending } = useLiveMutation((payload: CreateQuotationPayload) => quotationService.create(payload));

    const mutate: typeof run = (payload, callbacks) =>
        run(payload, { ...callbacks, onSuccess: (data) => { notify('quotations'); callbacks?.onSuccess?.(data); } });

    return { mutate, isPending };
}
