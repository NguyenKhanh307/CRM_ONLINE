import { useMutation, useQueryClient } from '@tanstack/react-query';
import { phanQuyenService } from '@/features/phan-quyen/services/phanQuyenService';

/**
 * Bật/tắt một quyền cho nhóm — gọi API ngay khi toggle.
 */
export const useTogglePermission = (roleId: number) => {
    const queryClient = useQueryClient();

    const assign = useMutation({
        mutationFn: (permissionId: number) =>
            phanQuyenService.assignPermission(roleId, permissionId),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['rolePermissions', roleId] });
        },
    });

    const revoke = useMutation({
        mutationFn: (permissionId: number) =>
            phanQuyenService.revokePermission(roleId, permissionId),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['rolePermissions', roleId] });
        },
    });

    return { assign, revoke };
};
