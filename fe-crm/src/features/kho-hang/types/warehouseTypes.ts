export interface UpdateWarehousePayload {
    name: string;
    address: string | null;
    isActive: boolean;
}

/** Payload tạo mới kho hàng — POST /api/warehouses. */
export interface CreateWarehousePayload {
    code: string;
    name: string;
    address: string | null;
    isActive: boolean;
}

export interface WarehouseResult {
    id: number;
    code: string;
    name: string;
    address: string | null;
    isActive: boolean;
    createdAt: string;
    updatedAt: string;
}
