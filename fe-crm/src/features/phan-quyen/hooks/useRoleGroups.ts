import { useQuery } from '@tanstack/react-query';
import { phanQuyenService } from '../services/phanQuyenService';

/**
 * Lấy danh sách nhóm người dùng từ API.
 */
export const useRoleGroups = () => {
    return useQuery({
        queryKey: ['roleGroups'],
        queryFn: () => phanQuyenService.getRoles().then(res => res.data.data.items),
    });
};
