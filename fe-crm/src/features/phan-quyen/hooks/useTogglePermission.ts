import { useLiveMutation, type MutateCallbacks } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { phanQuyenService } from '../services/phanQuyenService';

// dạng trả về giống nhau cho cả assign lẫn revoke — gói lại notify sau khi thành công
interface ToggleMutation {
    mutate: (permissionId: number, callbacks?: MutateCallbacks<unknown>) => Promise<unknown>;
    isPending: boolean;
}

// bọc một mutation gán/thu hồi quyền, tự báo danh sách quyền của nhóm làm mới sau khi thành công
function wrapWithNotify(roleId: number, run: ReturnType<typeof useLiveMutation<number, unknown>>): ToggleMutation {
    const mutate: ToggleMutation['mutate'] = (permissionId, callbacks) =>
        run.mutate(permissionId, {
            ...callbacks,
            onSuccess: (data) => { notify(`role-permissions:${roleId}`); callbacks?.onSuccess?.(data); },
        });
    return { mutate, isPending: run.isPending };
}

// bật/tắt một quyền cho nhóm — gọi api ngay khi toggle
export function useTogglePermission(roleId: number) {
    const assignRun = useLiveMutation((permissionId: number) => phanQuyenService.assignPermission(roleId, permissionId));
    const revokeRun = useLiveMutation((permissionId: number) => phanQuyenService.revokePermission(roleId, permissionId));

    const assign = wrapWithNotify(roleId, assignRun);
    const revoke = wrapWithNotify(roleId, revokeRun);

    return { assign, revoke };
}
