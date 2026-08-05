import axiosInstance from '@/core/axios/axiosInstance';
import type { ApiResponse } from '@/shared/types/api';

// một dòng hàng trong báo giá công khai
export interface PublicQuotationLine {
    productName: string;
    unit: string | null;
    quantity: number;
    unitPrice: number;
    discount: number;
    amount: number;
}

// báo giá hiển thị trên trang phản hồi công khai
export interface PublicQuotationView {
    code: string;
    customerName: string;
    contactName: string;
    quoteDate: string | null;
    validUntil: string | null;
    note: string | null;
    subtotal: number;
    discount: number;
    tax: number;
    total: number;
    status: string | null;
    customerResponse: string | null;
    customerResponseNote: string | null;
    items: PublicQuotationLine[];
}

// hành động phản hồi của khách
export type RespondAction = 'accept' | 'adjust' | 'reject';

export const publicQuotationService = {
    // lấy báo giá công khai theo mã báo giá (code) — không còn dùng token bí mật
    getByCode: (code: string) =>
        axiosInstance.get<ApiResponse<PublicQuotationView>>(`/api/public/quotations/${code}`),
    // gửi phản hồi của khách (đồng ý/điều chỉnh/không đồng ý)
    respond: (code: string, action: RespondAction, note?: string) =>
        axiosInstance.post<ApiResponse<null>>(`/api/public/quotations/${code}/respond`, { action, note }),
};
