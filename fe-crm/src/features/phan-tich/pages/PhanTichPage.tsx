import { useLocation } from 'react-router-dom';
import { useMemo } from 'react';
import { DonutChart } from '@/features/dashboard/components/DonutChart';
import type { DonutSegment } from '@/features/dashboard/types/dashboardTypes';
import type { CopilotChartData, CopilotChartSegment } from '@/shared/copilot/copilotTypes';

// map {label,value}[] của Copilot sang DonutSegment[] (count+pct) mà DonutChart cần
const toSegments = (segs: CopilotChartSegment[]): DonutSegment[] => {
    const total = segs.reduce((sum, s) => sum + s.value, 0);
    return segs.map((s) => ({ label: s.label, count: s.value, pct: total === 0 ? 0 : Math.round((s.value / total) * 1000) / 10 }));
};

// đọc biểu đồ đã lưu tạm khi bấm "Xem biểu đồ so sánh" (CopilotWidget#handleActionClick) — dùng
// làm phương án dự phòng khi router state trống (F5 hoặc vào thẳng URL /phan-tich)
const readCachedChart = (): CopilotChartData | undefined => {
    try {
        const raw = sessionStorage.getItem('copilot:lastChart');
        return raw ? (JSON.parse(raw) as CopilotChartData) : undefined;
    } catch {
        return undefined;
    }
};

// trang đích khi bấm nút "Xem biểu đồ so sánh" trong khung chat Copilot — chỉ 1 tiêu đề + 1 biểu đồ
// tròn, dữ liệu do BE Copilot dò theo đúng nội dung vừa hỏi (xem AskCopilotUseCase). Ưu tiên đọc
// từ React Router state (nhanh, không cần parse); F5/vào thẳng URL thì state mất -> đọc lại từ
// sessionStorage (CopilotWidget đã lưu song song lúc điều hướng) để biểu đồ không biến mất
const PhanTichPage = () => {
    const location = useLocation();
    const stateChart = (location.state as { chart?: CopilotChartData } | null)?.chart;
    // eslint-disable-next-line react-hooks/exhaustive-deps
    const chart = useMemo(() => stateChart ?? readCachedChart(), [stateChart]);

    return (
        <div className="p-6 bg-bg-main min-h-[calc(100vh-50px)]">
            {chart ? (
                <div className="max-w-2xl mx-auto">
                    <h1 className="text-xl font-semibold text-text-main mb-4 text-center">{chart.title}</h1>
                    <div className="bg-white rounded-card p-6 shadow-sm">
                        <DonutChart segments={toSegments(chart.segments)} />
                    </div>
                </div>
            ) : (
                <div className="text-center text-gray-400 py-20">
                    Chưa có dữ liệu so sánh. Hãy hỏi trợ lý AI một câu so sánh (vd "So sánh doanh thu
                    theo chiến dịch") rồi bấm nút "Xem biểu đồ so sánh" trong khung chat.
                </div>
            )}
        </div>
    );
};

export default PhanTichPage;
