import { FiPlus, FiUpload } from 'react-icons/fi';

interface EmptyStateProps {
    illustration: string;
    title: string;
    description: string;
    onAdd?: () => void;
    onImport?: () => void;
    addLabel?: string;
    importLabel?: string;
}

// empty state dùng chung cho các trang chưa có bản ghi
// hiển thị ảnh minh họa, tiêu đề, mô tả và 2 nút hành động
export const EmptyState = ({
    illustration,
    title,
    description,
    onAdd,
    onImport,
    addLabel = 'Thêm mới',
    importLabel = 'Nhập khẩu',
}: EmptyStateProps) => {
    return (
        <div className="flex flex-col items-center justify-center py-20 px-6 text-center">
            <img
                src={illustration}
                alt=""
                className="w-56 h-56 object-contain"
            />
            <h2 className="text-xl font-bold text-text-main mt-8">{title}</h2>
            <p className="text-md text-gray-500 mt-2 max-w-md">{description}</p>
            <div className="flex items-center gap-3 mt-6">
                <button
                    type="button"
                    onClick={onAdd}
                    className="border border-primary text-primary bg-transparent rounded-btn px-4 py-2 text-md hover:bg-blue-50 transition-colors flex items-center gap-1"
                >
                    <FiPlus size={15} />
                    {addLabel}
                </button>
                <span className="text-md text-gray-400">hoặc</span>
                <button
                    type="button"
                    onClick={onImport}
                    className="bg-primary text-white rounded-btn px-4 py-2 text-md hover:opacity-90 transition-colors flex items-center gap-2"
                >
                    <FiUpload size={15} />
                    {importLabel}
                </button>
            </div>
        </div>
    );
};
