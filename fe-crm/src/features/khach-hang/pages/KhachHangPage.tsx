import { useState, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import { FiEdit2, FiTrash2, FiUpload, FiShare2, FiPlus, FiDownload, FiToggleRight, FiToggleLeft } from 'react-icons/fi';
import type { ColumnDef } from '@tanstack/react-table';
import { DataTable } from '@/shared/components/table/DataTable';
import { ConfirmModal } from '@/shared/components/ConfirmModal';
import { HandoverModal } from '@/shared/components/HandoverModal';
import { ExportModal } from '@/shared/components/export/ExportModal';
import { exportRows } from '@/shared/components/export/exportFile';
import { useAlert } from '@/shared/alert/useAlert';
import { useActiveUsers } from '@/features/users/hooks/useActiveUsers';
import { useOrgUnits } from '@/features/users/hooks/useOrgUnits';
import { toIdNameMap } from '@/shared/utils/lookup';
import { useCustomerList } from '../hooks/useCustomerList';
import { useDeleteCustomer } from '../hooks/useDeleteCustomer';
import { useHandoverBulkCustomer } from '../hooks/useHandoverBulkCustomer';
import { useCustomerWorkflow, type CustomerAction } from '../hooks/useCustomerWorkflow';
import { getCustomerColumns } from '../config/customerColumns';
import { customerExportColumns } from '../config/customerExportColumns';
import { CustomerEditModal } from '../components/CustomerEditModal';
import type { CustomerResult } from '../types/customerTypes';

const KhachHangPage = () => {
    const navigate = useNavigate();
    const { showAlert } = useAlert();
    const { data = [], isLoading } = useCustomerList();
    const { mutate: deleteFn, isPending: isDeleting } = useDeleteCustomer();
    const { mutate: handoverFn, isPending: isHandovering } = useHandoverBulkCustomer();
    const { mutate: workflowFn } = useCustomerWorkflow();
    const { data: users } = useActiveUsers();
    const { data: orgUnits } = useOrgUnits();

    const [editTarget, setEditTarget] = useState<CustomerResult | null>(null);
    const [deleteTarget, setDeleteTarget] = useState<number | null>(null);
    const [selectedRows, setSelectedRows] = useState<CustomerResult[]>([]);
    const [bulkDeleteOpen, setBulkDeleteOpen] = useState(false);
    const [handoverOpen, setHandoverOpen] = useState(false);
    const [exportOpen, setExportOpen] = useState(false);

    const rowsToExport = selectedRows.length > 0 ? selectedRows : data;

    /** Chạy hành động kích hoạt/ngừng khách hàng, báo lỗi qua alert nếu không hợp lệ. */
    const runAction = (id: number, action: CustomerAction) =>
        workflowFn({ id, action }, {
            onError: (err: unknown) => {
                const msg = (err as { response?: { data?: { message?: string } } })?.response?.data?.message
                    ?? 'Không thực hiện được hành động';
                showAlert(msg);
            },
        });

    const columns = useMemo<ColumnDef<CustomerResult>[]>(() => [
        ...getCustomerColumns({
            users: toIdNameMap(users, 'id', 'fullName'),
            orgUnits: toIdNameMap(orgUnits, 'id', 'name'),
        }),
        {
            id: 'actions',
            header: '',
            enableSorting: false,
            size: 80,
            cell: ({ row }) => {
                const c = row.original;
                return (
                    <div className="flex gap-1 justify-end" onClick={(e) => e.stopPropagation()}>
                        {c.status !== 'active' && (
                            <button className="p-1.5 rounded hover:bg-green-50 text-gray-400 hover:text-success"
                                title="Kích hoạt" onClick={() => runAction(c.id, 'activate')}>
                                <FiToggleRight size={14} />
                            </button>
                        )}
                        {c.status === 'active' && (
                            <button className="p-1.5 rounded hover:bg-yellow-50 text-gray-400 hover:text-warning"
                                title="Ngừng hoạt động" onClick={() => runAction(c.id, 'deactivate')}>
                                <FiToggleLeft size={14} />
                            </button>
                        )}
                        <button className="p-1.5 rounded hover:bg-gray-100 text-gray-400 hover:text-primary"
                            title="Chỉnh sửa" onClick={() => setEditTarget(c)}>
                            <FiEdit2 size={14} />
                        </button>
                        <button className="p-1.5 rounded hover:bg-red-50 text-gray-400 hover:text-danger"
                            title="Xóa" onClick={() => setDeleteTarget(c.id)}>
                            <FiTrash2 size={14} />
                        </button>
                    </div>
                );
            },
        },
    ], [users, orgUnits]);

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
                    <button
                        onClick={() => setExportOpen(true)}
                        className="flex items-center gap-1.5 px-3 py-1.5 rounded-btn border border-gray-300 text-md text-gray-600 hover:bg-gray-50"
                    >
                        <FiDownload size={14} />
                        Xuất file{selectedRows.length > 0 ? ` (${selectedRows.length})` : ''}
                    </button>
                    <button
                        onClick={() => navigate('/khach-hang/them-moi')}
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
                    emptyText="Chưa có khách hàng nào"
                    onSelectionChange={setSelectedRows}
                    quickFilters={[
                        { id: 'active',   label: 'Hoạt động',       field: 'status', value: 'active' },
                        { id: 'inactive', label: 'Không hoạt động',  field: 'status', value: 'inactive' },
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

            <ExportModal
                open={exportOpen}
                columns={customerExportColumns}
                rowCount={rowsToExport.length}
                onClose={() => setExportOpen(false)}
                onExport={(keys, format) => {
                    exportRows(rowsToExport, customerExportColumns, keys, format, 'khach-hang');
                    setExportOpen(false);
                }}
            />

            <CustomerEditModal item={editTarget} onClose={() => setEditTarget(null)} />

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

export default KhachHangPage;
