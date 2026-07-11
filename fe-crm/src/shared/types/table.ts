/** Thông tin một cột dùng để render toolbar, sort panel, column toggle. */
export interface ColumnMeta {
    id: string;
    header: string;
    isVisible: boolean;
    canSort: boolean;
    toggleVisibility: () => void;
}

/** Tag filter nhanh hiển thị bên trái toolbar (đã gắn trạng thái — cho TableToolbar). */
export interface QuickFilter {
    id: string;
    label: string;
    isActive: boolean;
    onToggle: () => void;
}

/**
 * Khai báo tag lọc nhanh ở page (DataTable tự quản lý đóng/mở + lọc).
 * Khớp khi `String(row[field]) === value`.
 */
export interface QuickFilterDef {
    id: string;
    label: string;
    field: string;
    value: string;
}

/**
 * Trạng thái server-mode của DataTable: phân trang + tìm kiếm + tag lọc nhanh xử lý ở server.
 * Truyền prop `server` → DataTable tắt phân trang/lọc client và báo mọi thay đổi ra ngoài.
 */
export interface ServerTableState {
    /** Tổng số bản ghi khớp bộ lọc hiện tại (từ PageResult.total). */
    totalRows: number;
    /** Trang hiện tại (0-based). */
    pageIndex: number;
    /** Số dòng mỗi trang. */
    pageSize: number;
    onPageChange: (pageIndex: number) => void;
    onPageSizeChange: (size: number) => void;
    /** Từ khóa tìm kiếm đã áp (page giữ state; DataTable tự debounce ô nhập). */
    searchValue: string;
    onSearchChange: (value: string) => void;
    /** Giá trị tag lọc nhanh đang chọn (null = bỏ lọc) — page map sang param `status`. */
    onQuickFilterChange?: (value: string | null) => void;
}

export type FilterOperator =
    | 'is'
    | 'is_not'
    | 'contains'
    | 'does_not_contain'
    | 'is_empty'
    | 'is_not_empty';

/** Một điều kiện lọc theo cột (Filter records panel). */
export interface FilterCondition {
    id: string;
    fieldId: string;
    operator: FilterOperator;
    value: string;
}

/**
 * Một mục trong menu chuột phải của dòng bảng (thay cho cột "Thao tác" cũ).
 * Nhãn hiển thị dạng chữ; mục `danger` tô đỏ và được tách bằng đường kẻ.
 */
export interface RowAction {
    key: string;
    label: string;
    onClick: () => void;
    danger?: boolean;
    disabled?: boolean;
}

/** Một rule tô màu có điều kiện (Conditional coloring panel). */
export interface ConditionalRule {
    id: string;
    color: string;
    scope: 'cell' | 'row';
    fieldId: string;
    operator: FilterOperator;
    value: string;
}
