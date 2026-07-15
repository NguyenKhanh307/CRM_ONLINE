import type { ReactNode } from 'react';

/** Một thẻ thống kê trên trang chi tiết (số liệu tóm tắt của bản ghi). */
export interface StatCard {
    label: string;
    value: ReactNode;
    /** Tông màu nền: trung tính / tích cực (tiền, đã xong) / cảnh báo (chờ, quá hạn). */
    tone?: 'neutral' | 'success' | 'warning';
}

const TONE_CLS: Record<NonNullable<StatCard['tone']>, string> = {
    neutral: 'bg-white',
    success: 'bg-green-50',
    warning: 'bg-amber-50',
};

const VALUE_CLS: Record<NonNullable<StatCard['tone']>, string> = {
    neutral: 'text-text-main',
    success: 'text-green-700',
    warning: 'text-amber-700',
};

interface StatCardsProps {
    stats: StatCard[];
}

/** Hàng thẻ thống kê tóm tắt của trang chi tiết — bố cục giống ảnh mẫu (đếm / tiền đã / tiền chờ). */
export const StatCards = ({ stats }: StatCardsProps) => (
    <div className="grid grid-cols-1 sm:grid-cols-3 gap-3 mb-4">
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
