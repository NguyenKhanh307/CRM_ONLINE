import { PieChart, Pie, Cell, ResponsiveContainer, Tooltip } from 'recharts';
import type { DonutSegment } from '../types/dashboardTypes';
import { colorAt } from './chartTheme';
import { formatNumber } from '@/shared/utils/number';

interface DonutChartProps {
    /** Các phần của biểu đồ. */
    segments: DonutSegment[];
    /** Nhãn tổng ở giữa (mặc định "Tổng"). */
    centerLabel?: string;
}

/**
 * Biểu đồ tròn có tổng ở giữa + chú thích liệt kê nhãn · số · %.
 */
export const DonutChart = ({ segments, centerLabel = 'Tổng' }: DonutChartProps) => {
    const total = segments.reduce((s, x) => s + x.count, 0);
    if (total === 0) return <p className="text-sm text-gray-400 py-8 text-center">Chưa có dữ liệu</p>;

    return (
        <div className="flex items-center gap-4">
            <div className="relative shrink-0" style={{ width: 160, height: 160 }}>
                <ResponsiveContainer width="100%" height="100%">
                    <PieChart>
                        <Pie data={segments} dataKey="count" nameKey="label" cx="50%" cy="50%"
                             innerRadius={52} outerRadius={76} paddingAngle={1} isAnimationActive={false}>
                            {segments.map((_, i) => <Cell key={i} fill={colorAt(i)} />)}
                        </Pie>
                        <Tooltip formatter={(v) => formatNumber(Number(v))} />
                    </PieChart>
                </ResponsiveContainer>
                <div className="absolute inset-0 flex flex-col items-center justify-center pointer-events-none">
                    <span className="text-sm text-gray-400">{centerLabel}</span>
                    <span className="text-lg font-semibold text-text-main">{formatNumber(total)}</span>
                </div>
            </div>
            <ul className="flex-1 space-y-1.5 max-h-44 overflow-y-auto">
                {segments.map((seg, i) => (
                    <li key={i} className="flex items-center gap-2 text-sm">
                        <span className="w-2.5 h-2.5 rounded-full shrink-0" style={{ background: colorAt(i) }} />
                        <span className="flex-1 truncate text-text-main">{seg.label}</span>
                        <span className="text-gray-500">{formatNumber(seg.count)}</span>
                        <span className="text-gray-400 w-12 text-right">{seg.pct}%</span>
                    </li>
                ))}
            </ul>
        </div>
    );
};
