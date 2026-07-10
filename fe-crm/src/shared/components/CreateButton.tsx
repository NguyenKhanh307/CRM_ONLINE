import { FiPlus } from 'react-icons/fi';
import { ActionButton } from './ActionButton';
import { SHORTCUTS } from '@/shared/keyboard/shortcuts';

/**
 * Nút "Thêm" trên header trang danh sách.
 * Phím tắt chỉ là nhãn — trang phải tự đăng ký hành động qua `usePageShortcuts({ onCreate })`.
 */
export const CreateButton = ({ onClick, label = 'Thêm' }: { onClick: () => void; label?: string }) => (
    <ActionButton variant="primary" icon={FiPlus} shortcut={SHORTCUTS.CREATE.keys} onClick={onClick}>
        {label}
    </ActionButton>
);
