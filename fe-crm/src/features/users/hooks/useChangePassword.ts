import { useLiveMutation } from '@/core/data/useLiveMutation';
import { userService } from '../services/userService';
import type { ChangePasswordPayload } from '../types/userTypes';

// đổi mật khẩu của người dùng đang đăng nhập (POST /api/auth/change-password)
export function useChangePassword() {
    return useLiveMutation((payload: ChangePasswordPayload) => userService.changePassword(payload));
}
