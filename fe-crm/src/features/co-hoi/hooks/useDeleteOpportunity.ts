import { useMutation, useQueryClient } from '@tanstack/react-query';
import { opportunityService } from '../services/opportunityService';

export function useDeleteOpportunity() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (id: number) => opportunityService.remove(id),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['opportunities'] }),
    });
}
