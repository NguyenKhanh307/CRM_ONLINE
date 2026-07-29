import axiosInstance from '@/core/axios/axiosInstance';
import type { ApiResponse, PageResult } from '@/shared/types/api';

export interface ProductCategoryResult {
    id: number;
    code: string;
    name: string;
    sortOrder: number;
    isActive: boolean;
    createdAt?: string;
    updatedAt?: string;
}

/** Payload tạo mới danh mục sản phẩm. */
export interface CreateProductCategoryPayload {
    code: string;
    name: string;
    sortOrder: number;
    isActive: boolean;
}

/** Payload cập nhật danh mục sản phẩm — không có `code` (không đổi được sau khi tạo). */
export interface UpdateProductCategoryPayload {
    name: string;
    sortOrder: number;
    isActive: boolean;
}

export const productCategoryService = {
    /** Lấy danh sách danh mục — dùng cho dropdown lẫn trang quản lý. */
    getList: () =>
        axiosInstance.get<ApiResponse<PageResult<ProductCategoryResult>>>('/api/product-categories', {
            params: { size: 200 },
        }),
    /** Tạo danh mục mới. */
    create: (body: CreateProductCategoryPayload) =>
        axiosInstance.post<ApiResponse<ProductCategoryResult>>('/api/product-categories', body),
    /** Cập nhật danh mục theo id. */
    update: (id: number, body: UpdateProductCategoryPayload) =>
        axiosInstance.put<ApiResponse<ProductCategoryResult>>(`/api/product-categories/${id}`, body),
    /** Xóa danh mục theo id. */
    remove: (id: number) => axiosInstance.delete(`/api/product-categories/${id}`),
};
