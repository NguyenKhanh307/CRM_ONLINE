import { useQuery } from '@tanstack/react-query';
import { userService } from '../services/userService';

/** Lấy hồ sơ của người dùng đang đăng nhập (GET /api/auth/me). */
export function useMyProfile() {
    return useQuery({
        queryKey: ['my-profile'],
        queryFn: () => userService.getMyProfile().then((res) => res.data.data),
    });
}
