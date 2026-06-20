/** Thông tin một cột dùng để render toolbar, sort panel, column toggle. */
export interface ColumnMeta {
    id: string;
    header: string;
    isVisible: boolean;
    canSort: boolean;
    toggleVisibility: () => void;
}

export type SortDirection = 'asc' | 'desc';

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

/** Một rule tô màu có điều kiện (Conditional coloring panel). */
export interface ConditionalRule {
    id: string;
    color: string;
    scope: 'cell' | 'row';
    fieldId: string;
    operator: FilterOperator;
    value: string;
}
