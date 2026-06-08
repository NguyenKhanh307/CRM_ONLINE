import { EmptyState } from '@/shared/components/EmptyState';

const LienHePage = () => (
    <div className="p-6 bg-bg-main min-h-[calc(100vh-50px)] flex items-center justify-center">
        <EmptyState
            illustration="/images/empty-states/lien-he.svg"
            title="Quản lý liên hệ"
            description="Lưu trữ và quản lý thông tin liên hệ của khách hàng và đối tác."
        />
    </div>
);

export default LienHePage;
