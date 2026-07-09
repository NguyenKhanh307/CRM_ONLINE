import { FiMenu } from 'react-icons/fi';
import { NotificationButton } from './header/NotificationPopup';
import { ShortcutsButton } from './header/ShortcutsPopup';
import { TransferWorkButton } from './header/TransferWorkModal';
import { UserMenuButton } from './header/UserMenu';

interface HeaderProps {
    onToggleSidebar: () => void;
}

export const Header = ({ onToggleSidebar }: HeaderProps) => {
    return (
        <header className="flex items-center justify-between h-[50px] px-4 bg-white border-b border-gray-200 shrink-0 z-40">
            {/* Left: hamburger */}
            <button
                onClick={onToggleSidebar}
                className="p-2 rounded hover:bg-gray-100 text-gray-500 hover:text-text-main"
                title="Đóng/mở sidebar"
            >
                <FiMenu size={20} />
            </button>

            {/* Middle: tiêu đề + nút hành động của trang, do PageHeaderSlot bơm vào qua portal */}
            <div id="page-header-slot" className="flex-1 flex items-center justify-between gap-2 min-w-0 px-3" />

            {/* Right: action icons */}
            <div className="flex items-center gap-1">
                <NotificationButton />
                <ShortcutsButton />
                <TransferWorkButton />
                <div className="w-px h-5 bg-gray-200 mx-1" />
                <UserMenuButton />
            </div>
        </header>
    );
};
