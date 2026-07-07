import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';
import type { TimeSeriesPoint } from '../types/dashboardTypes';
import { COLORS, shortMonth } from './chartTheme';
import { formatNumber } from '@/shared/utils/number';

interface Props {
    data: TimeSeriesPoint[];
    color?: string;
    height?: number;
}

/**
 * Biểu đồ cột theo tháng (số nguyên) — dùng cho số tài khoản tạo mới theo tháng.
 */
export const MonthlyBar = ({ data, color = COLORS.primary, height = 240 }: Props) => {
    const rows = data.map((d) => ({ name: shortMonth(d.period), value: d.value }));
    return (
        <div style={{ height }}>
            <ResponsiveContainer width="100%" height="100%">
                <BarChart data={rows} margin={{ top: 8, right: 8, bottom: 0, left: 8 }}>
                    <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#f0f0f0" />
                    <XAxis dataKey="name" tick={{ fontSize: 12 }} />
                    <YAxis tick={{ fontSize: 11 }} allowDecimals={false} width={36} />
                    <Tooltip formatter={(v) => formatNumber(Number(v))} />
                    <Bar dataKey="value" fill={color} radius={[3, 3, 0, 0]} barSize={16} />
                </BarChart>
            </ResponsiveContainer>
        </div>
    );
};
