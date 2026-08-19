import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { phanQuyenService } from '../services/phanQuyenService';

// xóa nhóm người dùng (chỉ áp dụng với non-system roles) — báo danh sách nhóm làm mới sau khi thành công
export function useDeleteGroup() {
    const { mutate: run, isPending } = useLiveMutation((id: number) => phanQuyenService.deleteRole(id));

    const mutate: typeof run = (id, callbacks) =>
        run(id, { ...callbacks, onSuccess: (data) => { notify('role-groups'); callbacks?.onSuccess?.(data); } });

    return { mutate, isPending };
}
