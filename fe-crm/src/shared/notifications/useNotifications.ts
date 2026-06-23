import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { notificationService } from './notificationService';

/** Lấy danh sách thông báo của tôi (tự refetch mỗi 30s). */
export function useNotificationList() {
    return useQuery({
        queryKey: ['notifications'],
        queryFn: () => notificationService.getList().then(r => r.data.data),
        refetchInterval: 30_000,
    });
}

/** Lấy số thông báo chưa đọc (tự refetch mỗi 30s). */
export function useUnreadCount() {
    return useQuery({
        queryKey: ['notifications', 'unread-count'],
        queryFn: () => notificationService.getUnreadCount().then(r => r.data.data),
        refetchInterval: 30_000,
    });
}

/** Đánh dấu đã đọc (một hoặc tất cả) và làm mới danh sách + badge. */
export function useMarkNotifications() {
    const qc = useQueryClient();
    const invalidate = () => qc.invalidateQueries({ queryKey: ['notifications'] });
    const markOne = useMutation({
        mutationFn: (id: number) => notificationService.markRead(id),
        onSuccess: invalidate,
    });
    const markAll = useMutation({
        mutationFn: () => notificationService.markAllRead(),
        onSuccess: invalidate,
    });
    return { markOne, markAll };
}
