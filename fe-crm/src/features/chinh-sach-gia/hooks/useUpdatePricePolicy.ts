import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { pricingService } from '../services/pricingService';
import type { UpdatePricePolicyPayload } from '../types/pricingTypes';

// cập nhật chính sách giá — báo danh sách VÀ trang chi tiết đang mở làm mới sau khi thành công
export function useUpdatePricePolicy() {
    const { mutate: run, isPending } = useLiveMutation(
        ({ id, payload }: { id: number; payload: UpdatePricePolicyPayload }) => pricingService.update(id, payload));

    const mutate: typeof run = (input, callbacks) =>
        run(input, {
            ...callbacks,
            onSuccess: (data) => { notify('price-policies'); notify(`price-policy:${input.id}`); callbacks?.onSuccess?.(data); },
        });

    return { mutate, isPending };
}
