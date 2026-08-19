import { useLiveQuery } from '@/core/data/useLiveQuery';
import { userService } from '../services/userService';

// lấy hồ sơ của người dùng đang đăng nhập (GET /api/auth/me)
export function useMyProfile() {
    return useLiveQuery('my-profile', () => userService.getMyProfile().then((res) => res.data.data));
}
