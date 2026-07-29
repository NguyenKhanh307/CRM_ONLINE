import { useMutation, useQueryClient } from '@tanstack/react-query';
import { customerService } from '../services/customerService';
import type { UpdateCustomerPayload } from '../types/customerTypes';

/** Cập nhật khách hàng — invalidate danh sách sau khi thành công. */
export function useUpdateCustomer() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: ({ id, payload }: { id: number; payload: UpdateCustomerPayload }) =>
            customerService.update(id, payload),
        onSuccess: (_d, v) => {
            qc.invalidateQueries({ queryKey: ['customers'] });
            qc.invalidateQueries({ queryKey: ['customer', v.id] });
        },
    });
}
