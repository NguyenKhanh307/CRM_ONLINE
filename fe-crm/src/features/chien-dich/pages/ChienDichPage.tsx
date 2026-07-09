import { useState, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import { FiTrash2, FiUpload, FiShare2, FiPlus, FiDownload } from 'react-icons/fi';
import type { ColumnDef } from '@tanstack/react-table';
import type { RowAction } from '@/shared/types/table';
import { DataTable } from '@/shared/components/table/DataTable';
import { ConfirmModal } from '@/shared/components/ConfirmModal';
import { HandoverModal } from '@/shared/components/HandoverModal';
import { ExportModal } from '@/shared/components/export/ExportModal';
import { exportRows } from '@/shared/components/export/exportFile';
import { useAlert } from '@/shared/alert/useAlert';
import { useCampaignList } from '../hooks/useCampaignList';
import { useDeleteCampaign } from '../hooks/useDeleteCampaign';
import { useHandoverBulkCampaign } from '../hooks/useHandoverBulkCampaign';
import { useCampaignWorkflow, type CampaignAction } from '../hooks/useCampaignWorkflow';
import { getCampaignColumns } from '../config/campaignColumns';
import { campaignExportColumns } from '../config/campaignExportColumns';
import { CampaignEditModal } from '../components/CampaignEditModal';
import type { CampaignResult } from '../types/campaignTypes';

const ChienDichPage = () => {
    const navigate = useNavigate();
    const { showAlert } = useAlert();
    const { data = [], isLoading } = useCampaignList();
    const { mutate: deleteFn, isPending: isDeleting } = useDeleteCampaign();
    const { mutate: handoverFn, isPending: isHandovering } = useHandoverBulkCampaign();
    const { mutate: workflowFn } = useCampaignWorkflow();

    const [editTarget, setEditTarget] = useState<CampaignResult | null>(null);
    const [deleteTarget, setDeleteTarget] = useState<number | null>(null);
    const [selectedRows, setSelectedRows] = useState<CampaignResult[]>([]);
    const [bulkDeleteOpen, setBulkDeleteOpen] = useState(false);
    const [handoverOpen, setHandoverOpen] = useState(false);
    const [exportOpen, setExportOpen] = useState(false);

    const rowsToExport = selectedRows.length > 0 ? selectedRows : data;

    const runAction = (id: number, action: CampaignAction) =>
        workflowFn({ id, action }, {
            onError: (err: unknown) => {
                const msg = (err as { response?: { data?: { message?: string } } })?.response?.data?.message
                    ?? 'Không thực hiện được hành động';
                showAlert(msg);
            },
        });

    const columns = useMemo<ColumnDef<CampaignResult>[]>(() => getCampaignColumns(), []);

    /** Thao tác của một chiến dịch — hiện trong menu chuột phải. */
    const rowActions = (c: CampaignResult): RowAction[] => [
        { key: 'detail', label: 'Chi tiết', onClick: () => navigate(`/chien-dich/${c.id}`) },
        ...(c.status === 'draft' || c.status === 'scheduled' || c.status === 'paused'
            ? [{ key: 'start', label: 'Bắt đầu chạy', onClick: () => runAction(c.id, 'start') }]
            : []),
        ...(c.status === 'running'
            ? [{ key: 'pause', label: 'Tạm dừng', onClick: () => runAction(c.id, 'pause') }]
            : []),
        ...(c.status === 'running' || c.status === 'paused'
            ? [{ key: 'complete', label: 'Hoàn tất', onClick: () => runAction(c.id, 'complete') }]
            : []),
        ...(c.status !== 'completed' && c.status !== 'cancelled'
            ? [{ key: 'cancel', label: 'Hủy', onClick: () => runAction(c.id, 'cancel') }]
            : []),
        { key: 'edit', label: 'Chỉnh sửa', onClick: () => setEditTarget(c) },
        { key: 'delete', label: 'Xóa', danger: true, onClick: () => setDeleteTarget(c.id) },
    ];

    return (
        <div className="p-6 bg-bg-main min-h-screen">
            <div className="flex items-center justify-between mb-4">
                <h1 className="text-xl font-semibold text-text-main">Chiến dịch</h1>
                <div className="flex items-center gap-2">
                    <button onClick={() => navigate('/chien-dich/nhap-file')}
                        className="flex items-center gap-1.5 px-3 py-1.5 rounded-btn border border-gray-300 text-md text-gray-600 hover:bg-gray-50">
                        <FiUpload size={14} /> Nhập file
                    </button>
                    <button onClick={() => setExportOpen(true)}
                        className="flex items-center gap-1.5 px-3 py-1.5 rounded-btn border border-gray-300 text-md text-gray-600 hover:bg-gray-50">
                        <FiDownload size={14} /> Xuất file{selectedRows.length > 0 ? ` (${selectedRows.length})` : ''}
                    </button>
                    <button onClick={() => navigate('/chien-dich/them-moi')}
                        className="flex items-center gap-1.5 px-3 py-1.5 rounded-btn bg-primary text-white text-md hover:opacity-90">
                        <FiPlus size={14} /> Thêm mới
                    </button>
                    {selectedRows.length > 0 && (
                        <>
                            <button onClick={() => setHandoverOpen(true)}
                                className="flex items-center gap-2 px-3 py-1.5 rounded-btn bg-blue-50 text-primary text-md font-medium hover:bg-blue-100">
                                <FiShare2 size={14} /> Bàn giao ({selectedRows.length})
                            </button>
                            <button onClick={() => setBulkDeleteOpen(true)}
                                className="flex items-center gap-2 px-3 py-1.5 rounded-btn bg-red-50 text-danger text-md font-medium hover:bg-red-100">
                                <FiTrash2 size={14} /> Xóa đã chọn ({selectedRows.length})
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
                    emptyText="Chưa có Chiến dịch nào"
                    onSelectionChange={setSelectedRows}
                    rowActions={rowActions}
                    onRowDoubleClick={(c) => navigate(`/chien-dich/${c.id}`)}
                    quickFilters={[
                        { id: 'draft',     label: 'Nháp',        field: 'status', value: 'draft' },
                        { id: 'running',   label: 'Đang chạy',   field: 'status', value: 'running' },
                        { id: 'paused',    label: 'Tạm dừng',    field: 'status', value: 'paused' },
                        { id: 'completed', label: 'Hoàn tất',    field: 'status', value: 'completed' },
                    ]}
                />
            </div>

            {deleteTarget !== null && (
                <ConfirmModal
                    message="Bạn có chắc muốn xóa Chiến dịch này?"
                    confirmLabel="Xóa" confirmDanger isLoading={isDeleting}
                    onConfirm={() => deleteFn(deleteTarget, { onSuccess: () => setDeleteTarget(null) })}
                    onCancel={() => setDeleteTarget(null)}
                />
            )}

            {bulkDeleteOpen && (
                <ConfirmModal
                    message={`Xóa ${selectedRows.length} Chiến dịch đã chọn?`}
                    confirmLabel="Xóa tất cả" confirmDanger isLoading={isDeleting}
                    onConfirm={() => { Promise.all(selectedRows.map(r => deleteFn(r.id))); setBulkDeleteOpen(false); }}
                    onCancel={() => setBulkDeleteOpen(false)}
                />
            )}

            <ExportModal
                open={exportOpen}
                columns={campaignExportColumns}
                rowCount={rowsToExport.length}
                onClose={() => setExportOpen(false)}
                onExport={(keys, format) => { exportRows(rowsToExport, campaignExportColumns, keys, format, 'chien-dich'); setExportOpen(false); }}
            />

            <CampaignEditModal item={editTarget} onClose={() => setEditTarget(null)} />

            <HandoverModal
                open={handoverOpen} count={selectedRows.length} isLoading={isHandovering}
                onClose={() => setHandoverOpen(false)}
                onConfirm={(toUserId, reason) =>
                    handoverFn({ ids: selectedRows.map(r => r.id), toUserId, reason },
                        { onSuccess: () => { setHandoverOpen(false); setSelectedRows([]); } })}
            />
        </div>
    );
};

export default ChienDichPage;
