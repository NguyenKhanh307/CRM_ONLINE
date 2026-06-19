import { useQuery } from '@tanstack/react-query';
import { phanQuyenService } from '../services/phanQuyenService';

/**
 * Lấy danh sách quyền đã gán cho một nhóm.
 * @param roleId ID nhóm đang chọn
 */
export const useRolePermissions = (roleId: number | null) => {
    return useQuery({
        queryKey: ['rolePermissions', roleId],
        queryFn: () => phanQuyenService.getRolePermissions(roleId!).then(res => res.data.data),
        enabled: roleId !== null,
    });
};
