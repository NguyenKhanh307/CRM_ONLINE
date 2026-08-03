import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { customerService } from '../services/customerService';
import type { AssignCustomerSharePayload } from '../types/customerTypes';

// lấy danh sách chia sẻ của khách hàng
export function useCustomerShares(customerId: number) {
    return useQuery({
        queryKey: ['customer-shares', customerId],
        queryFn: () => customerService.getShares(customerId).then(r => r.data.data),
        enabled: customerId > 0,
    });
}

// gán chia sẻ mới — invalidate danh sách sau khi thành công
export function useAssignCustomerShare(customerId: number) {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (payload: AssignCustomerSharePayload) => customerService.assignShare(customerId, payload),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['customer-shares', customerId] }),
    });
}

// thu hồi chia sẻ (theo userId) — invalidate danh sách sau khi thành công
export function useRevokeCustomerShare(customerId: number) {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (userId: number) => customerService.revokeShare(customerId, userId),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['customer-shares', customerId] }),
    });
}
