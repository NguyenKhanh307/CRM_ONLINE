import { EmptyState } from '@/shared/components/EmptyState';

const ChaoHangPage = () => (
    <div className="p-6 bg-bg-main min-h-[calc(100vh-50px)] flex items-center justify-center">
        <EmptyState
            illustration="/images/empty-states/chao-hang.svg"
            title="Quản lý chào hàng"
            description="Theo dõi và quản lý các hoạt động chào hàng tới khách hàng tiềm năng."
        />
    </div>
);

export default ChaoHangPage;
