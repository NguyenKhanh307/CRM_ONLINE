import { useMutation, useQueryClient } from '@tanstack/react-query';
import { opportunityService } from '../services/opportunityService';
import type { UpdateOpportunityPayload } from '../types/opportunityTypes';

export function useUpdateOpportunity() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: ({ id, payload }: { id: number; payload: UpdateOpportunityPayload }) =>
            opportunityService.update(id, payload),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['opportunities'] }),
    });
}
