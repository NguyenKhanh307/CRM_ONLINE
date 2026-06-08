import { EmptyState } from '@/shared/components/EmptyState';

const MucTieuPage = () => (
    <div className="p-6 bg-bg-main min-h-[calc(100vh-50px)] flex items-center justify-center">
        <EmptyState
            illustration="/images/empty-states/muc-tieu.svg"
            title="Quản lý mục tiêu"
            description="Thiết lập và theo dõi mục tiêu kinh doanh của nhóm."
        />
    </div>
);

export default MucTieuPage;
