import { useLiveQuery } from '@/core/data/useLiveQuery';
import { useLiveMutation } from '@/core/data/useLiveMutation';
import { notify } from '@/core/data/dataBus';
import { notificationService } from './notificationService';

// lấy danh sách thông báo của tôi (tự tải lại mỗi 30s)
export function useNotificationList() {
    return useLiveQuery('notifications', () => notificationService.getList().then(r => r.data.data), true, 30_000);
}

// lấy số thông báo chưa đọc (tự tải lại mỗi 30s) — dùng chung key 'notifications' để mark-read
// báo được cả hai cùng lúc
export function useUnreadCount() {
    return useLiveQuery(
        'notifications:unread-count',
        () => notificationService.getUnreadCount().then(r => r.data.data),
        true,
        30_000,
    );
}

// đánh dấu đã đọc (một hoặc tất cả) và làm mới danh sách + badge
export function useMarkNotifications() {
    const markOneMutation = useLiveMutation((id: number) => notificationService.markRead(id));
    const markAllMutation = useLiveMutation<void, Awaited<ReturnType<typeof notificationService.markAllRead>>>(() => notificationService.markAllRead());

    const markOne: typeof markOneMutation.mutate = (id, callbacks) =>
        markOneMutation.mutate(id, { ...callbacks, onSuccess: (data) => { notify('notifications'); callbacks?.onSuccess?.(data); } });
    const markAll: typeof markAllMutation.mutate = (input, callbacks) =>
        markAllMutation.mutate(input, { ...callbacks, onSuccess: (data) => { notify('notifications'); callbacks?.onSuccess?.(data); } });

    return {
        markOne: { mutate: markOne, isPending: markOneMutation.isPending },
        markAll: { mutate: markAll, isPending: markAllMutation.isPending },
    };
}

// xóa mềm thông báo (các dòng được chọn hoặc tất cả) và làm mới danh sách + badge
export function useDeleteNotifications() {
    const deleteBulkMutation = useLiveMutation((ids: number[]) => notificationService.deleteBulk(ids));
    const deleteAllMutation = useLiveMutation<void, Awaited<ReturnType<typeof notificationService.deleteAll>>>(() => notificationService.deleteAll());

    const deleteBulk: typeof deleteBulkMutation.mutate = (ids, callbacks) =>
        deleteBulkMutation.mutate(ids, { ...callbacks, onSuccess: (data) => { notify('notifications'); callbacks?.onSuccess?.(data); } });
    const deleteAll: typeof deleteAllMutation.mutate = (input, callbacks) =>
        deleteAllMutation.mutate(input, { ...callbacks, onSuccess: (data) => { notify('notifications'); callbacks?.onSuccess?.(data); } });

    return {
        deleteBulk: { mutate: deleteBulk, isPending: deleteBulkMutation.isPending },
        deleteAll: { mutate: deleteAll, isPending: deleteAllMutation.isPending },
    };
}
