export interface CustomerResult {
    id: number;
    code: string;
    name: string;
    type: string;
    taxCode: string | null;
    phone: string | null;
    email: string | null;
    address: string | null;
    source: string | null;
    status: string;
    ownerId: number | null;
    unitId: number | null;
    createdAt: string;
    updatedAt: string;
}
