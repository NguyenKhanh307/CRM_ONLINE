import { DataTable } from '@/shared/components/table/DataTable';
import { useProductList } from '../hooks/useProductList';
import { productColumns } from '../config/productColumns';

const SanPhamPage = () => {
    const { data = [], isLoading } = useProductList();

    return (
        <div className="p-6 bg-bg-main min-h-screen">
            <h1 className="text-xl font-semibold text-text-main mb-4">Sản phẩm</h1>
            <div className="bg-white rounded-card p-4 shadow-sm">
                <DataTable
                    data={data}
                    columns={productColumns}
                    isLoading={isLoading}
                    emptyText="Chưa có sản phẩm nào"
                    quickFilters={[
                        { id: 'active',       label: 'Đang bán',  isActive: false, onToggle: () => {} },
                        { id: 'discontinued', label: 'Ngừng bán', isActive: false, onToggle: () => {} },
                    ]}
                />
            </div>
        </div>
    );
};

export default SanPhamPage;
