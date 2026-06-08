import { EmptyState } from '@/shared/components/EmptyState';

const CoHoiPage = () => (
    <div className="p-6 bg-bg-main min-h-[calc(100vh-50px)] flex items-center justify-center">
        <EmptyState
            illustration="/images/empty-states/co-hoi.svg"
            title="Quản lý cơ hội"
            description="Theo dõi và phát triển các cơ hội bán hàng trong pipeline."
        />
    </div>
);

export default CoHoiPage;
