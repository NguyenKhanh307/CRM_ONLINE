import { EmptyState } from '@/shared/components/EmptyState';

const AoCoHoiPage = () => (
    <div className="p-6 bg-bg-main min-h-[calc(100vh-50px)] flex items-center justify-center">
        <EmptyState
            illustration="/images/empty-states/ao-co-hoi.svg"
            title="Ao cơ hội"
            description="Quản lý vòng đời và phễu cơ hội bán hàng."
        />
    </div>
);

export default AoCoHoiPage;
