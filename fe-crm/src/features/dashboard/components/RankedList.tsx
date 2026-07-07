import type { RankedItem } from '../types/dashboardTypes';
import { COLORS } from './chartTheme';

interface Props {
    items: RankedItem[];
    /** Hàm format giá trị (theo đơn vị hiển thị). */
    format: (v: number) => string;
    color?: string;
}

/**
 * Danh sách xếp hạng theo giá trị: nhãn + thanh tỉ lệ + giá trị (vd cơ hội giá trị lớn).
 */
export const RankedList = ({ items, format, color = COLORS.primary }: Props) => {
    if (items.length === 0) return <p className="text-sm text-gray-400 py-8 text-center">Chưa có dữ liệu</p>;
    const max = Math.max(...items.map((i) => i.value), 1);
    return (
        <ul className="space-y-2.5">
            {items.map((it, i) => (
                <li key={i}>
                    <div className="flex justify-between text-sm mb-1">
                        <span className="text-text-main truncate flex-1">{it.label}</span>
                        <span className="text-gray-600 ml-2 shrink-0">{format(it.value)}</span>
                    </div>
                    <div className="h-1.5 bg-gray-100 rounded-full overflow-hidden">
                        <div className="h-full rounded-full" style={{ width: `${(it.value / max) * 100}%`, background: color }} />
                    </div>
                </li>
            ))}
        </ul>
    );
};
