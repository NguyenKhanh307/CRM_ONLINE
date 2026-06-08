import { DataTable } from '@/shared/components/table/DataTable';
import { useOrderList } from '../hooks/useOrderList';
import { orderColumns } from '../config/orderColumns';

const DonHangPage = () => {
    const { data = [], isLoading } = useOrderList();

    return (
        <div className="p-6 bg-bg-main min-h-screen">
            <h1 className="text-xl font-semibold text-text-main mb-4">Đơn hàng</h1>
            <div className="bg-white rounded-card p-4 shadow-sm">
                <DataTable
                    data={data}
                    columns={orderColumns}
                    isLoading={isLoading}
                    emptyText="Chưa có đơn hàng nào"
                    quickFilters={[
                        { id: 'confirmed',  label: 'Đã xác nhận', isActive: false, onToggle: () => {} },
                        { id: 'processing', label: 'Đang xử lý',  isActive: false, onToggle: () => {} },
                        { id: 'delivered',  label: 'Đã giao',     isActive: false, onToggle: () => {} },
                    ]}
                />
            </div>
        </div>
    );
};

export default DonHangPage;
