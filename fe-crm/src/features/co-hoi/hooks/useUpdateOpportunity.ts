import { useMutation, useQueryClient } from '@tanstack/react-query';
import { opportunityService } from '../services/opportunityService';
import type { UpdateOpportunityPayload } from '../types/opportunityTypes';

// cập nhật cơ hội — invalidate danh sách sau khi thành công
export function useUpdateOpportunity() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: ({ id, payload }: { id: number; payload: UpdateOpportunityPayload }) =>
            opportunityService.update(id, payload),
        onSuccess: (_d, v) => {
            qc.invalidateQueries({ queryKey: ['opportunities'] });
            qc.invalidateQueries({ queryKey: ['opportunity', v.id] });
        },
    });
}
