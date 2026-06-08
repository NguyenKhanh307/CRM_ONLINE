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
}
