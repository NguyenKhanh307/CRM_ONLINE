import type { IconType } from 'react-icons';
import {
    FiHome,
    FiDisc,
    FiUserCheck,
    FiPhoneCall,
    FiBriefcase,
    FiAward,
    FiTag,
    FiShoppingCart,
    FiDollarSign,
    FiLock,
    FiActivity,
    FiTarget,
    FiList,
    FiShield,
    FiTrash2,
} from 'react-icons/fi';

export interface NavItem {
    label: string;
    path: string;
    icon: IconType;
}

export const NAV_ITEMS: NavItem[] = [
    { label: 'Bàn làm việc', path: '/dashboard',  icon: FiHome },
    { label: 'Tiềm năng',    path: '/tiem-nang',   icon: FiDisc },
    { label: 'Chào hàng',    path: '/chao-hang',   icon: FiUserCheck },
    { label: 'Liên hệ',      path: '/lien-he',     icon: FiPhoneCall },
    { label: 'Khách hàng',   path: '/khach-hang',  icon: FiBriefcase },
    { label: 'Cơ hội',       path: '/co-hoi',      icon: FiAward },
    { label: 'Báo giá',      path: '/bao-gia',     icon: FiTag },
    { label: 'Đơn hàng',     path: '/don-hang',    icon: FiShoppingCart },
    { label: 'Hóa đơn',      path: '/hoa-don',     icon: FiDollarSign },
    { label: 'Ao cơ hội',    path: '/ao-co-hoi',   icon: FiLock },
    { label: 'Hoạt động',    path: '/hoat-dong',   icon: FiActivity },
    { label: 'Mục tiêu',     path: '/muc-tieu',    icon: FiTarget },
    { label: 'Tất cả',       path: '/tat-ca',      icon: FiList },
    { label: 'Phân quyền',  path: '/phan-quyen',  icon: FiShield },
    { label: 'Thùng rác',   path: '/thung-rac',   icon: FiTrash2 },
];
