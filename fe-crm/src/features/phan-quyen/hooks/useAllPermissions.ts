import { useQuery } from '@tanstack/react-query';
import { phanQuyenService } from '../services/phanQuyenService';

// lấy toàn bộ danh sách quyền hạn có trong hệ thống
export const useAllPermissions = () => {
    return useQuery({
        queryKey: ['allPermissions'],
        queryFn: () => phanQuyenService.getPermissions().then(res => res.data.data.items),
        staleTime: 5 * 60 * 1000,
    });
};
