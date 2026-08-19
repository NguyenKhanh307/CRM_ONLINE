import { useLiveQuery } from '@/core/data/useLiveQuery';
import { phanQuyenService } from '../services/phanQuyenService';

// lấy danh sách thành viên của một nhóm theo roleId đang chọn
export function useRoleMembers(roleId: number | null) {
    const enabled = roleId !== null;
    return useLiveQuery(
        `role-members:${roleId}`,
        () => phanQuyenService.getRoleMembers(roleId as number).then(res => res.data.data),
        enabled,
    );
}
