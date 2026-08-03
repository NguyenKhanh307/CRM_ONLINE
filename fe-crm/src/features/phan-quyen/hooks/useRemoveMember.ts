import { useMutation, useQueryClient } from '@tanstack/react-query';
import { phanQuyenService } from '../services/phanQuyenService';

// xóa người dùng khỏi nhóm
export const useRemoveMember = (roleId: number) => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (userId: number) => phanQuyenService.removeMember(userId, roleId),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['roleMembers', roleId] });
            queryClient.invalidateQueries({ queryKey: ['allUserRoles'] });
        },
    });
};
