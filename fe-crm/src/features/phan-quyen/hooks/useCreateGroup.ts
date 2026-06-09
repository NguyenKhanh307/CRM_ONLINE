import { useMutation, useQueryClient } from '@tanstack/react-query';
import { phanQuyenService } from '@/features/phan-quyen/services/phanQuyenService';
import type { GroupFormPayload } from '@/features/phan-quyen/types/phanQuyenTypes';

/**
 * Tạo nhóm người dùng mới.
 */
export const useCreateGroup = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (payload: GroupFormPayload) => phanQuyenService.createRole(payload),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['roleGroups'] });
        },
    });
};
