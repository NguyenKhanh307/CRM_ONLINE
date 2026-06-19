import { useMutation, useQueryClient } from '@tanstack/react-query';
import { phanQuyenService } from '../services/phanQuyenService';

/** Thu hồi tài khoản nhân viên, cập nhật cache danh sách thành viên của nhóm. */
export const useRevokeUser = (roleId: number) => {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: (userId: number) => phanQuyenService.revokeUser(userId),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['roleMembers', roleId] }),
    });
};
