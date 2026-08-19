import type { ReactNode } from 'react';
// một dòng thông tin trong panel thông tin của trang chi tiết: nhãn + giá trị
interface InfoCardProps {
    // tiêu đề thẻ (mặc định "Thông tin")
    title?: string;
    // các dòng InfoRow của bản ghi
    children: ReactNode;
}

// thẻ thông tin cột trái của trang chi tiết 2 cột
// chỉ chứa danh sách InfoRow (một field/dòng)
// bản ghi nhiều field (hóa đơn, đơn hàng) tự cuộn trong thẻ thay vì kéo dài trang
export const InfoCard = ({ title = 'Thông tin', children }: InfoCardProps) => (
    <div className="bg-white rounded-card shadow-sm p-4">
        <h2 className="text-sm font-semibold text-text-main mb-2">{title}</h2>
        <div className="overflow-auto max-h-[calc(100vh-260px)]">{children}</div>
    </div>
);
