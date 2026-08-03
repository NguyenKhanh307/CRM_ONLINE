import axiosInstance from '@/core/axios/axiosInstance';
import type { ApiResponse, PageResult } from '@/shared/types/api';
import type { RoleGroup, GroupPermission, GroupMember, GroupFormPayload, UserRoleAssignment } from '../types/phanQuyenTypes';

// service tập trung tất cả api calls cho trang Phân quyền
export const phanQuyenService = {
    // lấy danh sách nhóm người dùng (roles)
    getRoles: () =>
        axiosInstance.get<ApiResponse<PageResult<RoleGroup>>>('/api/roles', { params: { size: 100 } }),

    // lấy danh sách tất cả quyền hạn
    getPermissions: () =>
        axiosInstance.get<ApiResponse<PageResult<GroupPermission>>>('/api/permissions', { params: { size: 100 } }),

    // lấy danh sách tất cả người dùng (để chọn thêm thành viên)
    getUsers: () =>
        axiosInstance.get<ApiResponse<PageResult<GroupMember>>>('/api/users', { params: { size: 100 } }),

    // lấy danh sách quyền đã gán cho một nhóm
    getRolePermissions: (roleId: number) =>
        axiosInstance.get<ApiResponse<GroupPermission[]>>(`/api/roles/${roleId}/permissions`),

    // lấy danh sách thành viên của một nhóm
    getRoleMembers: (roleId: number) =>
        axiosInstance.get<ApiResponse<GroupMember[]>>(`/api/roles/${roleId}/members`),

    // lấy toàn bộ liên kết user-role hiện có (mỗi người chỉ thuộc một nhóm)
    getUserRoleAssignments: () =>
        axiosInstance.get<ApiResponse<UserRoleAssignment[]>>('/api/roles/user-assignments'),

    // tạo nhóm mới
    createRole: (body: GroupFormPayload) =>
        axiosInstance.post<ApiResponse<RoleGroup>>('/api/roles', body),

    // cập nhật tên/mô tả nhóm
    updateRole: (id: number, body: { name: string; description?: string }) =>
        axiosInstance.put<ApiResponse<RoleGroup>>(`/api/roles/${id}`, body),

    // xóa nhóm (chỉ non-system)
    deleteRole: (id: number) =>
        axiosInstance.delete(`/api/roles/${id}`),

    // gán quyền cho nhóm
    assignPermission: (roleId: number, permissionId: number) =>
        axiosInstance.post<ApiResponse<void>>(`/api/roles/${roleId}/permissions`, { permissionId }),

    // thu hồi quyền khỏi nhóm
    revokePermission: (roleId: number, permissionId: number) =>
        axiosInstance.delete(`/api/roles/${roleId}/permissions/${permissionId}`),

    // thêm thành viên vào nhóm
    addMember: (userId: number, roleId: number) =>
        axiosInstance.post<ApiResponse<void>>(`/api/users/${userId}/roles`, { roleId }),

    // xóa thành viên khỏi nhóm
    removeMember: (userId: number, roleId: number) =>
        axiosInstance.delete(`/api/users/${userId}/roles/${roleId}`),

    // thu hồi tài khoản nhân viên (status -> locked)
    revokeUser: (userId: number) =>
        axiosInstance.put<ApiResponse<GroupMember>>(`/api/users/${userId}/revoke`),

    // kích hoạt lại tài khoản nhân viên (status -> active)
    reactivateUser: (userId: number) =>
        axiosInstance.put<ApiResponse<GroupMember>>(`/api/users/${userId}/reactivate`),

    // cập nhật năm được xem data cho nhân viên
    updateUserDataAccess: (userId: number, dataAccessFromYear: number | null) =>
        axiosInstance.put<ApiResponse<GroupMember>>(`/api/users/${userId}`, { dataAccessFromYear }),
};
