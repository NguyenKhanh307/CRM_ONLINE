import { useLiveQuery } from '@/core/data/useLiveQuery';
import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { pricingService } from '../services/pricingService';
import type { CreatePricePolicyProductPayload, UpdatePricePolicyProductPayload } from '../types/pricingTypes';

// lấy danh sách sản phẩm của chính sách
export function usePolicyProducts(policyId: number) {
    return useLiveQuery(`price-policy-products:${policyId}`,
        () => pricingService.getProducts(policyId).then(r => r.data.data), policyId > 0);
}

// tạo mới sản phẩm của chính sách — báo danh sách sản phẩm của chính sách làm mới sau khi thành công
export function useCreatePolicyProduct(policyId: number) {
    const { mutate: run, isPending } = useLiveMutation(
        (payload: CreatePricePolicyProductPayload) => pricingService.createProduct(policyId, payload));

    const mutate: typeof run = (payload, callbacks) =>
        run(payload, { ...callbacks, onSuccess: (data) => { notify(`price-policy-products:${policyId}`); callbacks?.onSuccess?.(data); } });

    return { mutate, isPending };
}

// cập nhật sản phẩm của chính sách — báo danh sách sản phẩm của chính sách làm mới sau khi thành công
export function useUpdatePolicyProduct(policyId: number) {
    const { mutate: run, isPending } = useLiveMutation(
        ({ id, payload }: { id: number; payload: UpdatePricePolicyProductPayload }) =>
            pricingService.updateProduct(policyId, id, payload));

    const mutate: typeof run = (input, callbacks) =>
        run(input, { ...callbacks, onSuccess: (data) => { notify(`price-policy-products:${policyId}`); callbacks?.onSuccess?.(data); } });

    return { mutate, isPending };
}

// xóa sản phẩm của chính sách — báo danh sách sản phẩm của chính sách làm mới sau khi thành công
export function useDeletePolicyProduct(policyId: number) {
    const { mutate: run, isPending } = useLiveMutation((id: number) => pricingService.removeProduct(policyId, id));

    const mutate: typeof run = (id, callbacks) =>
        run(id, { ...callbacks, onSuccess: (data) => { notify(`price-policy-products:${policyId}`); callbacks?.onSuccess?.(data); } });

    return { mutate, isPending };
}
