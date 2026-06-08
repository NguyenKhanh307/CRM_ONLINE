import { useState } from 'react';
import { FiRefreshCw, FiTrash2 } from 'react-icons/fi';
import { DataTable } from '@/shared/components/table/DataTable';
import { useAlert } from '@/shared/alert/useAlert';
import ModuleSelectDropdown from '@/features/thung-rac/components/ModuleSelectDropdown';
import { TRASH_MODULES, DEFAULT_MODULE_ID } from '@/features/thung-rac/config/trashModulesConfig';

/** Trang thùng rác — hiển thị bản ghi đã xóa theo từng phân hệ. */
const ThungRacPage = () => {
    const [selectedModuleId, setSelectedModuleId] = useState(DEFAULT_MODULE_ID);
    const [refreshKey, setRefreshKey] = useState(0);
    const { showAlert } = useAlert();

    const currentModule = TRASH_MODULES.find(m => m.id === selectedModuleId) ?? TRASH_MODULES[0];

    const handleRefresh = () => setRefreshKey(k => k + 1);

    const handleEmptyTrash = () => {
        showAlert('Đã làm trống thùng rác!');
    };

    return (
        <div className="p-6 bg-bg-main min-h-screen">
            {/* Toolbar */}
            <div className="flex items-center justify-between mb-4">
                <div className="flex items-center gap-2">
                    <ModuleSelectDropdown
                        modules={TRASH_MODULES}
                        selectedId={selectedModuleId}
                        onChange={setSelectedModuleId}
                    />
                    <button
                        className="flex items-center justify-center w-8 h-8 bg-white border border-gray-300 rounded-btn text-gray-500 hover:border-primary hover:text-primary transition-colors"
                        onClick={handleRefresh}
                        title="Làm mới"
                    >
                        <FiRefreshCw size={14} />
                    </button>
                </div>

                <button
                    className="flex items-center gap-2 px-4 py-1.5 bg-danger text-white text-md rounded-btn hover:bg-red-600 transition-colors"
                    onClick={handleEmptyTrash}
                >
                    <FiTrash2 size={14} />
                    Làm trống thùng rác
                </button>
            </div>

            {/* Table */}
            <div className="bg-white rounded-card shadow-sm" key={`${selectedModuleId}-${refreshKey}`}>
                <DataTable
                    data={currentModule.mockData}
                    columns={currentModule.columns}
                    emptyText="Không có bản ghi nào"
                />
            </div>
        </div>
    );
};

export default ThungRacPage;
