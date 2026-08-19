import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { phanQuyenService } from '../services/phanQuyenService';

// cập nhật năm được xem data cho nhân viên — báo danh sách thành viên của nhóm làm mới sau khi thành công
export function useUpdateDataAccess(roleId: number) {
    const { mutate: run, isPending } = useLiveMutation(
        ({ userId, year }: { userId: number; year: number | null }) => phanQuyenService.updateUserDataAccess(userId, year));

    const mutate: typeof run = (input, callbacks) =>
        run(input, { ...callbacks, onSuccess: (data) => { notify(`role-members:${roleId}`); callbacks?.onSuccess?.(data); } });

    return { mutate, isPending };
}
