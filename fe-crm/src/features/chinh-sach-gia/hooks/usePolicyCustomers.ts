import { useLiveQuery } from '@/core/data/useLiveQuery';
import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { pricingService } from '../services/pricingService';
import type { CreatePricePolicyCustomerPayload } from '../types/pricingTypes';

// lấy danh sách khách hàng của chính sách
export function usePolicyCustomers(policyId: number) {
    return useLiveQuery(`price-policy-customers:${policyId}`,
        () => pricingService.getCustomers(policyId).then(r => r.data.data), policyId > 0);
}

// tạo mới khách hàng của chính sách — báo danh sách khách hàng của chính sách làm mới sau khi thành công
export function useCreatePolicyCustomer(policyId: number) {
    const { mutate: run, isPending } = useLiveMutation(
        (payload: CreatePricePolicyCustomerPayload) => pricingService.createCustomer(policyId, payload));

    const mutate: typeof run = (payload, callbacks) =>
        run(payload, { ...callbacks, onSuccess: (data) => { notify(`price-policy-customers:${policyId}`); callbacks?.onSuccess?.(data); } });

    return { mutate, isPending };
}

// xóa khách hàng của chính sách — báo danh sách khách hàng của chính sách làm mới sau khi thành công
export function useDeletePolicyCustomer(policyId: number) {
    const { mutate: run, isPending } = useLiveMutation((id: number) => pricingService.removeCustomer(policyId, id));

    const mutate: typeof run = (id, callbacks) =>
        run(id, { ...callbacks, onSuccess: (data) => { notify(`price-policy-customers:${policyId}`); callbacks?.onSuccess?.(data); } });

    return { mutate, isPending };
}
