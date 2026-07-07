import type { ReactNode } from 'react';
import { FiRefreshCw, FiMaximize2, FiSettings } from 'react-icons/fi';

interface DashCardProps {
    /** Tiêu đề thẻ. */
    title: string;
    /** Nhãn kỳ hiển thị góc phải (vd "Năm nay"). */
    periodLabel?: string;
    /** Callback khi bấm nút làm mới. */
    onRefresh?: () => void;
    /** Chú thích nhỏ dưới đáy thẻ (vd "Số liệu tính đến …"). */
    footNote?: string;
    /** Cho phép mở rộng chiếm 2 cột grid. */
    className?: string;
    children: ReactNode;
}

/**
 * Thẻ dashboard chuẩn AMIS: header (tiêu đề + kỳ + nút làm mới + icon trang trí) + nội dung.
 */
export const DashCard = ({ title, periodLabel, onRefresh, footNote, className = '', children }: DashCardProps) => (
    <div className={`bg-white rounded-card p-4 shadow-sm flex flex-col ${className}`}>
        <div className="flex items-start justify-between mb-3">
            <h3 className="text-md font-semibold text-text-main">{title}</h3>
            <div className="flex items-center gap-2 text-gray-400">
                {periodLabel && <span className="text-sm text-gray-500">{periodLabel}</span>}
                {onRefresh && (
                    <button onClick={onRefresh} className="hover:text-primary" title="Làm mới" type="button">
                        <FiRefreshCw size={14} />
                    </button>
                )}
                <FiMaximize2 size={14} className="opacity-40" />
                <FiSettings size={14} className="opacity-40" />
            </div>
        </div>
        <div className="flex-1">{children}</div>
        {footNote && <p className="text-sm text-gray-400 mt-3">{footNote}</p>}
    </div>
);
