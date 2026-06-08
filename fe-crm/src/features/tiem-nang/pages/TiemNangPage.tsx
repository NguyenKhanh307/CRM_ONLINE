import { DataTable } from '@/shared/components/table/DataTable';
import { useLeadList } from '../hooks/useLeadList';
import { leadColumns } from '../config/leadColumns';

const TiemNangPage = () => {
    const { data = [], isLoading } = useLeadList();

    return (
        <div className="p-6 bg-bg-main min-h-screen">
            <h1 className="text-xl font-semibold text-text-main mb-4">Tiềm năng</h1>
            <div className="bg-white rounded-card p-4 shadow-sm">
                <DataTable
                    data={data}
                    columns={leadColumns}
                    isLoading={isLoading}
                    emptyText="Chưa có tiềm năng nào"
                    quickFilters={[
                        { id: 'new_',      label: 'Mới',         isActive: false, onToggle: () => {} },
                        { id: 'contacted', label: 'Đã liên hệ',  isActive: false, onToggle: () => {} },
                        { id: 'qualified', label: 'Tiềm năng',   isActive: false, onToggle: () => {} },
                    ]}
                />
            </div>
        </div>
    );
};

export default TiemNangPage;
