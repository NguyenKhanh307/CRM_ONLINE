import { NavLink } from 'react-router-dom';
import { FiChevronsLeft, FiChevronsRight } from 'react-icons/fi';
import { useMemo } from 'react';
import { NAV_ITEMS } from './sidebar/sidebarConfig';
import { usePermission } from '@/core/permissions/usePermission';
import { useNotificationList } from '@/shared/notifications/useNotifications';

interface SidebarProps {
    isOpen: boolean;
    onToggle: () => void;
}

// loại thông báo mang tính "cập nhật/thay đổi" -> chấm vàng; còn lại (tạo mới/cần hành động) -> chấm đỏ
const UPDATE_NOTIFICATION_TYPES = new Set<string>([
    'quotation_approved', 'quotation_rejected', 'quotation_accepted',
    'quotation_customer_response', 'ticket_resolved',
]);

// sidebar điều hướng chính — lọc mục theo quyền, hiện chấm thông báo chưa đọc theo module
export const Sidebar = ({ isOpen, onToggle }: SidebarProps) => {
    const { hasRole, hasModuleAccess } = usePermission();
    const isAdmin = hasRole('ADMIN');
    const { data: notifications } = useNotificationList();

    // màu chấm theo module dựa trên thông báo CHƯA ĐỌC (đọc xong -> hết chấm)
    // tiền tố `type` trùng module key (lead_*, quotation_*, ticket_*…) nên tách trước dấu `_` ra module
    // đỏ = tạo mới/cần hành động, vàng = cập nhật/thay đổi; đỏ ưu tiên hơn vàng nếu module có cả hai
    // màu KHÔNG phụ thuộc mục có đang được chọn hay không
    const moduleDot = useMemo(() => {
        const map = new Map<string, 'red' | 'yellow'>();
        for (const n of notifications ?? []) {
            if (n.isRead || !n.type) continue;
            const mod = n.type.split('_')[0];
            const color: 'red' | 'yellow' = UPDATE_NOTIFICATION_TYPES.has(n.type) ? 'yellow' : 'red';
            if (map.get(mod) === 'red') continue;      // đỏ đã chốt, không hạ xuống vàng
            if (color === 'red') map.set(mod, 'red');
            else if (!map.has(mod)) map.set(mod, 'yellow');
        }
        return map;
    }, [notifications]);

    const visibleItems = useMemo(() => NAV_ITEMS.filter((item) => {
        if (item.adminOnly)    return isAdmin;
        if (item.module)       return !isAdmin && hasModuleAccess(item.module);
        if (item.nonAdminOnly) return !isAdmin;
        return true;
    }), [isAdmin, hasModuleAccess]);

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
                    {visibleItems.map(({ label, path, icon: Icon, module }) => {
                        // chấm báo thông báo chưa đọc thuộc module này — đỏ (cần đọc) / vàng (mới cập nhật); hết khi đã đọc
                        const dot = module ? moduleDot.get(module) : undefined;
                        return (
                            <li key={path}>
                                <NavLink
                                    to={path}
                                    className={({ isActive }) =>
                                        `flex items-center gap-3 px-3 py-2 rounded-lg text-sm whitespace-nowrap transition-colors ${
                                            isActive
                                                ? 'bg-primary text-white font-medium shadow-sm'
                                                : 'text-gray-600 hover:bg-gray-100 hover:text-text-main'
                                        }`
                                    }
                                >
                                    <Icon size={16} className="shrink-0" />
                                    {label}
                                    {dot && (
                                        <span
                                            className={`ml-auto w-2 h-2 rounded-full shrink-0 ${
                                                dot === 'red' ? 'bg-red-500' : 'bg-amber-400'
                                            }`}
                                            title={dot === 'red' ? 'Có thông báo mới cần đọc' : 'Có cập nhật mới'}
                                        />
                                    )}
                                </NavLink>
                            </li>
                        );
                    })}
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
