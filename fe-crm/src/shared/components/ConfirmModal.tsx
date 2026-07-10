import { useRef } from 'react';
import { FiX } from 'react-icons/fi';
import { DialogFooter } from '@/shared/components/ModalFooter';
import { useDialogKeyboardNav } from '@/shared/keyboard/useDialogKeyboardNav';

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
 * 4 mũi tên đổi qua lại giữa Hủy và nút xác nhận; Enter kích hoạt nút đang focus.
 */
export const ConfirmModal = ({
    message,
    onConfirm,
    onCancel,
    confirmLabel = 'Xác nhận',
    confirmDanger = false,
    isLoading = false,
}: ConfirmModalProps) => {
    const ref = useRef<HTMLDivElement>(null);
    // Hành động nguy hiểm → đứng sẵn ở nút Hủy để lỡ tay Enter không xóa mất bản ghi.
    useDialogKeyboardNav(ref, { onCancel, autoFocus: confirmDanger ? 'cancel' : 'confirm' });

    return (
        <div ref={ref} className="fixed inset-0 z-[9999] flex items-center justify-center">
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

                <DialogFooter
                    className="flex justify-end gap-1.5 px-5 pb-4"
                    onCancel={onCancel}
                    onConfirm={onConfirm}
                    confirmLabel={confirmLabel}
                    confirmDanger={confirmDanger}
                    confirmDisabled={isLoading}
                    cancelDisabled={isLoading}
                    showConfirmShortcut
                />
            </div>
        </div>
    );
};
