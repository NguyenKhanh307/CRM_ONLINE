import axiosInstance from '@/core/axios/axiosInstance';
import type { ApiResponse, PageResult, PageParams } from '@/shared/types/api';
import type { ContactResult } from '../types/contactTypes';

export const contactService = {
    getList: (params?: PageParams) =>
        axiosInstance.get<ApiResponse<PageResult<ContactResult>>>('/api/contacts', { params }),
    getById: (id: number) =>
        axiosInstance.get<ApiResponse<ContactResult>>(`/api/contacts/${id}`),
    remove: (id: number) =>
        axiosInstance.delete(`/api/contacts/${id}`),
};
