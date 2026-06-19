import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { pricingService } from '../services/pricingService';
import type { CreatePricePolicyProductPayload, UpdatePricePolicyProductPayload } from '../types/pricingTypes';

/** Lấy danh sách sản phẩm của chính sách. */
export function usePolicyProducts(policyId: number) {
    return useQuery({
        queryKey: ['price-policy-products', policyId],
        queryFn: () => pricingService.getProducts(policyId).then(r => r.data.data),
        enabled: policyId > 0,
    });
}

/** Tạo mới sản phẩm của chính sách — invalidate danh sách sau khi thành công. */
export function useCreatePolicyProduct(policyId: number) {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (payload: CreatePricePolicyProductPayload) =>
            pricingService.createProduct(policyId, payload),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['price-policy-products', policyId] }),
    });
}

/** Cập nhật sản phẩm của chính sách — invalidate danh sách sau khi thành công. */
export function useUpdatePolicyProduct(policyId: number) {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: ({ id, payload }: { id: number; payload: UpdatePricePolicyProductPayload }) =>
            pricingService.updateProduct(policyId, id, payload),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['price-policy-products', policyId] }),
    });
}

/** Xóa sản phẩm của chính sách — invalidate danh sách sau khi thành công. */
export function useDeletePolicyProduct(policyId: number) {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (id: number) => pricingService.removeProduct(policyId, id),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['price-policy-products', policyId] }),
    });
}
