import { useLiveQuery } from '@/core/data/useLiveQuery';
import { phanQuyenService } from '../services/phanQuyenService';

// lấy danh sách quyền đã gán cho một nhóm theo roleId đang chọn
export function useRolePermissions(roleId: number | null) {
    const enabled = roleId !== null;
    return useLiveQuery(
        `role-permissions:${roleId}`,
        () => phanQuyenService.getRolePermissions(roleId as number).then(res => res.data.data),
        enabled,
    );
}
