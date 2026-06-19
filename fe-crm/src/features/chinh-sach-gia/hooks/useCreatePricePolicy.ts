import { useMutation, useQueryClient } from '@tanstack/react-query';
import { pricingService } from '../services/pricingService';
import type { CreatePricePolicyPayload } from '../types/pricingTypes';

/** Tạo mới chính sách giá — invalidate danh sách sau khi thành công. */
export function useCreatePricePolicy() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (payload: CreatePricePolicyPayload) => pricingService.create(payload),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['price-policies'] }),
    });
}
