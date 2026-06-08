import { FiSearch, FiFilter, FiBarChart2, FiDroplet, FiEye, FiX } from 'react-icons/fi';
import type { QuickFilter } from '@/shared/types/table';

interface TableToolbarProps {
    globalFilter: string;
    onGlobalFilterChange: (value: string) => void;
    quickFilters?: QuickFilter[];
    selectedCount: number;
    onClearSelection: () => void;
    /** Filter records panel */
    isFilterOpen: boolean;
    onToggleFilter: () => void;
    activeFilterCount: number;
    /** Sort panel */
    isSortOpen: boolean;
    onToggleSort: () => void;
    activeSortCount: number;
    /** Conditional coloring panel */
    isColoringOpen: boolean;
    onToggleColoring: () => void;
    activeColoringCount: number;
    /** Column visibility panel */
    isColumnsOpen: boolean;
    onToggleColumns: () => void;
}

/**
 * Thanh công cụ phía trên bảng.
 * Trái: badge số dòng chọn + tag filter nhanh.
 * Phải: ô tìm kiếm + 4 icon buttons (Filter, Sort, Coloring, Columns).
 */
export const TableToolbar = ({
    globalFilter,
    onGlobalFilterChange,
    quickFilters = [],
    selectedCount,
    onClearSelection,
    isFilterOpen,
    onToggleFilter,
    activeFilterCount,
    isSortOpen,
    onToggleSort,
    activeSortCount,
    isColoringOpen,
    onToggleColoring,
    activeColoringCount,
    isColumnsOpen,
    onToggleColumns,
}: TableToolbarProps) => {
    return (
        <div className="flex items-center justify-between gap-3 mb-2 flex-wrap">
            {/* Left: selection badge + quick filter tags */}
            <div className="flex items-center gap-2 flex-wrap">
                {selectedCount > 0 && (
                    <span className="flex items-center gap-1 text-table bg-blue-50 text-primary border border-primary/30 px-2.5 py-1 rounded-btn">
                        ✓ {selectedCount} dòng được chọn
                        <button
                            onClick={onClearSelection}
                            className="ml-1 hover:text-danger transition-colors"
                            title="Bỏ chọn tất cả"
                        >
                            <FiX size={12} />
                        </button>
                    </span>
                )}

                {quickFilters.map((qf) => (
                    <button
                        key={qf.id}
                        onClick={qf.onToggle}
                        className={`text-table px-3 py-1 rounded-btn border transition-colors flex items-center gap-1.5 ${
                            qf.isActive
                                ? 'bg-primary text-white border-primary'
                                : 'bg-white border-gray-300 text-text-main hover:border-primary hover:text-primary'
                        }`}
                    >
                        {qf.label}
                        {qf.isActive && <FiX size={11} />}
                    </button>
                ))}
            </div>

            {/* Right: search + 4 icon buttons */}
            <div className="flex items-center gap-1.5">
                {/* Search */}
                <div className="relative">
                    <FiSearch
                        size={14}
                        className="absolute left-2.5 top-1/2 -translate-y-1/2 text-gray-400 pointer-events-none"
                    />
                    <input
                        type="text"
                        value={globalFilter}
                        onChange={(e) => onGlobalFilterChange(e.target.value)}
                        placeholder="Tìm kiếm..."
                        className="text-table border border-gray-300 rounded-btn pl-8 pr-3 py-1.5 w-52 focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary"
                    />
                    {globalFilter && (
                        <button
                            onClick={() => onGlobalFilterChange('')}
                            className="absolute right-2 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600"
                        >
                            <FiX size={12} />
                        </button>
                    )}
                </div>

                {/* Filter records */}
                <IconToolbarButton
                    icon={<FiFilter size={15} />}
                    isActive={isFilterOpen}
                    onClick={onToggleFilter}
                    badge={activeFilterCount}
                    title="Lọc bản ghi"
                />

                {/* Sort */}
                <IconToolbarButton
                    icon={<FiBarChart2 size={15} />}
                    isActive={isSortOpen}
                    onClick={onToggleSort}
                    badge={activeSortCount}
                    title="Sắp xếp"
                />

                {/* Conditional coloring */}
                <IconToolbarButton
                    icon={<FiDroplet size={15} />}
                    isActive={isColoringOpen}
                    onClick={onToggleColoring}
                    badge={activeColoringCount}
                    title="Tô màu có điều kiện"
                />

                {/* Column visibility */}
                <IconToolbarButton
                    icon={<FiEye size={15} />}
                    isActive={isColumnsOpen}
                    onClick={onToggleColumns}
                    title="Ẩn/hiện cột"
                />
            </div>
        </div>
    );
};

interface IconToolbarButtonProps {
    icon: React.ReactNode;
    isActive: boolean;
    onClick: () => void;
    badge?: number;
    title?: string;
}

const IconToolbarButton = ({ icon, isActive, onClick, badge, title }: IconToolbarButtonProps) => (
    <button
        onClick={onClick}
        title={title}
        className={`relative w-8 h-8 flex items-center justify-center rounded-btn border transition-colors ${
            isActive
                ? 'bg-primary text-white border-primary'
                : 'bg-white border-gray-300 text-text-main hover:border-primary hover:text-primary'
        }`}
    >
        {icon}
        {badge != null && badge > 0 && !isActive && (
            <span className="absolute -top-1.5 -right-1.5 bg-danger text-white text-xs rounded-full w-4 h-4 flex items-center justify-center leading-none">
                {badge}
            </span>
        )}
    </button>
);
