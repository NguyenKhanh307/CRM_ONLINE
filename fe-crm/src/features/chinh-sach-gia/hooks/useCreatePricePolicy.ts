import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { pricingService } from '../services/pricingService';
import type { CreatePricePolicyPayload } from '../types/pricingTypes';

// tạo mới chính sách giá — báo danh sách làm mới sau khi thành công
export function useCreatePricePolicy() {
    const { mutate: run, isPending } = useLiveMutation((payload: CreatePricePolicyPayload) => pricingService.create(payload));

    const mutate: typeof run = (payload, callbacks) =>
        run(payload, { ...callbacks, onSuccess: (data) => { notify('price-policies'); callbacks?.onSuccess?.(data); } });

    return { mutate, isPending };
}
