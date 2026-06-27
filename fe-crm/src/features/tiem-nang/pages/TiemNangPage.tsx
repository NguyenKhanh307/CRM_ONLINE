import { useState, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import { FiEdit2, FiTrash2, FiUpload, FiShare2, FiPlus, FiDownload, FiCheckCircle, FiXCircle } from 'react-icons/fi';
import type { ColumnDef } from '@tanstack/react-table';
import { DataTable } from '@/shared/components/table/DataTable';
import { ConfirmModal } from '@/shared/components/ConfirmModal';
import { ReasonModal } from '@/shared/components/ReasonModal';
import { HandoverModal } from '@/shared/components/HandoverModal';
import { ExportModal } from '@/shared/components/export/ExportModal';
import { exportRows } from '@/shared/components/export/exportFile';
import { useAlert } from '@/shared/alert/useAlert';
import { useActiveUsers } from '@/features/users/hooks/useActiveUsers';
import { useCustomerList } from '@/features/khach-hang/hooks/useCustomerList';
import { useContactList } from '@/features/lien-he/hooks/useContactList';
import { toIdNameMap } from '@/shared/utils/lookup';
import { useLeadList } from '../hooks/useLeadList';
import { useDeleteLead } from '../hooks/useDeleteLead';
import { useHandoverBulkLead } from '../hooks/useHandoverBulkLead';
import { useLeadWorkflow, type LeadAction } from '../hooks/useLeadWorkflow';
import { getLeadColumns } from '../config/leadColumns';
import { leadExportColumns } from '../config/leadExportColumns';
import { LeadEditModal } from '../components/LeadEditModal';
import type { LeadResult } from '../types/leadTypes';

const TiemNangPage = () => {
    const navigate = useNavigate();
    const { showAlert } = useAlert();
    const { data = [], isLoading } = useLeadList();
    const { mutate: deleteFn, isPending: isDeleting } = useDeleteLead();
    const { mutate: handoverFn, isPending: isHandovering } = useHandoverBulkLead();
    const { mutate: workflowFn } = useLeadWorkflow();
    const { data: users } = useActiveUsers();
    const { data: customers } = useCustomerList();
    const { data: contacts } = useContactList();

    const [editTarget, setEditTarget] = useState<LeadResult | null>(null);
    const [deleteTarget, setDeleteTarget] = useState<number | null>(null);
    const [loseTarget, setLoseTarget] = useState<number | null>(null);
    const [selectedRows, setSelectedRows] = useState<LeadResult[]>([]);
    const [bulkDeleteOpen, setBulkDeleteOpen] = useState(false);
    const [handoverOpen, setHandoverOpen] = useState(false);
    const [exportOpen, setExportOpen] = useState(false);

    const rowsToExport = selectedRows.length > 0 ? selectedRows : data;

    /** Chạy hành động chuyển trạng thái tiềm năng, báo lỗi qua alert nếu bước chuyển không hợp lệ. */
    const runAction = (id: number, action: LeadAction, reason?: string) =>
        workflowFn({ id, action, reason }, {
            onError: (err: unknown) => {
                const msg = (err as { response?: { data?: { message?: string } } })?.response?.data?.message
                    ?? 'Không thực hiện được hành động';
                showAlert(msg);
            },
        });

    const columns = useMemo<ColumnDef<LeadResult>[]>(() => [
        ...getLeadColumns({
            users: toIdNameMap(users, 'id', 'fullName'),
            customers: toIdNameMap(customers, 'id', 'name'),
            contacts: toIdNameMap(contacts, 'id', 'fullName'),
        }),
        {
            id: 'actions',
            header: '',
            enableSorting: false,
            size: 80,
            cell: ({ row }) => {
                const l = row.original;
                const isOpen = l.status !== 'converted' && l.status !== 'lost';
                return (
                    <div className="flex gap-1 justify-end" onClick={(e) => e.stopPropagation()}>
                        {l.status === 'qualified' && (
                            <button className="p-1.5 rounded hover:bg-green-50 text-gray-400 hover:text-success"
                                title="Chuyển đổi" onClick={() => runAction(l.id, 'convert')}>
                                <FiCheckCircle size={14} />
                            </button>
                        )}
                        {isOpen && (
                            <button className="p-1.5 rounded hover:bg-red-50 text-gray-400 hover:text-danger"
                                title="Đánh mất" onClick={() => setLoseTarget(l.id)}>
                                <FiXCircle size={14} />
                            </button>
                        )}
                        {l.status !== 'converted' && (
                            <button className="p-1.5 rounded hover:bg-gray-100 text-gray-400 hover:text-primary"
                                title="Chỉnh sửa" onClick={() => setEditTarget(l)}>
                                <FiEdit2 size={14} />
                            </button>
                        )}
                        <button className="p-1.5 rounded hover:bg-red-50 text-gray-400 hover:text-danger"
                            title="Xóa" onClick={() => setDeleteTarget(l.id)}>
                            <FiTrash2 size={14} />
                        </button>
                    </div>
                );
            },
        },
    ], [users, customers, contacts]);

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
                    <button
                        onClick={() => setExportOpen(true)}
                        className="flex items-center gap-1.5 px-3 py-1.5 rounded-btn border border-gray-300 text-md text-gray-600 hover:bg-gray-50"
                    >
                        <FiDownload size={14} />
                        Xuất file{selectedRows.length > 0 ? ` (${selectedRows.length})` : ''}
                    </button>
                    <button
                        onClick={() => navigate('/tiem-nang/them-moi')}
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
                    emptyText="Chưa có tiềm năng nào"
                    onSelectionChange={setSelectedRows}
                    quickFilters={[
                        { id: 'new',        label: 'Mới',         field: 'status', value: 'new' },
                        { id: 'contacting', label: 'Đang liên hệ', field: 'status', value: 'contacting' },
                        { id: 'qualified',  label: 'Đủ điều kiện', field: 'status', value: 'qualified' },
                        { id: 'converted',  label: 'Đã chuyển đổi',field: 'status', value: 'converted' },
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

            <ExportModal
                open={exportOpen}
                columns={leadExportColumns}
                rowCount={rowsToExport.length}
                onClose={() => setExportOpen(false)}
                onExport={(keys, format) => {
                    exportRows(rowsToExport, leadExportColumns, keys, format, 'tiem-nang');
                    setExportOpen(false);
                }}
            />

            {loseTarget !== null && (
                <ReasonModal
                    title="Đánh mất tiềm năng"
                    label="Lý do thất bại"
                    placeholder="Nhập lý do tiềm năng thất bại..."
                    confirmLabel="Đánh mất"
                    confirmDanger
                    onCancel={() => setLoseTarget(null)}
                    onConfirm={(reason) => {
                        runAction(loseTarget, 'lose', reason);
                        setLoseTarget(null);
                    }}
                />
            )}

            <LeadEditModal item={editTarget} onClose={() => setEditTarget(null)} />

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

export default TiemNangPage;
