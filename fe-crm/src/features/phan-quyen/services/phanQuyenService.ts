import axiosInstance from '@/core/axios/axiosInstance';
import type { ApiResponse, PageResult } from '@/shared/types/api';
import type { RoleGroup, GroupPermission, GroupMember, GroupFormPayload } from '@/features/phan-quyen/types/phanQuyenTypes';

/**
 * Service tập trung tất cả API calls cho trang Phân quyền.
 */
export const phanQuyenService = {
    /** Lấy danh sách nhóm người dùng (roles). */
    getRoles: () =>
        axiosInstance.get<ApiResponse<PageResult<RoleGroup>>>('/api/roles', { params: { size: 100 } }),

    /** Lấy danh sách tất cả quyền hạn. */
    getPermissions: () =>
        axiosInstance.get<ApiResponse<PageResult<GroupPermission>>>('/api/permissions', { params: { size: 100 } }),

    /** Lấy danh sách tất cả người dùng (để chọn thêm thành viên). */
    getUsers: () =>
        axiosInstance.get<ApiResponse<PageResult<GroupMember>>>('/api/users', { params: { size: 100 } }),

    /** Lấy danh sách quyền đã gán cho một nhóm. */
    getRolePermissions: (roleId: number) =>
        axiosInstance.get<ApiResponse<GroupPermission[]>>(`/api/roles/${roleId}/permissions`),

    /** Lấy danh sách thành viên của một nhóm. */
    getRoleMembers: (roleId: number) =>
        axiosInstance.get<ApiResponse<GroupMember[]>>(`/api/roles/${roleId}/members`),

    /** Tạo nhóm mới. */
    createRole: (body: GroupFormPayload) =>
        axiosInstance.post<ApiResponse<RoleGroup>>('/api/roles', body),

    /** Cập nhật tên/mô tả nhóm. */
    updateRole: (id: number, body: { name: string; description?: string }) =>
        axiosInstance.put<ApiResponse<RoleGroup>>(`/api/roles/${id}`, body),

    /** Xóa nhóm (chỉ non-system). */
    deleteRole: (id: number) =>
        axiosInstance.delete(`/api/roles/${id}`),

    /** Gán quyền cho nhóm. */
    assignPermission: (roleId: number, permissionId: number) =>
        axiosInstance.post<ApiResponse<void>>(`/api/roles/${roleId}/permissions`, { permissionId }),

    /** Thu hồi quyền khỏi nhóm. */
    revokePermission: (roleId: number, permissionId: number) =>
        axiosInstance.delete(`/api/roles/${roleId}/permissions/${permissionId}`),

    /** Thêm thành viên vào nhóm. */
    addMember: (userId: number, roleId: number) =>
        axiosInstance.post<ApiResponse<void>>(`/api/users/${userId}/roles`, { roleId }),

    /** Xóa thành viên khỏi nhóm. */
    removeMember: (userId: number, roleId: number) =>
        axiosInstance.delete(`/api/users/${userId}/roles/${roleId}`),
};
