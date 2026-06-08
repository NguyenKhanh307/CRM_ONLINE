export interface OpportunityResult {
    id: number;
    code: string;
    name: string;
    customerId: number | null;
    contactId: number | null;
    ownerId: number | null;
    stageId: number | null;
    amount: number | null;
    probability: number | null;
    expectedCloseDate: string | null;
    status: string;
    createdAt: string;
    updatedAt: string;
}
