export interface ContactResult {
    id: number;
    customerId: number | null;
    assignedUserId: number | null;
    fullName: string;
    position: string | null;
    email: string | null;
    gender: string | null;
    dateOfBirth: string | null;
    address: string | null;
    isPrimary: boolean;
    createdAt: string;
    updatedAt: string;
}
