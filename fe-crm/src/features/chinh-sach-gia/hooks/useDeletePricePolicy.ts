import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { pricingService } from '../services/pricingService';

// xóa chính sách giá — báo danh sách làm mới sau khi thành công
export function useDeletePricePolicy() {
    const { mutate: run, isPending } = useLiveMutation((id: number) => pricingService.remove(id));

    const mutate: typeof run = (id, callbacks) =>
        run(id, { ...callbacks, onSuccess: (data) => { notify('price-policies'); callbacks?.onSuccess?.(data); } });

    return { mutate, isPending };
}
