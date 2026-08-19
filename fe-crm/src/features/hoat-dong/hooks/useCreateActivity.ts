import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { activityService } from '../services/activityService';
import type { CreateActivityPayload } from '../types/activityTypes';

// tạo mới hoạt động — báo danh sách làm mới sau khi thành công
export function useCreateActivity() {
    const { mutate: run, isPending } = useLiveMutation((payload: CreateActivityPayload) => activityService.create(payload));

    const mutate: typeof run = (payload, callbacks) =>
        run(payload, { ...callbacks, onSuccess: (data) => { notify('activities'); callbacks?.onSuccess?.(data); } });

    return { mutate, isPending };
}
