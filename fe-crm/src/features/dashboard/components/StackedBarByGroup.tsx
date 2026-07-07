import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import type { GroupedStatusRow } from '../types/dashboardTypes';
import { colorAt, viStatus } from './chartTheme';

interface Props {
    rows: GroupedStatusRow[];
    /** Phân hệ để dịch nhãn trạng thái (mặc định opportunity). */
    module?: string;
}

/**
 * Biểu đồ thanh ngang xếp chồng theo nhóm (nhân viên) — mỗi màu là một trạng thái.
 */
export const StackedBarByGroup = ({ rows, module = 'opportunity' }: Props) => {
    if (rows.length === 0) return <p className="text-sm text-gray-400 py-8 text-center">Chưa có dữ liệu</p>;

    // Tập trạng thái xuất hiện, giữ thứ tự gặp đầu tiên
    const statuses: string[] = [];
    rows.forEach((r) => r.segments.forEach((s) => { if (!statuses.includes(s.label)) statuses.push(s.label); }));

    const data = rows.map((r) => {
        const rec: Record<string, string | number> = { group: r.group };
        statuses.forEach((st) => { rec[st] = r.segments.find((s) => s.label === st)?.count ?? 0; });
        return rec;
    });

    return (
        <div style={{ height: Math.max(rows.length * 40 + 48, 160) }}>
            <ResponsiveContainer width="100%" height="100%">
                <BarChart data={data} layout="vertical" margin={{ top: 4, right: 8, bottom: 0, left: 8 }}>
                    <CartesianGrid strokeDasharray="3 3" horizontal={false} stroke="#f0f0f0" />
                    <XAxis type="number" tick={{ fontSize: 11 }} allowDecimals={false} />
                    <YAxis type="category" dataKey="group" tick={{ fontSize: 12 }} width={110} />
                    <Tooltip />
                    <Legend wrapperStyle={{ fontSize: 12 }} />
                    {statuses.map((st, i) => (
                        <Bar key={st} dataKey={st} name={viStatus(module, st)} stackId="a" fill={colorAt(i)} barSize={18} />
                    ))}
                </BarChart>
            </ResponsiveContainer>
        </div>
    );
};
