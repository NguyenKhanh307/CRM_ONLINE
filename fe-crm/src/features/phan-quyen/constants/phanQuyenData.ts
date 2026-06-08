import type { ModulePermission } from '@/features/phan-quyen/types/phanQuyenTypes';

const mk = (moduleId: string, label: string, perms: { suffix: string; name: string; roles: string[] }[]): ModulePermission => ({
    id: moduleId,
    label,
    isOpen: false,
    permissions: perms.map(p => ({
        id: `${moduleId.toUpperCase().replace(/-/g, '_')}_${p.suffix}`,
        name: p.name,
        checked: p.roles.length > 0,
        roles: p.roles,
    })),
});

export const INITIAL_MODULES: ModulePermission[] = [
    mk('tiem-nang', 'Tiềm năng', [
        { suffix: 'VIEW',   name: 'Xem danh sách', roles: ['Admin', 'Manager', 'Staff', 'Viewer'] },
        { suffix: 'CREATE', name: 'Thêm mới',      roles: ['Admin', 'Manager'] },
        { suffix: 'EDIT',   name: 'Chỉnh sửa',     roles: ['Admin', 'Manager'] },
        { suffix: 'DELETE', name: 'Xóa',            roles: ['Admin'] },
    ]),
    mk('chao-hang', 'Chào hàng', [
        { suffix: 'VIEW',   name: 'Xem danh sách', roles: ['Admin', 'Manager', 'Staff', 'Viewer'] },
        { suffix: 'CREATE', name: 'Thêm mới',      roles: ['Admin', 'Manager', 'Staff'] },
        { suffix: 'EDIT',   name: 'Chỉnh sửa',     roles: ['Admin', 'Manager'] },
        { suffix: 'DELETE', name: 'Xóa',            roles: ['Admin'] },
    ]),
    mk('lien-he', 'Liên hệ', [
        { suffix: 'VIEW',   name: 'Xem danh sách', roles: ['Admin', 'Manager', 'Staff', 'Viewer'] },
        { suffix: 'CREATE', name: 'Thêm mới',      roles: ['Admin', 'Manager', 'Staff'] },
        { suffix: 'EDIT',   name: 'Chỉnh sửa',     roles: ['Admin', 'Manager'] },
        { suffix: 'DELETE', name: 'Xóa',            roles: ['Admin'] },
    ]),
    mk('khach-hang', 'Khách hàng', [
        { suffix: 'VIEW',   name: 'Xem danh sách', roles: ['Admin', 'Manager', 'Staff', 'Viewer'] },
        { suffix: 'CREATE', name: 'Thêm mới',      roles: ['Admin', 'Manager'] },
        { suffix: 'EDIT',   name: 'Chỉnh sửa',     roles: ['Admin', 'Manager'] },
        { suffix: 'DELETE', name: 'Xóa',            roles: ['Admin'] },
    ]),
    mk('co-hoi', 'Cơ hội', [
        { suffix: 'VIEW',   name: 'Xem danh sách', roles: ['Admin', 'Manager', 'Staff', 'Viewer'] },
        { suffix: 'CREATE', name: 'Thêm mới',      roles: ['Admin', 'Manager', 'Staff'] },
        { suffix: 'EDIT',   name: 'Chỉnh sửa',     roles: ['Admin', 'Manager'] },
        { suffix: 'DELETE', name: 'Xóa',            roles: ['Admin'] },
    ]),
    mk('bao-gia', 'Báo giá', [
        { suffix: 'VIEW',   name: 'Xem danh sách', roles: ['Admin', 'Manager', 'Staff', 'Viewer'] },
        { suffix: 'CREATE', name: 'Thêm mới',      roles: ['Admin', 'Manager'] },
        { suffix: 'EDIT',   name: 'Chỉnh sửa',     roles: ['Admin', 'Manager'] },
        { suffix: 'DELETE', name: 'Xóa',            roles: ['Admin'] },
        { suffix: 'APPROVE', name: 'Duyệt',         roles: ['Admin', 'Manager'] },
    ]),
    mk('don-hang', 'Đơn hàng', [
        { suffix: 'VIEW',   name: 'Xem danh sách', roles: ['Admin', 'Manager', 'Staff', 'Viewer'] },
        { suffix: 'CREATE', name: 'Thêm mới',      roles: ['Admin', 'Manager'] },
        { suffix: 'EDIT',   name: 'Chỉnh sửa',     roles: ['Admin', 'Manager'] },
        { suffix: 'DELETE', name: 'Xóa',            roles: ['Admin'] },
        { suffix: 'APPROVE', name: 'Duyệt',         roles: ['Admin'] },
    ]),
    mk('hoa-don', 'Hóa đơn', [
        { suffix: 'VIEW',   name: 'Xem danh sách', roles: ['Admin', 'Manager', 'Staff', 'Viewer'] },
        { suffix: 'CREATE', name: 'Thêm mới',      roles: ['Admin', 'Manager'] },
        { suffix: 'EDIT',   name: 'Chỉnh sửa',     roles: ['Admin', 'Manager'] },
        { suffix: 'DELETE', name: 'Xóa',            roles: ['Admin'] },
    ]),
    mk('ao-co-hoi', 'Ao cơ hội', [
        { suffix: 'VIEW',   name: 'Xem danh sách', roles: ['Admin', 'Manager', 'Staff'] },
        { suffix: 'CREATE', name: 'Thêm mới',      roles: ['Admin', 'Manager'] },
        { suffix: 'EDIT',   name: 'Chỉnh sửa',     roles: ['Admin', 'Manager'] },
        { suffix: 'DELETE', name: 'Xóa',            roles: ['Admin'] },
    ]),
    mk('hoat-dong', 'Hoạt động', [
        { suffix: 'VIEW',   name: 'Xem danh sách', roles: ['Admin', 'Manager', 'Staff', 'Viewer'] },
        { suffix: 'CREATE', name: 'Thêm mới',      roles: ['Admin', 'Manager', 'Staff'] },
        { suffix: 'EDIT',   name: 'Chỉnh sửa',     roles: ['Admin', 'Manager', 'Staff'] },
        { suffix: 'DELETE', name: 'Xóa',            roles: ['Admin', 'Manager'] },
    ]),
    mk('muc-tieu', 'Mục tiêu', [
        { suffix: 'VIEW',   name: 'Xem danh sách', roles: ['Admin', 'Manager', 'Staff', 'Viewer'] },
        { suffix: 'CREATE', name: 'Thêm mới',      roles: ['Admin', 'Manager'] },
        { suffix: 'EDIT',   name: 'Chỉnh sửa',     roles: ['Admin', 'Manager'] },
        { suffix: 'DELETE', name: 'Xóa',            roles: ['Admin'] },
    ]),
    mk('nguoi-dung', 'Người dùng', [
        { suffix: 'VIEW',       name: 'Xem danh sách', roles: ['Admin', 'Manager'] },
        { suffix: 'CREATE',     name: 'Thêm mới',      roles: ['Admin'] },
        { suffix: 'EDIT',       name: 'Chỉnh sửa',     roles: ['Admin'] },
        { suffix: 'DELETE',     name: 'Xóa',            roles: ['Admin'] },
        { suffix: 'PERMISSION', name: 'Phân quyền',     roles: ['Admin'] },
    ]),
];
