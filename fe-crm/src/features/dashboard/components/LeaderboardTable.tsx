import type { ReactNode } from 'react';
import { ScrollFrame } from '@/shared/components/table/ScrollFrame';

// một cột của bảng xếp hạng nhiều chỉ số (khác RankedList — chỉ có 1 giá trị/thanh bar)
export interface LeaderboardColumn<T> {
    key: string;
    label: string;
    align?: 'left' | 'right';
    render: (row: T) => ReactNode;
}

interface LeaderboardTableProps<T> {
    columns: LeaderboardColumn<T>[];
    rows: T[];
    rowKey: (row: T) => string | number;
    onRowClick?: (row: T) => void;
    visibleRows?: number;
    emptyText?: string;
}

/**
 * Bảng xếp hạng nhiều cột (vd Nhân viên|Doanh thu|Thắng|Thua|Tỷ lệ thắng) — dùng khi RankedList
 * (1 giá trị/thanh bar) không đủ diễn đạt. Cuộn riêng qua ScrollFrame, header dính.
 */
export function LeaderboardTable<T>({ columns, rows, rowKey, onRowClick, visibleRows = 8, emptyText = 'Chưa có dữ liệu' }: LeaderboardTableProps<T>) {
    if (rows.length === 0) return <p className="text-sm text-gray-400 py-8 text-center">{emptyText}</p>;

    return (
        <ScrollFrame visibleRows={visibleRows}>
            <table className="w-full text-table min-w-max">
                <thead>
                    <tr className="text-left text-sm text-gray-500">
                        {columns.map((c) => (
                            <th key={c.key} className={`py-2 px-3 font-medium ${c.align === 'right' ? 'text-right' : ''}`}>
                                {c.label}
                            </th>
                        ))}
                    </tr>
                </thead>
                <tbody>
                    {rows.map((row) => (
                        <tr
                            key={rowKey(row)}
                            onClick={onRowClick ? () => onRowClick(row) : undefined}
                            className={`border-b border-gray-100 last:border-0 ${onRowClick ? 'hover:bg-blue-50 cursor-pointer' : ''}`}
                        >
                            {columns.map((c) => (
                                <td key={c.key} className={`py-2 px-3 text-text-main ${c.align === 'right' ? 'text-right' : ''}`}>
                                    {c.render(row)}
                                </td>
                            ))}
                        </tr>
                    ))}
                </tbody>
            </table>
        </ScrollFrame>
    );
}
