export interface UpdateOrderPayload {
    quotationId?: number | null;
    ownerId: number | null;
    orderDate: string | null;
    deliveryDate?: string | null;
    // status / isLocked: KHÔNG gửi — đổi qua hành động.
    note: string | null;
}

// một dòng hàng gửi kèm khi tạo/sửa Đơn hàng — không gửi amount, backend tự tính
export interface OrderItemPayload {
    productId: number;
    unit: string | null;
    quantity: number;
    unitPrice: number;
    discount: number;
    taxRate: number;
    note: string | null;
}

// payload tạo mới Đơn hàng — POST /api/orders (kèm items[])
export interface CreateOrderPayload {
    code: string;
    quotationId: number | null;
    ownerId: number | null;
    orderDate: string | null;
    deliveryDate: string | null;
    // status: KHÔNG gửi — đơn hàng luôn tạo ở 'draft'.
    note: string | null;
    items: OrderItemPayload[];
}

// dòng hàng trả về từ GET /api/orders/{id}/items
export interface OrderItemResult {
    id: number;
    orderId: number;
    productId: number | null;
    unit: string | null;
    quantity: number;
    unitPrice: number;
    discount: number;
    taxRate: number;
    amount: number;
    note: string | null;
}

// customerId/contactId/opportunityId/campaignId không còn — mọi liên kết tra qua quotationId
// (Quotation vẫn giữ đủ các field này)
export interface OrderResult {
    id: number;
    code: string;
    quotationId: number | null;
    ownerId: number | null;
    orderDate: string | null;
    deliveryDate: string | null;
    status: string;
    isLocked: boolean;
    subtotal: number | null;
    discount: number | null;
    tax: number | null;
    total: number | null;
    note: string | null;
    createdAt: string;
    updatedAt: string;
    // Tên khóa ngoại do BE resolve sẵn (INameResolver).
    quotationCode: string | null;
    ownerName: string | null;
    // Audit: BE tự đóng dấu (AuditInterceptor).
    createdBy: number | null;
    updatedBy: number | null;
    createdByName: string | null;
    updatedByName: string | null;
}
