import { useMutation, useQueryClient } from '@tanstack/react-query';
import { pricingService } from '../services/pricingService';
import type { CreatePricePolicyPayload } from '../types/pricingTypes';

export function useCreatePricePolicy() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (payload: CreatePricePolicyPayload) => pricingService.create(payload),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['price-policies'] }),
    });
}
