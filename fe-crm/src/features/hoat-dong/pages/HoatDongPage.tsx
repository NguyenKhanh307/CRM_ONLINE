import { EmptyState } from '@/shared/components/EmptyState';

const HoatDongPage = () => (
    <div className="p-6 bg-bg-main min-h-[calc(100vh-50px)] flex items-center justify-center">
        <EmptyState
            illustration="/images/empty-states/hoat-dong.svg"
            title="Quản lý hoạt động"
            description="Ghi nhận và theo dõi các hoạt động chăm sóc khách hàng."
        />
    </div>
);

export default HoatDongPage;
