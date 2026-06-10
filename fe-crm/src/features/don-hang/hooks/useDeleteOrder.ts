import { useMutation, useQueryClient } from '@tanstack/react-query';
import { orderService } from '../services/orderService';

export function useDeleteOrder() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (id: number) => orderService.remove(id),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['orders'] }),
    });
}
