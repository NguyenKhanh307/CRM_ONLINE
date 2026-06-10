export interface UpdateLeadPayload {
    name: string;
    ownerId: number | null;
    customerId: number | null;
    contactId: number | null;
    source: string | null;
    status: string;
    estimatedValue: number | null;
    phone: string | null;
    email: string | null;
    note: string | null;
}

export interface LeadResult {
    id: number;
    code: string;
    name: string;
    ownerId: number | null;
    customerId: number | null;
    contactId: number | null;
    source: string | null;
    status: string;
    estimatedValue: number | null;
    phone: string | null;
    email: string | null;
    note: string | null;
    createdAt: string;
    updatedAt: string;
}
