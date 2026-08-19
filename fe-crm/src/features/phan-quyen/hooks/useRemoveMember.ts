import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { phanQuyenService } from '../services/phanQuyenService';

// xóa người dùng khỏi nhóm — báo thành viên nhóm và liên kết user-role làm mới sau khi thành công
export function useRemoveMember(roleId: number) {
    const { mutate: run, isPending } = useLiveMutation((userId: number) => phanQuyenService.removeMember(userId, roleId));

    const mutate: typeof run = (userId, callbacks) =>
        run(userId, {
            ...callbacks,
            onSuccess: (data) => {
                notify(`role-members:${roleId}`);
                notify('user-role-assignments');
                callbacks?.onSuccess?.(data);
            },
        });

    return { mutate, isPending };
}
