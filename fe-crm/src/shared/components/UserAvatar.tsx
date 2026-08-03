import type { CSSProperties } from 'react';

interface Props {
    // họ tên — dùng lấy chữ cái đầu khi không có ảnh
    fullName?: string | null;
    // URL ảnh đại diện; có thì hiện ảnh, không thì hiện chữ cái đầu
    avatarUrl?: string | null;
    // kích thước (px) — mặc định 32
    size?: number;
    className?: string;
}

// ảnh đại diện người dùng dùng chung — hiện ảnh nếu có `avatarUrl`,
// ngược lại hiện 2 chữ cái đầu của họ tên trên nền primary
// dùng ở header, menu người dùng, trang thiết lập tài khoản để đồng nhất
export const UserAvatar = ({ fullName, avatarUrl, size = 32, className = '' }: Props) => {
    const initials = (fullName || 'U').trim().slice(0, 2).toUpperCase();
    const style: CSSProperties = { width: size, height: size, fontSize: Math.round(size * 0.4) };
    return (
        <span
            className={`inline-flex items-center justify-center rounded-full overflow-hidden bg-primary text-white font-semibold shrink-0 leading-none ${className}`}
            style={style}
        >
            {avatarUrl
                ? <img src={avatarUrl} alt={fullName ?? 'Ảnh đại diện'} className="w-full h-full object-cover" />
                : initials}
        </span>
    );
};
