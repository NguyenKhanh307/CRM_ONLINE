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
    // xem cơ hội của 1 nhân viên cụ thể — chỉ ADMIN/SALES_MANAGER, hiện chỉ /api/opportunities hỗ trợ
    // (drill-down ở trang phân tích /phan-tich); nhân viên thường bị BE ép về chính họ bất kể truyền gì
    ownerId?: number;
    // loại bỏ bản ghi đã hết hạn (status='expired') — hiện chỉ /api/quotations hỗ trợ, dùng cho ô
    // chọn báo giá ở form khác (Đơn hàng/Hóa đơn), KHÔNG bật ở trang danh sách /bao-gia
    excludeExpired?: boolean;
}
