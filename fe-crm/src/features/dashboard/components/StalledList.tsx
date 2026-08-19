import type { CountedRankedList } from '../types/dashboardTypes';
import { formatNumber } from '@/shared/utils/number';
import { COLORS } from './chartTheme';
import { RankedList } from './RankedList';

interface StalledListProps {
    data: CountedRankedList;
    // đơn vị đếm (vd "cơ hội", "lead")
    unitLabel: string;
}

/**
 * Hiển thị CountedRankedList: tổng số lớn (cảnh báo) + top item xếp theo số ngày chờ/không hoạt động.
 * Dùng cho cơ hội "treo" (B9/E21) và lead pool chung (C16).
 */
export const StalledList = ({ data, unitLabel }: StalledListProps) => {
    if (data.total === 0) {
        return <p className="text-sm text-gray-400 py-8 text-center">Không có {unitLabel} nào cần chú ý</p>;
    }
    return (
        <div>
            <div className="text-xl font-semibold text-danger mb-3">
                {formatNumber(data.total)} {unitLabel}
            </div>
            <RankedList items={data.items} format={(v) => `${formatNumber(v)} ngày`} color={COLORS.danger} />
        </div>
    );
};
