import axiosInstance from '@/core/axios/axiosInstance';
import type { ApiResponse, PageResult, PageParams } from '@/shared/types/api';
import type {
    PricePolicyResult,
    CreatePricePolicyPayload,
    UpdatePricePolicyPayload,
    PricePolicyProductResult,
    CreatePricePolicyProductPayload,
    UpdatePricePolicyProductPayload,
    PricePolicyCustomerResult,
    CreatePricePolicyCustomerPayload,
    PricePolicyCustomerCategoryResult,
    CreatePricePolicyCustomerCategoryPayload,
    PricePolicyProductTypeResult,
    CreatePricePolicyProductTypePayload,
    PricePolicyEmployeeResult,
    CreatePricePolicyEmployeePayload,
} from '../types/pricingTypes';

export const pricingService = {
    getList: (params?: PageParams) =>
        axiosInstance.get<ApiResponse<PageResult<PricePolicyResult>>>('/api/price-policies', { params }),
    getById: (id: number) =>
        axiosInstance.get<ApiResponse<PricePolicyResult>>(`/api/price-policies/${id}`),
    create: (payload: CreatePricePolicyPayload) =>
        axiosInstance.post<ApiResponse<PricePolicyResult>>('/api/price-policies', payload),
    update: (id: number, payload: UpdatePricePolicyPayload) =>
        axiosInstance.put<ApiResponse<PricePolicyResult>>(`/api/price-policies/${id}`, payload),
    remove: (id: number) =>
        axiosInstance.delete(`/api/price-policies/${id}`),

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

    getCustomerCategories: (policyId: number) =>
        axiosInstance.get<ApiResponse<PricePolicyCustomerCategoryResult[]>>(`/api/price-policies/${policyId}/customer-categories`),
    createCustomerCategory: (policyId: number, payload: CreatePricePolicyCustomerCategoryPayload) =>
        axiosInstance.post<ApiResponse<PricePolicyCustomerCategoryResult>>(`/api/price-policies/${policyId}/customer-categories`, payload),
    removeCustomerCategory: (policyId: number, id: number) =>
        axiosInstance.delete(`/api/price-policies/${policyId}/customer-categories/${id}`),

    getProductTypes: (policyId: number) =>
        axiosInstance.get<ApiResponse<PricePolicyProductTypeResult[]>>(`/api/price-policies/${policyId}/product-types`),
    createProductType: (policyId: number, payload: CreatePricePolicyProductTypePayload) =>
        axiosInstance.post<ApiResponse<PricePolicyProductTypeResult>>(`/api/price-policies/${policyId}/product-types`, payload),
    removeProductType: (policyId: number, id: number) =>
        axiosInstance.delete(`/api/price-policies/${policyId}/product-types/${id}`),

    getEmployees: (policyId: number) =>
        axiosInstance.get<ApiResponse<PricePolicyEmployeeResult[]>>(`/api/price-policies/${policyId}/employees`),
    createEmployee: (policyId: number, payload: CreatePricePolicyEmployeePayload) =>
        axiosInstance.post<ApiResponse<PricePolicyEmployeeResult>>(`/api/price-policies/${policyId}/employees`, payload),
    removeEmployee: (policyId: number, id: number) =>
        axiosInstance.delete(`/api/price-policies/${policyId}/employees/${id}`),
};
