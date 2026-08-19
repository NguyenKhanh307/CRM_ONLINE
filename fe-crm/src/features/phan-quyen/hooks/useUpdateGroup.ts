import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { phanQuyenService } from '../services/phanQuyenService';

// cập nhật tên/mô tả nhóm người dùng — báo danh sách nhóm làm mới sau khi thành công
export function useUpdateGroup() {
    const { mutate: run, isPending } = useLiveMutation(
        ({ id, name, description }: { id: number; name: string; description?: string }) =>
            phanQuyenService.updateRole(id, { name, description }));

    const mutate: typeof run = (input, callbacks) =>
        run(input, { ...callbacks, onSuccess: (data) => { notify('role-groups'); callbacks?.onSuccess?.(data); } });

    return { mutate, isPending };
}
