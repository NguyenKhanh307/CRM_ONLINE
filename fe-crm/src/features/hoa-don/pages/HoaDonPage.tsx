import { useState, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
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
import { useInvoiceWorkflow, type InvoiceAction } from '../hooks/useInvoiceWorkflow';
import { usePagedInvoiceList } from '../hooks/usePagedInvoiceList';
import { invoiceService } from '../services/invoiceService';
import { useInvoiceItems } from '../hooks/useInvoiceItems';
import { useDeleteInvoice } from '../hooks/useDeleteInvoice';
import { useHandoverBulkInvoice } from '../hooks/useHandoverBulkInvoice';
import { getInvoiceColumns } from '../config/invoiceColumns';
import { invoiceExportColumns } from '../config/invoiceExportColumns';
import { InvoiceEditModal } from '../components/InvoiceEditModal';
import type { InvoiceItemResult, InvoiceResult } from '../types/invoiceTypes';

/** Tag lọc nhanh — hằng ngoài component để giữ ref ổn định giữa các lần render. */
const QUICK_FILTERS = [
    { id: 'draft',          label: 'Nháp',                 field: 'status', value: 'draft' },
    { id: 'sent',           label: 'Đã gửi',               field: 'status', value: 'sent' },
    { id: 'partially_paid', label: 'Thanh toán một phần',  field: 'status', value: 'partially_paid' },
    { id: 'paid',           label: 'Đã thanh toán',        field: 'status', value: 'paid' },
];

const HoaDonPage = () => {
    const navigate = useNavigate();
    const goCreate = () => navigate('/hoa-don/them-moi');
    usePageShortcuts({ onCreate: goCreate });
    const { showAlert } = useAlert();
    // Server-side pagination + search + tag lọc nhanh
    const [page, setPage] = useState(0);
    const [pageSize, setPageSize] = useState(10);
    const [search, setSearch] = useState('');
    const [quickStatus, setQuickStatus] = useState<string | null>(null);
    const { data: pageData, isLoading } = usePagedInvoiceList({
        page, size: pageSize, sortBy: 'createdAt', sortDir: 'desc',
        q: search || undefined, status: quickStatus || undefined,
    });
    const data = pageData?.items ?? [];
    const total = pageData?.total ?? 0;
    const { mutate: deleteFn, isPending: isDeleting } = useDeleteInvoice();
    const { mutate: handoverFn, isPending: isHandovering } = useHandoverBulkInvoice();
    const { mutate: workflowFn } = useInvoiceWorkflow();

    const [editTarget, setEditTarget] = useState<InvoiceResult | null>(null);
    const [deleteTarget, setDeleteTarget] = useState<number | null>(null);
    const [selectedRows, setSelectedRows] = useState<InvoiceResult[]>([]);
    const [bulkDeleteOpen, setBulkDeleteOpen] = useState(false);
    const [handoverOpen, setHandoverOpen] = useState(false);
    const [exportOpen, setExportOpen] = useState(false);
    const [selectedRecord, setSelectedRecord] = useState<InvoiceResult | null>(null);

    const productMap = useProductMap();
    const { data: items = [], isLoading: itemsLoading } = useInvoiceItems(selectedRecord?.id ?? null);
    const itemColumns = useMemo(() => getLineItemPanelColumns<InvoiceItemResult>(productMap, { showTax: true }), [productMap]);


    /** Chạy một hành động chuyển trạng thái Hóa đơn, báo lỗi qua alert nếu bước chuyển không hợp lệ. */
    const runAction = (id: number, action: InvoiceAction) =>
        workflowFn({ id, action }, {
            onError: (err: unknown) => {
                const msg = (err as { response?: { data?: { message?: string } } })?.response?.data?.message
                    ?? 'Không thực hiện được hành động';
                showAlert(msg);
            },
        });

    const columns = useMemo<ColumnDef<InvoiceResult>[]>(() => getInvoiceColumns(), []);

    /** Thao tác của một hóa đơn — hiện trong menu chuột phải. */
    const rowActions = (o: InvoiceResult): RowAction[] => [
        { key: 'detail', label: 'Xem chi tiết', onClick: () => navigate(`/hoa-don/${o.id}`) },
        ...(o.status === 'draft'
            ? [{ key: 'issue', label: 'Phát hành', onClick: () => runAction(o.id, 'issue') }]
            : []),
        ...(o.status === 'draft' || o.status === 'sent' || o.status === 'partially_paid'
            ? [{ key: 'cancel', label: 'Hủy hóa đơn', onClick: () => runAction(o.id, 'cancel') }]
            : []),
        ...(!o.isLocked
            ? [{ key: 'edit', label: 'Chỉnh sửa', onClick: () => setEditTarget(o) }]
            : []),
        { key: 'delete', label: 'Xóa', danger: true, onClick: () => setDeleteTarget(o.id) },
    ];

    return (
        <div className="p-6 bg-bg-main">
            <PageHeaderSlot>
                <h1 className="text-lg font-semibold text-text-main truncate">Hóa đơn</h1>
                <div className="flex items-center gap-1.5">
                    <ActionButton variant="secondary" icon={FiUpload} onClick={() => navigate('/hoa-don/nhap-file')}>
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
                    emptyText="Chưa có Hóa đơn nào"
                    onSelectionChange={setSelectedRows}
                    onRowSelect={setSelectedRecord}
                    visibleRows={7}
                    autoSelectFirstRow
                    server={{
                        totalRows: total,
                        pageIndex: page,
                        pageSize,
                        onPageChange: setPage,
                        onPageSizeChange: setPageSize,
                        searchValue: search,
                        onSearchChange: (v) => { setSearch(v); setPage(0); },
                        onQuickFilterChange: (v) => { setQuickStatus(v); setPage(0); },
                    }}
                    rowActions={rowActions}
                    onRowDoubleClick={(o) => navigate(`/hoa-don/${o.id}`)}
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
                rowCount={selectedRows.length > 0 ? selectedRows.length : total}
                onClose={() => setExportOpen(false)}
                onExport={async (keys, format) => {
                    // Không tick dòng → tải toàn bộ kết quả đang lọc từ server rồi xuất
                    const rows = selectedRows.length > 0
                        ? selectedRows
                        : (await invoiceService.getList({ page: 0, size: 10000, sortBy: 'createdAt', sortDir: 'desc', q: search || undefined, status: quickStatus || undefined })).data.data.items;
                    exportRows(rows, invoiceExportColumns, keys, format, 'hoa-don');
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
