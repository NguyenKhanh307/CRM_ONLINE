import axiosInstance from '@/core/axios/axiosInstance';
import type { ApiResponse, PageResult, PageParams } from '@/shared/types/api';
import type { QuotationResult } from '../types/quotationTypes';

export const quotationService = {
    getList: (params?: PageParams) =>
        axiosInstance.get<ApiResponse<PageResult<QuotationResult>>>('/api/quotations', { params }),
    getById: (id: number) =>
        axiosInstance.get<ApiResponse<QuotationResult>>(`/api/quotations/${id}`),
    remove: (id: number) =>
        axiosInstance.delete(`/api/quotations/${id}`),
};
