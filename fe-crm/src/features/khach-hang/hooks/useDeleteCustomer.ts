import { useMutation, useQueryClient } from '@tanstack/react-query';
import { customerService } from '../services/customerService';

export function useDeleteCustomer() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (id: number) => customerService.remove(id),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['customers'] }),
    });
}
