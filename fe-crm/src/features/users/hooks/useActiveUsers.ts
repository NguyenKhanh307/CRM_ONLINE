import { useLiveQuery } from '@/core/data/useLiveQuery';
import { userService } from '../services/userService';

// lấy danh sách user active — dùng cho dropdown người phụ trách / chủ sở hữu
export function useActiveUsers(enabled = true) {
    return useLiveQuery('users-active', () => userService.listActive().then((r) => r.data.data.items), enabled);
}
