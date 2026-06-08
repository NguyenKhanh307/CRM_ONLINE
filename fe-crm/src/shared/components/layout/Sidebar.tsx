import { NavLink } from 'react-router-dom';
import { FiChevronsLeft, FiChevronsRight } from 'react-icons/fi';
import { NAV_ITEMS } from './sidebar/sidebarConfig';

interface SidebarProps {
    isOpen: boolean;
    onToggle: () => void;
}

export const Sidebar = ({ isOpen, onToggle }: SidebarProps) => {
    return (
        <aside
            className={`
                shrink-0 flex flex-col bg-white border-r border-gray-200
                overflow-hidden transition-[width] duration-200
                ${isOpen ? 'w-60' : 'w-0'}
            `}
        >
            {/* Nav */}
            <nav className="flex-1 overflow-y-auto py-2 px-2">
                <ul className="space-y-0.5">
                    {NAV_ITEMS.map(({ label, path, icon: Icon }) => (
                        <li key={path}>
                            <NavLink
                                to={path}
                                className={({ isActive }) =>
                                    `flex items-center gap-3 px-3 py-2 rounded-lg text-sm whitespace-nowrap transition-colors ${
                                        isActive
                                            ? 'bg-blue-50 text-primary font-medium'
                                            : 'text-gray-600 hover:bg-gray-100 hover:text-text-main'
                                    }`
                                }
                            >
                                <Icon size={16} className="shrink-0" />
                                {label}
                            </NavLink>
                        </li>
                    ))}
                </ul>
            </nav>

            {/* Collapse button */}
            <div className="shrink-0 border-t border-gray-100 p-2 flex justify-end">
                <button
                    onClick={onToggle}
                    className="p-1.5 rounded hover:bg-gray-100 text-gray-400 hover:text-text-main"
                    title={isOpen ? 'Thu gọn sidebar' : 'Mở rộng sidebar'}
                >
                    {isOpen ? <FiChevronsLeft size={18} /> : <FiChevronsRight size={18} />}
                </button>
            </div>
        </aside>
    );
};
