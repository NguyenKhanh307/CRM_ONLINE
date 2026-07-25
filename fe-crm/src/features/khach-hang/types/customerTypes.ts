export interface UpdateCustomerPayload {
    name: string;
    shortName?: string | null;
    type: string;
    taxCode: string | null;
    phone: string | null;
    email: string | null;
    website?: string | null;
    address: string | null;
    industry?: string | null;
    source: string | null;
    // status: KHÔNG gửi — đổi qua hành động activate/deactivate.
    creditDays?: number | null;
    creditLimit?: number | null;
    bankAccount?: string | null;
    bankName?: string | null;
    rating?: string | null;
    annualRevenue?: number | null;
    employeeSize?: string | null;
    isDistributor?: boolean;
    ownerId: number | null;
}

/** Payload tạo mới khách hàng — POST /api/customers. */
export interface CreateCustomerPayload {
    code: string;
    name: string;
    shortName: string | null;
    type: string;
    taxCode: string | null;
    phone: string | null;
    email: string | null;
    website: string | null;
    address: string | null;
    industry: string | null;
    source: string | null;
    // status: KHÔNG gửi — khách hàng luôn tạo ở trạng thái 'active'.
    creditDays: number | null;
    creditLimit: number | null;
    bankAccount: string | null;
    bankName: string | null;
    rating: string | null;
    annualRevenue: number | null;
    employeeSize: string | null;
    isDistributor: boolean;
    ownerId: number | null;
}

export interface CustomerResult {
    id: number;
    code: string;
    name: string;
    shortName: string | null;
    type: string;
    taxCode: string | null;
    phone: string | null;
    email: string | null;
    website: string | null;
    address: string | null;
    industry: string | null;
    source: string | null;
    status: string;
    creditDays: number | null;
    creditLimit: number | null;
    bankAccount: string | null;
    bankName: string | null;
    rating: string | null;
    annualRevenue: number | null;
    employeeSize: string | null;
    isDistributor: boolean;
    ownerId: number | null;
    // Audit: BE tự đóng dấu, client không gửi lên.
    createdBy: number | null;
    updatedBy: number | null;
    createdAt: string;
    updatedAt: string;
    // Tên khóa ngoại do BE resolve sẵn (INameResolver).
    ownerName: string | null;
    createdByName: string | null;
    updatedByName: string | null;
}
