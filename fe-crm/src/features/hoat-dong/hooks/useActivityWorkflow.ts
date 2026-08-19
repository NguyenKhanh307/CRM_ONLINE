import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { activityService } from '../services/activityService';

// loại hành động chuyển trạng thái hoạt động
export type ActivityAction = 'start' | 'complete' | 'cancel' | 'reopen';

// chạy hành động chuyển trạng thái hoạt động: start (bắt đầu) / complete (hoàn thành) / cancel (hủy) / reopen (mở lại)
export function useActivityWorkflow() {
    const { mutate: run, isPending } = useLiveMutation(
        ({ id, action }: { id: number; action: ActivityAction }) => activityService[action](id));

    const mutate: typeof run = (input, callbacks) =>
        run(input, { ...callbacks, onSuccess: (data) => { notify('activities'); callbacks?.onSuccess?.(data); } });

    return { mutate, isPending };
}
