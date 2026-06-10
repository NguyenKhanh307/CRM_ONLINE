import { useState, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import { FiEdit2, FiTrash2, FiUpload } from 'react-icons/fi';
import type { ColumnDef } from '@tanstack/react-table';
import { DataTable } from '@/shared/components/table/DataTable';
import { ConfirmModal } from '@/shared/components/ConfirmModal';
import { useLeadList } from '../hooks/useLeadList';
import { useDeleteLead } from '../hooks/useDeleteLead';
import { leadColumns } from '../config/leadColumns';
import { LeadEditModal } from '../components/LeadEditModal';
import type { LeadResult } from '../types/leadTypes';

const TiemNangPage = () => {
    const navigate = useNavigate();
    const { data = [], isLoading } = useLeadList();
    const { mutate: deleteFn, isPending: isDeleting } = useDeleteLead();

    const [editTarget, setEditTarget] = useState<LeadResult | null>(null);
    const [deleteTarget, setDeleteTarget] = useState<number | null>(null);
    const [selectedRows, setSelectedRows] = useState<LeadResult[]>([]);
    const [bulkDeleteOpen, setBulkDeleteOpen] = useState(false);

    const columns = useMemo<ColumnDef<LeadResult>[]>(() => [
        ...leadColumns,
        {
            id: 'actions',
            header: '',
            enableSorting: false,
            size: 80,
            cell: ({ row }) => (
                <div className="flex gap-1 justify-end" onClick={(e) => e.stopPropagation()}>
                    <button
                        className="p-1.5 rounded hover:bg-gray-100 text-gray-400 hover:text-primary"
                        title="Chỉnh sửa"
                        onClick={() => setEditTarget(row.original)}
                    >
                        <FiEdit2 size={14} />
                    </button>
                    <button
                        className="p-1.5 rounded hover:bg-red-50 text-gray-400 hover:text-danger"
                        title="Xóa"
                        onClick={() => setDeleteTarget(row.original.id)}
                    >
                        <FiTrash2 size={14} />
                    </button>
                </div>
            ),
        },
    ], []);

    return (
        <div className="p-6 bg-bg-main min-h-screen">
            <div className="flex items-center justify-between mb-4">
                <h1 className="text-xl font-semibold text-text-main">Tiềm năng</h1>
                <div className="flex items-center gap-2">
                    <button
                        onClick={() => navigate('/tiem-nang/nhap-file')}
                        className="flex items-center gap-1.5 px-3 py-1.5 rounded-btn border border-gray-300 text-md text-gray-600 hover:bg-gray-50"
                    >
                        <FiUpload size={14} />
                        Nhập file
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
                    emptyText="Chưa có tiềm năng nào"
                    onSelectionChange={setSelectedRows}
                    quickFilters={[
                        { id: 'new_',      label: 'Mới',        isActive: false, onToggle: () => {} },
                        { id: 'contacted', label: 'Đã liên hệ', isActive: false, onToggle: () => {} },
                        { id: 'qualified', label: 'Tiềm năng',  isActive: false, onToggle: () => {} },
                    ]}
                />
            </div>

            {deleteTarget !== null && (
                <ConfirmModal
                    message="Bạn có chắc muốn xóa tiềm năng này?"
                    confirmLabel="Xóa"
                    confirmDanger
                    isLoading={isDeleting}
                    onConfirm={() => deleteFn(deleteTarget, { onSuccess: () => setDeleteTarget(null) })}
                    onCancel={() => setDeleteTarget(null)}
                />
            )}

            {bulkDeleteOpen && (
                <ConfirmModal
                    message={`Xóa ${selectedRows.length} tiềm năng đã chọn?`}
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

            <LeadEditModal item={editTarget} onClose={() => setEditTarget(null)} />
        </div>
    );
};

export default TiemNangPage;
