export interface UpdateLeadPayload {
    name: string;
    companyName?: string | null;
    leadType?: string | null;
    ownerId: number | null;
    customerId: number | null;
    contactId: number | null;
    campaignId?: number | null;
    title?: string | null;
    department?: string | null;
    taxCode?: string | null;
    website?: string | null;
    industry?: string | null;
    source: string | null;
    // status: KHÔNG gửi — đổi tự động (chấm điểm) hoặc qua hành động convert/lose.
    estimatedValue: number | null;
    phone: string | null;
    email: string | null;
    doNotCall?: boolean;
    doNotEmail?: boolean;
    note: string | null;
}

/** Payload tạo mới tiềm năng — POST /api/leads. */
export interface CreateLeadPayload {
    code: string;
    name: string;
    companyName: string | null;
    leadType: string | null;
    ownerId: number | null;
    customerId: number | null;
    contactId: number | null;
    campaignId: number | null;
    title: string | null;
    department: string | null;
    taxCode: string | null;
    website: string | null;
    industry: string | null;
    source: string | null;
    // status: KHÔNG gửi — tiềm năng luôn tạo ở trạng thái 'new'.
    estimatedValue: number | null;
    phone: string | null;
    email: string | null;
    doNotCall: boolean;
    doNotEmail: boolean;
    note: string | null;
}

export interface LeadResult {
    id: number;
    code: string;
    name: string;
    companyName: string | null;
    leadType: string | null;
    ownerId: number | null;
    customerId: number | null;
    contactId: number | null;
    campaignId: number | null;
    title: string | null;
    department: string | null;
    taxCode: string | null;
    website: string | null;
    industry: string | null;
    source: string | null;
    status: string;
    estimatedValue: number | null;
    phone: string | null;
    email: string | null;
    doNotCall: boolean;
    doNotEmail: boolean;
    note: string | null;
    score: number;
    createdAt: string;
    updatedAt: string;
    // Tên khóa ngoại do BE resolve sẵn (INameResolver).
    ownerName: string | null;
    customerName: string | null;
    contactName: string | null;
    campaignName: string | null;
    // Audit: BE tự đóng dấu (AuditInterceptor).
    createdBy: number | null;
    updatedBy: number | null;
    createdByName: string | null;
    updatedByName: string | null;
}
