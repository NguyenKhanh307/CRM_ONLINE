import { useState, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import { FiTrash2, FiUpload, FiPlus, FiDownload } from 'react-icons/fi';
import type { ColumnDef } from '@tanstack/react-table';
import type { RowAction } from '@/shared/types/table';
import { DataTable } from '@/shared/components/table/DataTable';
import { ConfirmModal } from '@/shared/components/ConfirmModal';
import { ExportModal } from '@/shared/components/export/ExportModal';
import { exportRows } from '@/shared/components/export/exportFile';
import { useAlert } from '@/shared/alert/useAlert';
import { useActivityList } from '../hooks/useActivityList';
import { useDeleteActivity } from '../hooks/useDeleteActivity';
import { useActivityWorkflow, type ActivityAction } from '../hooks/useActivityWorkflow';
import { getActivityColumns } from '../config/activityColumns';
import { activityExportColumns } from '../config/activityExportColumns';
import { ActivityEditModal } from '../components/ActivityEditModal';
import type { ActivityResult } from '../types/activityTypes';

const HoatDongPage = () => {
    const navigate = useNavigate();
    const { showAlert } = useAlert();
    const { data = [], isLoading } = useActivityList();
    const { mutate: deleteFn, isPending: isDeleting } = useDeleteActivity();
    const { mutate: workflowFn } = useActivityWorkflow();

    const [editTarget, setEditTarget] = useState<ActivityResult | null>(null);
    const [deleteTarget, setDeleteTarget] = useState<number | null>(null);
    const [selectedRows, setSelectedRows] = useState<ActivityResult[]>([]);
    const [bulkDeleteOpen, setBulkDeleteOpen] = useState(false);
    const [exportOpen, setExportOpen] = useState(false);

    const rowsToExport = selectedRows.length > 0 ? selectedRows : data;

    /** Chạy hành động chuyển trạng thái hoạt động, báo lỗi qua alert nếu không hợp lệ. */
    const runAction = (id: number, action: ActivityAction) =>
        workflowFn({ id, action }, {
            onError: (err: unknown) => {
                const msg = (err as { response?: { data?: { message?: string } } })?.response?.data?.message
                    ?? 'Không thực hiện được hành động';
                showAlert(msg);
            },
        });

    const columns = useMemo<ColumnDef<ActivityResult>[]>(() => getActivityColumns(), []);

    /** Thao tác của một hoạt động — hiện trong menu chuột phải. */
    const rowActions = (a: ActivityResult): RowAction[] => [
        ...(a.status === 'planned'
            ? [{ key: 'start', label: 'Bắt đầu', onClick: () => runAction(a.id, 'start') }]
            : []),
        ...(a.status === 'in_progress'
            ? [{ key: 'complete', label: 'Hoàn thành', onClick: () => runAction(a.id, 'complete') }]
            : []),
        ...(a.status === 'planned' || a.status === 'in_progress'
            ? [{ key: 'cancel', label: 'Hủy', onClick: () => runAction(a.id, 'cancel') }]
            : []),
        { key: 'edit', label: 'Chỉnh sửa', onClick: () => setEditTarget(a) },
        { key: 'delete', label: 'Xóa', danger: true, onClick: () => setDeleteTarget(a.id) },
    ];

    return (
        <div className="p-6 bg-bg-main min-h-screen">
            <div className="flex items-center justify-between mb-4">
                <h1 className="text-xl font-semibold text-text-main">Hoạt động</h1>
                <div className="flex items-center gap-2">
                    <button
                        onClick={() => navigate('/hoat-dong/nhap-file')}
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
                        onClick={() => navigate('/hoat-dong/them-moi')}
                        className="flex items-center gap-1.5 px-3 py-1.5 rounded-btn bg-primary text-white text-md hover:opacity-90"
                    >
                        <FiPlus size={14} />
                        Thêm mới
                    </button>
                    {selectedRows.length > 0 && (
                        <button
                            onClick={() => setBulkDeleteOpen(true)}
                            className="flex items-center gap-2 px-3 py-1.5 rounded-btn bg-red-50 text-danger text-md font-medium hover:bg-red-100"
                        >
                            <FiTrash2 size={14} />
                            Xóa đã chọn ({selectedRows.length})
                        </button>
                    )}
                </div>
            </div>
            <div className="bg-white rounded-card p-4 shadow-sm">
                <DataTable
                    data={data}
                    columns={columns}
                    isLoading={isLoading}
                    emptyText="Chưa có hoạt động nào"
                    onSelectionChange={setSelectedRows}
                    rowActions={rowActions}
                    onRowDoubleClick={(a) => setEditTarget(a)}
                    quickFilters={[
                        { id: 'planned',     label: 'Đã lên kế hoạch', field: 'status', value: 'planned' },
                        { id: 'in_progress', label: 'Đang thực hiện',  field: 'status', value: 'in_progress' },
                        { id: 'done',        label: 'Hoàn thành',      field: 'status', value: 'done' },
                    ]}
                />
            </div>

            {deleteTarget !== null && (
                <ConfirmModal
                    message="Bạn có chắc muốn xóa hoạt động này?"
                    confirmLabel="Xóa"
                    confirmDanger
                    isLoading={isDeleting}
                    onConfirm={() => deleteFn(deleteTarget, { onSuccess: () => setDeleteTarget(null) })}
                    onCancel={() => setDeleteTarget(null)}
                />
            )}

            {bulkDeleteOpen && (
                <ConfirmModal
                    message={`Xóa ${selectedRows.length} hoạt động đã chọn?`}
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
                columns={activityExportColumns}
                rowCount={rowsToExport.length}
                onClose={() => setExportOpen(false)}
                onExport={(keys, format) => {
                    exportRows(rowsToExport, activityExportColumns, keys, format, 'hoat-dong');
                    setExportOpen(false);
                }}
            />

            <ActivityEditModal item={editTarget} onClose={() => setEditTarget(null)} />
        </div>
    );
};

export default HoatDongPage;
