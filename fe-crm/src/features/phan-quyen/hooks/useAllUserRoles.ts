import { useQuery } from '@tanstack/react-query';
import { phanQuyenService } from '../services/phanQuyenService';

// lấy toàn bộ liên kết user-role hiện có -> tập userId đã thuộc một nhóm bất kỳ, dùng để modal
// "Thêm thành viên" loại người đã có nhóm khỏi danh sách tìm kiếm (mỗi người chỉ thuộc một nhóm quyền)
export const useAllUserRoles = () => {
    return useQuery({
        queryKey: ['allUserRoles'],
        queryFn: () => phanQuyenService.getUserRoleAssignments().then(res => res.data.data),
    });
};
