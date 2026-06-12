import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { pricingService } from '../services/pricingService';
import type { CreatePricePolicyProductPayload, UpdatePricePolicyProductPayload } from '../types/pricingTypes';

export function usePolicyProducts(policyId: number) {
    return useQuery({
        queryKey: ['price-policy-products', policyId],
        queryFn: () => pricingService.getProducts(policyId).then(r => r.data.data),
        enabled: policyId > 0,
    });
}

export function useCreatePolicyProduct(policyId: number) {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (payload: CreatePricePolicyProductPayload) =>
            pricingService.createProduct(policyId, payload),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['price-policy-products', policyId] }),
    });
}

export function useUpdatePolicyProduct(policyId: number) {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: ({ id, payload }: { id: number; payload: UpdatePricePolicyProductPayload }) =>
            pricingService.updateProduct(policyId, id, payload),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['price-policy-products', policyId] }),
    });
}

export function useDeletePolicyProduct(policyId: number) {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (id: number) => pricingService.removeProduct(policyId, id),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['price-policy-products', policyId] }),
    });
}
