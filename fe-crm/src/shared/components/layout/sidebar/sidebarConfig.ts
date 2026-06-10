import type { IconType } from 'react-icons';
import {
    FiHome,
    FiDisc,
    FiPhoneCall,
    FiBriefcase,
    FiAward,
    FiTag,
    FiShoppingCart,
    FiActivity,
    FiShield,
    FiTrash2,
    FiBox,
    FiArchive,
    FiUserPlus,
} from 'react-icons/fi';

export interface NavItem {
    label: string;
    path: string;
    icon: IconType;
    /** Tên module permission (vd: 'lead', 'order'). Ẩn với ADMIN, hiện khi user có ít nhất 1 quyền trong module. */
    module?: string;
    /** Chỉ hiện với ADMIN. */
    adminOnly?: boolean;
    /** Ẩn với ADMIN — dành cho nhân viên. */
    nonAdminOnly?: boolean;
}

export const NAV_ITEMS: NavItem[] = [
    { label: 'Bàn làm việc', path: '/dashboard',           icon: FiHome },
    { label: 'Tiềm năng',    path: '/tiem-nang',           icon: FiDisc,         module: 'lead' },
    { label: 'Liên hệ',      path: '/lien-he',             icon: FiPhoneCall,    module: 'contact' },
    { label: 'Khách hàng',   path: '/khach-hang',          icon: FiBriefcase,    module: 'customer' },
    { label: 'Cơ hội',       path: '/co-hoi',              icon: FiAward,        module: 'opportunity' },
    { label: 'Báo giá',      path: '/bao-gia',             icon: FiTag,          module: 'quotation' },
    { label: 'Đơn hàng',     path: '/don-hang',            icon: FiShoppingCart, module: 'order' },
    { label: 'Hoạt động',    path: '/hoat-dong',           icon: FiActivity,     module: 'activity' },
    { label: 'Sản phẩm',     path: '/san-pham',            icon: FiBox,          module: 'product' },
    { label: 'Kho hàng',     path: '/kho-hang',            icon: FiArchive,      module: 'warehouse' },
    { label: 'Phân quyền',   path: '/phan-quyen',          icon: FiShield,       adminOnly: true },
    { label: 'Đăng ký NV',   path: '/dang-ky-nhan-vien',  icon: FiUserPlus,     adminOnly: true },
    { label: 'Thùng rác',    path: '/thung-rac',           icon: FiTrash2,       nonAdminOnly: true },
];
