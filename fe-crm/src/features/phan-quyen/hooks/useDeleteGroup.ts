import { useMutation, useQueryClient } from '@tanstack/react-query';
import { phanQuyenService } from '@/features/phan-quyen/services/phanQuyenService';

/**
 * Xóa nhóm người dùng (chỉ áp dụng với non-system roles).
 */
export const useDeleteGroup = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (id: number) => phanQuyenService.deleteRole(id),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['roleGroups'] });
        },
    });
};
