import axiosInstance from '@/core/axios/axiosInstance';
import type { ApiResponse } from '@/shared/types/api';
import type { PublicCampaign, PublicProduct, QuoteRequestItem, TrackedLead, ViewProductPayload } from '../types/trackingTypes';

// body nộp form liên hệ trên landing page
export interface SubmitTrackingPayload {
    code: string;
    name: string;
    companyName: string;
    email: string;
    phone: string;
    note: string;
    points: number;
}

// body yêu cầu báo giá các sản phẩm đã chọn trên landing page
export interface RequestQuotePayload {
    code: string;
    name: string;
    companyName: string;
    email: string;
    phone: string;
    note: string;
    items: QuoteRequestItem[];
}

// gọi nhóm api web tracking công khai (`/api/tracking/**`, không cần JWT)
// trang demo mô phỏng website của khách nên không được dùng endpoint đã đăng nhập
export const trackingService = {
    // chiến dịch đang chạy để gắn nguồn cho tiềm năng
    getCampaigns: () =>
        axiosInstance.get<ApiResponse<PublicCampaign[]>>('/api/tracking/campaigns'),

    // mở phiên: khôi phục tiềm năng theo mã, hoặc tạo mới (kèm chiến dịch nguồn nếu có)
    visit: (code: string | null, campaignId: number | null) =>
        axiosInstance.post<ApiResponse<TrackedLead>>('/api/tracking/visit', { code, campaignId }),

    // ghi nhận một hành vi + cộng điểm
    score: (code: string, action: string, label: string, points: number) =>
        axiosInstance.post<ApiResponse<TrackedLead>>('/api/tracking/score', { code, action, label, points }),

    // nộp form liên hệ: cập nhật thông tin tiềm năng + cộng điểm
    submit: (payload: SubmitTrackingPayload) =>
        axiosInstance.post<ApiResponse<TrackedLead>>('/api/tracking/submit', payload),

    // danh sách sản phẩm đang hoạt động để khách duyệt trên landing page
    getProducts: () =>
        axiosInstance.get<ApiResponse<PublicProduct[]>>('/api/tracking/products'),

    // yêu cầu báo giá các sản phẩm đã chọn: cập nhật thông tin liên hệ + ghi lead_items
    requestQuote: (payload: RequestQuotePayload) =>
        axiosInstance.post<ApiResponse<TrackedLead>>('/api/tracking/request-quote', payload),

    // ghi nhận lượt xem chi tiết một sản phẩm (lần đầu mới ghi lead_items + cộng điểm)
    viewProduct: (payload: ViewProductPayload) =>
        axiosInstance.post<ApiResponse<TrackedLead>>('/api/tracking/view-product', payload),
};
