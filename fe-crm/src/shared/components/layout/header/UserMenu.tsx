import { useRef, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { FiKey, FiUser, FiLogOut } from 'react-icons/fi';
import { useAuth } from '@/core/auth/useAuth';
import { UserAvatar } from '@/shared/components/UserAvatar';
import { ChangePasswordModal } from '@/features/users/components/ChangePasswordModal';

interface Props {
    onClose: () => void;
    onChangePassword: () => void;
}

const UserMenuDropdown = ({ onClose, onChangePassword }: Props) => {
    const { user, logout } = useAuth();
    const navigate = useNavigate();
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
        { icon: <FiKey size={15} />, label: 'Đổi mật khẩu', onClick: onChangePassword },
        { icon: <FiUser size={15} />, label: 'Thiết lập tài khoản', onClick: () => navigate('/tai-khoan') },
    ];

    return (
        <div
            ref={ref}
            className="absolute right-0 top-full mt-2 w-[220px] bg-white rounded-card shadow-lg border border-gray-200 z-50 py-1"
        >
            {/* User info */}
            <div className="px-4 py-3 border-b border-gray-100 flex items-center gap-3">
                <UserAvatar fullName={user?.fullName} avatarUrl={user?.avatarUrl} size={40} />
                <div className="min-w-0">
                    <p className="text-sm font-semibold text-text-main truncate">
                        {user?.fullName ?? 'Người dùng'}
                    </p>
                    <p className="text-sm text-gray-400 truncate mt-0.5">
                        {user?.email ?? ''}
                    </p>
                </div>
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
    const [showChangePassword, setShowChangePassword] = useState(false);

    return (
        <div className="relative">
            <button
                onClick={() => setOpen(v => !v)}
                className="rounded-full hover:opacity-90 focus:outline-none focus-visible:ring-2 focus-visible:ring-primary"
                title={user?.fullName ?? 'Tài khoản'}
            >
                <UserAvatar fullName={user?.fullName} avatarUrl={user?.avatarUrl} size={32} />
            </button>
            {open && (
                <UserMenuDropdown
                    onClose={() => setOpen(false)}
                    onChangePassword={() => setShowChangePassword(true)}
                />
            )}
            {showChangePassword && (
                <ChangePasswordModal onClose={() => setShowChangePassword(false)} />
            )}
        </div>
    );
};
