import { useLiveQuery } from '@/core/data/useLiveQuery';
import { phanQuyenService } from '../services/phanQuyenService';

// lấy toàn bộ danh sách người dùng (dùng cho modal thêm thành viên)
export function useAllUsers() {
    return useLiveQuery('all-users', () => phanQuyenService.getUsers().then(res => res.data.data.items));
}
