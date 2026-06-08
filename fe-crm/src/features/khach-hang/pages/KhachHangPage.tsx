import { EmptyState } from '@/shared/components/EmptyState';

const KhachHangPage = () => (
    <div className="p-6 bg-bg-main min-h-[calc(100vh-50px)] flex items-center justify-center">
        <EmptyState
            illustration="/images/empty-states/khach-hang.svg"
            title="Quản lý khách hàng"
            description="Quản lý hồ sơ và lịch sử giao dịch của khách hàng."
        />
    </div>
);

export default KhachHangPage;
