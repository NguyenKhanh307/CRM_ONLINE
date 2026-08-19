// Mồ côi — 0 nơi import (dữ liệu revenue/cost/profit theo tháng không còn hiển thị ở dashboard
// nào từ khi ManagerDashboardView redesign + StaffDashboardView cố ý bỏ qua các field này).
// Mở lại: bỏ comment component này + wiring vào ManagerDashboardView/StaffDashboardView + bỏ
// comment các field totalRevenue/totalCost/totalProfit/*ByMonth ở SalesDashboardResult (BE).
// import {
//     ComposedChart, Bar, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer,
// } from 'recharts';
// import type { TimeSeriesPoint } from '../types/dashboardTypes';
// import { COLORS, shortMonth } from './chartTheme';
//
// interface Props {
//     revenue: TimeSeriesPoint[];
//     cost: TimeSeriesPoint[];
//     profit: TimeSeriesPoint[];
//     /** Hàm format giá trị (theo đơn vị hiển thị). */
//     format: (v: number) => string;
// }
//
// /**
//  * Biểu đồ combo Doanh thu (cột) + Chi phí (cột) + Lợi nhuận (đường) theo 12 tháng.
//  */
// export const RevenueCostProfitChart = ({ revenue, cost, profit, format }: Props) => {
//     const data = revenue.map((r, i) => ({
//         name: shortMonth(r.period),
//         revenue: r.value,
//         cost: cost[i]?.value ?? 0,
//         profit: profit[i]?.value ?? 0,
//     }));
//
//     return (
//         <div className="h-72">
//             <ResponsiveContainer width="100%" height="100%">
//                 <ComposedChart data={data} margin={{ top: 8, right: 8, bottom: 0, left: 8 }}>
//                     <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#f0f0f0" />
//                     <XAxis dataKey="name" tick={{ fontSize: 12 }} />
//                     <YAxis tick={{ fontSize: 11 }} tickFormatter={(v) => format(v)} width={70} />
//                     <Tooltip formatter={(v) => format(Number(v))} />
//                     <Legend wrapperStyle={{ fontSize: 12 }} />
//                     <Bar name="Doanh thu" dataKey="revenue" fill={COLORS.success} radius={[3, 3, 0, 0]} barSize={10} />
//                     <Bar name="Chi phí" dataKey="cost" fill={COLORS.danger} radius={[3, 3, 0, 0]} barSize={10} />
//                     <Line name="Lợi nhuận" type="monotone" dataKey="profit" stroke={COLORS.primary} strokeWidth={2} dot={{ r: 2 }} />
//                 </ComposedChart>
//             </ResponsiveContainer>
//         </div>
//     );
// };
