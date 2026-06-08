import { DataTable } from '@/shared/components/table/DataTable';
import { useActivityList } from '../hooks/useActivityList';
import { activityColumns } from '../config/activityColumns';

const HoatDongPage = () => {
    const { data = [], isLoading } = useActivityList();

    return (
        <div className="p-6 bg-bg-main min-h-screen">
            <h1 className="text-xl font-semibold text-text-main mb-4">Hoạt động</h1>
            <div className="bg-white rounded-card p-4 shadow-sm">
                <DataTable
                    data={data}
                    columns={activityColumns}
                    isLoading={isLoading}
                    emptyText="Chưa có hoạt động nào"
                    quickFilters={[
                        { id: 'pending',   label: 'Chờ xử lý',  isActive: false, onToggle: () => {} },
                        { id: 'completed', label: 'Hoàn thành', isActive: false, onToggle: () => {} },
                    ]}
                />
            </div>
        </div>
    );
};

export default HoatDongPage;
