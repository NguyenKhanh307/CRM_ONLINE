// chiều cao dòng/header của bảng — suy ra từ design token trong `tailwind.config.js`
// dùng để giới hạn chiều cao khung cuộn sao cho hiện đúng N dòng

// dòng dữ liệu: text-table 13px × line-height 1.5 (≈20px) + py-2 (16px) + border-b (1px)
export const TABLE_ROW_HEIGHT = 37;

// header: text-title 14px × 1.5 (≈21px) + py-2 (16px), cộng 2px đường kẻ dưới
export const TABLE_HEADER_HEIGHT = 39;

// chiều cao tối đa của khung cuộn để hiện đúng `visibleRows` dòng, đã tính cả header dính
// làm tròn dư vài pixel nên dòng kế tiếp lộ một phần — chủ ý, báo hiệu còn nội dung bên dưới
export function tableScrollMaxHeight(visibleRows: number): number {
    return TABLE_HEADER_HEIGHT + visibleRows * TABLE_ROW_HEIGHT;
}
