import { useState, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import { FiEdit2, FiTrash2, FiUpload } from 'react-icons/fi';
import type { ColumnDef } from '@tanstack/react-table';
import { DataTable } from '@/shared/components/table/DataTable';
import { ConfirmModal } from '@/shared/components/ConfirmModal';
import { useCustomerList } from '../hooks/useCustomerList';
import { useDeleteCustomer } from '../hooks/useDeleteCustomer';
import { customerColumns } from '../config/customerColumns';
import { CustomerEditModal } from '../components/CustomerEditModal';
import type { CustomerResult } from '../types/customerTypes';

const KhachHangPage = () => {
    const navigate = useNavigate();
    const { data = [], isLoading } = useCustomerList();
    const { mutate: deleteFn, isPending: isDeleting } = useDeleteCustomer();

    const [editTarget, setEditTarget] = useState<CustomerResult | null>(null);
    const [deleteTarget, setDeleteTarget] = useState<number | null>(null);
    const [selectedRows, setSelectedRows] = useState<CustomerResult[]>([]);
    const [bulkDeleteOpen, setBulkDeleteOpen] = useState(false);

    const columns = useMemo<ColumnDef<CustomerResult>[]>(() => [
        ...customerColumns,
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
                <h1 className="text-xl font-semibold text-text-main">Khách hàng</h1>
                <div className="flex items-center gap-2">
                    <button
                        onClick={() => navigate('/khach-hang/nhap-file')}
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
                    emptyText="Chưa có khách hàng nào"
                    onSelectionChange={setSelectedRows}
                    quickFilters={[
                        { id: 'active',   label: 'Hoạt động',       isActive: false, onToggle: () => {} },
                        { id: 'inactive', label: 'Không hoạt động',  isActive: false, onToggle: () => {} },
                    ]}
                />
            </div>

            {deleteTarget !== null && (
                <ConfirmModal
                    message="Bạn có chắc muốn xóa khách hàng này?"
                    confirmLabel="Xóa"
                    confirmDanger
                    isLoading={isDeleting}
                    onConfirm={() => deleteFn(deleteTarget, { onSuccess: () => setDeleteTarget(null) })}
                    onCancel={() => setDeleteTarget(null)}
                />
            )}

            {bulkDeleteOpen && (
                <ConfirmModal
                    message={`Xóa ${selectedRows.length} khách hàng đã chọn?`}
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

            <CustomerEditModal item={editTarget} onClose={() => setEditTarget(null)} />
        </div>
    );
};

export default KhachHangPage;
