export interface QuotationResult {
    id: number;
    code: string;
    customerId: number | null;
    contactId: number | null;
    ownerId: number | null;
    quoteDate: string | null;
    validUntil: string | null;
    status: string;
    subtotal: number | null;
    discount: number | null;
    tax: number | null;
    total: number | null;
    note: string | null;
    createdAt: string;
    updatedAt: string;
}
