import { DataTable } from '@/shared/components/table/DataTable';
import { useWarehouseList } from '../hooks/useWarehouseList';
import { warehouseColumns } from '../config/warehouseColumns';

const KhoHangPage = () => {
    const { data = [], isLoading } = useWarehouseList();

    return (
        <div className="p-6 bg-bg-main min-h-screen">
            <h1 className="text-xl font-semibold text-text-main mb-4">Kho hàng</h1>
            <div className="bg-white rounded-card p-4 shadow-sm">
                <DataTable
                    data={data}
                    columns={warehouseColumns}
                    isLoading={isLoading}
                    emptyText="Chưa có kho hàng nào"
                    quickFilters={[
                        { id: 'active',   label: 'Đang hoạt động',    isActive: false, onToggle: () => {} },
                        { id: 'inactive', label: 'Ngừng hoạt động',   isActive: false, onToggle: () => {} },
                    ]}
                />
            </div>
        </div>
    );
};

export default KhoHangPage;
