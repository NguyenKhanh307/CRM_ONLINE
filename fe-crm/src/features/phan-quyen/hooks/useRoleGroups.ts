import { useLiveQuery } from '@/core/data/useLiveQuery';
import { phanQuyenService } from '../services/phanQuyenService';

// lấy danh sách nhóm người dùng từ api
export function useRoleGroups() {
    return useLiveQuery('role-groups', () => phanQuyenService.getRoles().then(res => res.data.data.items));
}
