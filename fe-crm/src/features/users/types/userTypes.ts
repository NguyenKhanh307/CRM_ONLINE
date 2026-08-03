export type UserStatusType = 'active' | 'inactive' | 'locked';

export interface UserResult {
    id: number;
    email: string;
    fullName: string;
    phone: string | null;
    avatarUrl: string | null;
    status: UserStatusType;
    lastLoginAt: string | null;
    createdAt: string;
    updatedAt: string;
}

export interface RegisterEmployeePayload {
    email: string;
    fullName: string;
    phone?: string;
    roleId?: number;
}

export interface ActivateAccountPayload {
    token: string;
    newPassword: string;
}

export interface HandoverAllPayload {
    fromUserId: number;
    toUserId: number;
    reason?: string;
}

// hồ sơ của người dùng đang đăng nhập (GET /api/auth/me)
export type ProfileResult = UserResult;

// payload cập nhật hồ sơ cá nhân (PUT /api/auth/me)
export interface UpdateProfilePayload {
    fullName: string;
    phone?: string;
    avatarUrl?: string;
}

// payload đổi mật khẩu (POST /api/auth/change-password)
export interface ChangePasswordPayload {
    currentPassword: string;
    newPassword: string;
}
