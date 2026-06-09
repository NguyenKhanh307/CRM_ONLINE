import { useQuery } from '@tanstack/react-query';
import { phanQuyenService } from '@/features/phan-quyen/services/phanQuyenService';

/**
 * Lấy toàn bộ danh sách người dùng (dùng cho modal thêm thành viên).
 */
export const useAllUsers = () => {
    return useQuery({
        queryKey: ['allUsers'],
        queryFn: () => phanQuyenService.getUsers().then(res => res.data.data.items),
    });
};
