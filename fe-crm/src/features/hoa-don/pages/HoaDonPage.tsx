import { useState, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import { FiEdit2, FiTrash2, FiUpload, FiShare2, FiPlus, FiDownload, FiSend, FiXCircle } from 'react-icons/fi';
import type { ColumnDef } from '@tanstack/react-table';
import { DataTable } from '@/shared/components/table/DataTable';
import { ConfirmModal } from '@/shared/components/ConfirmModal';
import { HandoverModal } from '@/shared/components/HandoverModal';
import { ExportModal } from '@/shared/components/export/ExportModal';
import { exportRows } from '@/shared/components/export/exportFile';
import { useAlert } from '@/shared/alert/useAlert';
import { useInvoiceWorkflow, type InvoiceAction } from '../hooks/useInvoiceWorkflow';
import { useActiveUsers } from '@/features/users/hooks/useActiveUsers';
import { useOrgUnits } from '@/features/users/hooks/useOrgUnits';
import { useCustomerList } from '@/features/khach-hang/hooks/useCustomerList';
import { useContactList } from '@/features/lien-he/hooks/useContactList';
import { useOpportunityList } from '@/features/co-hoi/hooks/useOpportunityList';
import { useQuotationList } from '@/features/bao-gia/hooks/useQuotationList';
import { toIdNameMap } from '@/shared/utils/lookup';
import { useInvoiceList } from '../hooks/useInvoiceList';
import { useDeleteInvoice } from '../hooks/useDeleteInvoice';
import { useHandoverBulkInvoice } from '../hooks/useHandoverBulkInvoice';
import { getInvoiceColumns } from '../config/invoiceColumns';
import { invoiceExportColumns } from '../config/invoiceExportColumns';
import { InvoiceEditModal } from '../components/InvoiceEditModal';
import type { InvoiceResult } from '../types/invoiceTypes';

const HoaDonPage = () => {
    const navigate = useNavigate();
    const { showAlert } = useAlert();
    const { data = [], isLoading } = useInvoiceList();
    const { mutate: deleteFn, isPending: isDeleting } = useDeleteInvoice();
    const { mutate: handoverFn, isPending: isHandovering } = useHandoverBulkInvoice();
    const { mutate: workflowFn } = useInvoiceWorkflow();
    const { data: users } = useActiveUsers();
    const { data: orgUnits } = useOrgUnits();
    const { data: customers } = useCustomerList();
    const { data: contacts } = useContactList();
    const { data: opportunities } = useOpportunityList();
    const { data: quotations } = useQuotationList();

    const [editTarget, setEditTarget] = useState<InvoiceResult | null>(null);
    const [deleteTarget, setDeleteTarget] = useState<number | null>(null);
    const [selectedRows, setSelectedRows] = useState<InvoiceResult[]>([]);
    const [bulkDeleteOpen, setBulkDeleteOpen] = useState(false);
    const [handoverOpen, setHandoverOpen] = useState(false);
    const [exportOpen, setExportOpen] = useState(false);

    const rowsToExport = selectedRows.length > 0 ? selectedRows : data;

    /** Chạy một hành động chuyển trạng thái Hóa đơn, báo lỗi qua alert nếu bước chuyển không hợp lệ. */
    const runAction = (id: number, action: InvoiceAction) =>
        workflowFn({ id, action }, {
            onError: (err: unknown) => {
                const msg = (err as { response?: { data?: { message?: string } } })?.response?.data?.message
                    ?? 'Không thực hiện được hành động';
                showAlert(msg);
            },
        });

    const columns = useMemo<ColumnDef<InvoiceResult>[]>(() => [
        ...getInvoiceColumns({
            customers: toIdNameMap(customers, 'id', 'name'),
            contacts: toIdNameMap(contacts, 'id', 'fullName'),
            quotations: toIdNameMap(quotations, 'id', 'code'),
            opportunities: toIdNameMap(opportunities, 'id', 'name'),
            users: toIdNameMap(users, 'id', 'fullName'),
            orgUnits: toIdNameMap(orgUnits, 'id', 'name'),
            invoices: toIdNameMap(data, 'id', 'code'),
        }),
        {
            id: 'actions',
            header: '',
            enableSorting: false,
            size: 80,
            cell: ({ row }) => {
                const o = row.original;
                return (
                    <div className="flex gap-1 justify-end" onClick={(e) => e.stopPropagation()}>
                        {o.status === 'draft' && (
                            <button className="p-1.5 rounded hover:bg-blue-50 text-gray-400 hover:text-primary"
                                title="Phát hành" onClick={() => runAction(o.id, 'issue')}>
                                <FiSend size={14} />
                            </button>
                        )}
                        {(o.status === 'draft' || o.status === 'sent' || o.status === 'partially_paid') && (
                            <button className="p-1.5 rounded hover:bg-red-50 text-gray-400 hover:text-danger"
                                title="Hủy hóa đơn" onClick={() => runAction(o.id, 'cancel')}>
                                <FiXCircle size={14} />
                            </button>
                        )}
                        {!o.isLocked && (
                            <button className="p-1.5 rounded hover:bg-gray-100 text-gray-400 hover:text-primary"
                                title="Chỉnh sửa" onClick={() => setEditTarget(o)}>
                                <FiEdit2 size={14} />
                            </button>
                        )}
                        <button className="p-1.5 rounded hover:bg-red-50 text-gray-400 hover:text-danger"
                            title="Xóa" onClick={() => setDeleteTarget(o.id)}>
                            <FiTrash2 size={14} />
                        </button>
                    </div>
                );
            },
        },
    ], [data, users, orgUnits, customers, contacts, opportunities, quotations]);

    return (
        <div className="p-6 bg-bg-main min-h-screen">
            <div className="flex items-center justify-between mb-4">
                <h1 className="text-xl font-semibold text-text-main">Hóa đơn</h1>
                <div className="flex items-center gap-2">
                    <button
                        onClick={() => navigate('/hoa-don/nhap-file')}
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
                        onClick={() => navigate('/hoa-don/them-moi')}
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
                    emptyText="Chưa có Hóa đơn nào"
                    onSelectionChange={setSelectedRows}
                    quickFilters={[
                        { id: 'draft',          label: 'Nháp',                 field: 'status', value: 'draft' },
                        { id: 'sent',           label: 'Đã gửi',               field: 'status', value: 'sent' },
                        { id: 'partially_paid', label: 'Thanh toán một phần',  field: 'status', value: 'partially_paid' },
                        { id: 'paid',           label: 'Đã thanh toán',        field: 'status', value: 'paid' },
                    ]}
                />
            </div>

            {deleteTarget !== null && (
                <ConfirmModal
                    message="Bạn có chắc muốn xóa Hóa đơn này?"
                    confirmLabel="Xóa"
                    confirmDanger
                    isLoading={isDeleting}
                    onConfirm={() => deleteFn(deleteTarget, { onSuccess: () => setDeleteTarget(null) })}
                    onCancel={() => setDeleteTarget(null)}
                />
            )}

            {bulkDeleteOpen && (
                <ConfirmModal
                    message={`Xóa ${selectedRows.length} Hóa đơn đã chọn?`}
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
                columns={invoiceExportColumns}
                rowCount={rowsToExport.length}
                onClose={() => setExportOpen(false)}
                onExport={(keys, format) => {
                    exportRows(rowsToExport, invoiceExportColumns, keys, format, 'hoa-don');
                    setExportOpen(false);
                }}
            />

            <InvoiceEditModal item={editTarget} onClose={() => setEditTarget(null)} />

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

export default HoaDonPage;
