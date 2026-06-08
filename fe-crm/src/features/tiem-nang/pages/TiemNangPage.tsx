import { useNavigate } from 'react-router-dom';
import { EmptyState } from '@/shared/components/EmptyState';

const TiemNangPage = () => {
    const navigate = useNavigate();

    return (
        <div className="p-6 bg-bg-main min-h-[calc(100vh-50px)] flex items-center justify-center">
            <EmptyState
                illustration="/images/empty-states/tiem-nang.svg"
                title="Quản lý tiềm năng"
                description="Ghi nhận thông tin tiềm năng và tiến hành chăm sóc để chuyển đổi thành cơ hội bán hàng."
                onAdd={() => navigate('/tiem-nang/them-moi')}
            />
        </div>
    );
};

export default TiemNangPage;
