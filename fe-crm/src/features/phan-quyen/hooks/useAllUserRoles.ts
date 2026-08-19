import { useLiveQuery } from '@/core/data/useLiveQuery';
import { phanQuyenService } from '../services/phanQuyenService';

// lấy toàn bộ liên kết user-role hiện có -> tập userId đã thuộc một nhóm bất kỳ, dùng để modal
// "Thêm thành viên" loại người đã có nhóm khỏi danh sách tìm kiếm (mỗi người chỉ thuộc một nhóm quyền)
export function useAllUserRoles() {
    return useLiveQuery('user-role-assignments', () => phanQuyenService.getUserRoleAssignments().then(res => res.data.data));
}
