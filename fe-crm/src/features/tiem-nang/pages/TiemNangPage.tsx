import { useState, useMemo } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { FiTrash2, FiUpload, FiShare2, FiPlus, FiDownload } from 'react-icons/fi';
import type { ColumnDef } from '@tanstack/react-table';
import type { RowAction } from '@/shared/types/table';
import { PageHeaderSlot } from '@/shared/components/layout/PageHeaderSlot';
import { DataTable } from '@/shared/components/table/DataTable';
import { ConfirmModal } from '@/shared/components/ConfirmModal';
import { ReasonModal } from '@/shared/components/ReasonModal';
import { HandoverModal } from '@/shared/components/HandoverModal';
import { ExportModal } from '@/shared/components/export/ExportModal';
import { exportRows } from '@/shared/components/export/exportFile';
import { useAlert } from '@/shared/alert/useAlert';
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
    const [searchParams] = useSearchParams();
    const focusId = searchParams.get('focus');
    const { showAlert } = useAlert();
    const { data = [], isLoading } = useLeadList();
    const { mutate: deleteFn, isPending: isDeleting } = useDeleteLead();
    const { mutate: handoverFn, isPending: isHandovering } = useHandoverBulkLead();
    const { mutate: workflowFn } = useLeadWorkflow();

    const [editTarget, setEditTarget] = useState<LeadResult | null>(null);
    const [deleteTarget, setDeleteTarget] = useState<number | null>(null);
    const [loseTarget, setLoseTarget] = useState<number | null>(null);
    const [selectedRows, setSelectedRows] = useState<LeadResult[]>([]);
    const [bulkDeleteOpen, setBulkDeleteOpen] = useState(false);
    const [handoverOpen, setHandoverOpen] = useState(false);
    const [exportOpen, setExportOpen] = useState(false);

    const rowsToExport = selectedRows.length > 0 ? selectedRows : data;

    /** Chạy hành động chuyển trạng thái tiềm năng, báo lỗi qua alert nếu bước chuyển không hợp lệ.
     *  Convert thành công → điều hướng sang Cơ hội (KH + LH + Cơ hội vừa được tạo). */
    const runAction = (id: number, action: LeadAction, reason?: string) =>
        workflowFn({ id, action, reason }, {
            onSuccess: () => {
                if (action === 'convert') {
                    showAlert('Đã tạo Khách hàng, Liên hệ và Cơ hội từ tiềm năng');
                    navigate('/co-hoi');
                }
            },
            onError: (err: unknown) => {
                const msg = (err as { response?: { data?: { message?: string } } })?.response?.data?.message
                    ?? 'Không thực hiện được hành động';
                showAlert(msg);
            },
        });

    const columns = useMemo<ColumnDef<LeadResult>[]>(() => getLeadColumns(), []);

    /** Thao tác của một tiềm năng — hiện trong menu chuột phải. */
    const rowActions = (l: LeadResult): RowAction[] => {
        const isOpen = l.status !== 'converted' && l.status !== 'lost';
        return [
            ...(l.status === 'qualified'
                ? [{ key: 'convert', label: 'Chuyển đổi', onClick: () => runAction(l.id, 'convert') }]
                : []),
            ...(isOpen
                ? [{ key: 'lose', label: 'Đánh mất', onClick: () => setLoseTarget(l.id) }]
                : []),
            ...(l.status !== 'converted'
                ? [{ key: 'edit', label: 'Chỉnh sửa', onClick: () => setEditTarget(l) }]
                : []),
            { key: 'delete', label: 'Xóa', danger: true, onClick: () => setDeleteTarget(l.id) },
        ];
    };

    return (
        <div className="p-6 bg-bg-main">
            <PageHeaderSlot>
                <h1 className="text-lg font-semibold text-text-main truncate">Tiềm năng</h1>
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
            </PageHeaderSlot>
            <div className="bg-white rounded-card p-4 shadow-sm">
                <DataTable
                    data={data}
                    columns={columns}
                    isLoading={isLoading}
                    emptyText="Chưa có tiềm năng nào"
                    onSelectionChange={setSelectedRows}
                    focusId={focusId}
                    rowActions={rowActions}
                    onRowDoubleClick={(l) => { if (l.status !== 'converted') setEditTarget(l); }}
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
