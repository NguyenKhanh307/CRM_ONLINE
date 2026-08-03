import type { ReactNode } from 'react';
import type { IconType } from 'react-icons';

interface FieldErrorProps {
    // thông báo lỗi — có giá trị thì ô nhập viền đỏ và hiện dòng chữ đỏ bên dưới
    error?: string | null;
    // dòng gợi ý xám — bị dòng lỗi thay thế khi có lỗi
    hint?: string;
    // icon xám neo bên trái trong ô nhập. Ô nhập phải có sẵn padding trái (pl-9)
    icon?: IconType;
    children: ReactNode;
}

// bọc một ô nhập để hiện lỗi ngay dưới nó (viền đỏ + dòng chữ đỏ)
// dùng khi form đã có sẵn `<label>` riêng và chỉ cần gắn thêm phần báo lỗi —
// các modal chỉnh sửa. Form dựng mới thì dùng `FormField` (có luôn label)
export const FieldError = ({ error, hint, icon: Icon, children }: FieldErrorProps) => (
    <>
        {/* [&_input],[&_select],[&_textarea]: tô viền đỏ cho ô nhập bên trong khi có lỗi */}
        <div
            className={`relative ${
                error ? '[&_input]:border-danger [&_select]:border-danger [&_textarea]:border-danger' : ''
            }`}
        >
            {Icon && (
                <span className="absolute inset-y-0 left-3 flex items-center text-gray-400 pointer-events-none">
                    <Icon size={16} />
                </span>
            )}
            {children}
        </div>
        {error ? (
            <p className="text-xs text-danger mt-1">{error}</p>
        ) : (
            hint && <p className="text-xs text-gray-400 mt-1">{hint}</p>
        )}
    </>
);

interface Props extends FieldErrorProps {
    label: string;
    required?: boolean;
}

// field của form một cột: label nằm trên, ô nhập nằm dưới
// khác `FieldRow` (label trái 148px, dùng cho lưới 2 cột của `FormSection`) — dùng cho các
// form một cột: đăng nhập, kích hoạt, đăng ký nhân viên, thiết lập tài khoản, modal nhóm quyền…
// quy ước báo lỗi giống `FieldRow`: viền đỏ ô nhập + dòng chữ đỏ bên dưới. Lỗi nhập liệu
// luôn hiện tại chỗ như thế này, KHÔNG dùng popup (popup chỉ dành cho xác nhận thêm/sửa/xóa)
export const FormField = ({ label, required, ...rest }: Props) => (
    <div>
        <label className="block text-md font-medium text-text-main mb-1">
            {label}
            {required && <span className="text-danger ml-0.5">*</span>}
        </label>
        <FieldError {...rest} />
    </div>
);
