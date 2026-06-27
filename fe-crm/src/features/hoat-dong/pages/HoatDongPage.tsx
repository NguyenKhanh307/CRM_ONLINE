import { useState, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import { FiEdit2, FiTrash2, FiUpload, FiPlus, FiDownload, FiPlay, FiCheckCircle, FiXCircle } from 'react-icons/fi';
import type { ColumnDef } from '@tanstack/react-table';
import { DataTable } from '@/shared/components/table/DataTable';
import { ConfirmModal } from '@/shared/components/ConfirmModal';
import { ExportModal } from '@/shared/components/export/ExportModal';
import { exportRows } from '@/shared/components/export/exportFile';
import { useAlert } from '@/shared/alert/useAlert';
import { useActiveUsers } from '@/features/users/hooks/useActiveUsers';
import { toIdNameMap } from '@/shared/utils/lookup';
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
    const { data: users } = useActiveUsers();

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

    const columns = useMemo<ColumnDef<ActivityResult>[]>(() => [
        ...getActivityColumns({
            users: toIdNameMap(users, 'id', 'fullName'),
        }),
        {
            id: 'actions',
            header: '',
            enableSorting: false,
            size: 80,
            cell: ({ row }) => {
                const a = row.original;
                return (
                    <div className="flex gap-1 justify-end" onClick={(e) => e.stopPropagation()}>
                        {a.status === 'planned' && (
                            <button className="p-1.5 rounded hover:bg-yellow-50 text-gray-400 hover:text-warning"
                                title="Bắt đầu" onClick={() => runAction(a.id, 'start')}>
                                <FiPlay size={14} />
                            </button>
                        )}
                        {a.status === 'in_progress' && (
                            <button className="p-1.5 rounded hover:bg-green-50 text-gray-400 hover:text-success"
                                title="Hoàn thành" onClick={() => runAction(a.id, 'complete')}>
                                <FiCheckCircle size={14} />
                            </button>
                        )}
                        {(a.status === 'planned' || a.status === 'in_progress') && (
                            <button className="p-1.5 rounded hover:bg-red-50 text-gray-400 hover:text-danger"
                                title="Hủy" onClick={() => runAction(a.id, 'cancel')}>
                                <FiXCircle size={14} />
                            </button>
                        )}
                        <button className="p-1.5 rounded hover:bg-gray-100 text-gray-400 hover:text-primary"
                            title="Chỉnh sửa" onClick={() => setEditTarget(a)}>
                            <FiEdit2 size={14} />
                        </button>
                        <button className="p-1.5 rounded hover:bg-red-50 text-gray-400 hover:text-danger"
                            title="Xóa" onClick={() => setDeleteTarget(a.id)}>
                            <FiTrash2 size={14} />
                        </button>
                    </div>
                );
            },
        },
    ], [users]);

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
