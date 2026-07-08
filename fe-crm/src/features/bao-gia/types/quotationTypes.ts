export interface UpdateQuotationPayload {
    customerId: number | null;
    contactId: number | null;
    opportunityId?: number | null;
    ownerId: number | null;
    quoteDate: string | null;
    validUntil: string | null;
    currency?: string | null;
    exchangeRate?: number | null;
    // status: KHÔNG gửi — đổi qua hành động submit/approve/reject/send.
    subtotal: number | null;
    discount: number | null;
    tax: number | null;
    total: number | null;
    note: string | null;
}

/** Nội dung email báo giá mặc định (GET /api/quotations/{id}/email-draft). */
export interface QuotationEmailDraft {
    toEmail: string;
    recipientName: string;
    subject: string;
    body: string;
}

/** Payload gửi email báo giá — POST /api/quotations/{id}/send. */
export interface SendQuotationPayload {
    subject: string;
    body: string;
}

/** Một dòng hàng gửi kèm khi tạo báo giá. */
export interface QuotationItemPayload {
    productId: number;
    unit: string | null;
    quantity: number;
    unitPrice: number;
    discount: number;
    taxRate: number;
    amount: number;
    note: string | null;
}

/** Payload tạo mới báo giá — POST /api/quotations (kèm items[]). */
export interface CreateQuotationPayload {
    code: string;
    customerId: number | null;
    contactId: number | null;
    opportunityId: number | null;
    ownerId: number | null;
    quoteDate: string | null;
    validUntil: string | null;
    currency: string;
    exchangeRate: number;
    // status: KHÔNG gửi — báo giá luôn tạo ở trạng thái Nháp.
    subtotal: number;
    discount: number;
    tax: number;
    total: number;
    note: string | null;
    items: QuotationItemPayload[];
}

/** Dòng hàng trả về từ GET /api/quotations/{id}/items. */
export interface QuotationItemResult {
    id: number;
    quotationId: number;
    productId: number | null;
    unit: string | null;
    quantity: number;
    unitPrice: number;
    discount: number;
    taxRate: number;
    amount: number;
    note: string | null;
}

export interface QuotationResult {
    id: number;
    code: string;
    customerId: number | null;
    contactId: number | null;
    opportunityId: number | null;
    pricePolicyId: number | null;
    isPrimary: boolean;
    isLocked: boolean;
    ownerId: number | null;
    quoteDate: string | null;
    validUntil: string | null;
    currency: string | null;
    exchangeRate: number | null;
    status: string;
    subtotal: number | null;
    discount: number | null;
    tax: number | null;
    total: number | null;
    note: string | null;
    customerResponse: string | null;
    customerResponseNote: string | null;
    customerRespondedAt: string | null;
    /** Email đã gửi báo giá tới — chỉ có khi response của hành động send. */
    sentToEmail?: string | null;
    createdAt: string;
    updatedAt: string;
}
