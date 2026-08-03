import { useMutation, useQueryClient } from '@tanstack/react-query';
import { opportunityService } from '../services/opportunityService';

// xóa cơ hội — invalidate danh sách sau khi thành công
export function useDeleteOpportunity() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (id: number) => opportunityService.remove(id),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['opportunities'] }),
    });
}
