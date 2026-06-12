import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { pricingService } from '../services/pricingService';
import type { CreatePricePolicyCustomerCategoryPayload } from '../types/pricingTypes';

export function usePolicyCustomerCategories(policyId: number) {
    return useQuery({
        queryKey: ['price-policy-customer-categories', policyId],
        queryFn: () => pricingService.getCustomerCategories(policyId).then(r => r.data.data),
        enabled: policyId > 0,
    });
}

export function useCreatePolicyCustomerCategory(policyId: number) {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (payload: CreatePricePolicyCustomerCategoryPayload) =>
            pricingService.createCustomerCategory(policyId, payload),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['price-policy-customer-categories', policyId] }),
    });
}

export function useDeletePolicyCustomerCategory(policyId: number) {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (id: number) => pricingService.removeCustomerCategory(policyId, id),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['price-policy-customer-categories', policyId] }),
    });
}
