import { Link } from 'react-router-dom';
import { FiLifeBuoy, FiTag, FiFileText } from 'react-icons/fi';
import type { UrgentItem } from '../types/dashboardTypes';

interface Props {
    items: UrgentItem[];
}

/** Ánh xạ loại việc gấp → route + icon + màu. */
const META: Record<UrgentItem['type'], { to: (id: number | null) => string; icon: typeof FiTag; color: string }> = {
    ticket: { to: (id) => `/cham-soc/${id}`, icon: FiLifeBuoy, color: 'text-danger' },
    quotation: { to: () => '/bao-gia', icon: FiTag, color: 'text-warning' },
    invoice: { to: () => '/hoa-don', icon: FiFileText, color: 'text-primary' },
};

/**
 * Danh sách việc gấp — mỗi mục link tới bản ghi liên quan.
 */
export const UrgentList = ({ items }: Props) => {
    if (items.length === 0) return <p className="text-sm text-gray-400 py-8 text-center">Không có việc gấp 🎉</p>;
    return (
        <ul className="divide-y divide-gray-100">
            {items.map((it, i) => {
                const m = META[it.type];
                const Icon = m.icon;
                return (
                    <li key={i}>
                        <Link to={m.to(it.refId)} className="flex items-center gap-3 py-2 hover:bg-gray-50 rounded px-1">
                            <Icon className={`${m.color} shrink-0`} size={16} />
                            <div className="flex-1 min-w-0">
                                <div className="text-sm text-text-main truncate">
                                    <span className="font-medium">{it.code}</span> — {it.label}
                                </div>
                                <div className="text-sm text-gray-400">{it.dueInfo}</div>
                            </div>
                        </Link>
                    </li>
                );
            })}
        </ul>
    );
};
