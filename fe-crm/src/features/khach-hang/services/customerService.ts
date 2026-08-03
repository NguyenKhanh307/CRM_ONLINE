import axiosInstance from '@/core/axios/axiosInstance';
import type { ApiResponse, PageResult, PageParams } from '@/shared/types/api';
import type {
    CreateCustomerPayload, CustomerResult, UpdateCustomerPayload,
    CustomerShareResult, AssignCustomerSharePayload,
} from '../types/customerTypes';
import type { ImportOptions, ImportBulkResult } from '@/shared/components/import/importTypes';
import type { CustomerRelatedResult } from '@/shared/types/related';

export const customerService = {
    getList: (params?: PageParams) =>
        axiosInstance.get<ApiResponse<PageResult<CustomerResult>>>('/api/customers', { params }),
    create: (payload: CreateCustomerPayload) =>
        axiosInstance.post<ApiResponse<CustomerResult>>('/api/customers', payload),
    getById: (id: number) =>
        axiosInstance.get<ApiResponse<CustomerResult>>(`/api/customers/${id}`),
    update: (id: number, payload: UpdateCustomerPayload) =>
        axiosInstance.put<ApiResponse<CustomerResult>>(`/api/customers/${id}`, payload),
    remove: (id: number) =>
        axiosInstance.delete(`/api/customers/${id}`),
    importBulk: (rows: Record<string, unknown>[], options: ImportOptions) =>
        axiosInstance.post<ApiResponse<ImportBulkResult>>('/api/customers/import-bulk', {
            importType: options.importType,
            ownerMode: options.ownerMode,
            specificOwnerId: options.specificOwnerId ?? null,
            ownerFileColumn: options.ownerFileColumn ?? null,
            rows,
        }),
    handoverBulk: (payload: { ids: number[]; toUserId: number; reason?: string }) =>
        axiosInstance.post('/api/customers/handover-bulk', payload),
    // bản ghi liên quan cho trang chi tiết 360° (liên hệ, cơ hội, báo giá, đơn, hóa đơn, phiếu CS, hoạt động)
    getRelated: (id: number) =>
        axiosInstance.get<ApiResponse<CustomerRelatedResult>>(`/api/customers/${id}/related`),
    // kích hoạt khách hàng (-> active)
    activate: (id: number) => axiosInstance.post<ApiResponse<CustomerResult>>(`/api/customers/${id}/activate`),
    // ngừng hoạt động khách hàng (-> inactive)
    deactivate: (id: number) => axiosInstance.post<ApiResponse<CustomerResult>>(`/api/customers/${id}/deactivate`),
    // danh sách chia sẻ khách hàng này cho các user khác
    getShares: (customerId: number) =>
        axiosInstance.get<ApiResponse<CustomerShareResult[]>>(`/api/customers/${customerId}/shares`),
    // gán chia sẻ khách hàng cho một user (view/edit)
    assignShare: (customerId: number, payload: AssignCustomerSharePayload) =>
        axiosInstance.post<ApiResponse<CustomerShareResult>>(`/api/customers/${customerId}/shares`, payload),
    // thu hồi chia sẻ
    revokeShare: (customerId: number, userId: number) =>
        axiosInstance.delete(`/api/customers/${customerId}/shares/${userId}`),
};
