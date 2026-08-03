import { useMutation, useQueryClient } from '@tanstack/react-query';
import { phanQuyenService } from '../services/phanQuyenService';

// cập nhật năm được xem data cho nhân viên, cập nhật cache danh sách thành viên
export const useUpdateDataAccess = (roleId: number) => {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: ({ userId, year }: { userId: number; year: number | null }) =>
            phanQuyenService.updateUserDataAccess(userId, year),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['roleMembers', roleId] }),
    });
};
