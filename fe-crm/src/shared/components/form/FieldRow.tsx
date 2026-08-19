import type { ReactNode } from 'react';

// hàng field trong form: label trái 148px + control bên phải
// dùng trong lưới grid-cols-2 của FormSection
// truyền `error` để hiện thông báo lỗi đỏ dưới ô nhập (kèm viền đỏ qua class `has-error`)
export const FieldRow = ({
    label,
    required,
    alignTop,
    error,
    children,
}: {
    label: string;
    required?: boolean;
    // căn label lên đầu (cho textarea)
    alignTop?: boolean;
    // thông báo lỗi — có giá trị thì ô nhập viền đỏ và hiện dòng lỗi bên dưới
    error?: string | null;
    children: ReactNode;
}) => (
    <div className={`flex ${alignTop || error ? 'items-start' : 'items-center'} gap-3`}>
        <span
            // label trái 148px, căn lên đầu nếu alignTop hoặc có lỗi
            className={`text-sm text-gray-600 flex-shrink-0 w-[148px] ${alignTop || error ? 'pt-1.5' : ''}`}
        >
            {label}
            {required && <span className="text-danger ml-0.5">*</span>}
        </span>
        <div className="flex-1 min-w-0">
            {/* [&_input],[&_select],[&_textarea]: tô viền đỏ cho ô nhập bên trong khi có lỗi */}
            <div className={error ? '[&_input]:border-danger [&_select]:border-danger [&_textarea]:border-danger' : undefined}>
                {children}
            </div>
            {error && <p className="text-xs text-danger mt-1">{error}</p>}
        </div>
    </div>
);
