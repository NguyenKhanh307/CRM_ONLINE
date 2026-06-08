import { useRef, useEffect, useState } from 'react';
import {
    FiKey,
    FiUser,
    FiShield,
    FiLogOut,
    FiChevronDown,
    FiGlobe,
} from 'react-icons/fi';
import { useAuth } from '@/core/auth/useAuth';

interface Props {
    onClose: () => void;
}

const UserMenuDropdown = ({ onClose }: Props) => {
    const { user, logout } = useAuth();
    const ref = useRef<HTMLDivElement>(null);

    useEffect(() => {
        const handler = (e: MouseEvent) => {
            if (ref.current && !ref.current.contains(e.target as Node)) {
                onClose();
            }
        };
        document.addEventListener('mousedown', handler);
        return () => document.removeEventListener('mousedown', handler);
    }, [onClose]);

    const menuItems = [
        { icon: <FiKey size={15} />, label: 'Đổi mật khẩu', onClick: () => {} },
        { icon: <FiUser size={15} />, label: 'Thiết lập tài khoản', onClick: () => {} },
        { icon: <FiShield size={15} />, label: 'Thiết lập bảo mật', onClick: () => {} },
    ];

    return (
        <div
            ref={ref}
            className="absolute right-0 top-full mt-2 w-[220px] bg-white rounded-card shadow-lg border border-gray-200 z-50 py-1"
        >
            {/* User info */}
            <div className="px-4 py-3 border-b border-gray-100">
                <p className="text-sm font-semibold text-text-main truncate">
                    {user?.username ?? 'Người dùng'}
                </p>
                <p className="text-sm text-gray-400 truncate mt-0.5">
                    {user?.email ?? ''}
                </p>
            </div>

            {/* Menu items */}
            <ul className="py-1">
                {menuItems.map(({ icon, label, onClick }) => (
                    <li key={label}>
                        <button
                            onClick={() => { onClick(); onClose(); }}
                            className="w-full flex items-center gap-3 px-4 py-2 text-sm text-text-main hover:bg-gray-50 text-left"
                        >
                            <span className="text-gray-500">{icon}</span>
                            {label}
                        </button>
                    </li>
                ))}

                {/* Language */}
                <li>
                    <button className="w-full flex items-center gap-3 px-4 py-2 text-sm text-text-main hover:bg-gray-50 text-left">
                        <span className="text-base leading-none">🇻🇳</span>
                        <span>Ngôn ngữ: Việt Nam</span>
                        <FiChevronDown size={14} className="ml-auto text-gray-400" />
                    </button>
                </li>
            </ul>

            <div className="border-t border-gray-100 py-1">
                <button
                    onClick={() => { logout(); onClose(); }}
                    className="w-full flex items-center gap-3 px-4 py-2 text-sm text-red-500 hover:bg-red-50 text-left"
                >
                    <FiLogOut size={15} />
                    Đăng xuất
                </button>
            </div>
        </div>
    );
};

export const UserMenuButton = () => {
    const { user } = useAuth();
    const [open, setOpen] = useState(false);

    const initials = user?.username
        ? user.username.slice(0, 2).toUpperCase()
        : 'U';

    return (
        <div className="relative">
            <button
                onClick={() => setOpen(v => !v)}
                className="flex items-center justify-center w-8 h-8 rounded-full bg-primary text-white text-sm font-semibold hover:opacity-90"
                title={user?.username ?? 'Tài khoản'}
            >
                {initials}
            </button>
            {open && <UserMenuDropdown onClose={() => setOpen(false)} />}
        </div>
    );
};
