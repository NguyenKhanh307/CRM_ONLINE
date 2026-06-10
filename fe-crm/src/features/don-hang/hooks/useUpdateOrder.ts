import { useMutation, useQueryClient } from '@tanstack/react-query';
import { orderService } from '../services/orderService';
import type { UpdateOrderPayload } from '../types/orderTypes';

export function useUpdateOrder() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: ({ id, payload }: { id: number; payload: UpdateOrderPayload }) =>
            orderService.update(id, payload),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['orders'] }),
    });
}
