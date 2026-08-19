import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { quotationService } from '../services/quotationService';

// xóa báo giá — báo danh sách làm mới sau khi thành công
export function useDeleteQuotation() {
    const { mutate: run, isPending } = useLiveMutation((id: number) => quotationService.remove(id));

    const mutate: typeof run = (id, callbacks) =>
        run(id, { ...callbacks, onSuccess: (data) => { notify('quotations'); callbacks?.onSuccess?.(data); } });

    return { mutate, isPending };
}
