import { useMutation, useQueryClient } from '@tanstack/react-query';
import { activityService } from '../services/activityService';
import type { UpdateActivityPayload } from '../types/activityTypes';

// cập nhật hoạt động — invalidate danh sách sau khi thành công
export function useUpdateActivity() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: ({ id, payload }: { id: number; payload: UpdateActivityPayload }) =>
            activityService.update(id, payload),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['activities'] }),
    });
}
