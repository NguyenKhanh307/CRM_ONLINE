import { FiX } from 'react-icons/fi';

interface AlertModalProps {
    message: string;
    onClose: () => void;
}

/**
 * Modal thông báo dùng chung — hiển thị message + nút Đóng.
 * Được trigger qua useAlert() hook.
 */
export const AlertModal = ({ message, onClose }: AlertModalProps) => (
    <div className="fixed inset-0 z-[9999] flex items-center justify-center">
        {/* Backdrop */}
        <div
            className="absolute inset-0 bg-black/20"
            onClick={onClose}
        />

        {/* Dialog */}
        <div className="relative bg-white rounded-card shadow-lg w-full max-w-sm mx-4">
            {/* Close button */}
            <button
                type="button"
                onClick={onClose}
                className="absolute top-3 right-3 text-gray-400 hover:text-gray-600 transition-colors"
            >
                <FiX size={16} />
            </button>

            {/* Message */}
            <div className="px-5 pt-5 pb-4 pr-8">
                <p className="text-md text-text-main">{message}</p>
            </div>

            {/* Footer */}
            <div className="flex justify-end px-5 pb-4">
                <button
                    type="button"
                    onClick={onClose}
                    className="bg-primary text-white text-md px-4 py-1.5 rounded-btn hover:bg-blue-600 transition-colors"
                >
                    Đóng
                </button>
            </div>
        </div>
    </div>
);
