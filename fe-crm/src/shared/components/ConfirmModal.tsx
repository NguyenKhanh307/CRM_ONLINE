import { FiX } from 'react-icons/fi';

interface ConfirmModalProps {
    message: string;
    onConfirm: () => void;
    onCancel: () => void;
    confirmLabel?: string;
    confirmDanger?: boolean;
    isLoading?: boolean;
}

/**
 * Modal xác nhận dùng chung — thay thế window.confirm() để đồng nhất giao diện.
 * confirmDanger=true → nút xác nhận màu đỏ (cho hành động nguy hiểm).
 */
export const ConfirmModal = ({
    message,
    onConfirm,
    onCancel,
    confirmLabel = 'Xác nhận',
    confirmDanger = false,
    isLoading = false,
}: ConfirmModalProps) => (
    <div className="fixed inset-0 z-[9999] flex items-center justify-center">
        {/* Backdrop */}
        <div className="absolute inset-0 bg-black/20" onClick={onCancel} />

        {/* Dialog */}
        <div className="relative bg-white rounded-card shadow-lg w-full max-w-sm mx-4">
            {/* Close button */}
            <button
                type="button"
                onClick={onCancel}
                className="absolute top-3 right-3 text-gray-400 hover:text-gray-600 transition-colors"
            >
                <FiX size={16} />
            </button>

            {/* Message */}
            <div className="px-5 pt-5 pb-4 pr-8">
                <p className="text-md text-text-main">{message}</p>
            </div>

            {/* Footer */}
            <div className="flex justify-end gap-2 px-5 pb-4">
                <button
                    type="button"
                    onClick={onCancel}
                    disabled={isLoading}
                    className="text-md px-4 py-1.5 rounded-btn border border-gray-300 text-gray-600 hover:bg-gray-50 transition-colors disabled:opacity-50"
                >
                    Hủy
                </button>
                <button
                    type="button"
                    onClick={onConfirm}
                    disabled={isLoading}
                    className={`text-md px-4 py-1.5 rounded-btn text-white transition-colors disabled:opacity-50 ${
                        confirmDanger
                            ? 'bg-danger hover:bg-red-600'
                            : 'bg-primary hover:bg-blue-600'
                    }`}
                >
                    {confirmLabel}
                </button>
            </div>
        </div>
    </div>
);
