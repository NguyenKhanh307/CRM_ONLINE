import { useState, useMemo } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { FiTrash2, FiUpload, FiShare2, FiDownload } from 'react-icons/fi';
import type { ColumnDef } from '@tanstack/react-table';
import type { RowAction } from '@/shared/types/table';
import { PageHeaderSlot } from '@/shared/components/layout/PageHeaderSlot';
import { ActionButton } from '@/shared/components/ActionButton';
import { CreateButton } from '@/shared/components/CreateButton';
import { usePageShortcuts } from '@/shared/keyboard/PageShortcutsProvider';
import { DataTable } from '@/shared/components/table/DataTable';
import { RecordItemsPanel } from '@/shared/components/table/RecordItemsPanel';
import { getLineItemPanelColumns } from '@/shared/components/table/lineItemPanelColumns';
import { useProductMap } from '@/features/san-pham/hooks/useProductMap';
import { ConfirmModal } from '@/shared/components/ConfirmModal';
import { HandoverModal } from '@/shared/components/HandoverModal';
import { ExportModal } from '@/shared/components/export/ExportModal';
import { exportRows } from '@/shared/components/export/exportFile';
import { useAlert } from '@/shared/alert/useAlert';
import { usePermission } from '@/core/permissions/usePermission';
import { useQuotationList } from '../hooks/useQuotationList';
import { useQuotationItems } from '../hooks/useQuotationItems';
import { useDeleteQuotation } from '../hooks/useDeleteQuotation';
import { useHandoverBulkQuotation } from '../hooks/useHandoverBulkQuotation';
import { useQuotationWorkflow, type QuotationAction } from '../hooks/useQuotationWorkflow';
import { getQuotationColumns } from '../config/quotationColumns';
import { quotationExportColumns } from '../config/quotationExportColumns';
import { QuotationEditModal } from '../components/QuotationEditModal';
import { SendQuotationModal } from '../components/SendQuotationModal';
import { ReasonModal } from '@/shared/components/ReasonModal';
import type { QuotationItemResult, QuotationResult } from '../types/quotationTypes';

/** Tag lọc nhanh — hằng ngoài component để giữ ref ổn định giữa các lần render. */
const QUICK_FILTERS = [
    { id: 'draft',    label: 'Nháp',      field: 'status', value: 'draft' },
    { id: 'pending',  label: 'Chờ duyệt', field: 'status', value: 'pending' },
    { id: 'approved', label: 'Đã duyệt',  field: 'status', value: 'approved' },
    { id: 'sent',     label: 'Đã gửi',    field: 'status', value: 'sent' },
];

const BaoGiaPage = () => {
    const navigate = useNavigate();
    const goCreate = () => navigate('/bao-gia/them-moi');
    usePageShortcuts({ onCreate: goCreate });
    const [searchParams] = useSearchParams();
    const focusId = searchParams.get('focus');
    const { showAlert } = useAlert();
    const { hasRole } = usePermission();
    const isManager = hasRole('ADMIN') || hasRole('SALES_MANAGER');
    const { data = [], isLoading } = useQuotationList();
    const { mutate: deleteFn, isPending: isDeleting } = useDeleteQuotation();
    const { mutate: handoverFn, isPending: isHandovering } = useHandoverBulkQuotation();
    const { mutate: workflowFn } = useQuotationWorkflow();

    const [editTarget, setEditTarget] = useState<QuotationResult | null>(null);
    const [deleteTarget, setDeleteTarget] = useState<number | null>(null);
    const [rejectTarget, setRejectTarget] = useState<number | null>(null);
    const [sendTarget, setSendTarget] = useState<number | null>(null);
    const [selectedRows, setSelectedRows] = useState<QuotationResult[]>([]);
    const [bulkDeleteOpen, setBulkDeleteOpen] = useState(false);
    const [handoverOpen, setHandoverOpen] = useState(false);
    const [exportOpen, setExportOpen] = useState(false);
    const [selectedRecord, setSelectedRecord] = useState<QuotationResult | null>(null);

    const productMap = useProductMap();
    const { data: items = [], isLoading: itemsLoading } = useQuotationItems(selectedRecord?.id ?? null);
    const itemColumns = useMemo(() => getLineItemPanelColumns<QuotationItemResult>(productMap, { showTax: true }), [productMap]);

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

    const columns = useMemo<ColumnDef<QuotationResult>[]>(() => getQuotationColumns(), []);

    /** Thao tác của một báo giá — hiện trong menu chuột phải. */
    const rowActions = (q: QuotationResult): RowAction[] => [
        ...(q.status === 'draft'
            ? [{ key: 'submit', label: 'Gửi duyệt', onClick: () => runAction(q.id, 'submit') }]
            : []),
        ...(q.status === 'pending' && isManager
            ? [
                { key: 'approve', label: 'Duyệt', onClick: () => runAction(q.id, 'approve') },
                { key: 'reject', label: 'Từ chối', onClick: () => setRejectTarget(q.id) },
            ]
            : []),
        ...(q.status === 'approved'
            ? [{ key: 'send', label: 'Gửi email cho khách', onClick: () => setSendTarget(q.id) }]
            : []),
        ...(q.status === 'sent'
            ? [{ key: 'accept', label: 'Khách chấp nhận', onClick: () => runAction(q.id, 'accept') }]
            : []),
        ...(q.opportunityId && !q.isPrimary
            ? [{ key: 'setPrimary', label: 'Đặt làm báo giá đồng bộ', onClick: () => runAction(q.id, 'setPrimary') }]
            : []),
        ...(q.status === 'accepted' && !q.isLocked
            ? [{ key: 'convertToOrder', label: 'Chuyển thành đơn hàng', onClick: () => runAction(q.id, 'convertToOrder') }]
            : []),
        ...(!q.isLocked
            ? [{ key: 'edit', label: 'Chỉnh sửa', onClick: () => setEditTarget(q) }]
            : []),
        { key: 'delete', label: 'Xóa', danger: true, onClick: () => setDeleteTarget(q.id) },
    ];

    return (
        <div className="p-6 bg-bg-main">
            <PageHeaderSlot>
                <h1 className="text-lg font-semibold text-text-main truncate">Báo giá</h1>
                <div className="flex items-center gap-1.5">
                    <ActionButton variant="secondary" icon={FiUpload} onClick={() => navigate('/bao-gia/nhap-file')}>
                        Nhập
                    </ActionButton>
                    <ActionButton variant="secondary" icon={FiDownload} onClick={() => setExportOpen(true)}>
                        Xuất{selectedRows.length > 0 ? ` (${selectedRows.length})` : ''}
                    </ActionButton>
                    <CreateButton onClick={goCreate} />
                    {selectedRows.length > 0 && (
                        <>
                            <ActionButton variant="info" icon={FiShare2} onClick={() => setHandoverOpen(true)}>
                                Bàn giao ({selectedRows.length})
                            </ActionButton>
                            <ActionButton variant="danger" icon={FiTrash2} onClick={() => setBulkDeleteOpen(true)}>
                                Xóa ({selectedRows.length})
                            </ActionButton>
                        </>
                    )}
                </div>
            </PageHeaderSlot>
            <div className="bg-white rounded-card p-4 shadow-sm">
                <DataTable
                    data={data}
                    columns={columns}
                    isLoading={isLoading}
                    emptyText="Chưa có báo giá nào"
                    onSelectionChange={setSelectedRows}
                    onRowSelect={setSelectedRecord}
                    visibleRows={7}
                    autoSelectFirstRow
                    focusId={focusId}
                    rowActions={rowActions}
                    onRowDoubleClick={(q) => { if (!q.isLocked) setEditTarget(q); }}
                    quickFilters={QUICK_FILTERS}
                />
            </div>

            <div className="bg-white rounded-card p-4 shadow-sm mt-4">
                <RecordItemsPanel
                    title={selectedRecord?.code}
                    columns={itemColumns}
                    rows={items}
                    isLoading={itemsLoading}
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

            <SendQuotationModal quotationId={sendTarget} onClose={() => setSendTarget(null)} />

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
