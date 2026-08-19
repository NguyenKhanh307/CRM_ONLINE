import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { phanQuyenService } from '../services/phanQuyenService';

// thu hồi tài khoản nhân viên — báo danh sách thành viên của nhóm làm mới sau khi thành công
export function useRevokeUser(roleId: number) {
    const { mutate: run, isPending } = useLiveMutation((userId: number) => phanQuyenService.revokeUser(userId));

    const mutate: typeof run = (userId, callbacks) =>
        run(userId, { ...callbacks, onSuccess: (data) => { notify(`role-members:${roleId}`); callbacks?.onSuccess?.(data); } });

    return { mutate, isPending };
}
