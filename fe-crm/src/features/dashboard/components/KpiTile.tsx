import { useId, type ReactNode } from 'react';
import { FiTrendingUp, FiTrendingDown } from 'react-icons/fi';
import { AreaChart, Area, ResponsiveContainer } from 'recharts';
import type { TimeSeriesPoint } from '../types/dashboardTypes';
import { COLORS } from './chartTheme';

interface KpiTileProps {
    label: string;
    // giá trị đã format sẵn để hiển thị
    value: ReactNode;
    // % tăng trưởng so kỳ trước (null = ẩn badge)
    growthPct?: number | null;
    // chuỗi dữ liệu cho sparkline nền (tùy chọn)
    sparkline?: TimeSeriesPoint[];
    // màu sparkline (mặc định primary)
    color?: string;
    // nội dung phụ dưới giá trị (vd breakdown)
    children?: ReactNode;
}

// badge % tăng trưởng: xanh mũi tên lên khi dương, đỏ mũi tên xuống khi âm
const GrowthBadge = ({ pct }: { pct: number }) => {
    const up = pct >= 0;
    return (
        <span className={`inline-flex items-center gap-1 text-sm font-medium ${up ? 'text-success' : 'text-danger'}`}>
            {up ? <FiTrendingUp size={13} /> : <FiTrendingDown size={13} />}
            {Math.abs(pct).toFixed(0)}%
        </span>
    );
};

/**
 * Thẻ KPI: số lớn + nhãn + badge % tăng + sparkline nền tùy chọn + nội dung phụ.
 */
export const KpiTile = ({ label, value, growthPct, sparkline, color = COLORS.primary, children }: KpiTileProps) => {
    const rawId = useId();
    const gid = `spark-${rawId.replace(/[^a-zA-Z0-9]/g, '')}`;
    return (
        <div className="bg-white rounded-card p-4 shadow-sm">
            <div className="flex items-center justify-between">
                <span className="text-sm text-gray-500">{label}</span>
                {growthPct != null && <GrowthBadge pct={growthPct} />}
            </div>
            <div className="text-xl font-semibold text-text-main mt-1">{value}</div>
            {children}
            {sparkline && sparkline.length > 0 && (
                <div className="h-10 mt-2 -mx-1">
                    <ResponsiveContainer width="100%" height="100%">
                        <AreaChart data={sparkline} margin={{ top: 2, right: 0, bottom: 0, left: 0 }}>
                            <defs>
                                <linearGradient id={gid} x1="0" y1="0" x2="0" y2="1">
                                    <stop offset="0%" stopColor={color} stopOpacity={0.35} />
                                    <stop offset="100%" stopColor={color} stopOpacity={0} />
                                </linearGradient>
                            </defs>
                            <Area type="monotone" dataKey="value" stroke={color} strokeWidth={2}
                                  fill={`url(#${gid})`} isAnimationActive={false} />
                        </AreaChart>
                    </ResponsiveContainer>
                </div>
            )}
        </div>
    );
};
