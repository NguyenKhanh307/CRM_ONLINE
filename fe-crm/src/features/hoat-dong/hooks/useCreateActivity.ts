import { useMutation, useQueryClient } from '@tanstack/react-query';
import { activityService } from '../services/activityService';
import type { CreateActivityPayload } from '../types/activityTypes';

// tạo mới hoạt động — invalidate danh sách sau khi thành công
export function useCreateActivity() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (payload: CreateActivityPayload) => activityService.create(payload),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['activities'] }),
    });
}
