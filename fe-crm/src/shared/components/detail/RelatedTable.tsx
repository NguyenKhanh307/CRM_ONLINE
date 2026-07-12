import type { ReactNode } from 'react';
import { useNavigate } from 'react-router-dom';
import type { RelatedGroup, RelatedModule, RelatedRecord } from '../../types/related';
import { MODULE_ROUTES, recordPath } from '../../utils/moduleRoutes';
import { formatISODate } from '../../utils/date';
import { formatCurrency } from '../../utils/number';

/** Cột hiển thị của bảng bản ghi liên quan — mỗi tab tự chọn cột và nhãn phù hợp. */
export interface RelatedColumn {
    key: keyof RelatedRecord;
    label: string;
    align?: 'left' | 'right';
    /** Ghi đè cách render (mặc định: date → dd/mm/yyyy, amount → tiền tệ, còn lại → chuỗi). */
    render?: (row: RelatedRecord) => ReactNode;
}

interface RelatedTableProps {
    group: RelatedGroup | undefined;
    columns: RelatedColumn[];
    /** Phân hệ — dùng cho link "xem trong danh sách" khi số bản ghi vượt quá số dòng đã nạp. */
    module: RelatedModule;
    emptyText: string;
}

/** Giá trị mặc định của một ô theo kiểu dữ liệu. */
const defaultCell = (row: RelatedRecord, key: keyof RelatedRecord): ReactNode => {
    const v = row[key];
    if (v === null || v === undefined || v === '') return '—';
    if (key === 'date') return formatISODate(String(v));
    if (key === 'amount') return formatCurrency(Number(v));
    return String(v);
};

/**
 * Bảng bản ghi liên quan (chỉ đọc) trên trang chi tiết 360°.
 * Bấm một dòng → mở bản ghi đó (trang chi tiết nếu có, ngược lại nhảy về danh sách + focus dòng).
 */
export const RelatedTable = ({ group, columns, module, emptyText }: RelatedTableProps) => {
    const navigate = useNavigate();
    const items = group?.items ?? [];
    const total = group?.total ?? 0;

    if (items.length === 0) {
        return <div className="py-8 text-center text-sm text-gray-400">{emptyText}</div>;
    }

    return (
        <div>
            <div className="overflow-x-auto">
                <table className="w-full text-table">
                    <thead>
                        <tr className="text-left text-sm text-gray-500 border-b border-gray-200">
                            {columns.map(c => (
                                <th key={String(c.key)} className={`py-2 px-3 font-medium ${c.align === 'right' ? 'text-right' : ''}`}>
                                    {c.label}
                                </th>
                            ))}
                        </tr>
                    </thead>
                    <tbody>
                        {items.map(row => (
                            <tr
                                key={row.id}
                                onClick={() => navigate(recordPath(row.module, row.id))}
                                className="border-b border-gray-100 last:border-0 hover:bg-blue-50 cursor-pointer"
                            >
                                {columns.map(c => (
                                    <td key={String(c.key)} className={`py-2 px-3 text-text-main ${c.align === 'right' ? 'text-right' : ''}`}>
                                        {c.render ? c.render(row) : defaultCell(row, c.key)}
                                    </td>
                                ))}
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>

            {total > items.length && (
                <div className="pt-3 text-sm text-gray-500">
                    Còn {total - items.length} bản ghi nữa —{' '}
                    <button onClick={() => navigate(MODULE_ROUTES[module])} className="text-primary hover:underline">
                        xem trong danh sách
                    </button>
                </div>
            )}
        </div>
    );
};
