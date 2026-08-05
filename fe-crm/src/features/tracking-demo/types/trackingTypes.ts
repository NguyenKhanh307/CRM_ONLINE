// tiềm năng web đang được theo dõi trong phiên demo
export interface TrackedLead {
    code: string;
    score: number;
    status: string;
    campaignId: number | null;
}

// chiến dịch đang chạy — dạng rút gọn từ `GET /api/tracking/campaigns`
export interface PublicCampaign {
    id: number;
    code: string;
    name: string;
}

// một dòng nhật ký hành vi trong phiên (chỉ ở phía trình duyệt, để minh họa)
export interface SessionEvent {
    label: string;
    points: number;
    at: string;
}

// thông tin khách nhập ở form liên hệ
export interface LeadFormState {
    name: string;
    companyName: string;
    email: string;
    phone: string;
    note: string;
}

// sản phẩm công khai — dạng rút gọn từ `GET /api/tracking/products` (không có costPrice)
export interface PublicProduct {
    id: number;
    sku: string;
    name: string;
    categoryId: number | null;
    categoryName: string | null;
    unit: string | null;
    basePrice: number | null;
    vatRate: number | null;
    description: string | null;
}

// một dòng trong giỏ yêu cầu báo giá
export interface QuoteRequestItem {
    productId: number;
    quantity: number;
}

// body ghi nhận lượt xem chi tiết một sản phẩm
export interface ViewProductPayload {
    code: string;
    productId: number;
}
