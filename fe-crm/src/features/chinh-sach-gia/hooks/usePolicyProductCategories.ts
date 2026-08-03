import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { pricingService } from '../services/pricingService';
import type { CreatePricePolicyProductCategoryPayload } from '../types/pricingTypes';

// lấy danh sách danh mục sản phẩm của chính sách
export function usePolicyProductCategories(policyId: number) {
    return useQuery({
        queryKey: ['price-policy-product-categories', policyId],
        queryFn: () => pricingService.getProductCategories(policyId).then(r => r.data.data),
        enabled: policyId > 0,
    });
}

// thêm danh mục sản phẩm vào chính sách — be tự bulk-seed sản phẩm thuộc danh mục vào
// price_policy_products, nên phải invalidate CẢ danh sách danh mục lẫn danh sách sản phẩm
// (tab "Sản phẩm" mới thấy các dòng vừa được thêm mà không cần f5 thủ công)
export function useCreatePolicyProductCategory(policyId: number) {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (payload: CreatePricePolicyProductCategoryPayload) =>
            pricingService.createProductCategory(policyId, payload),
        onSuccess: () => {
            qc.invalidateQueries({ queryKey: ['price-policy-product-categories', policyId] });
            qc.invalidateQueries({ queryKey: ['price-policy-products', policyId] });
        },
    });
}

// xóa danh mục sản phẩm của chính sách (chỉ xóa marker, giữ nguyên các dòng sản phẩm đã bulk-seed)
export function useDeletePolicyProductCategory(policyId: number) {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (id: number) => pricingService.removeProductCategory(policyId, id),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['price-policy-product-categories', policyId] }),
    });
}
