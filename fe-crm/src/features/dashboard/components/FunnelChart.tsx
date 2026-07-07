import type { FunnelStage } from '../types/dashboardTypes';
import { colorAt } from './chartTheme';
import { formatNumber } from '@/shared/utils/number';

interface Props {
    stages: FunnelStage[];
}

/**
 * Phễu chuyển đổi: mỗi giai đoạn là một thanh ngang có bề rộng tỉ lệ với % so giai đoạn đầu.
 */
export const FunnelChart = ({ stages }: Props) => {
    if (stages.length === 0) return <p className="text-sm text-gray-400 py-8 text-center">Chưa có dữ liệu</p>;
    return (
        <div className="space-y-2">
            {stages.map((st, i) => (
                <div key={i}>
                    <div className="flex justify-between text-sm mb-1">
                        <span className="text-text-main truncate">{st.label}</span>
                        <span className="text-gray-500">{formatNumber(st.count)} · {st.pctOfFirst}%</span>
                    </div>
                    <div className="h-6 bg-gray-100 rounded-btn overflow-hidden">
                        <div className="h-full rounded-btn flex items-center justify-end pr-2 text-white text-sm font-medium"
                             style={{ width: `${Math.max(st.pctOfFirst, 3)}%`, background: colorAt(i) }}>
                        </div>
                    </div>
                </div>
            ))}
        </div>
    );
};
