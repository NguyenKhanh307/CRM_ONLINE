import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { phanQuyenService } from '../services/phanQuyenService';
import type { GroupFormPayload } from '../types/phanQuyenTypes';

// tạo nhóm người dùng mới — báo danh sách nhóm làm mới sau khi thành công
export function useCreateGroup() {
    const { mutate: run, isPending } = useLiveMutation((payload: GroupFormPayload) => phanQuyenService.createRole(payload));

    const mutate: typeof run = (payload, callbacks) =>
        run(payload, { ...callbacks, onSuccess: (data) => { notify('role-groups'); callbacks?.onSuccess?.(data); } });

    return { mutate, isPending };
}
