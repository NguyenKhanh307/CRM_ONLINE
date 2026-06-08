import { DataTable } from '@/shared/components/table/DataTable';
import { useContactList } from '../hooks/useContactList';
import { contactColumns } from '../config/contactColumns';

const LienHePage = () => {
    const { data = [], isLoading } = useContactList();

    return (
        <div className="p-6 bg-bg-main min-h-screen">
            <h1 className="text-xl font-semibold text-text-main mb-4">Liên hệ</h1>
            <div className="bg-white rounded-card p-4 shadow-sm">
                <DataTable
                    data={data}
                    columns={contactColumns}
                    isLoading={isLoading}
                    emptyText="Chưa có liên hệ nào"
                    quickFilters={[
                        { id: 'primary', label: 'Liên hệ chính', isActive: false, onToggle: () => {} },
                    ]}
                />
            </div>
        </div>
    );
};

export default LienHePage;
