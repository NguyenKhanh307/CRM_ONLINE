import { useMutation, useQueryClient } from '@tanstack/react-query';
import { customerService } from '../services/customerService';
import type { UpdateCustomerPayload } from '../types/customerTypes';

export function useUpdateCustomer() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: ({ id, payload }: { id: number; payload: UpdateCustomerPayload }) =>
            customerService.update(id, payload),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['customers'] }),
    });
}
