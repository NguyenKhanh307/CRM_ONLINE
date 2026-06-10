export type UserStatusType = 'active' | 'inactive' | 'locked';

export interface UserResult {
    id: number;
    email: string;
    fullName: string;
    phone: string | null;
    avatarUrl: string | null;
    unitId: number | null;
    status: UserStatusType;
    lastLoginAt: string | null;
    createdAt: string;
    updatedAt: string;
}

export interface RegisterEmployeePayload {
    email: string;
    fullName: string;
    phone?: string;
    unitId?: number;
    roleId?: number;
}

export interface ActivateAccountPayload {
    token: string;
    newPassword: string;
}

export interface HandoverBulkPayload {
    ids: number[];
    toUserId: number;
    reason?: string;
}

export interface HandoverAllPayload {
    fromUserId: number;
    toUserId: number;
    reason?: string;
}
