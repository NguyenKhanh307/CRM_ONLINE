import { useMutation, useQueryClient } from '@tanstack/react-query';
import { phanQuyenService } from '../services/phanQuyenService';
import type { GroupFormPayload } from '../types/phanQuyenTypes';

// tạo nhóm người dùng mới
export const useCreateGroup = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (payload: GroupFormPayload) => phanQuyenService.createRole(payload),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['roleGroups'] });
        },
    });
};
