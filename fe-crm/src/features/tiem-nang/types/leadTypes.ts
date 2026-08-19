export interface UpdateLeadPayload {
    name: string;
    companyName?: string | null;
    leadType?: string | null;
    ownerId: number | null;
    contactId: number | null;
    convertedOpportunityId?: number | null;
    campaignId?: number | null;
    taxCode?: string | null;
    website?: string | null;
    industry?: string | null;
    source: string | null;
    // đổi tự động (chấm điểm) hoặc qua hành động qualify/lose/claim; cũng có thể tự gán tay
    // (kể cả 'converted') nếu người dùng đã tách dữ liệu thủ công — không còn action convert riêng
    status?: string;
    phone: string | null;
    email: string | null;
    note: string | null;
}

// payload tạo mới tiềm năng — POST /api/leads
export interface CreateLeadPayload {
    code: string;
    name: string;
    companyName: string | null;
    leadType: string | null;
    ownerId: number | null;
    contactId: number | null;
    campaignId: number | null;
    taxCode: string | null;
    website: string | null;
    industry: string | null;
    source: string | null;
    // status: KHÔNG gửi — tiềm năng luôn tạo ở trạng thái 'new'.
    phone: string | null;
    email: string | null;
    note: string | null;
}

export interface LeadResult {
    id: number;
    code: string;
    name: string;
    companyName: string | null;
    leadType: string | null;
    ownerId: number | null;
    contactId: number | null;
    convertedOpportunityId: number | null;
    campaignId: number | null;
    taxCode: string | null;
    website: string | null;
    industry: string | null;
    source: string | null;
    status: string;
    phone: string | null;
    email: string | null;
    note: string | null;
    score: number;
    createdAt: string;
    updatedAt: string;
    // Tên khóa ngoại do BE resolve sẵn (INameResolver).
    ownerName: string | null;
    contactName: string | null;
    campaignName: string | null;
    // Audit: BE tự đóng dấu (AuditInterceptor).
    createdBy: number | null;
    updatedBy: number | null;
    createdByName: string | null;
    updatedByName: string | null;
}

// dòng "sản phẩm quan tâm" của tiềm năng — lead_items
export interface LeadItemResult {
    id: number;
    leadId: number;
    productId: number;
    quantity: number;
    interestType: 'viewed' | 'requested_quote';
    note: string | null;
    createdAt: string;
}

export interface CreateLeadItemPayload {
    productId: number;
    interestType: 'viewed' | 'requested_quote';
    note?: string | null;
}
