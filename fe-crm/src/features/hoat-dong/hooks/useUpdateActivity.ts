import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { activityService } from '../services/activityService';
import type { UpdateActivityPayload } from '../types/activityTypes';

// cập nhật hoạt động — báo danh sách làm mới sau khi thành công
export function useUpdateActivity() {
    const { mutate: run, isPending } = useLiveMutation(
        ({ id, payload }: { id: number; payload: UpdateActivityPayload }) => activityService.update(id, payload));

    const mutate: typeof run = (input, callbacks) =>
        run(input, { ...callbacks, onSuccess: (data) => { notify('activities'); callbacks?.onSuccess?.(data); } });

    return { mutate, isPending };
}
