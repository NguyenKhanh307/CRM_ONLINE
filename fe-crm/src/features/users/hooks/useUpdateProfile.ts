import { useMutation, useQueryClient } from '@tanstack/react-query';
import { userService } from '../services/userService';
import type { UpdateProfilePayload } from '../types/userTypes';

// cập nhật hồ sơ cá nhân (PUT /api/auth/me)
export function useUpdateProfile() {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (payload: UpdateProfilePayload) =>
            userService.updateMyProfile(payload).then((res) => res.data.data),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['my-profile'] });
        },
    });
}
