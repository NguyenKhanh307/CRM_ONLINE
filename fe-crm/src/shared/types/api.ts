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
    /** Từ khóa tìm kiếm server-side (LIKE trên các cột chính của module). */
    q?: string;
    /** Giá trị tag lọc nhanh của module (BE map sang cột status/type/isActive... tương ứng). */
    status?: string;
}
