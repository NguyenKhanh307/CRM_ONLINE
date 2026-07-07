import axiosInstance from '@/core/axios/axiosInstance';
import type { ApiResponse, PageResult, PageParams } from '@/shared/types/api';
import type { CreateInvoicePayload, InvoiceItemPayload, InvoiceItemResult, InvoiceResult, UpdateInvoicePayload, InvoicePaymentScheduleResult, PaymentSchedulePayload } from '../types/invoiceTypes';
import type { ImportOptions, ImportBulkResult } from '@/shared/components/import/importTypes';

export const invoiceService = {
    getList: (params?: PageParams) =>
        axiosInstance.get<ApiResponse<PageResult<InvoiceResult>>>('/api/invoices', { params }),
    create: (payload: CreateInvoicePayload) =>
        axiosInstance.post<ApiResponse<InvoiceResult>>('/api/invoices', payload),
    getItems: (invoiceId: number) =>
        axiosInstance.get<ApiResponse<InvoiceItemResult[]>>(`/api/invoices/${invoiceId}/items`),
    createItem: (invoiceId: number, payload: InvoiceItemPayload) =>
        axiosInstance.post<ApiResponse<InvoiceItemResult>>(`/api/invoices/${invoiceId}/items`, payload),
    updateItem: (invoiceId: number, itemId: number, payload: InvoiceItemPayload) =>
        axiosInstance.put<ApiResponse<InvoiceItemResult>>(`/api/invoices/${invoiceId}/items/${itemId}`, payload),
    deleteItem: (invoiceId: number, itemId: number) =>
        axiosInstance.delete(`/api/invoices/${invoiceId}/items/${itemId}`),
    getById: (id: number) =>
        axiosInstance.get<ApiResponse<InvoiceResult>>(`/api/invoices/${id}`),
    update: (id: number, payload: UpdateInvoicePayload) =>
        axiosInstance.put<ApiResponse<InvoiceResult>>(`/api/invoices/${id}`, payload),
    remove: (id: number) =>
        axiosInstance.delete(`/api/invoices/${id}`),
    importBulk: (rows: Record<string, unknown>[], options: ImportOptions) =>
        axiosInstance.post<ApiResponse<ImportBulkResult>>('/api/invoices/import-bulk', {
            importType: options.importType,
            ownerMode: options.ownerMode,
            specificOwnerId: options.specificOwnerId ?? null,
            ownerFileColumn: options.ownerFileColumn ?? null,
            rows,
        }),
    handoverBulk: (payload: { ids: number[]; toUserId: number; reason?: string }) =>
        axiosInstance.post('/api/invoices/handover-bulk', payload),
    /** Phát hành hóa đơn (draft → sent, khóa dữ liệu). */
    issue: (id: number) => axiosInstance.post<ApiResponse<InvoiceResult>>(`/api/invoices/${id}/issue`),
    /** Hủy hóa đơn (→ cancelled). */
    cancel: (id: number) => axiosInstance.post<ApiResponse<InvoiceResult>>(`/api/invoices/${id}/cancel`),

    /** Danh sách đợt thanh toán của hóa đơn. */
    getPaymentSchedules: (invoiceId: number) =>
        axiosInstance.get<ApiResponse<InvoicePaymentScheduleResult[]>>(`/api/invoices/${invoiceId}/payment-schedules`),
    /** Thêm một đợt thanh toán → BE tự suy ra paymentStatus. */
    addPaymentSchedule: (invoiceId: number, payload: PaymentSchedulePayload) =>
        axiosInstance.post<ApiResponse<InvoicePaymentScheduleResult>>(`/api/invoices/${invoiceId}/payment-schedules`, payload),
    /** Cập nhật một đợt thanh toán. */
    updatePaymentSchedule: (invoiceId: number, id: number, payload: PaymentSchedulePayload) =>
        axiosInstance.put<ApiResponse<InvoicePaymentScheduleResult>>(`/api/invoices/${invoiceId}/payment-schedules/${id}`, payload),
    /** Xóa một đợt thanh toán. */
    deletePaymentSchedule: (invoiceId: number, id: number) =>
        axiosInstance.delete(`/api/invoices/${invoiceId}/payment-schedules/${id}`),
};
