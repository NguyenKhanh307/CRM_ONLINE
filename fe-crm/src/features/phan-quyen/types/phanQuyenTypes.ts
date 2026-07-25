/** Nhóm người dùng (mapping với bảng roles). */
export interface RoleGroup {
    id: number;
    code: string;
    name: string;
    description: string | null;
    isSystem: boolean;
    createdAt: string;
    updatedAt: string;
}

/** Quyền hạn (mapping với bảng permissions). */
export interface GroupPermission {
    id: number;
    code: string;
    name: string;
    module: string;
    description: string | null;
    createdAt: string;
    updatedAt: string;
}

/** Thành viên trong nhóm (mapping với bảng users). */
export interface GroupMember {
    id: number;
    fullName: string;
    email: string;
    status: string;
    phone: string | null;
    avatarUrl: string | null;
    dataAccessFromYear: number | null;
}

/** Payload tạo/sửa nhóm. */
export interface GroupFormPayload {
    code?: string;
    name: string;
    description?: string;
    isSystem?: boolean;
}
