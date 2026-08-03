import { useMutation, useQueryClient } from '@tanstack/react-query';
import { customerService } from '../services/customerService';
import type { CreateCustomerPayload } from '../types/customerTypes';

// tạo mới khách hàng — invalidate danh sách sau khi thành công
export function useCreateCustomer() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (payload: CreateCustomerPayload) => customerService.create(payload),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['customers'] }),
    });
}
