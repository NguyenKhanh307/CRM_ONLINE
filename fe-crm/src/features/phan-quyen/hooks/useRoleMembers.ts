import { useQuery } from '@tanstack/react-query';
import { phanQuyenService } from '@/features/phan-quyen/services/phanQuyenService';

/**
 * Lấy danh sách thành viên của một nhóm.
 * @param roleId ID nhóm đang chọn
 */
export const useRoleMembers = (roleId: number | null) => {
    return useQuery({
        queryKey: ['roleMembers', roleId],
        queryFn: () => phanQuyenService.getRoleMembers(roleId!).then(res => res.data.data),
        enabled: roleId !== null,
    });
};
