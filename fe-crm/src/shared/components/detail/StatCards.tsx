import type { ReactNode } from 'react';
// một thẻ thống kê trên trang chi tiết (số liệu tóm tắt của bản ghi)
export interface StatCard {
    label: string;
    value: ReactNode;
    // tông màu nền: trung tính / tích cực (tiền, đã xong) / cảnh báo (chờ, quá hạn)
    tone?: 'neutral' | 'success' | 'warning';
}
// tông màu nền của thẻ thống kê
const TONE_CLS: Record<NonNullable<StatCard['tone']>, string> = {
    neutral: 'bg-white',
    success: 'bg-green-50',
    warning: 'bg-amber-50',
};
// tông màu chữ của giá trị trong thẻ thống kê
const VALUE_CLS: Record<NonNullable<StatCard['tone']>, string> = {
    neutral: 'text-text-main',
    success: 'text-green-700',
    warning: 'text-amber-700',
};
// props của hàng thẻ thống kê tóm tắt
interface StatCardsProps {
    stats: StatCard[];
}

// hàng thẻ thống kê tóm tắt của trang chi tiết — bố cục giống ảnh mẫu (đếm / tiền đã / tiền chờ)
export const StatCards = ({ stats }: StatCardsProps) => (
    <div className="grid grid-cols-1 sm:grid-cols-3 gap-3 mb-4">
        {/* hiển thị từng thẻ thống kê */}
        {stats.map(s => {
            const tone = s.tone ?? 'neutral';
            return (
                <div key={s.label} className={`rounded-card shadow-sm p-4 ${TONE_CLS[tone]}`}>
                    <div className={`text-xl font-semibold ${VALUE_CLS[tone]}`}>{s.value}</div>
                    <div className="text-sm text-gray-500 mt-0.5">{s.label}</div>
                </div>
            );
        })}
    </div>
);
