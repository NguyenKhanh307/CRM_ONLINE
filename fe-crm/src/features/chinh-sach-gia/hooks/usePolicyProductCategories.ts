import { useLiveQuery } from '@/core/data/useLiveQuery';
import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { pricingService } from '../services/pricingService';
import type { CreatePricePolicyProductCategoryPayload } from '../types/pricingTypes';

// lấy danh sách danh mục sản phẩm của chính sách
export function usePolicyProductCategories(policyId: number) {
    return useLiveQuery(`price-policy-product-categories:${policyId}`,
        () => pricingService.getProductCategories(policyId).then(r => r.data.data), policyId > 0);
}

// thêm danh mục sản phẩm vào chính sách — be tự bulk-seed sản phẩm thuộc danh mục vào
// price_policy_products, nên phải báo CẢ danh sách danh mục lẫn danh sách sản phẩm làm mới
// (tab "Sản phẩm" mới thấy các dòng vừa được thêm mà không cần f5 thủ công)
export function useCreatePolicyProductCategory(policyId: number) {
    const { mutate: run, isPending } = useLiveMutation(
        (payload: CreatePricePolicyProductCategoryPayload) => pricingService.createProductCategory(policyId, payload));

    const mutate: typeof run = (payload, callbacks) =>
        run(payload, {
            ...callbacks,
            onSuccess: (data) => {
                notify(`price-policy-product-categories:${policyId}`);
                notify(`price-policy-products:${policyId}`);
                callbacks?.onSuccess?.(data);
            },
        });

    return { mutate, isPending };
}

// xóa danh mục sản phẩm của chính sách (chỉ xóa marker, giữ nguyên các dòng sản phẩm đã bulk-seed)
export function useDeletePolicyProductCategory(policyId: number) {
    const { mutate: run, isPending } = useLiveMutation((id: number) => pricingService.removeProductCategory(policyId, id));

    const mutate: typeof run = (id, callbacks) =>
        run(id, { ...callbacks, onSuccess: (data) => { notify(`price-policy-product-categories:${policyId}`); callbacks?.onSuccess?.(data); } });

    return { mutate, isPending };
}
