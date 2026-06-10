import { useMutation, useQueryClient } from '@tanstack/react-query';
import { leadService } from '../services/leadService';
import type { UpdateLeadPayload } from '../types/leadTypes';

export function useUpdateLead() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: ({ id, payload }: { id: number; payload: UpdateLeadPayload }) =>
            leadService.update(id, payload),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['leads'] }),
    });
}
