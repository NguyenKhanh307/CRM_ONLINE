import axiosInstance from '@/core/axios/axiosInstance';
import type { ApiResponse } from '@/shared/types/api';

// thông báo trả về từ GET /api/notifications
export interface NotificationResult {
    id: number;
    type: string | null;
    title: string | null;
    content: string | null;
    leadId: number | null;
    // id bản ghi đích (lead/quotation/ticket...) để điều hướng + focus dòng khi bấm thông báo
    targetId: number | null;
    isRead: boolean;
    createdAt: string;
}

export const notificationService = {
    getList: () =>
        axiosInstance.get<ApiResponse<NotificationResult[]>>('/api/notifications'),
    getUnreadCount: () =>
        axiosInstance.get<ApiResponse<number>>('/api/notifications/unread-count'),
    markRead: (id: number) =>
        axiosInstance.post(`/api/notifications/${id}/read`),
    markAllRead: () =>
        axiosInstance.post('/api/notifications/read-all'),
    // xóa mềm các thông báo được chọn — chỉ ẩn khỏi hộp thông báo của chính người dùng
    deleteBulk: (ids: number[]) =>
        axiosInstance.post('/api/notifications/delete-bulk', { ids }),
    // xóa mềm toàn bộ thông báo của người dùng hiện tại
    deleteAll: () =>
        axiosInstance.post('/api/notifications/delete-all'),
};
