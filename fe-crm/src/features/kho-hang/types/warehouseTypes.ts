export interface WarehouseResult {
    id: number;
    code: string;
    name: string;
    address: string | null;
    managerId: number | null;
    isActive: boolean;
    createdAt: string;
    updatedAt: string;
}
