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
} from 'react-icons/fi';

export interface NavItem {
    label: string;
    path: string;
    icon: IconType;
}

export const NAV_ITEMS: NavItem[] = [
    { label: 'Bàn làm việc', path: '/dashboard',  icon: FiHome },
    { label: 'Tiềm năng',    path: '/tiem-nang',   icon: FiDisc },
    { label: 'Liên hệ',      path: '/lien-he',     icon: FiPhoneCall },
    { label: 'Khách hàng',   path: '/khach-hang',  icon: FiBriefcase },
    { label: 'Cơ hội',       path: '/co-hoi',      icon: FiAward },
    { label: 'Báo giá',      path: '/bao-gia',     icon: FiTag },
    { label: 'Đơn hàng',     path: '/don-hang',    icon: FiShoppingCart },
    { label: 'Hoạt động',    path: '/hoat-dong',   icon: FiActivity },
    { label: 'Sản phẩm',     path: '/san-pham',    icon: FiBox },
    { label: 'Kho hàng',     path: '/kho-hang',    icon: FiArchive },
    { label: 'Phân quyền',   path: '/phan-quyen',  icon: FiShield },
    { label: 'Thùng rác',    path: '/thung-rac',   icon: FiTrash2 },
];
