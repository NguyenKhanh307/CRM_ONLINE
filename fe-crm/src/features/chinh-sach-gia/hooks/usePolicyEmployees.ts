import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { pricingService } from '../services/pricingService';
import type { CreatePricePolicyEmployeePayload } from '../types/pricingTypes';

// lấy danh sách nhân viên áp dụng chính sách
export function usePolicyEmployees(policyId: number) {
    return useQuery({
        queryKey: ['price-policy-employees', policyId],
        queryFn: () => pricingService.getEmployees(policyId).then(r => r.data.data),
        enabled: policyId > 0,
    });
}

// tạo mới nhân viên áp dụng chính sách — invalidate danh sách sau khi thành công
export function useCreatePolicyEmployee(policyId: number) {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (payload: CreatePricePolicyEmployeePayload) =>
            pricingService.createEmployee(policyId, payload),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['price-policy-employees', policyId] }),
    });
}

// xóa nhân viên khỏi chính sách — invalidate danh sách sau khi thành công
export function useDeletePolicyEmployee(policyId: number) {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (id: number) => pricingService.removeEmployee(policyId, id),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['price-policy-employees', policyId] }),
    });
}
