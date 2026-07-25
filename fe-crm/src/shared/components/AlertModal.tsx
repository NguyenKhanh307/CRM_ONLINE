import { useRef } from 'react';
import { FiX } from 'react-icons/fi';
import { ActionButton } from '@/shared/components/ActionButton';
import { SHORTCUTS } from '@/shared/keyboard/shortcuts';
import { useDialogKeyboardNav } from '@/shared/keyboard/useDialogKeyboardNav';

interface AlertModalProps {
    message: string;
    onClose: () => void;
}

/**
 * Modal thông báo dùng chung — hiển thị message + nút Đóng.
 * Được trigger qua useAlert() hook. Nút Đóng tự focus nên Enter/Esc đóng được ngay.
 */
export const AlertModal = ({ message, onClose }: AlertModalProps) => {
    const ref = useRef<HTMLDivElement>(null);
    useDialogKeyboardNav(ref, { onCancel: onClose });

    return (
        // data-modal-layer: dấu để popup nổi khác (vd chuông thông báo) không tự đóng khi bấm vào modal này
        <div ref={ref} data-modal-layer className="fixed inset-0 z-[9999] flex items-center justify-center">
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
                    <ActionButton variant="primary" dialogButton shortcut={SHORTCUTS.CONFIRM.keys} onClick={onClose}>
                        Đóng
                    </ActionButton>
                </div>
            </div>
        </div>
    );
};
