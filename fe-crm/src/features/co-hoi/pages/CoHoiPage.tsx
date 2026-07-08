import { useState, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import { FiTrash2, FiUpload, FiShare2, FiPlus, FiDownload, FiSliders } from 'react-icons/fi';
import type { ColumnDef } from '@tanstack/react-table';
import { DataTable } from '@/shared/components/table/DataTable';
import { ConfirmModal } from '@/shared/components/ConfirmModal';
import { HandoverModal } from '@/shared/components/HandoverModal';
import { ExportModal } from '@/shared/components/export/ExportModal';
import { exportRows } from '@/shared/components/export/exportFile';
import { useAlert } from '@/shared/alert/useAlert';
import { useActiveUsers } from '@/features/users/hooks/useActiveUsers';
import { useCustomerList } from '@/features/khach-hang/hooks/useCustomerList';
import { useContactList } from '@/features/lien-he/hooks/useContactList';
import { toIdNameMap } from '@/shared/utils/lookup';
import { useOpportunityList } from '../hooks/useOpportunityList';
import { useOpportunityStages } from '../hooks/useOpportunityStages';
import { useDeleteOpportunity } from '../hooks/useDeleteOpportunity';
import { useHandoverBulkOpportunity } from '../hooks/useHandoverBulkOpportunity';
import { useCreateQuotationFromOpportunity } from '../hooks/useCreateQuotationFromOpportunity';
import { getOpportunityColumns } from '../config/opportunityColumns';
import { opportunityExportColumns } from '../config/opportunityExportColumns';
import { OpportunityEditModal } from '../components/OpportunityEditModal';
import type { OpportunityResult } from '../types/opportunityTypes';

const CoHoiPage = () => {
    const navigate = useNavigate();
    const { showAlert } = useAlert();
    const { data = [], isLoading } = useOpportunityList();
    const { mutate: deleteFn, isPending: isDeleting } = useDeleteOpportunity();
    const { mutate: handoverFn, isPending: isHandovering } = useHandoverBulkOpportunity();
    const { mutateAsync: createQuoteFn } = useCreateQuotationFromOpportunity();
    const { data: users } = useActiveUsers();
    const { data: customers } = useCustomerList();
    const { data: contacts } = useContactList();
    const { data: stages } = useOpportunityStages();

    const [editTarget, setEditTarget] = useState<OpportunityResult | null>(null);
    const [deleteTarget, setDeleteTarget] = useState<number | null>(null);
    const [selectedRows, setSelectedRows] = useState<OpportunityResult[]>([]);
    const [bulkDeleteOpen, setBulkDeleteOpen] = useState(false);
    const [handoverOpen, setHandoverOpen] = useState(false);
    const [exportOpen, setExportOpen] = useState(false);

    const rowsToExport = selectedRows.length > 0 ? selectedRows : data;

    /** Tạo báo giá từ cơ hội (clone) rồi điều hướng sang danh sách báo giá. */
    const createQuote = async (opportunityId: number) => {
        try {
            await createQuoteFn(opportunityId);
            showAlert('Đã tạo báo giá từ cơ hội');
            navigate('/bao-gia');
        } catch (err) {
            const msg = (err as { response?: { data?: { message?: string } } })?.response?.data?.message
                ?? 'Không tạo được báo giá từ cơ hội';
            showAlert(msg);
        }
    };

    const columns = useMemo<ColumnDef<OpportunityResult>[]>(() => [
        ...getOpportunityColumns({
            customers: toIdNameMap(customers, 'id', 'name'),
            contacts: toIdNameMap(contacts, 'id', 'fullName'),
            users: toIdNameMap(users, 'id', 'fullName'),
            stages: toIdNameMap(stages, 'id', 'name'),
        }),
    ], [users, customers, contacts, stages]);

    return (
        <div className="p-6 bg-bg-main min-h-screen">
            <div className="flex items-center justify-between mb-4">
                <h1 className="text-xl font-semibold text-text-main">Cơ hội</h1>
                <div className="flex items-center gap-2">
                    <button
                        onClick={() => navigate('/co-hoi/pipeline')}
                        className="flex items-center gap-1.5 px-3 py-1.5 rounded-btn border border-gray-300 text-md text-gray-600 hover:bg-gray-50"
                    >
                        <FiSliders size={14} />
                        Quản lý giai đoạn
                    </button>
                    <button
                        onClick={() => navigate('/co-hoi/nhap-file')}
                        className="flex items-center gap-1.5 px-3 py-1.5 rounded-btn border border-gray-300 text-md text-gray-600 hover:bg-gray-50"
                    >
                        <FiUpload size={14} />
                        Nhập file
                    </button>
                    <button
                        onClick={() => setExportOpen(true)}
                        className="flex items-center gap-1.5 px-3 py-1.5 rounded-btn border border-gray-300 text-md text-gray-600 hover:bg-gray-50"
                    >
                        <FiDownload size={14} />
                        Xuất file{selectedRows.length > 0 ? ` (${selectedRows.length})` : ''}
                    </button>
                    <button
                        onClick={() => navigate('/co-hoi/them-moi')}
                        className="flex items-center gap-1.5 px-3 py-1.5 rounded-btn bg-primary text-white text-md hover:opacity-90"
                    >
                        <FiPlus size={14} />
                        Thêm mới
                    </button>
                    {selectedRows.length > 0 && (
                        <>
                            <button
                                onClick={() => setHandoverOpen(true)}
                                className="flex items-center gap-2 px-3 py-1.5 rounded-btn bg-blue-50 text-primary text-md font-medium hover:bg-blue-100"
                            >
                                <FiShare2 size={14} />
                                Bàn giao ({selectedRows.length})
                            </button>
                            <button
                                onClick={() => setBulkDeleteOpen(true)}
                                className="flex items-center gap-2 px-3 py-1.5 rounded-btn bg-red-50 text-danger text-md font-medium hover:bg-red-100"
                            >
                                <FiTrash2 size={14} />
                                Xóa đã chọn ({selectedRows.length})
                            </button>
                        </>
                    )}
                </div>
            </div>
            <div className="bg-white rounded-card p-4 shadow-sm">
                <DataTable
                    data={data}
                    columns={columns}
                    isLoading={isLoading}
                    emptyText="Chưa có cơ hội nào"
                    onSelectionChange={setSelectedRows}
                    onRowDoubleClick={(o) => setEditTarget(o)}
                    rowActions={(o) => [
                        { key: 'quote', label: 'Tạo báo giá từ cơ hội', onClick: () => createQuote(o.id) },
                        { key: 'edit', label: 'Chỉnh sửa', onClick: () => setEditTarget(o) },
                        { key: 'delete', label: 'Xóa', danger: true, onClick: () => setDeleteTarget(o.id) },
                    ]}
                    quickFilters={[
                        { id: 'open', label: 'Đang mở',  field: 'status', value: 'open' },
                        { id: 'won',  label: 'Đã thắng', field: 'status', value: 'won' },
                        { id: 'lost', label: 'Đã thua',  field: 'status', value: 'lost' },
                    ]}
                />
            </div>

            {deleteTarget !== null && (
                <ConfirmModal
                    message="Bạn có chắc muốn xóa cơ hội này?"
                    confirmLabel="Xóa"
                    confirmDanger
                    isLoading={isDeleting}
                    onConfirm={() => deleteFn(deleteTarget, { onSuccess: () => setDeleteTarget(null) })}
                    onCancel={() => setDeleteTarget(null)}
                />
            )}

            {bulkDeleteOpen && (
                <ConfirmModal
                    message={`Xóa ${selectedRows.length} cơ hội đã chọn?`}
                    confirmLabel="Xóa tất cả"
                    confirmDanger
                    isLoading={isDeleting}
                    onConfirm={() => {
                        Promise.all(selectedRows.map(r => deleteFn(r.id)));
                        setBulkDeleteOpen(false);
                    }}
                    onCancel={() => setBulkDeleteOpen(false)}
                />
            )}

            <ExportModal
                open={exportOpen}
                columns={opportunityExportColumns}
                rowCount={rowsToExport.length}
                onClose={() => setExportOpen(false)}
                onExport={(keys, format) => {
                    exportRows(rowsToExport, opportunityExportColumns, keys, format, 'co-hoi');
                    setExportOpen(false);
                }}
            />

            <OpportunityEditModal item={editTarget} onClose={() => setEditTarget(null)} />

            <HandoverModal
                open={handoverOpen}
                count={selectedRows.length}
                isLoading={isHandovering}
                onClose={() => setHandoverOpen(false)}
                onConfirm={(toUserId, reason) =>
                    handoverFn({ ids: selectedRows.map(r => r.id), toUserId, reason },
                        { onSuccess: () => { setHandoverOpen(false); setSelectedRows([]); } })
                }
            />
        </div>
    );
};

export default CoHoiPage;
