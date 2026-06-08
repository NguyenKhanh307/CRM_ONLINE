import { EmptyState } from '@/shared/components/EmptyState';

const DonHangPage = () => (
    <div className="p-6 bg-bg-main min-h-[calc(100vh-50px)] flex items-center justify-center">
        <EmptyState
            illustration="/images/empty-states/don-hang.svg"
            title="Quản lý đơn hàng"
            description="Theo dõi và xử lý các đơn hàng từ khách hàng."
        />
    </div>
);

export default DonHangPage;
