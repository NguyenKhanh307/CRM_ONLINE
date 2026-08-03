import { useMutation, useQueryClient } from '@tanstack/react-query';
import { pricingService } from '../services/pricingService';
import type { UpdatePricePolicyPayload } from '../types/pricingTypes';

// cập nhật chính sách giá — invalidate danh sách sau khi thành công
export function useUpdatePricePolicy() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: ({ id, payload }: { id: number; payload: UpdatePricePolicyPayload }) =>
            pricingService.update(id, payload),
        onSuccess: (_data, { id }) => {
            qc.invalidateQueries({ queryKey: ['price-policies'] });
            qc.invalidateQueries({ queryKey: ['price-policies', id] });
        },
    });
}
