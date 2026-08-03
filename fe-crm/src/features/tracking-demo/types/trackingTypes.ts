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
