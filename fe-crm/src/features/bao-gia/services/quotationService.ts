import axiosInstance from '@/core/axios/axiosInstance';
import type { ApiResponse, PageResult, PageParams } from '@/shared/types/api';
import type { CreateQuotationPayload, QuotationEmailDraft, QuotationItemPayload, QuotationItemResult, QuotationResult, SendQuotationPayload, UpdateQuotationPayload } from '../types/quotationTypes';
import type { ImportOptions, ImportBulkResult } from '@/shared/components/import/importTypes';

export const quotationService = {
    getList: (params?: PageParams) =>
        axiosInstance.get<ApiResponse<PageResult<QuotationResult>>>('/api/quotations', { params }),
    create: (payload: CreateQuotationPayload) =>
        axiosInstance.post<ApiResponse<QuotationResult>>('/api/quotations', payload),
    getItems: (quotationId: number) =>
        axiosInstance.get<ApiResponse<QuotationItemResult[]>>(`/api/quotations/${quotationId}/items`),
    createItem: (quotationId: number, payload: QuotationItemPayload) =>
        axiosInstance.post<ApiResponse<QuotationItemResult>>(`/api/quotations/${quotationId}/items`, payload),
    updateItem: (quotationId: number, itemId: number, payload: QuotationItemPayload) =>
        axiosInstance.put<ApiResponse<QuotationItemResult>>(`/api/quotations/${quotationId}/items/${itemId}`, payload),
    deleteItem: (quotationId: number, itemId: number) =>
        axiosInstance.delete(`/api/quotations/${quotationId}/items/${itemId}`),
    getById: (id: number) =>
        axiosInstance.get<ApiResponse<QuotationResult>>(`/api/quotations/${id}`),
    update: (id: number, payload: UpdateQuotationPayload) =>
        axiosInstance.put<ApiResponse<QuotationResult>>(`/api/quotations/${id}`, payload),
    remove: (id: number) =>
        axiosInstance.delete(`/api/quotations/${id}`),
    importBulk: (rows: Record<string, unknown>[], options: ImportOptions) =>
        axiosInstance.post<ApiResponse<ImportBulkResult>>('/api/quotations/import-bulk', {
            importType: options.importType,
            ownerMode: options.ownerMode,
            specificOwnerId: options.specificOwnerId ?? null,
            ownerFileColumn: options.ownerFileColumn ?? null,
            rows,
        }),
    handoverBulk: (payload: { ids: number[]; toUserId: number; reason?: string }) =>
        axiosInstance.post('/api/quotations/handover-bulk', payload),
    /** Nhân viên gửi báo giá lên quản lý duyệt (draft → pending). */
    submit: (id: number) =>
        axiosInstance.post<ApiResponse<QuotationResult>>(`/api/quotations/${id}/submit`),
    /** Quản lý duyệt báo giá (pending → approved). */
    approve: (id: number, comment?: string) =>
        axiosInstance.post<ApiResponse<QuotationResult>>(`/api/quotations/${id}/approve`, { comment }),
    /** Quản lý từ chối báo giá (pending → draft). */
    reject: (id: number, comment?: string) =>
        axiosInstance.post<ApiResponse<QuotationResult>>(`/api/quotations/${id}/reject`, { comment }),
    /** Lấy nội dung email báo giá mặc định để soạn trước khi gửi. */
    getEmailDraft: (id: number) =>
        axiosInstance.get<ApiResponse<QuotationEmailDraft>>(`/api/quotations/${id}/email-draft`),
    /** Nhân viên gửi email báo giá cho khách (approved → sent) kèm tiêu đề/nội dung tùy biến. */
    send: (id: number, payload?: SendQuotationPayload) =>
        axiosInstance.post<ApiResponse<QuotationResult>>(`/api/quotations/${id}/send`, payload),
    /** Khách chấp nhận báo giá (sent → accepted). */
    accept: (id: number) =>
        axiosInstance.post<ApiResponse<QuotationResult>>(`/api/quotations/${id}/accept`),
    /** Đặt báo giá làm báo giá đồng bộ (primary) của cơ hội. */
    setPrimary: (id: number) =>
        axiosInstance.post<ApiResponse<QuotationResult>>(`/api/quotations/${id}/set-primary`),
    /** Chuyển báo giá thành đơn hàng (khóa báo giá + cơ hội Chốt Thắng). */
    convertToOrder: (id: number) =>
        axiosInstance.post<ApiResponse<unknown>>(`/api/quotations/${id}/convert-to-order`),
    /** Clone báo giá từ cơ hội (sao chép sâu KH/LH/chính sách giá + dòng hàng). */
    fromOpportunity: (opportunityId: number) =>
        axiosInstance.post<ApiResponse<QuotationResult>>(`/api/quotations/from-opportunity/${opportunityId}`),
    /** Cập nhật lại dòng hàng báo giá theo cơ hội nguồn (xóa + clone lại, giữ liên kết). */
    syncItemsFromOpportunity: (id: number) =>
        axiosInstance.post<ApiResponse<QuotationResult>>(`/api/quotations/${id}/sync-items-from-opportunity`),
};
