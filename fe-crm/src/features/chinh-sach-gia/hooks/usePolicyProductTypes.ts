import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { pricingService } from '../services/pricingService';
import type { CreatePricePolicyProductTypePayload } from '../types/pricingTypes';

export function usePolicyProductTypes(policyId: number) {
    return useQuery({
        queryKey: ['price-policy-product-types', policyId],
        queryFn: () => pricingService.getProductTypes(policyId).then(r => r.data.data),
        enabled: policyId > 0,
    });
}

export function useCreatePolicyProductType(policyId: number) {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (payload: CreatePricePolicyProductTypePayload) =>
            pricingService.createProductType(policyId, payload),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['price-policy-product-types', policyId] }),
    });
}

export function useDeletePolicyProductType(policyId: number) {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (id: number) => pricingService.removeProductType(policyId, id),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['price-policy-product-types', policyId] }),
    });
}
