import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { userService } from '../services/userService';
import type { UpdateProfilePayload } from '../types/userTypes';

// cập nhật hồ sơ cá nhân (PUT /api/auth/me) — báo hồ sơ làm mới sau khi thành công
export function useUpdateProfile() {
    const { mutate: run, isPending } = useLiveMutation(
        (payload: UpdateProfilePayload) => userService.updateMyProfile(payload).then((res) => res.data.data));

    const mutate: typeof run = (payload, callbacks) =>
        run(payload, { ...callbacks, onSuccess: (data) => { notify('my-profile'); callbacks?.onSuccess?.(data); } });

    return { mutate, isPending };
}
