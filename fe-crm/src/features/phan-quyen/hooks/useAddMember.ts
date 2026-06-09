import { useMutation, useQueryClient } from '@tanstack/react-query';
import { phanQuyenService } from '@/features/phan-quyen/services/phanQuyenService';

/**
 * Thêm người dùng vào nhóm.
 */
export const useAddMember = (roleId: number) => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (userId: number) => phanQuyenService.addMember(userId, roleId),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['roleMembers', roleId] });
        },
    });
};
