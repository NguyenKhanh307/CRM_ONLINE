import axiosInstance from '@/core/axios/axiosInstance';
import type { ApiResponse, PageResult, PageParams } from '@/shared/types/api';
import type { ImportOptions, ImportBulkResult } from '@/shared/components/import/importTypes';
import type {
    PricePolicyResult,
    CreatePricePolicyPayload,
    UpdatePricePolicyPayload,
    PricePolicyProductResult,
    CreatePricePolicyProductPayload,
    UpdatePricePolicyProductPayload,
    PricePolicyCustomerResult,
    CreatePricePolicyCustomerPayload,
    PricePolicyProductCategoryResult,
    CreatePricePolicyProductCategoryPayload,
    ResolvePriceResult,
} from '../types/pricingTypes';

export const pricingService = {
    getList: (params?: PageParams) =>
        axiosInstance.get<ApiResponse<PageResult<PricePolicyResult>>>('/api/price-policies', { params }),
    // chính sách giá mà người dùng hiện tại (và khách hàng đang chọn, nếu có) được phép sử dụng — dùng cho dropdown form
    getEligibleList: (params?: { customerId?: number }) =>
        axiosInstance.get<ApiResponse<PricePolicyResult[]>>('/api/price-policies/eligible', { params }),
    // tra cứu đơn giá & chiết khấu theo chính sách giá cho một sản phẩm + số lượng
    resolve: (pricePolicyId: number, productId: number, quantity: number, customerId?: number | null) =>
        axiosInstance.get<ApiResponse<ResolvePriceResult>>('/api/pricing/resolve', {
            params: { pricePolicyId, productId, quantity, customerId: customerId ?? undefined },
        }),
    getById: (id: number) =>
        axiosInstance.get<ApiResponse<PricePolicyResult>>(`/api/price-policies/${id}`),
    create: (payload: CreatePricePolicyPayload) =>
        axiosInstance.post<ApiResponse<PricePolicyResult>>('/api/price-policies', payload),
    update: (id: number, payload: UpdatePricePolicyPayload) =>
        axiosInstance.put<ApiResponse<PricePolicyResult>>(`/api/price-policies/${id}`, payload),
    remove: (id: number) =>
        axiosInstance.delete(`/api/price-policies/${id}`),
    importBulk: (rows: Record<string, unknown>[], options: ImportOptions) =>
        axiosInstance.post<ApiResponse<ImportBulkResult>>('/api/price-policies/import-bulk', {
            importType: options.importType,
            rows,
        }),

    getProducts: (policyId: number) =>
        axiosInstance.get<ApiResponse<PricePolicyProductResult[]>>(`/api/price-policies/${policyId}/products`),
    createProduct: (policyId: number, payload: CreatePricePolicyProductPayload) =>
        axiosInstance.post<ApiResponse<PricePolicyProductResult>>(`/api/price-policies/${policyId}/products`, payload),
    updateProduct: (policyId: number, id: number, payload: UpdatePricePolicyProductPayload) =>
        axiosInstance.put<ApiResponse<PricePolicyProductResult>>(`/api/price-policies/${policyId}/products/${id}`, payload),
    removeProduct: (policyId: number, id: number) =>
        axiosInstance.delete(`/api/price-policies/${policyId}/products/${id}`),

    getCustomers: (policyId: number) =>
        axiosInstance.get<ApiResponse<PricePolicyCustomerResult[]>>(`/api/price-policies/${policyId}/customers`),
    createCustomer: (policyId: number, payload: CreatePricePolicyCustomerPayload) =>
        axiosInstance.post<ApiResponse<PricePolicyCustomerResult>>(`/api/price-policies/${policyId}/customers`, payload),
    removeCustomer: (policyId: number, id: number) =>
        axiosInstance.delete(`/api/price-policies/${policyId}/customers/${id}`),

    getProductCategories: (policyId: number) =>
        axiosInstance.get<ApiResponse<PricePolicyProductCategoryResult[]>>(`/api/price-policies/${policyId}/product-categories`),
    // thêm danh mục — be tự bulk-seed sản phẩm thuộc danh mục vào tab "Sản phẩm" (giá để trống)
    createProductCategory: (policyId: number, payload: CreatePricePolicyProductCategoryPayload) =>
        axiosInstance.post<ApiResponse<PricePolicyProductCategoryResult>>(`/api/price-policies/${policyId}/product-categories`, payload),
    removeProductCategory: (policyId: number, id: number) =>
        axiosInstance.delete(`/api/price-policies/${policyId}/product-categories/${id}`),
};
