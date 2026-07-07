import { useState, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import { FiEdit2, FiTrash2, FiUpload, FiShare2, FiDownload, FiSend, FiCheck, FiXCircle, FiMail, FiStar, FiFileText } from 'react-icons/fi';
import type { ColumnDef } from '@tanstack/react-table';
import { DataTable } from '@/shared/components/table/DataTable';
import { ConfirmModal } from '@/shared/components/ConfirmModal';
import { HandoverModal } from '@/shared/components/HandoverModal';
import { ExportModal } from '@/shared/components/export/ExportModal';
import { exportRows } from '@/shared/components/export/exportFile';
import { useAlert } from '@/shared/alert/useAlert';
import { usePermission } from '@/core/permissions/usePermission';
import { useActiveUsers } from '@/features/users/hooks/useActiveUsers';
import { useCustomerList } from '@/features/khach-hang/hooks/useCustomerList';
import { useContactList } from '@/features/lien-he/hooks/useContactList';
import { useOpportunityList } from '@/features/co-hoi/hooks/useOpportunityList';
import { toIdNameMap } from '@/shared/utils/lookup';
import { useQuotationList } from '../hooks/useQuotationList';
import { useDeleteQuotation } from '../hooks/useDeleteQuotation';
import { useHandoverBulkQuotation } from '../hooks/useHandoverBulkQuotation';
import { useQuotationWorkflow, type QuotationAction } from '../hooks/useQuotationWorkflow';
import { getQuotationColumns } from '../config/quotationColumns';
import { quotationExportColumns } from '../config/quotationExportColumns';
import { QuotationEditModal } from '../components/QuotationEditModal';
import { ReasonModal } from '@/shared/components/ReasonModal';
import type { QuotationResult } from '../types/quotationTypes';

const BaoGiaPage = () => {
    const navigate = useNavigate();
    const { showAlert } = useAlert();
    const { hasRole } = usePermission();
    const isManager = hasRole('ADMIN') || hasRole('SALES_MANAGER');
    const { data = [], isLoading } = useQuotationList();
    const { mutate: deleteFn, isPending: isDeleting } = useDeleteQuotation();
    const { mutate: handoverFn, isPending: isHandovering } = useHandoverBulkQuotation();
    const { mutate: workflowFn } = useQuotationWorkflow();
    const { data: users } = useActiveUsers();
    const { data: customers } = useCustomerList();
    const { data: contacts } = useContactList();
    const { data: opportunities } = useOpportunityList();

    const [editTarget, setEditTarget] = useState<QuotationResult | null>(null);
    const [deleteTarget, setDeleteTarget] = useState<number | null>(null);
    const [rejectTarget, setRejectTarget] = useState<number | null>(null);
    const [selectedRows, setSelectedRows] = useState<QuotationResult[]>([]);
    const [bulkDeleteOpen, setBulkDeleteOpen] = useState(false);
    const [handoverOpen, setHandoverOpen] = useState(false);
    const [exportOpen, setExportOpen] = useState(false);

    const rowsToExport = selectedRows.length > 0 ? selectedRows : data;

    /** Chạy một hành động chuyển trạng thái báo giá, báo lỗi qua alert nếu bước chuyển không hợp lệ. */
    const runAction = (id: number, action: QuotationAction, comment?: string) =>
        workflowFn({ id, action, comment }, {
            onSuccess: (res: unknown) => {
                if (action === 'send') {
                    const email = (res as { data?: { data?: { sentToEmail?: string | null } } })?.data?.data?.sentToEmail;
                    showAlert(email ? `Đã gửi báo giá tới email: ${email}` : 'Đã gửi báo giá cho khách hàng');
                } else if (action === 'convertToOrder') {
                    showAlert('Đã chuyển báo giá thành đơn hàng');
                    navigate('/don-hang');
                }
            },
            onError: (err: unknown) => {
                const msg = (err as { response?: { data?: { message?: string } } })?.response?.data?.message
                    ?? 'Không thực hiện được hành động';
                showAlert(msg);
            },
        });

    const columns = useMemo<ColumnDef<QuotationResult>[]>(() => [
        ...getQuotationColumns({
            customers: toIdNameMap(customers, 'id', 'name'),
            contacts: toIdNameMap(contacts, 'id', 'fullName'),
            opportunities: toIdNameMap(opportunities, 'id', 'name'),
            users: toIdNameMap(users, 'id', 'fullName'),
        }),
        {
            id: 'actions',
            header: '',
            enableSorting: false,
            size: 80,
            cell: ({ row }) => {
                const q = row.original;
                return (
                    <div className="flex gap-1 justify-end" onClick={(e) => e.stopPropagation()}>
                        {q.status === 'draft' && (
                            <button className="p-1.5 rounded hover:bg-blue-50 text-gray-400 hover:text-primary"
                                title="Gửi duyệt" onClick={() => runAction(q.id, 'submit')}>
                                <FiSend size={14} />
                            </button>
                        )}
                        {q.status === 'pending' && isManager && (
                            <>
                                <button className="p-1.5 rounded hover:bg-green-50 text-gray-400 hover:text-success"
                                    title="Duyệt" onClick={() => runAction(q.id, 'approve')}>
                                    <FiCheck size={14} />
                                </button>
                                <button className="p-1.5 rounded hover:bg-red-50 text-gray-400 hover:text-danger"
                                    title="Từ chối" onClick={() => setRejectTarget(q.id)}>
                                    <FiXCircle size={14} />
                                </button>
                            </>
                        )}
                        {q.status === 'approved' && (
                            <button className="p-1.5 rounded hover:bg-blue-50 text-gray-400 hover:text-primary"
                                title="Gửi email cho khách" onClick={() => runAction(q.id, 'send')}>
                                <FiMail size={14} />
                            </button>
                        )}
                        {q.status === 'sent' && (
                            <button className="p-1.5 rounded hover:bg-green-50 text-gray-400 hover:text-success"
                                title="Khách chấp nhận" onClick={() => runAction(q.id, 'accept')}>
                                <FiCheck size={14} />
                            </button>
                        )}
                        {q.opportunityId && !q.isPrimary && (
                            <button className="p-1.5 rounded hover:bg-amber-50 text-gray-400 hover:text-warning"
                                title="Đặt làm báo giá đồng bộ" onClick={() => runAction(q.id, 'setPrimary')}>
                                <FiStar size={14} />
                            </button>
                        )}
                        {q.status === 'accepted' && !q.isLocked && (
                            <button className="p-1.5 rounded hover:bg-green-50 text-gray-400 hover:text-success"
                                title="Chuyển thành đơn hàng" onClick={() => runAction(q.id, 'convertToOrder')}>
                                <FiFileText size={14} />
                            </button>
                        )}
                        {!q.isLocked && (
                            <button className="p-1.5 rounded hover:bg-gray-100 text-gray-400 hover:text-primary"
                                title="Chỉnh sửa" onClick={() => setEditTarget(q)}>
                                <FiEdit2 size={14} />
                            </button>
                        )}
                        <button className="p-1.5 rounded hover:bg-red-50 text-gray-400 hover:text-danger"
                            title="Xóa" onClick={() => setDeleteTarget(q.id)}>
                            <FiTrash2 size={14} />
                        </button>
                    </div>
                );
            },
        },
    ], [users, customers, contacts, opportunities, isManager]);

    return (
        <div className="p-6 bg-bg-main min-h-screen">
            <div className="flex items-center justify-between mb-4">
                <h1 className="text-xl font-semibold text-text-main">Báo giá</h1>
                <div className="flex items-center gap-2">
                    <button
                        onClick={() => navigate('/bao-gia/nhap-file')}
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
                    emptyText="Chưa có báo giá nào"
                    onSelectionChange={setSelectedRows}
                    quickFilters={[
                        { id: 'draft',    label: 'Nháp',      field: 'status', value: 'draft' },
                        { id: 'pending',  label: 'Chờ duyệt', field: 'status', value: 'pending' },
                        { id: 'approved', label: 'Đã duyệt',  field: 'status', value: 'approved' },
                        { id: 'sent',     label: 'Đã gửi',    field: 'status', value: 'sent' },
                    ]}
                />
            </div>

            {deleteTarget !== null && (
                <ConfirmModal
                    message="Bạn có chắc muốn xóa báo giá này?"
                    confirmLabel="Xóa"
                    confirmDanger
                    isLoading={isDeleting}
                    onConfirm={() => deleteFn(deleteTarget, { onSuccess: () => setDeleteTarget(null) })}
                    onCancel={() => setDeleteTarget(null)}
                />
            )}

            {bulkDeleteOpen && (
                <ConfirmModal
                    message={`Xóa ${selectedRows.length} báo giá đã chọn?`}
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
                columns={quotationExportColumns}
                rowCount={rowsToExport.length}
                onClose={() => setExportOpen(false)}
                onExport={(keys, format) => {
                    exportRows(rowsToExport, quotationExportColumns, keys, format, 'bao-gia');
                    setExportOpen(false);
                }}
            />

            {rejectTarget !== null && (
                <ReasonModal
                    title="Từ chối báo giá"
                    label="Lý do từ chối"
                    placeholder="Nhập lý do để nhân viên chỉnh sửa lại..."
                    confirmLabel="Từ chối"
                    confirmDanger
                    onCancel={() => setRejectTarget(null)}
                    onConfirm={(reason) => {
                        runAction(rejectTarget, 'reject', reason);
                        setRejectTarget(null);
                    }}
                />
            )}

            <QuotationEditModal item={editTarget} onClose={() => setEditTarget(null)} />

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

export default BaoGiaPage;
