import { useMutation, useQueryClient } from '@tanstack/react-query';
import { pricingService } from '../services/pricingService';

export function useDeletePricePolicy() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (id: number) => pricingService.remove(id),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['price-policies'] }),
    });
}
