import axiosInstance from '@/core/axios/axiosInstance';
import type { ApiResponse, PageResult, PageParams } from '@/shared/types/api';
import type { CreateOrderPayload, OrderItemPayload, OrderItemResult, OrderResult, UpdateOrderPayload } from '../types/orderTypes';
import type { ImportOptions, ImportBulkResult } from '@/shared/components/import/importTypes';

export const orderService = {
    getList: (params?: PageParams) =>
        axiosInstance.get<ApiResponse<PageResult<OrderResult>>>('/api/orders', { params }),
    create: (payload: CreateOrderPayload) =>
        axiosInstance.post<ApiResponse<OrderResult>>('/api/orders', payload),
    getItems: (orderId: number) =>
        axiosInstance.get<ApiResponse<OrderItemResult[]>>(`/api/orders/${orderId}/items`),
    createItem: (orderId: number, payload: OrderItemPayload) =>
        axiosInstance.post<ApiResponse<OrderItemResult>>(`/api/orders/${orderId}/items`, payload),
    updateItem: (orderId: number, itemId: number, payload: OrderItemPayload) =>
        axiosInstance.put<ApiResponse<OrderItemResult>>(`/api/orders/${orderId}/items/${itemId}`, payload),
    deleteItem: (orderId: number, itemId: number) =>
        axiosInstance.delete(`/api/orders/${orderId}/items/${itemId}`),
    getById: (id: number) =>
        axiosInstance.get<ApiResponse<OrderResult>>(`/api/orders/${id}`),
    update: (id: number, payload: UpdateOrderPayload) =>
        axiosInstance.put<ApiResponse<OrderResult>>(`/api/orders/${id}`, payload),
    remove: (id: number) =>
        axiosInstance.delete(`/api/orders/${id}`),
    importBulk: (rows: Record<string, unknown>[], options: ImportOptions) =>
        axiosInstance.post<ApiResponse<ImportBulkResult>>('/api/orders/import-bulk', {
            importType: options.importType,
            ownerMode: options.ownerMode,
            specificOwnerId: options.specificOwnerId ?? null,
            ownerFileColumn: options.ownerFileColumn ?? null,
            rows,
        }),
    handoverBulk: (payload: { ids: number[]; toUserId: number; reason?: string }) =>
        axiosInstance.post('/api/orders/handover-bulk', payload),
    /** Xác nhận đơn hàng (draft → confirmed). */
    confirm: (id: number) => axiosInstance.post<ApiResponse<OrderResult>>(`/api/orders/${id}/confirm`),
    /** Chuyển đơn hàng sang đang xử lý (confirmed → processing). */
    process: (id: number) => axiosInstance.post<ApiResponse<OrderResult>>(`/api/orders/${id}/process`),
    /** Hoàn tất đơn hàng (→ completed). */
    complete: (id: number) => axiosInstance.post<ApiResponse<OrderResult>>(`/api/orders/${id}/complete`),
    /** Hủy đơn hàng (→ cancelled). */
    cancel: (id: number) => axiosInstance.post<ApiResponse<OrderResult>>(`/api/orders/${id}/cancel`),
    /** Xuất hóa đơn từ đơn hàng (1-1). */
    createInvoice: (id: number) => axiosInstance.post<ApiResponse<unknown>>(`/api/orders/${id}/create-invoice`),
};
