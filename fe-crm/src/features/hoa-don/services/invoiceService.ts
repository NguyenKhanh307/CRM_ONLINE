import axiosInstance from '@/core/axios/axiosInstance';
import type { ApiResponse, PageResult, PageParams } from '@/shared/types/api';
import type {
    CreateInvoicePayload, InvoiceItemPayload, InvoiceItemResult, InvoiceResult, UpdateInvoicePayload,
    InvoicePaymentScheduleResult, PaymentSchedulePayload,
    InvoiceRevenueRecordResult, CreateInvoiceRevenueRecordPayload, UpdateInvoiceRevenueRecordPayload,
} from '../types/invoiceTypes';
import type { ImportOptions, ImportBulkResult } from '@/shared/components/import/importTypes';
import type { InvoiceRelatedResult } from '@/shared/types/related';

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
    // bản ghi liên quan cho trang chi tiết (phiếu chăm sóc, hoạt động)
    getRelated: (id: number) =>
        axiosInstance.get<ApiResponse<InvoiceRelatedResult>>(`/api/invoices/${id}/related`),
    // phát hành hóa đơn (draft -> sent, khóa dữ liệu)
    issue: (id: number) => axiosInstance.post<ApiResponse<InvoiceResult>>(`/api/invoices/${id}/issue`),
    // hủy hóa đơn (-> cancelled)
    cancel: (id: number) => axiosInstance.post<ApiResponse<InvoiceResult>>(`/api/invoices/${id}/cancel`),

    // danh sách đợt thanh toán của hóa đơn
    getPaymentSchedules: (invoiceId: number) =>
        axiosInstance.get<ApiResponse<InvoicePaymentScheduleResult[]>>(`/api/invoices/${invoiceId}/payment-schedules`),
    // thêm một đợt thanh toán -> be tự suy ra paymentStatus
    addPaymentSchedule: (invoiceId: number, payload: PaymentSchedulePayload) =>
        axiosInstance.post<ApiResponse<InvoicePaymentScheduleResult>>(`/api/invoices/${invoiceId}/payment-schedules`, payload),
    // cập nhật một đợt thanh toán
    updatePaymentSchedule: (invoiceId: number, id: number, payload: PaymentSchedulePayload) =>
        axiosInstance.put<ApiResponse<InvoicePaymentScheduleResult>>(`/api/invoices/${invoiceId}/payment-schedules/${id}`, payload),
    // xóa một đợt thanh toán
    deletePaymentSchedule: (invoiceId: number, id: number) =>
        axiosInstance.delete(`/api/invoices/${invoiceId}/payment-schedules/${id}`),

    // danh sách bản ghi doanh số/chia hoa hồng của hóa đơn
    getRevenueRecords: (invoiceId: number) =>
        axiosInstance.get<ApiResponse<InvoiceRevenueRecordResult[]>>(`/api/invoices/${invoiceId}/revenue-records`),
    // thêm một bản ghi doanh số
    addRevenueRecord: (invoiceId: number, payload: CreateInvoiceRevenueRecordPayload) =>
        axiosInstance.post<ApiResponse<InvoiceRevenueRecordResult>>(`/api/invoices/${invoiceId}/revenue-records`, payload),
    // cập nhật một bản ghi doanh số (userId không đổi)
    updateRevenueRecord: (invoiceId: number, id: number, payload: UpdateInvoiceRevenueRecordPayload) =>
        axiosInstance.put<ApiResponse<InvoiceRevenueRecordResult>>(`/api/invoices/${invoiceId}/revenue-records/${id}`, payload),
    // xóa một bản ghi doanh số
    deleteRevenueRecord: (invoiceId: number, id: number) =>
        axiosInstance.delete(`/api/invoices/${invoiceId}/revenue-records/${id}`),
};
