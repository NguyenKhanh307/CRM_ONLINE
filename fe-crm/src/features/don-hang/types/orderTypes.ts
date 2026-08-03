export interface UpdateOrderPayload {
    customerId: number | null;
    contactId: number | null;
    quotationId?: number | null;
    opportunityId?: number | null;
    campaignId?: number | null;
    ownerId: number | null;
    orderDate: string | null;
    deliveryDate?: string | null;
    currency?: string | null;
    exchangeRate?: number | null;
    // status / isLocked: KHÔNG gửi — đổi qua hành động.
    billingAddress?: string | null;
    taxCode?: string | null;
    subtotal: number | null;
    discount: number | null;
    tax: number | null;
    total: number | null;
    note: string | null;
}

// một dòng hàng gửi kèm khi tạo Đơn hàng
export interface OrderItemPayload {
    productId: number;
    unit: string | null;
    quantity: number;
    unitPrice: number;
    discount: number;
    taxRate: number;
    amount: number;
    note: string | null;
}

// payload tạo mới Đơn hàng — POST /api/orders (kèm items[])
export interface CreateOrderPayload {
    code: string;
    customerId: number | null;
    contactId: number | null;
    quotationId: number | null;
    opportunityId: number | null;
    campaignId: number | null;
    ownerId: number | null;
    orderDate: string | null;
    deliveryDate: string | null;
    currency: string;
    exchangeRate: number;
    // status: KHÔNG gửi — đơn hàng luôn tạo ở 'draft'.
    billingAddress: string | null;
    taxCode: string | null;
    subtotal: number;
    discount: number;
    tax: number;
    total: number;
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

export interface OrderResult {
    id: number;
    code: string;
    customerId: number | null;
    contactId: number | null;
    quotationId: number | null;
    opportunityId: number | null;
    campaignId: number | null;
    ownerId: number | null;
    orderDate: string | null;
    deliveryDate: string | null;
    currency: string | null;
    exchangeRate: number | null;
    status: string;
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
    opportunityName: string | null;
    campaignName: string | null;
    ownerName: string | null;
    // Audit: BE tự đóng dấu (AuditInterceptor).
    createdBy: number | null;
    updatedBy: number | null;
    createdByName: string | null;
    updatedByName: string | null;
}
