import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { pricingService } from '../services/pricingService';
import type { CreatePricePolicyCustomerPayload } from '../types/pricingTypes';

/** Lấy danh sách khách hàng của chính sách. */
export function usePolicyCustomers(policyId: number) {
    return useQuery({
        queryKey: ['price-policy-customers', policyId],
        queryFn: () => pricingService.getCustomers(policyId).then(r => r.data.data),
        enabled: policyId > 0,
    });
}

/** Tạo mới khách hàng của chính sách — invalidate danh sách sau khi thành công. */
export function useCreatePolicyCustomer(policyId: number) {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (payload: CreatePricePolicyCustomerPayload) =>
            pricingService.createCustomer(policyId, payload),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['price-policy-customers', policyId] }),
    });
}

/** Xóa khách hàng của chính sách — invalidate danh sách sau khi thành công. */
export function useDeletePolicyCustomer(policyId: number) {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (id: number) => pricingService.removeCustomer(policyId, id),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['price-policy-customers', policyId] }),
    });
}
