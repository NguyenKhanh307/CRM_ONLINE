export interface UpdateOpportunityPayload {
    name: string;
    opportunityType?: string | null;
    customerId: number | null;
    contactId: number | null;
    ownerId: number | null;
    stageId: number | null;
    amount: number | null;
    expectedRevenue?: number | null;
    probability: number | null;
    expectedCloseDate: string | null;
    source?: string | null;
    winLossReason?: string | null;
    description?: string | null;
    status: string;
}

/** Một dòng hàng gửi kèm khi tạo cơ hội. */
export interface OpportunityItemPayload {
    productId: number;
    quantity: number;
    unitPrice: number;
    discount: number;
    amount: number;
    note: string | null;
}

/** Payload tạo mới cơ hội — POST /api/opportunities (kèm items[]). */
export interface CreateOpportunityPayload {
    code: string;
    name: string;
    opportunityType: string | null;
    customerId: number | null;
    contactId: number | null;
    ownerId: number | null;
    stageId: number | null;
    amount: number | null;
    expectedRevenue: number | null;
    probability: number | null;
    expectedCloseDate: string | null;
    source: string | null;
    winLossReason: string | null;
    description: string | null;
    status: string;
    items: OpportunityItemPayload[];
}

/** Dòng hàng trả về từ GET /api/opportunities/{id}/items. */
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
    amount: number | null;
    expectedRevenue: number | null;
    probability: number | null;
    expectedCloseDate: string | null;
    source: string | null;
    winLossReason: string | null;
    description: string | null;
    status: string;
    createdAt: string;
    updatedAt: string;
}
