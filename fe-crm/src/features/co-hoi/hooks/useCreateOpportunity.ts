import { useMutation, useQueryClient } from '@tanstack/react-query';
import { opportunityService } from '../services/opportunityService';
import type { CreateOpportunityPayload } from '../types/opportunityTypes';

// tạo mới cơ hội — invalidate danh sách sau khi thành công
export function useCreateOpportunity() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (payload: CreateOpportunityPayload) => opportunityService.create(payload),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['opportunities'] }),
    });
}
