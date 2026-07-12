import type { ReactNode } from 'react';

interface InfoRowProps {
    label: string;
    /** Giá trị; rỗng/null sẽ hiện dấu "—". */
    value: ReactNode;
}

/** Dòng "nhãn — giá trị" trong panel thông tin của trang chi tiết. */
export const InfoRow = ({ label, value }: InfoRowProps) => (
    <div className="flex gap-2 py-1.5 border-b border-gray-100 last:border-0">
        <span className="w-40 shrink-0 text-sm text-gray-500">{label}</span>
        <span className="text-table text-text-main break-words">
            {value === null || value === undefined || value === '' ? '—' : value}
        </span>
    </div>
);
