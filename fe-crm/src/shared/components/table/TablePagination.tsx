import { useState, useEffect } from 'react';

const PAGE_SIZE_OPTIONS = [10, 20, 50] as const;

interface TablePaginationProps {
    pageIndex: number;
    pageCount: number;
    pageSize: number;
    totalRows: number;
    canPreviousPage: boolean;
    canNextPage: boolean;
    onPageChange: (page: number) => void;
    onPageSizeChange: (size: number) => void;
}

/**
 * Thanh phân trang: điều hướng trang và chọn số dòng mỗi trang.
 * Số trang có thể nhập trực tiếp, nhấn Enter hoặc blur để chuyển trang.
 */
export const TablePagination = ({
    pageIndex,
    pageCount,
    pageSize,
    totalRows,
    canPreviousPage,
    canNextPage,
    onPageChange,
    onPageSizeChange,
}: TablePaginationProps) => {
    const from = totalRows === 0 ? 0 : pageIndex * pageSize + 1;
    const to = Math.min((pageIndex + 1) * pageSize, totalRows);

    const [inputValue, setInputValue] = useState(String(pageIndex + 1));

    /** Đồng bộ khi trang thay đổi từ bên ngoài (nút điều hướng, filter...). */
    useEffect(() => {
        setInputValue(String(pageIndex + 1));
    }, [pageIndex]);

    const commitPage = () => {
        const parsed = parseInt(inputValue, 10);
        const total = pageCount || 1;
        if (!isNaN(parsed) && parsed >= 1 && parsed <= total) {
            onPageChange(parsed - 1);
        } else {
            setInputValue(String(pageIndex + 1));
        }
    };

    const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
        if (e.key === 'Enter') {
            e.currentTarget.blur();
        } else if (e.key === 'Escape') {
            setInputValue(String(pageIndex + 1));
            e.currentTarget.blur();
        }
    };

    return (
        <div className="flex items-center justify-between mt-2 text-table text-gray-600">
            {/* Thông tin số bản ghi */}
            <span>
                Hiển thị {from}–{to} trong {totalRows} bản ghi
            </span>

            <div className="flex items-center gap-3">
                {/* Chọn số dòng mỗi trang */}
                <div className="flex items-center gap-1">
                    <span>Hiển thị</span>
                    <select
                        value={pageSize}
                        onChange={(e) => onPageSizeChange(Number(e.target.value))}
                        className="border border-gray-300 rounded-btn px-2 py-0.5 text-table bg-white focus:outline-none focus:border-primary"
                    >
                        {PAGE_SIZE_OPTIONS.map((size) => (
                            <option key={size} value={size}>{size}</option>
                        ))}
                    </select>
                    <span>dòng</span>
                </div>

                {/* Nút điều hướng + input trang */}
                <div className="flex items-center gap-1">
                    <NavButton
                        onClick={() => onPageChange(0)}
                        disabled={!canPreviousPage}
                        label="«"
                        title="Trang đầu"
                    />
                    <NavButton
                        onClick={() => onPageChange(pageIndex - 1)}
                        disabled={!canPreviousPage}
                        label="‹"
                        title="Trang trước"
                    />

                    <div className="flex items-center gap-1 px-1">
                        <input
                            type="text"
                            inputMode="numeric"
                            value={inputValue}
                            onChange={(e) => setInputValue(e.target.value)}
                            onBlur={commitPage}
                            onKeyDown={handleKeyDown}
                            className="w-10 h-7 text-center text-table font-medium text-text-main border border-gray-300 rounded-btn focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary"
                            title="Nhập số trang"
                        />
                        <span className="text-gray-400">/</span>
                        <span className="text-text-main font-medium">{pageCount || 1}</span>
                    </div>

                    <NavButton
                        onClick={() => onPageChange(pageIndex + 1)}
                        disabled={!canNextPage}
                        label="›"
                        title="Trang sau"
                    />
                    <NavButton
                        onClick={() => onPageChange(pageCount - 1)}
                        disabled={!canNextPage}
                        label="»"
                        title="Trang cuối"
                    />
                </div>
            </div>
        </div>
    );
};

interface NavButtonProps {
    onClick: () => void;
    disabled: boolean;
    label: string;
    title: string;
}

/** Nút điều hướng trang với trạng thái disabled. */
const NavButton = ({ onClick, disabled, label, title }: NavButtonProps) => (
    <button
        onClick={onClick}
        disabled={disabled}
        title={title}
        className="w-7 h-7 flex items-center justify-center rounded border border-gray-300 text-sm disabled:opacity-40 disabled:cursor-not-allowed hover:enabled:bg-gray-50 hover:enabled:border-primary hover:enabled:text-primary transition-colors"
    >
        {label}
    </button>
);
