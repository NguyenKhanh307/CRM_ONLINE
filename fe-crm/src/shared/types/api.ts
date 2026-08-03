export interface ApiResponse<T> {
    data: T;
    message: string;
    status: number;
}

export interface PageResult<T> {
    items: T[];
    total: number;
    page: number;
    size: number;
    totalPages: number;
}

export interface PageParams {
    page?: number;
    size?: number;
    sortBy?: string;
    sortDir?: 'asc' | 'desc';
    // từ khóa tìm kiếm server-side (LIKE trên các cột chính của module)
    q?: string;
    // giá trị tag lọc nhanh của module (BE map sang cột status/type/isActive... tương ứng)
    status?: string;
    // thu hẹp theo khách hàng — hiện chỉ /api/contacts hỗ trợ (ô chọn Liên hệ theo khách đang chọn)
    customerId?: number;
}
