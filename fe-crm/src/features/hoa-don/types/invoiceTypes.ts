export interface UpdateInvoicePayload {
    orderId?: number | null;
    ownerId: number | null;
    invoiceDate: string | null;
    dueDate?: string | null;
    // status / paymentStatus / isLocked: KHÔNG gửi — đổi qua hành động & suy ra từ đợt thanh toán.
    note: string | null;
}

// một dòng hàng gửi kèm khi tạo/sửa Hóa đơn — không gửi amount, backend tự tính
export interface InvoiceItemPayload {
    productId: number;
    unit: string | null;
    quantity: number;
    unitPrice: number;
    discount: number;
    taxRate: number;
    note: string | null;
}

// payload tạo mới Hóa đơn — POST /api/invoices (kèm items[])
export interface CreateInvoicePayload {
    code: string;
    // đơn hàng mà hóa đơn này thu tiền cho
    orderId: number | null;
    ownerId: number | null;
    invoiceDate: string | null;
    dueDate: string | null;
    // status / paymentStatus: KHÔNG gửi — hóa đơn luôn tạo ở 'draft'/'unpaid'.
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
    bankName: string | null;
    bankAccount: string | null;
    note: string | null;
}

// payload tạo/sửa một đợt thanh toán
export interface PaymentSchedulePayload {
    installmentNo: number | null;
    dueDate: string | null;
    amount: number | null;
    paidAmount: number | null;
    status: PaymentScheduleStatus;
    bankName: string | null;
    bankAccount: string | null;
    note: string | null;
}

// customerId/contactId/quotationId/opportunityId/campaignId không còn — mọi liên kết tra qua
// orderId (Order -> quotationId -> Quotation vẫn giữ đủ các field này)
export interface InvoiceResult {
    id: number;
    code: string;
    // đơn hàng mà hóa đơn này thu tiền cho
    orderId: number | null;
    ownerId: number | null;
    invoiceDate: string | null;
    dueDate: string | null;
    status: string;
    paymentStatus: string;
    isLocked: boolean;
    subtotal: number | null;
    discount: number | null;
    tax: number | null;
    total: number | null;
    note: string | null;
    createdAt: string;
    updatedAt: string;
    // Tên khóa ngoại do BE resolve sẵn (INameResolver).
    orderCode: string | null;
    ownerName: string | null;
    // Audit: BE tự đóng dấu (AuditInterceptor).
    createdBy: number | null;
    updatedBy: number | null;
    createdByName: string | null;
    updatedByName: string | null;
}
