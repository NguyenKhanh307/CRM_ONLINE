import { DataTable } from '@/shared/components/table/DataTable';
import { useQuotationList } from '../hooks/useQuotationList';
import { quotationColumns } from '../config/quotationColumns';

const BaoGiaPage = () => {
    const { data = [], isLoading } = useQuotationList();

    return (
        <div className="p-6 bg-bg-main min-h-screen">
            <h1 className="text-xl font-semibold text-text-main mb-4">Báo giá</h1>
            <div className="bg-white rounded-card p-4 shadow-sm">
                <DataTable
                    data={data}
                    columns={quotationColumns}
                    isLoading={isLoading}
                    emptyText="Chưa có báo giá nào"
                    quickFilters={[
                        { id: 'draft',    label: 'Nháp',      isActive: false, onToggle: () => {} },
                        { id: 'sent',     label: 'Đã gửi',    isActive: false, onToggle: () => {} },
                        { id: 'approved', label: 'Đã duyệt',  isActive: false, onToggle: () => {} },
                    ]}
                />
            </div>
        </div>
    );
};

export default BaoGiaPage;
