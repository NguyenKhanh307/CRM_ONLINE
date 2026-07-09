import type { ReactNode } from 'react';
import { tableScrollMaxHeight } from './tableMetrics';

/** Một cột của bảng dòng hàng (chỉ-xem) ở panel dưới. */
export interface ItemPanelColumn<T> {
    key: string;
    header: string;
    align?: 'left' | 'right' | 'center';
    render: (row: T, index: number) => ReactNode;
}

interface RecordItemsPanelProps<T> {
    /** Nhãn bản ghi đang chọn; bỏ trống nghĩa là chưa chọn dòng nào. */
    title?: string;
    columns: ItemPanelColumn<T>[];
    rows: T[];
    isLoading?: boolean;
    /** Thông báo khi chưa chọn bản ghi nào. */
    placeholder?: string;
    /** Thông báo khi bản ghi đang chọn không có dòng hàng. */
    emptyText?: string;
    /** Số dòng hàng thấy được mà không cần cuộn; phần dư cuộn trong khung. */
    visibleRows?: number;
}

const stickyHeadCell =
    'sticky top-0 z-10 bg-gray-50 text-table font-medium text-gray-600 px-3 py-2 shadow-[inset_0_-1px_0_0_#e5e7eb]';

const alignClass: Record<'left' | 'right' | 'center', string> = {
    left: 'text-left',
    right: 'text-right',
    center: 'text-center',
};

/**
 * Bảng dưới (chỉ-xem) hiển thị danh sách dòng hàng của bản ghi đang được chọn ở bảng trên.
 * Dùng cho bố cục 2 bảng (master–detail) của Cơ hội / Báo giá / Đơn hàng / Hóa đơn.
 */
export const RecordItemsPanel = <T extends { id: number }>({
    title,
    columns,
    rows,
    isLoading = false,
    placeholder = 'Chọn một bản ghi ở bảng trên để xem chi tiết dòng hàng',
    emptyText = 'Bản ghi này chưa có dòng hàng nào',
    visibleRows = 3,
}: RecordItemsPanelProps<T>) => {
    const message = !title ? placeholder : isLoading ? 'Đang tải...' : rows.length === 0 ? emptyText : null;

    return (
        <div className="flex flex-col">
            <div className="flex items-center gap-2 px-3 py-2 bg-blue-50 border border-gray-200 rounded-t-btn">
                <span className="text-md font-medium text-primary">Hàng hóa</span>
                {title && <span className="text-md text-gray-500 truncate">— {title}</span>}
            </div>

            <div
                className="overflow-auto border border-t-0 border-gray-200 rounded-b-btn"
                style={{ maxHeight: tableScrollMaxHeight(visibleRows) }}
            >
                <table className="w-full border-collapse">
                    <thead>
                        <tr>
                            {/* shadow-inset thay viền dưới: viền của th dính bị border-collapse vẽ theo bảng nên trôi mất khi cuộn */}
                            <th className={`${stickyHeadCell} text-center w-12`}>STT</th>
                            {columns.map((col) => (
                                <th
                                    key={col.key}
                                    className={`${stickyHeadCell} whitespace-nowrap ${alignClass[col.align ?? 'left']}`}
                                >
                                    {col.header}
                                </th>
                            ))}
                        </tr>
                    </thead>
                    <tbody>
                        {message ? (
                            <tr>
                                <td colSpan={columns.length + 1} className="text-table text-center py-8 text-gray-400">
                                    {message}
                                </td>
                            </tr>
                        ) : (
                            rows.map((row, index) => (
                                <tr key={row.id} className={index % 2 === 0 ? 'bg-white' : 'bg-gray-50'}>
                                    <td className="text-table text-text-main px-3 py-2 border-b border-gray-200 text-center">
                                        {index + 1}
                                    </td>
                                    {columns.map((col) => (
                                        <td
                                            key={col.key}
                                            className={`text-table text-text-main px-3 py-2 border-b border-gray-200 whitespace-nowrap ${alignClass[col.align ?? 'left']}`}
                                        >
                                            {col.render(row, index)}
                                        </td>
                                    ))}
                                </tr>
                            ))
                        )}
                    </tbody>
                </table>
            </div>
        </div>
    );
};
