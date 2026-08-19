import { useLiveQuery } from '@/core/data/useLiveQuery';
import { phanQuyenService } from '../services/phanQuyenService';

// lấy toàn bộ danh sách quyền hạn có trong hệ thống
export function useAllPermissions() {
    return useLiveQuery('permissions', () => phanQuyenService.getPermissions().then(res => res.data.data.items));
}
