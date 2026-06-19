import { useMutation, useQueryClient } from '@tanstack/react-query';
import { activityService } from '../services/activityService';

/** Xóa hoạt động — invalidate danh sách sau khi thành công. */
export function useDeleteActivity() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (id: number) => activityService.remove(id),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['activities'] }),
    });
}
