import { FiPhone, FiUsers, FiMail, FiCheckSquare, FiFileText } from 'react-icons/fi';
import type { IconType } from 'react-icons';
import type { RelatedGroup } from '../../types/related';
import { formatISODate } from '../../utils/date';

/** Icon + nhãn theo loại hoạt động (cột `code` của RelatedRecord). */
const TYPE_META: Record<string, { icon: IconType; label: string }> = {
    call:    { icon: FiPhone,       label: 'Cuộc gọi' },
    meeting: { icon: FiUsers,       label: 'Cuộc hẹn' },
    email:   { icon: FiMail,        label: 'Email' },
    task:    { icon: FiCheckSquare, label: 'Công việc' },
    note:    { icon: FiFileText,    label: 'Ghi chú' },
};

/** Nhãn trạng thái hoạt động. */
const STATUS_LABEL: Record<string, string> = {
    planned:     'Dự kiến',
    in_progress: 'Đang làm',
    done:        'Hoàn thành',
    cancelled:   'Đã hủy',
};

const STATUS_CLASS: Record<string, string> = {
    planned:     'bg-gray-100 text-gray-600',
    in_progress: 'bg-blue-50 text-primary',
    done:        'bg-green-100 text-green-700',
    cancelled:   'bg-red-100 text-red-600',
};

interface TimelineProps {
    group: RelatedGroup | undefined;
    emptyText?: string;
    /** Chiều cao tối đa khung cuộn (px) — dòng timeline cao không đều nên dùng số cố định. */
    maxHeight?: number;
}

/**
 * Dòng thời gian hoạt động của một bản ghi (khách hàng / cơ hội).
 * Dữ liệu lấy từ nhóm `activities` của API `/related`.
 */
export const Timeline = ({ group, emptyText = 'Chưa có hoạt động nào.', maxHeight = 420 }: TimelineProps) => {
    const items = group?.items ?? [];

    if (items.length === 0) {
        return <div className="py-8 text-center text-sm text-gray-400">{emptyText}</div>;
    }

    return (
        // Khung cuộn riêng: dòng thời gian dài không kéo dài trang
        <ul className="space-y-3 overflow-auto pr-1" style={{ maxHeight }}>
            {items.map(a => {
                const meta = TYPE_META[a.code ?? ''] ?? { icon: FiFileText, label: a.code ?? '' };
                const Icon = meta.icon;
                return (
                    <li key={a.id} className="flex gap-3">
                        <span className="mt-0.5 flex h-7 w-7 shrink-0 items-center justify-center rounded-full bg-blue-50 text-primary">
                            <Icon size={13} />
                        </span>
                        <div className="min-w-0 flex-1 border-b border-gray-100 pb-3 last:border-0">
                            <div className="flex items-center gap-2 flex-wrap">
                                <span className="text-table font-medium text-text-main">{a.name}</span>
                                {a.status && (
                                    <span className={`px-1.5 py-0.5 rounded text-sm ${STATUS_CLASS[a.status] ?? 'bg-gray-100 text-gray-600'}`}>
                                        {STATUS_LABEL[a.status] ?? a.status}
                                    </span>
                                )}
                            </div>
                            <div className="text-sm text-gray-500 mt-0.5 flex flex-wrap gap-x-3">
                                <span>{meta.label}</span>
                                {a.date && <span>{formatISODate(a.date)}</span>}
                                {a.ownerName && <span>{a.ownerName}</span>}
                            </div>
                        </div>
                    </li>
                );
            })}
        </ul>
    );
};
