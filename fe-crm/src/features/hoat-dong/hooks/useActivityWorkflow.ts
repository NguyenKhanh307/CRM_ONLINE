import { useMutation, useQueryClient } from '@tanstack/react-query';
import { activityService } from '../services/activityService';

// loại hành động chuyển trạng thái hoạt động
export type ActivityAction = 'start' | 'complete' | 'cancel';

// chạy hành động chuyển trạng thái hoạt động: start (bắt đầu) / complete (hoàn thành) / cancel (hủy)
export function useActivityWorkflow() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: ({ id, action }: { id: number; action: ActivityAction }) => activityService[action](id),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['activities'] }),
    });
}
