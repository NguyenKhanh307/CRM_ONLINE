export interface CampaignResult {
    id: number;
    code: string;
    name: string;
    type: string;
    status: string;
    channel: string | null;
    startDate: string | null;
    endDate: string | null;
    budget: number | null;
    actualCost: number | null;
    targetSize: number | null;
    expectedRevenue: number | null;
    ownerId: number | null;
    description: string | null;
    createdAt: string;
    updatedAt: string;
    // Tên khóa ngoại do BE resolve sẵn (INameResolver).
    ownerName: string | null;
    // Audit: BE tự đóng dấu (AuditInterceptor).
    createdBy: number | null;
    updatedBy: number | null;
    createdByName: string | null;
    updatedByName: string | null;
}

// payload tạo mới Chiến dịch — POST /api/campaigns
export interface CreateCampaignPayload {
    code: string;
    name: string;
    type: string | null;
    channel: string | null;
    startDate: string | null;
    endDate: string | null;
    budget: number | null;
    actualCost: number | null;
    targetSize: number | null;
    expectedRevenue: number | null;
    ownerId: number | null;
    description: string | null;
}

export interface UpdateCampaignPayload {
    name: string | null;
    type: string | null;
    channel: string | null;
    startDate: string | null;
    endDate: string | null;
    budget: number | null;
    actualCost: number | null;
    targetSize: number | null;
    expectedRevenue: number | null;
    ownerId: number | null;
    description: string | null;
}

export interface CampaignMemberResult {
    id: number;
    campaignId: number;
    leadId: number | null;
    contactId: number | null;
    name: string | null;
    email: string | null;
    phone: string | null;
    status: string;
    sentAt: string | null;
    respondedAt: string | null;
    createdAt: string;
}

export interface CreateCampaignMemberPayload {
    leadId: number | null;
    contactId: number | null;
    name: string | null;
    email: string | null;
    phone: string | null;
}

export interface CampaignStatsResult {
    campaignId: number;
    memberCount: number;
    sentCount: number;
    leadCount: number;
    opportunityCount: number;
    wonOpportunityCount: number;
    orderCount: number;
    revenue: number | null;
    actualCost: number | null;
}

export interface SendCampaignEmailPayload {
    subject: string;
    body: string;
}
