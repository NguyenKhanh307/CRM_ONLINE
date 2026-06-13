import type { ReactNode } from 'react';

/**
 * Hàng field trong form: label trái 148px + control bên phải.
 * Dùng trong lưới grid-cols-2 của FormSection.
 */
export const FieldRow = ({
    label,
    required,
    alignTop,
    children,
}: {
    label: string;
    required?: boolean;
    /** Căn label lên đầu (cho textarea). */
    alignTop?: boolean;
    children: ReactNode;
}) => (
    <div className={`flex ${alignTop ? 'items-start' : 'items-center'} gap-3`}>
        <span
            className={`text-sm text-gray-600 flex-shrink-0 w-[148px] ${alignTop ? 'pt-1.5' : ''}`}
        >
            {label}
            {required && <span className="text-danger ml-0.5">*</span>}
        </span>
        <div className="flex-1 min-w-0">{children}</div>
    </div>
);
