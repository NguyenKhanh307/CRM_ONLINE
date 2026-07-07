import { AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';
import type { TimeSeriesPoint } from '../types/dashboardTypes';
import { COLORS, shortMonth } from './chartTheme';

interface Props {
    data: TimeSeriesPoint[];
    color?: string;
    format: (v: number) => string;
    height?: number;
}

/**
 * Biểu đồ vùng (area) một chuỗi theo tháng — dùng cho doanh số kỳ vọng và các trend.
 */
export const AreaTrend = ({ data, color = COLORS.primary, format, height = 240 }: Props) => {
    const rows = data.map((d) => ({ name: shortMonth(d.period), value: d.value }));
    const gid = `area-${color.replace(/[^a-zA-Z0-9]/g, '')}`;
    return (
        <div style={{ height }}>
            <ResponsiveContainer width="100%" height="100%">
                <AreaChart data={rows} margin={{ top: 8, right: 8, bottom: 0, left: 8 }}>
                    <defs>
                        <linearGradient id={gid} x1="0" y1="0" x2="0" y2="1">
                            <stop offset="0%" stopColor={color} stopOpacity={0.3} />
                            <stop offset="100%" stopColor={color} stopOpacity={0} />
                        </linearGradient>
                    </defs>
                    <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#f0f0f0" />
                    <XAxis dataKey="name" tick={{ fontSize: 12 }} />
                    <YAxis tick={{ fontSize: 11 }} tickFormatter={(v) => format(v)} width={70} />
                    <Tooltip formatter={(v) => format(Number(v))} />
                    <Area type="monotone" dataKey="value" stroke={color} strokeWidth={2} fill={`url(#${gid})`} />
                </AreaChart>
            </ResponsiveContainer>
        </div>
    );
};
