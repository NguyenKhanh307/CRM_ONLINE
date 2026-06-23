import axiosInstance from '@/core/axios/axiosInstance';
import type { ApiResponse } from '@/shared/types/api';

/** Thông báo trả về từ GET /api/notifications. */
export interface NotificationResult {
    id: number;
    type: string | null;
    title: string | null;
    content: string | null;
    leadId: number | null;
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
};
