import type { ReactNode } from 'react';
import type { IconType } from 'react-icons';

type Variant = 'primary' | 'secondary' | 'outline' | 'info' | 'danger' | 'dangerSolid';

const VARIANT_CLS: Record<Variant, string> = {
    primary: 'bg-primary text-white hover:opacity-90',
    secondary: 'border border-gray-300 text-gray-600 hover:bg-gray-50',
    outline: 'border border-primary text-primary hover:bg-blue-50',
    info: 'bg-blue-50 text-primary font-medium hover:bg-blue-100',
    danger: 'bg-red-50 text-danger font-medium hover:bg-red-100',
    dangerSolid: 'bg-danger text-white hover:bg-red-600',
};

/** Nền của khối phím tắt — đậm hơn nền nút để tách bạch mà vẫn cùng tông. */
const SHORTCUT_CLS: Record<Variant, string> = {
    primary: 'bg-black/15 text-white',
    secondary: 'bg-gray-100 text-gray-500 border-l border-gray-300',
    outline: 'bg-blue-50 text-primary border-l border-primary',
    info: 'bg-blue-100 text-primary',
    danger: 'bg-red-100 text-danger',
    dangerSolid: 'bg-black/15 text-white',
};

const RING = 'focus:outline-none focus-visible:ring-2 focus-visible:ring-offset-1 focus-visible:ring-primary';

interface Props {
    children: ReactNode;
    onClick?: () => void;
    variant?: Variant;
    icon?: IconType;
    /** Tổ hợp phím hiển thị thành khối liền sát mép phải, ví dụ ['Alt','N']. */
    shortcut?: string[];
    disabled?: boolean;
    title?: string;
    type?: 'button' | 'submit';
    /** Đánh dấu `data-dialog-button` để useDialogKeyboardNav dò được nút này. */
    dialogButton?: boolean;
}

/**
 * Nút hành động dùng chung: header trang danh sách, header form, footer popup.
 * Phím tắt render thành khối liền bên phải, không phải chip <kbd> nằm giữa nút.
 */
export const ActionButton = ({
    children,
    onClick,
    variant = 'secondary',
    icon: Icon,
    shortcut,
    disabled,
    title,
    type = 'button',
    dialogButton,
}: Props) => (
    <button
        type={type}
        onClick={onClick}
        disabled={disabled}
        title={title}
        data-dialog-button={dialogButton ? '' : undefined}
        className={`inline-flex items-stretch rounded-btn overflow-hidden text-table disabled:opacity-50 ${RING} ${VARIANT_CLS[variant]}`}
    >
        <span className="flex items-center gap-1 px-2.5 py-1">
            {Icon && <Icon size={13} />}
            {children}
        </span>
        {shortcut && (
            <span className={`flex items-center px-1.5 text-[10px] font-mono ${SHORTCUT_CLS[variant]}`}>
                {shortcut.join(' ')}
            </span>
        )}
    </button>
);
