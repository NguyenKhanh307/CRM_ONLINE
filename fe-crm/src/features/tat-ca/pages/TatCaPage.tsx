import { EmptyState } from '@/shared/components/EmptyState';

const TatCaPage = () => (
    <div className="p-6 bg-bg-main min-h-[calc(100vh-50px)] flex items-center justify-center">
        <EmptyState
            illustration="/images/empty-states/tat-ca.svg"
            title="Tất cả bản ghi"
            description="Xem toàn bộ dữ liệu CRM tổng hợp tại một nơi."
        />
    </div>
);

export default TatCaPage;
