import { useQuery } from '@tanstack/react-query';
import { userService } from '../services/userService';

/**
 * Lấy danh sách user active — dùng cho dropdown người phụ trách / chủ sở hữu.
 */
export function useActiveUsers(enabled = true) {
    return useQuery({
        queryKey: ['users-active'],
        queryFn: () => userService.listActive().then((r) => r.data.data.items),
        enabled,
    });
}
