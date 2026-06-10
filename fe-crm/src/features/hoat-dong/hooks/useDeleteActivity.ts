import { useMutation, useQueryClient } from '@tanstack/react-query';
import { activityService } from '../services/activityService';

export function useDeleteActivity() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (id: number) => activityService.remove(id),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['activities'] }),
    });
}
