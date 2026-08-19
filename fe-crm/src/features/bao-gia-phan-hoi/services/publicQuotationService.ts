import axiosInstance from '@/core/axios/axiosInstance';
import type { ApiResponse } from '@/shared/types/api';

// một dòng hàng trong báo giá công khai
export interface PublicQuotationLine {
    // id dòng hàng (quotation_items.id) — dùng khi khách đề xuất sửa số lượng/xóa dòng
    id: number;
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

// hành động phản hồi ngay của khách — "Chỉnh sửa" đi qua proposeAdjustment() riêng vì gửi kèm dòng hàng
export type RespondAction = 'accept' | 'reject';

// một dòng hàng khách đề nghị giữ lại kèm số lượng mới (dòng khách xóa thì không gửi lên)
export interface ProposedAdjustmentItem {
    id: number;
    quantity: number;
}

export const publicQuotationService = {
    // lấy báo giá công khai theo mã báo giá (code) — không còn dùng token bí mật
    getByCode: (code: string) =>
        axiosInstance.get<ApiResponse<PublicQuotationView>>(`/api/public/quotations/${code}`),
    // gửi phản hồi ngay của khách (đồng ý/không đồng ý)
    respond: (code: string, action: RespondAction, note?: string) =>
        axiosInstance.post<ApiResponse<null>>(`/api/public/quotations/${code}/respond`, { action, note }),
    // khách "Chỉnh sửa" — chỉ gửi đề xuất (sửa số lượng/xóa dòng), KHÔNG áp thẳng vào báo giá thật
    proposeAdjustment: (code: string, items: ProposedAdjustmentItem[], note?: string) =>
        axiosInstance.post<ApiResponse<null>>(`/api/public/quotations/${code}/propose-adjustment`, { items, note }),
};
