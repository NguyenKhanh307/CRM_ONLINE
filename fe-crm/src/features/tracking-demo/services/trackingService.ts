import axiosInstance from '@/core/axios/axiosInstance';
import type { ApiResponse } from '@/shared/types/api';
import type { PublicCampaign, TrackedLead } from '../types/trackingTypes';

/** Body nộp form liên hệ trên landing page. */
export interface SubmitTrackingPayload {
    code: string;
    name: string;
    companyName: string;
    email: string;
    phone: string;
    note: string;
    points: number;
}

/**
 * Gọi nhóm API web tracking công khai (`/api/tracking/**`, không cần JWT).
 * Trang demo mô phỏng website của khách nên không được dùng endpoint đã đăng nhập.
 */
export const trackingService = {
    /** Chiến dịch đang chạy để gắn nguồn cho tiềm năng. */
    getCampaigns: () =>
        axiosInstance.get<ApiResponse<PublicCampaign[]>>('/api/tracking/campaigns'),

    /** Mở phiên: khôi phục tiềm năng theo mã, hoặc tạo mới (kèm chiến dịch nguồn nếu có). */
    visit: (code: string | null, campaignId: number | null) =>
        axiosInstance.post<ApiResponse<TrackedLead>>('/api/tracking/visit', { code, campaignId }),

    /** Ghi nhận một hành vi + cộng điểm. */
    score: (code: string, action: string, label: string, points: number) =>
        axiosInstance.post<ApiResponse<TrackedLead>>('/api/tracking/score', { code, action, label, points }),

    /** Nộp form liên hệ: cập nhật thông tin tiềm năng + cộng điểm. */
    submit: (payload: SubmitTrackingPayload) =>
        axiosInstance.post<ApiResponse<TrackedLead>>('/api/tracking/submit', payload),
};
