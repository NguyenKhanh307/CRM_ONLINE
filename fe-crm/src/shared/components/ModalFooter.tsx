import type { IconType } from 'react-icons';
import { ActionButton } from './ActionButton';
import { SHORTCUTS } from '@/shared/keyboard/shortcuts';

interface ModalFooterProps {
    onCancel: () => void;
    /** Đang lưu → vô hiệu nút và đổi nhãn. */
    saving?: boolean;
    saveLabel?: string;
    /** Ghi đè class của thanh footer khi modal có bố cục khác. */
    className?: string;
}

/**
 * Footer cho modal chỉnh sửa có `<form>`: Hủy (Esc) + Lưu (Ctrl+S, `type="submit"`).
 * Phím tắt chỉ là nhãn — `useFormKeyboardNav` mới là nơi xử lý Esc và Ctrl+S.
 */
export const ModalFooter = ({
    onCancel,
    saving,
    saveLabel = 'Lưu',
    className = 'flex justify-end gap-1.5 pt-2 border-t border-gray-100',
}: ModalFooterProps) => (
    <div className={className}>
        <ActionButton variant="secondary" shortcut={SHORTCUTS.CANCEL.keys} onClick={onCancel}>
            Hủy
        </ActionButton>
        <ActionButton variant="primary" type="submit" disabled={saving} shortcut={SHORTCUTS.SAVE.keys}>
            {saving ? 'Đang lưu…' : saveLabel}
        </ActionButton>
    </div>
);

interface DialogFooterProps {
    onCancel: () => void;
    onConfirm: () => void;
    confirmLabel: string;
    /** Nút xác nhận nền đỏ đặc (hành động nguy hiểm). */
    confirmDanger?: boolean;
    confirmIcon?: IconType;
    confirmDisabled?: boolean;
    /** Hiện nhãn `Enter` trên nút xác nhận. Tắt khi popup có ô nhập, vì Enter chưa chắc kích hoạt nút. */
    showConfirmShortcut?: boolean;
    cancelDisabled?: boolean;
    className?: string;
}

/**
 * Footer cho popup xác nhận. Nút mang `data-dialog-button` để `useDialogKeyboardNav`
 * dò được và cho 4 mũi tên đổi qua lại giữa chúng.
 */
export const DialogFooter = ({
    onCancel,
    onConfirm,
    confirmLabel,
    confirmDanger,
    confirmIcon,
    confirmDisabled,
    showConfirmShortcut,
    cancelDisabled,
    className = 'flex justify-end gap-1.5 px-5 py-3 border-t border-gray-200',
}: DialogFooterProps) => (
    <div className={className}>
        <ActionButton
            variant="secondary"
            dialogButton
            shortcut={SHORTCUTS.CANCEL.keys}
            onClick={onCancel}
            disabled={cancelDisabled}
        >
            Hủy
        </ActionButton>
        <ActionButton
            variant={confirmDanger ? 'dangerSolid' : 'primary'}
            dialogButton
            icon={confirmIcon}
            shortcut={showConfirmShortcut ? SHORTCUTS.CONFIRM.keys : undefined}
            onClick={onConfirm}
            disabled={confirmDisabled}
        >
            {confirmLabel}
        </ActionButton>
    </div>
);
