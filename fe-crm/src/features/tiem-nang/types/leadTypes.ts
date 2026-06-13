export interface UpdateLeadPayload {
    name: string;
    companyName?: string | null;
    leadType?: string | null;
    ownerId: number | null;
    customerId: number | null;
    contactId: number | null;
    title?: string | null;
    department?: string | null;
    taxCode?: string | null;
    website?: string | null;
    industry?: string | null;
    source: string | null;
    status: string;
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
    createdAt: string;
    updatedAt: string;
}
