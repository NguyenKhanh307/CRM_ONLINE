import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { phanQuyenService } from '../services/phanQuyenService';

// kích hoạt lại tài khoản nhân viên bị khóa — báo danh sách thành viên của nhóm làm mới sau khi thành công
export function useReactivateUser(roleId: number) {
    const { mutate: run, isPending } = useLiveMutation((userId: number) => phanQuyenService.reactivateUser(userId));

    const mutate: typeof run = (userId, callbacks) =>
        run(userId, { ...callbacks, onSuccess: (data) => { notify(`role-members:${roleId}`); callbacks?.onSuccess?.(data); } });

    return { mutate, isPending };
}
