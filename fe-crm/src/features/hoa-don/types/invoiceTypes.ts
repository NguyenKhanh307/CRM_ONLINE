export interface UpdateInvoicePayload {
    customerId: number | null;
    contactId: number | null;
    quotationId?: number | null;
    opportunityId?: number | null;
    ownerId: number | null;
    invoiceDate: string | null;
    dueDate?: string | null;
    currency?: string | null;
    exchangeRate?: number | null;
    // status / paymentStatus / isLocked: KHÔNG gửi — đổi qua hành động & suy ra từ đợt thanh toán.
    billingAddress?: string | null;
    taxCode?: string | null;
    subtotal: number | null;
    discount: number | null;
    tax: number | null;
    total: number | null;
    note: string | null;
}

/** Một dòng hàng gửi kèm khi tạo Hóa đơn. */
export interface InvoiceItemPayload {
    productId: number;
    unit: string | null;
    quantity: number;
    unitPrice: number;
    discount: number;
    taxRate: number;
    amount: number;
    note: string | null;
}

/** Payload tạo mới Hóa đơn — POST /api/invoices (kèm items[]). */
export interface CreateInvoicePayload {
    code: string;
    customerId: number | null;
    contactId: number | null;
    quotationId: number | null;
    opportunityId: number | null;
    ownerId: number | null;
    invoiceDate: string | null;
    dueDate: string | null;
    currency: string;
    exchangeRate: number;
    // status / paymentStatus: KHÔNG gửi — hóa đơn luôn tạo ở 'draft'/'unpaid'.
    billingAddress: string | null;
    taxCode: string | null;
    subtotal: number;
    discount: number;
    tax: number;
    total: number;
    note: string | null;
    items: InvoiceItemPayload[];
}

/** Dòng hàng trả về từ GET /api/invoices/{id}/items. */
export interface InvoiceItemResult {
    id: number;
    invoiceId: number;
    productId: number | null;
    unit: string | null;
    quantity: number;
    unitPrice: number;
    discount: number;
    taxRate: number;
    amount: number;
    note: string | null;
}

export interface InvoiceResult {
    id: number;
    code: string;
    customerId: number | null;
    contactId: number | null;
    quotationId: number | null;
    opportunityId: number | null;
    ownerId: number | null;
    invoiceDate: string | null;
    dueDate: string | null;
    currency: string | null;
    exchangeRate: number | null;
    status: string;
    paymentStatus: string;
    isLocked: boolean;
    billingAddress: string | null;
    taxCode: string | null;
    subtotal: number | null;
    discount: number | null;
    tax: number | null;
    total: number | null;
    note: string | null;
    createdAt: string;
    updatedAt: string;
}
