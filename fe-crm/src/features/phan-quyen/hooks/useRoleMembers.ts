import { useQuery } from '@tanstack/react-query';
import { phanQuyenService } from '../services/phanQuyenService';

// lấy danh sách thành viên của một nhóm theo roleId đang chọn
export const useRoleMembers = (roleId: number | null) => {
    return useQuery({
        queryKey: ['roleMembers', roleId],
        queryFn: () => phanQuyenService.getRoleMembers(roleId!).then(res => res.data.data),
        enabled: roleId !== null,
    });
};
