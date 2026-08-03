export interface UpdateInvoicePayload {
    customerId: number | null;
    contactId: number | null;
    quotationId?: number | null;
    opportunityId?: number | null;
    // đơn hàng mà hóa đơn này thu tiền cho
    orderId?: number | null;
    campaignId?: number | null;
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

// một dòng hàng gửi kèm khi tạo Hóa đơn
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

// payload tạo mới Hóa đơn — POST /api/invoices (kèm items[])
export interface CreateInvoicePayload {
    code: string;
    customerId: number | null;
    contactId: number | null;
    quotationId: number | null;
    opportunityId: number | null;
    // đơn hàng mà hóa đơn này thu tiền cho
    orderId: number | null;
    campaignId: number | null;
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

// dòng hàng trả về từ GET /api/invoices/{id}/items
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

export type PaymentScheduleStatus = 'pending' | 'partial' | 'paid' | 'overdue';

// một đợt thanh toán của hóa đơn (GET /api/invoices/{id}/payment-schedules)
export interface InvoicePaymentScheduleResult {
    id: number;
    invoiceId: number;
    installmentNo: number | null;
    dueDate: string | null;
    amount: number | null;
    paidAmount: number | null;
    status: PaymentScheduleStatus;
    paidAt: string | null;
    note: string | null;
}

// payload tạo/sửa một đợt thanh toán
export interface PaymentSchedulePayload {
    installmentNo: number | null;
    dueDate: string | null;
    amount: number | null;
    paidAmount: number | null;
    status: PaymentScheduleStatus;
    paidAt: string | null;
    note: string | null;
}

export interface InvoiceResult {
    id: number;
    code: string;
    customerId: number | null;
    contactId: number | null;
    quotationId: number | null;
    opportunityId: number | null;
    // đơn hàng mà hóa đơn này thu tiền cho
    orderId: number | null;
    campaignId: number | null;
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
    // Tên khóa ngoại do BE resolve sẵn (INameResolver).
    customerName: string | null;
    contactName: string | null;
    quotationCode: string | null;
    orderCode: string | null;
    opportunityName: string | null;
    campaignName: string | null;
    ownerName: string | null;
    // Audit: BE tự đóng dấu (AuditInterceptor).
    createdBy: number | null;
    updatedBy: number | null;
    createdByName: string | null;
    updatedByName: string | null;
}

// một bản ghi doanh số/chia hoa hồng theo nhân viên trên hóa đơn (GET .../revenue-records)
export interface InvoiceRevenueRecordResult {
    id: number;
    invoiceId: number;
    userId: number;
    revenueAmount: number | null;
    percentage: number | null;
    note: string | null;
    createdAt: string;
}

// payload tạo mới bản ghi doanh số — POST .../revenue-records (userId bắt buộc, không sửa được sau khi tạo)
export interface CreateInvoiceRevenueRecordPayload {
    userId: number;
    revenueAmount: number | null;
    percentage: number | null;
    note: string | null;
}

// payload sửa bản ghi doanh số — PUT .../revenue-records/{id} (userId/invoiceId KHÔNG sửa được)
export interface UpdateInvoiceRevenueRecordPayload {
    revenueAmount: number | null;
    percentage: number | null;
    note: string | null;
}
