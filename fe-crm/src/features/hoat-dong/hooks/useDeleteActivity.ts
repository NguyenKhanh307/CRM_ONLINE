import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { activityService } from '../services/activityService';

// xóa hoạt động — báo danh sách làm mới sau khi thành công
export function useDeleteActivity() {
    const { mutate: run, isPending } = useLiveMutation((id: number) => activityService.remove(id));

    const mutate: typeof run = (id, callbacks) =>
        run(id, { ...callbacks, onSuccess: (data) => { notify('activities'); callbacks?.onSuccess?.(data); } });

    return { mutate, isPending };
}
