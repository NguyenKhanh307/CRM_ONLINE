import type { ReactNode } from 'react';
import { useNavigate } from 'react-router-dom';
import { FiArrowLeft } from 'react-icons/fi';

// một mục trong hàng thông tin tóm tắt của header (vd "Mã: KH00045")
export interface DetailMeta {
    label: string;
    value: ReactNode;
}

interface DetailHeaderProps {
    // route quay lại danh sách, vd "/khach-hang"
    backTo: string;
    // nhãn nút quay lại (mặc định "Quay lại danh sách")
    backLabel?: string;
    title: string;
    // badge trạng thái hiển thị cạnh tiêu đề
    badge?: ReactNode;
    // hàng thông tin tóm tắt dưới tiêu đề — bỏ qua mục có value rỗng
    meta?: DetailMeta[];
    // nhóm nút hành động bên phải
    actions?: ReactNode;
}

// card header dùng chung cho mọi trang chi tiết: nút quay lại, tiêu đề, badge, hàng meta, nhóm nút
export const DetailHeader = ({ backTo, backLabel = 'Quay lại danh sách', title, badge, meta, actions }: DetailHeaderProps) => {
    const navigate = useNavigate();
    const shown = (meta ?? []).filter(m => m.value !== null && m.value !== undefined && m.value !== '');

    return (
        <>
            <button
                onClick={() => navigate(backTo)}
                className="flex items-center gap-1.5 text-sm text-gray-500 hover:text-primary mb-4"
            >
                <FiArrowLeft size={14} /> {backLabel}
            </button>

            <div className="bg-white rounded-card shadow-sm p-5 mb-4">
                <div className="flex items-start justify-between gap-4">
                    <div className="space-y-1 min-w-0">
                        <div className="flex items-center gap-3">
                            <h1 className="text-xl font-semibold text-text-main truncate">{title}</h1>
                            {badge}
                        </div>
                        {shown.length > 0 && (
                            <div className="text-sm text-gray-500 flex flex-wrap gap-x-4 gap-y-1">
                                {shown.map(m => (
                                    <span key={m.label}>
                                        {m.label}: <strong className="text-text-main">{m.value}</strong>
                                    </span>
                                ))}
                            </div>
                        )}
                    </div>
                    {actions && <div className="flex items-center gap-1.5 shrink-0">{actions}</div>}
                </div>
            </div>
        </>
    );
};
