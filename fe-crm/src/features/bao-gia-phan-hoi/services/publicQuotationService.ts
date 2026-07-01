import axiosInstance from '@/core/axios/axiosInstance';
import type { ApiResponse } from '@/shared/types/api';

/** Một dòng hàng trong báo giá công khai. */
export interface PublicQuotationLine {
    productName: string;
    unit: string | null;
    quantity: number;
    unitPrice: number;
    discount: number;
    amount: number;
}

/** Báo giá hiển thị trên trang phản hồi công khai. */
export interface PublicQuotationView {
    code: string;
    customerName: string;
    contactName: string;
    quoteDate: string | null;
    validUntil: string | null;
    currency: string | null;
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

/** Hành động phản hồi của khách. */
export type RespondAction = 'accept' | 'adjust' | 'reject';

export const publicQuotationService = {
    /** Lấy báo giá công khai theo token. */
    getByToken: (token: string) =>
        axiosInstance.get<ApiResponse<PublicQuotationView>>(`/api/public/quotations/${token}`),
    /** Gửi phản hồi của khách (đồng ý/điều chỉnh/không đồng ý). */
    respond: (token: string, action: RespondAction, note?: string) =>
        axiosInstance.post<ApiResponse<null>>(`/api/public/quotations/${token}/respond`, { action, note }),
};
