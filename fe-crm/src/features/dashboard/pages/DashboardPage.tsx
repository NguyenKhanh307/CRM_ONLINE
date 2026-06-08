import { EmptyState } from '@/shared/components/EmptyState';

const DashboardPage = () => (
    <div className="p-6 bg-bg-main min-h-[calc(100vh-50px)] flex items-center justify-center">
        <EmptyState
            illustration="/images/empty-states/dashboard.svg"
            title="Bàn làm việc"
            description="Tổng quan hoạt động và công việc của bạn."
        />
    </div>
);

export default DashboardPage;
