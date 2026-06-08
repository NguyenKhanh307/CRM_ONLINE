import { DataTable } from '@/shared/components/table/DataTable';
import { useOpportunityList } from '../hooks/useOpportunityList';
import { opportunityColumns } from '../config/opportunityColumns';

const CoHoiPage = () => {
    const { data = [], isLoading } = useOpportunityList();

    return (
        <div className="p-6 bg-bg-main min-h-screen">
            <h1 className="text-xl font-semibold text-text-main mb-4">Cơ hội</h1>
            <div className="bg-white rounded-card p-4 shadow-sm">
                <DataTable
                    data={data}
                    columns={opportunityColumns}
                    isLoading={isLoading}
                    emptyText="Chưa có cơ hội nào"
                    quickFilters={[
                        { id: 'open', label: 'Đang mở',  isActive: false, onToggle: () => {} },
                        { id: 'won',  label: 'Đã thắng', isActive: false, onToggle: () => {} },
                        { id: 'lost', label: 'Đã thua',  isActive: false, onToggle: () => {} },
                    ]}
                />
            </div>
        </div>
    );
};

export default CoHoiPage;
