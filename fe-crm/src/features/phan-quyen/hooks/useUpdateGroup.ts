import { useMutation, useQueryClient } from '@tanstack/react-query';
import { phanQuyenService } from '../services/phanQuyenService';

/**
 * Cập nhật tên/mô tả nhóm người dùng.
 */
export const useUpdateGroup = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: ({ id, name, description }: { id: number; name: string; description?: string }) =>
            phanQuyenService.updateRole(id, { name, description }),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['roleGroups'] });
        },
    });
};
