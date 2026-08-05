export interface UpdateQuotationPayload {
    customerId: number | null;
    contactId: number | null;
    opportunityId?: number | null;
    // chính sách giá áp dụng — nguồn đơn giá tự điền cho dòng hàng
    pricePolicyId?: number | null;
    ownerId: number | null;
    quoteDate: string | null;
    validUntil: string | null;
    // status: KHÔNG gửi — đổi qua hành động submit/approve/reject/send.
    note: string | null;
}

// một bước phê duyệt báo giá (GET /api/quotations/{id}/approvals)
export interface QuotationApprovalResult {
    id: number;
    quotationId: number;
    approverId: number | null;
    status: string;
    comment: string | null;
    approvedAt: string | null;
    createdAt: string;
}

// nội dung email báo giá mặc định (GET /api/quotations/{id}/email-draft)
export interface QuotationEmailDraft {
    toEmail: string;
    recipientName: string;
    subject: string;
    body: string;
}

// payload gửi email báo giá — POST /api/quotations/{id}/send
export interface SendQuotationPayload {
    // người nhận chính (bỏ trống -> BE lấy email của liên hệ/khách hàng trên báo giá)
    to?: string;
    // CC — nhiều email cách nhau bởi dấu phẩy/chấm phẩy
    cc?: string;
    // BCC — nhiều email cách nhau bởi dấu phẩy/chấm phẩy
    bcc?: string;
    subject: string;
    body: string;
}

// một dòng hàng gửi kèm khi tạo/sửa báo giá — không gửi amount, backend tự tính
export interface QuotationItemPayload {
    productId: number;
    unit: string | null;
    quantity: number;
    unitPrice: number;
    discount: number;
    taxRate: number;
    note: string | null;
    // khách chấp nhận/từ chối riêng dòng này khi phản hồi báo giá — chỉ dùng khi cập nhật
    lineStatus?: 'pending' | 'accepted' | 'rejected';
}

// payload tạo mới báo giá — POST /api/quotations (kèm items[])
export interface CreateQuotationPayload {
    code: string;
    customerId: number | null;
    contactId: number | null;
    opportunityId: number | null;
    // chính sách giá áp dụng — nguồn đơn giá tự điền cho dòng hàng
    pricePolicyId: number | null;
    ownerId: number | null;
    quoteDate: string | null;
    validUntil: string | null;
    // status: KHÔNG gửi — báo giá luôn tạo ở trạng thái Nháp.
    note: string | null;
    items: QuotationItemPayload[];
}

// dòng hàng trả về từ GET /api/quotations/{id}/items
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
    lineStatus: 'pending' | 'accepted' | 'rejected';
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
    status: string;
    subtotal: number | null;
    discount: number | null;
    tax: number | null;
    total: number | null;
    note: string | null;
    customerResponse: string | null;
    customerResponseNote: string | null;
    // email đã gửi báo giá tới — chỉ có khi response của hành động send
    sentToEmail?: string | null;
    createdAt: string;
    updatedAt: string;
    // Tên khóa ngoại do BE resolve sẵn (INameResolver).
    customerName: string | null;
    contactName: string | null;
    opportunityName: string | null;
    ownerName: string | null;
    // Audit: BE tự đóng dấu (AuditInterceptor).
    createdBy: number | null;
    updatedBy: number | null;
    createdByName: string | null;
    updatedByName: string | null;
}
