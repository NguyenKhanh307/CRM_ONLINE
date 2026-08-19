export interface UpdateOpportunityPayload {
    name: string;
    opportunityType?: string | null;
    customerId: number | null;
    contactId: number | null;
    ownerId: number | null;
    stageId: number | null;
    campaignId: number | null;
    pricePolicyId: number | null;
    amount: number | null;
    source?: string | null;
    winLossReason?: string | null;
    description?: string | null;
    // status/probability: KHÔNG gửi — backend suy ra từ giai đoạn pipeline.
}

// một dòng hàng gửi kèm khi tạo cơ hội — không gửi amount, backend tự tính từ quantity/unitPrice/discount
export interface OpportunityItemPayload {
    productId: number;
    unit: string | null;
    quantity: number;
    unitPrice: number;
    discount: number;
    taxRate: number;
    note: string | null;
}

// payload tạo mới cơ hội — POST /api/opportunities (kèm items[])
export interface CreateOpportunityPayload {
    code: string;
    name: string;
    opportunityType: string | null;
    customerId: number | null;
    contactId: number | null;
    ownerId: number | null;
    stageId: number | null;
    campaignId: number | null;
    pricePolicyId: number | null;
    amount: number | null;
    source: string | null;
    winLossReason: string | null;
    description: string | null;
    // status/probability: KHÔNG gửi — backend suy ra từ giai đoạn pipeline.
    items: OpportunityItemPayload[];
}

// dòng hàng trả về từ GET /api/opportunities/{id}/items
export interface OpportunityItemResult {
    id: number;
    opportunityId: number;
    productId: number | null;
    quantity: number;
    unitPrice: number;
    discount: number;
    amount: number;
    note: string | null;
}

export interface OpportunityResult {
    id: number;
    code: string;
    name: string;
    opportunityType: string | null;
    customerId: number | null;
    contactId: number | null;
    ownerId: number | null;
    stageId: number | null;
    campaignId: number | null;
    pricePolicyId: number | null;
    amount: number | null;
    source: string | null;
    winLossReason: string | null;
    description: string | null;
    status: string;
    createdAt: string;
    updatedAt: string;
    // Tên khóa ngoại do BE resolve sẵn (INameResolver).
    customerName: string | null;
    contactName: string | null;
    ownerName: string | null;
    stageName: string | null;
    campaignName: string | null;
    // Audit: BE tự đóng dấu (AuditInterceptor).
    createdBy: number | null;
    updatedBy: number | null;
    createdByName: string | null;
    updatedByName: string | null;
}
