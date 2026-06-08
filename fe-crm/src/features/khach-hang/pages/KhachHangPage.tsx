import { DataTable } from '@/shared/components/table/DataTable';
import { useCustomerList } from '../hooks/useCustomerList';
import { customerColumns } from '../config/customerColumns';

const KhachHangPage = () => {
    const { data = [], isLoading } = useCustomerList();

    return (
        <div className="p-6 bg-bg-main min-h-screen">
            <h1 className="text-xl font-semibold text-text-main mb-4">Khách hàng</h1>
            <div className="bg-white rounded-card p-4 shadow-sm">
                <DataTable
                    data={data}
                    columns={customerColumns}
                    isLoading={isLoading}
                    emptyText="Chưa có khách hàng nào"
                    quickFilters={[
                        { id: 'active',   label: 'Hoạt động',      isActive: false, onToggle: () => {} },
                        { id: 'inactive', label: 'Không hoạt động', isActive: false, onToggle: () => {} },
                    ]}
                />
            </div>
        </div>
    );
};

export default KhachHangPage;
