import { EmptyState } from '@/shared/components/EmptyState';

const HoaDonPage = () => (
    <div className="p-6 bg-bg-main min-h-[calc(100vh-50px)] flex items-center justify-center">
        <EmptyState
            illustration="/images/empty-states/hoa-don.svg"
            title="Quản lý hóa đơn"
            description="Tạo và quản lý hóa đơn thanh toán."
        />
    </div>
);

export default HoaDonPage;
