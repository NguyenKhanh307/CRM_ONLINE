import axiosInstance from '@/core/axios/axiosInstance';
import type { ApiResponse } from '@/shared/types/api';

// phiếu chăm sóc hiển thị trên trang public — chỉ dữ liệu công khai, không có thông tin nội bộ
export interface PublicTicketView {
    code: string;
    type: string;
    status: string;
    priority: string;
    subject: string;
    description: string | null;
    createdAt: string;
    slaDueAt: string | null;
    resolvedAt: string | null;
    closedAt: string | null;
    satisfactionScore: number | null;
    satisfactionComment: string | null;
}

export const publicTicketService = {
    // lấy phiếu công khai theo mã
    getByCode: (code: string) =>
        axiosInstance.get<ApiResponse<PublicTicketView>>(`/api/public/tickets/${code}`),
    // khách gửi đánh giá hài lòng (1-5) + nhận xét
    submitCsat: (code: string, score: number, comment?: string) =>
        axiosInstance.post<ApiResponse<PublicTicketView>>(`/api/public/tickets/${code}/csat`, { score, comment }),
};
