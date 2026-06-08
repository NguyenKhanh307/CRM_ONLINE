export interface OrderResult {
    id: number;
    code: string;
    customerId: number | null;
    contactId: number | null;
    ownerId: number | null;
    warehouseId: number | null;
    orderType: string;
    orderDate: string | null;
    status: string;
    paymentStatus: string;
    subtotal: number | null;
    discount: number | null;
    tax: number | null;
    total: number | null;
    note: string | null;
    createdAt: string;
    updatedAt: string;
}
