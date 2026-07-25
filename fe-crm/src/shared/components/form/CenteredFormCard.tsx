import type { ReactNode } from 'react';
import { FiArrowLeft } from 'react-icons/fi';
import type { IconType } from 'react-icons';

interface Props {
    icon: IconType;
    title: string;
    subtitle?: string;
    /** Có giá trị thì hiện nút quay lại bên trái tiêu đề. */
    onBack?: () => void;
    /** Bề rộng tối đa của cả khối tiêu đề lẫn thẻ. */
    maxWidth?: string;
    children: ReactNode;
}

/**
 * Khung trang cho form một cột: khối tiêu đề + thẻ trắng, cả hai cùng căn giữa màn hình.
 *
 * Root cố ý KHÔNG `min-h-screen` — `MainLayout` đã `h-screen overflow-hidden` và `<main>` là
 * vùng cuộn duy nhất, thêm chiều cao tối thiểu ở đây sẽ đẻ ra thanh cuộn dọc thừa.
 */
export const CenteredFormCard = ({
    icon: Icon,
    title,
    subtitle,
    onBack,
    maxWidth = 'max-w-xl',
    children,
}: Props) => (
    <div className="p-6 bg-bg-main">
        <div className={`${maxWidth} mx-auto`}>
            {/* Header */}
            <div className="flex items-center gap-3 mb-6">
                {onBack && (
                    <button
                        type="button"
                        onClick={onBack}
                        className="p-2 rounded hover:bg-gray-100 text-gray-500"
                        title="Quay lại"
                    >
                        <FiArrowLeft size={18} />
                    </button>
                )}
                <div className="flex items-center justify-center w-9 h-9 rounded-card bg-primary shrink-0">
                    <Icon size={18} className="text-white" />
                </div>
                <div className="min-w-0">
                    <h1 className="text-lg font-semibold text-text-main">{title}</h1>
                    {subtitle && <p className="text-sm text-gray-500">{subtitle}</p>}
                </div>
            </div>

            <div className="bg-white rounded-card shadow-sm p-6">{children}</div>
        </div>
    </div>
);
