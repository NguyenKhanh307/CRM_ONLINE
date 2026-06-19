import { useMutation, useQueryClient } from '@tanstack/react-query';
import { phanQuyenService } from '../services/phanQuyenService';

/** Kích hoạt lại tài khoản nhân viên bị khóa, cập nhật cache danh sách thành viên. */
export const useReactivateUser = (roleId: number) => {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (userId: number) => phanQuyenService.reactivateUser(userId),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['roleMembers', roleId] }),
    });
};
