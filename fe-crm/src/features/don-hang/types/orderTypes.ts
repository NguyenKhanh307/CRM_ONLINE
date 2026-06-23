export interface UpdateOrderPayload {
    customerId: number | null;
    contactId: number | null;
    quotationId?: number | null;
    opportunityId?: number | null;
    ownerId: number | null;
    executorUnitId?: number | null;
    parentOrderId?: number | null;
    orderType: string;
    orderDate: string | null;
    currency?: string | null;
    exchangeRate?: number | null;
    status: string;
    paymentStatus: string;
    creditDays?: number | null;
    paymentDueDate?: string | null;
    isInvoiced?: boolean;
    receiverName?: string | null;
    receiverPhone?: string | null;
    subtotal: number | null;
    discount: number | null;
    tax: number | null;
    total: number | null;
    note: string | null;
}

/** Một dòng hàng gửi kèm khi tạo đơn hàng. */
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

/** Payload tạo mới đơn hàng — POST /api/orders (kèm items[]). */
export interface CreateOrderPayload {
    code: string;
    customerId: number | null;
    contactId: number | null;
    quotationId: number | null;
    opportunityId: number | null;
    ownerId: number | null;
    executorUnitId: number | null;
    parentOrderId: number | null;
    orderType: string;
    orderDate: string | null;
    currency: string;
    exchangeRate: number;
    status: string;
    paymentStatus: string;
    creditDays: number | null;
    paymentDueDate: string | null;
    isInvoiced: boolean;
    receiverName: string | null;
    receiverPhone: string | null;
    subtotal: number;
    discount: number;
    tax: number;
    total: number;
    note: string | null;
    items: OrderItemPayload[];
}

/** Dòng hàng trả về từ GET /api/orders/{id}/items. */
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
    ownerId: number | null;
    executorUnitId: number | null;
    parentOrderId: number | null;
    orderType: string;
    orderDate: string | null;
    currency: string | null;
    exchangeRate: number | null;
    status: string;
    paymentStatus: string;
    creditDays: number | null;
    paymentDueDate: string | null;
    isInvoiced: boolean;
    receiverName: string | null;
    receiverPhone: string | null;
    subtotal: number | null;
    discount: number | null;
    tax: number | null;
    total: number | null;
    note: string | null;
    createdAt: string;
    updatedAt: string;
}
