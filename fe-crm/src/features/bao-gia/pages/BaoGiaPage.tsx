import { useNavigate } from 'react-router-dom';
import { EmptyState } from '@/shared/components/EmptyState';

const BaoGiaPage = () => {
    const navigate = useNavigate();

    return (
        <div className="p-6 bg-bg-main min-h-[calc(100vh-50px)] flex items-center justify-center">
            <EmptyState
                illustration="/images/empty-states/bao-gia.svg"
                title="Quản lý báo giá"
                description="Tạo và quản lý báo giá gửi đến khách hàng."
                onAdd={() => navigate('/bao-gia/them-moi')}
            />
        </div>
    );
};

export default BaoGiaPage;
