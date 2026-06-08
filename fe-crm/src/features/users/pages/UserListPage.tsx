import { useState, useMemo } from 'react';
import { DataTable } from '@/shared/components/table/DataTable';
import { userColumns, type UserRow, type UserStatus } from '../config/userColumns';

const MOCK_USERS: UserRow[] = [
    { id: 1,  fullName: 'Nguyễn Văn An',    email: 'an.nguyen@crm.vn',    role: 'Admin',   status: 'active',   createdAt: '2024-01-05' },
    { id: 2,  fullName: 'Trần Thị Bình',    email: 'binh.tran@crm.vn',    role: 'Manager', status: 'active',   createdAt: '2024-01-12' },
    { id: 3,  fullName: 'Lê Minh Cường',    email: 'cuong.le@crm.vn',     role: 'Staff',   status: 'active',   createdAt: '2024-02-03' },
    { id: 4,  fullName: 'Phạm Thị Dung',    email: 'dung.pham@crm.vn',    role: 'Staff',   status: 'inactive', createdAt: '2024-02-14' },
    { id: 5,  fullName: 'Hoàng Văn Em',     email: 'em.hoang@crm.vn',     role: 'Viewer',  status: 'active',   createdAt: '2024-03-01' },
    { id: 6,  fullName: 'Vũ Thị Phương',    email: 'phuong.vu@crm.vn',    role: 'Manager', status: 'active',   createdAt: '2024-03-08' },
    { id: 7,  fullName: 'Đặng Quốc Giang',  email: 'giang.dang@crm.vn',   role: 'Staff',   status: 'active',   createdAt: '2024-03-22' },
    { id: 8,  fullName: 'Bùi Thị Hoa',      email: 'hoa.bui@crm.vn',      role: 'Staff',   status: 'inactive', createdAt: '2024-04-05' },
    { id: 9,  fullName: 'Ngô Minh Hiếu',    email: 'hieu.ngo@crm.vn',     role: 'Viewer',  status: 'active',   createdAt: '2024-04-18' },
    { id: 10, fullName: 'Lý Thị Kim',       email: 'kim.ly@crm.vn',       role: 'Staff',   status: 'active',   createdAt: '2024-05-02' },
    { id: 11, fullName: 'Trương Văn Long',   email: 'long.truong@crm.vn',  role: 'Manager', status: 'active',   createdAt: '2024-05-15' },
    { id: 12, fullName: 'Mai Thị Lan',      email: 'lan.mai@crm.vn',      role: 'Staff',   status: 'inactive', createdAt: '2024-05-28' },
    { id: 13, fullName: 'Đinh Quang Minh',  email: 'minh.dinh@crm.vn',    role: 'Viewer',  status: 'active',   createdAt: '2024-06-10' },
    { id: 14, fullName: 'Phan Thị Nga',     email: 'nga.phan@crm.vn',     role: 'Staff',   status: 'active',   createdAt: '2024-06-25' },
    { id: 15, fullName: 'Cao Văn Oanh',     email: 'oanh.cao@crm.vn',     role: 'Staff',   status: 'active',   createdAt: '2024-07-08' },
    { id: 16, fullName: 'Tô Thị Phúc',      email: 'phuc.to@crm.vn',      role: 'Viewer',  status: 'inactive', createdAt: '2024-07-20' },
    { id: 17, fullName: 'Hà Minh Quân',     email: 'quan.ha@crm.vn',      role: 'Staff',   status: 'active',   createdAt: '2024-08-03' },
    { id: 18, fullName: 'Lương Thị Rồng',   email: 'rong.luong@crm.vn',   role: 'Manager', status: 'active',   createdAt: '2024-08-17' },
    { id: 19, fullName: 'Kiều Văn Sơn',     email: 'son.kieu@crm.vn',     role: 'Staff',   status: 'inactive', createdAt: '2024-09-01' },
    { id: 20, fullName: 'Doãn Thị Thảo',    email: 'thao.doan@crm.vn',    role: 'Staff',   status: 'active',   createdAt: '2024-09-14' },
    { id: 21, fullName: 'Giang Văn Uy',     email: 'uy.giang@crm.vn',     role: 'Viewer',  status: 'active',   createdAt: '2024-10-02' },
    { id: 22, fullName: 'Nông Thị Vân',     email: 'van.nong@crm.vn',     role: 'Staff',   status: 'active',   createdAt: '2024-10-18' },
    { id: 23, fullName: 'Tạ Quang Xuân',    email: 'xuan.ta@crm.vn',      role: 'Admin',   status: 'active',   createdAt: '2024-11-05' },
];

/** Trang danh sách người dùng. */
const UserListPage = () => {
    const [statusFilter, setStatusFilter] = useState<UserStatus | null>(null);

    /** Lọc data theo status tag đang active. */
    const filteredData = useMemo<UserRow[]>(() => {
        if (!statusFilter) return MOCK_USERS;
        return MOCK_USERS.filter((u) => u.status === statusFilter);
    }, [statusFilter]);

    /** Toggle quick filter status — bấm lại để bỏ chọn. */
    const toggleStatus = (s: UserStatus) =>
        setStatusFilter((cur) => (cur === s ? null : s));

    return (
        <div className="p-6 bg-bg-main min-h-screen">
            <h1 className="text-xl font-semibold text-text-main mb-4">Quản lý người dùng</h1>
            <div className="bg-white rounded-card p-4 shadow-sm">
                <DataTable
                    data={filteredData}
                    columns={userColumns}
                    emptyText="Chưa có người dùng nào"
                    quickFilters={[
                        {
                            id: 'active',
                            label: 'Hoạt động',
                            isActive: statusFilter === 'active',
                            onToggle: () => toggleStatus('active'),
                        },
                        {
                            id: 'inactive',
                            label: 'Vô hiệu',
                            isActive: statusFilter === 'inactive',
                            onToggle: () => toggleStatus('inactive'),
                        },
                    ]}
                />
            </div>
        </div>
    );
};

export default UserListPage;
