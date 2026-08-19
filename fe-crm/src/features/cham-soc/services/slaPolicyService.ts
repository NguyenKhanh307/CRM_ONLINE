import axiosInstance from '@/core/axios/axiosInstance';
import type { ApiResponse } from '@/shared/types/api';

export interface SlaPolicyResult {
    id: number;
    code: string;
    name: string;
    priority: string;
    firstResponseHours: number;
    resolutionHours: number;
    isActive: boolean;
    createdAt?: string;
    updatedAt?: string;
}

// payload tạo chính sách SLA mới
export interface CreateSlaPolicyPayload {
    code: string;
    name: string;
    priority: string;
    firstResponseHours: number;
    resolutionHours: number;
    isActive: boolean;
}

// payload cập nhật chính sách SLA — không có `code` (không đổi được sau khi tạo)
export interface UpdateSlaPolicyPayload {
    name: string;
    priority: string;
    firstResponseHours: number;
    resolutionHours: number;
    isActive: boolean;
}

export const slaPolicyService = {
    // lấy toàn bộ chính sách SLA (không phân trang — bảng nhỏ)
    getList: () => axiosInstance.get<ApiResponse<SlaPolicyResult[]>>('/api/sla-policies'),
    // tạo chính sách SLA mới
    create: (body: CreateSlaPolicyPayload) =>
        axiosInstance.post<ApiResponse<SlaPolicyResult>>('/api/sla-policies', body),
    // cập nhật chính sách SLA theo id
    update: (id: number, body: UpdateSlaPolicyPayload) =>
        axiosInstance.put<ApiResponse<SlaPolicyResult>>(`/api/sla-policies/${id}`, body),
    // xóa chính sách SLA theo id
    remove: (id: number) => axiosInstance.delete(`/api/sla-policies/${id}`),
};
