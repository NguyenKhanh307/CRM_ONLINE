import { useMutation } from '@tanstack/react-query';
import { userService } from '../services/userService';
import type { ChangePasswordPayload } from '../types/userTypes';

/** Đổi mật khẩu của người dùng đang đăng nhập (POST /api/auth/change-password). */
export function useChangePassword() {
    return useMutation({
        mutationFn: (payload: ChangePasswordPayload) => userService.changePassword(payload),
    });
}
