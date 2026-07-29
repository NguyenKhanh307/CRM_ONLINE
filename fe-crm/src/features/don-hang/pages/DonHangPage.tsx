import { useState, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import { FiTrash2, FiUpload, FiShare2, FiDownload } from 'react-icons/fi';
import type { ColumnDef } from '@tanstack/react-table';
import type { RowAction } from '@/shared/types/table';
import { PageHeaderSlot } from '@/shared/components/layout/PageHeaderSlot';
import { ActionButton } from '@/shared/components/ActionButton';
import { CreateButton } from '@/shared/components/CreateButton';
import { usePageShortcuts } from '@/shared/keyboard/PageShortcutsProvider';
import { usePermission } from '@/core/permissions/usePermission';
import { DataTable } from '@/shared/components/table/DataTable';
import { RecordItemsPanel } from '@/shared/components/table/RecordItemsPanel';
import { getLineItemPanelColumns } from '@/shared/components/table/lineItemPanelColumns';
import { useProductMap } from '@/features/san-pham/hooks/useProductMap';
import { ConfirmModal } from '@/shared/components/ConfirmModal';
import { HandoverModal } from '@/shared/components/HandoverModal';
import { ExportModal } from '@/shared/components/export/ExportModal';
import { exportRows } from '@/shared/components/export/exportFile';
import { useAlert } from '@/shared/alert/useAlert';
import { useOrderWorkflow, type OrderAction } from '../hooks/useOrderWorkflow';
import { usePagedOrderList } from '../hooks/usePagedOrderList';
import { orderService } from '../services/orderService';
import { useOrderItems } from '../hooks/useOrderItems';
import { useDeleteOrder } from '../hooks/useDeleteOrder';
import { useHandoverBulkOrder } from '../hooks/useHandoverBulkOrder';
import { getOrderColumns } from '../config/orderColumns';
import { orderExportColumns } from '../config/orderExportColumns';
import { OrderEditModal } from '../components/OrderEditModal';
import type { OrderItemResult, OrderResult } from '../types/orderTypes';

/** Tag lọc nhanh — hằng ngoài component để giữ ref ổn định giữa các lần render. */
const QUICK_FILTERS = [
    { id: 'draft',      label: 'Nháp',        field: 'status', value: 'draft' },
    { id: 'confirmed',  label: 'Đã xác nhận', field: 'status', value: 'confirmed' },
    { id: 'processing', label: 'Đang xử lý',   field: 'status', value: 'processing' },
    { id: 'completed',  label: 'Hoàn tất',     field: 'status', value: 'completed' },
];

const DonHangPage = () => {
    const navigate = useNavigate();
    const { can } = usePermission();
    const goCreate = () => navigate('/don-hang/them-moi');
    usePageShortcuts({ onCreate: can('order', 'create') ? goCreate : undefined });
    const { showAlert } = useAlert();
    // Server-side pagination + search + tag lọc nhanh
    const [page, setPage] = useState(0);
    const [pageSize, setPageSize] = useState(10);
    const [search, setSearch] = useState('');
    const [quickStatus, setQuickStatus] = useState<string | null>(null);
    const { data: pageData, isLoading } = usePagedOrderList({
        page, size: pageSize, sortBy: 'createdAt', sortDir: 'desc',
        q: search || undefined, status: quickStatus || undefined,
    });
    const data = pageData?.items ?? [];
    const total = pageData?.total ?? 0;
    const { mutate: deleteFn, isPending: isDeleting } = useDeleteOrder();
    const { mutate: handoverFn, isPending: isHandovering } = useHandoverBulkOrder();
    const { mutate: workflowFn } = useOrderWorkflow();

    const [editTarget, setEditTarget] = useState<OrderResult | null>(null);
    const [deleteTarget, setDeleteTarget] = useState<number | null>(null);
    const [selectedRows, setSelectedRows] = useState<OrderResult[]>([]);
    const [bulkDeleteOpen, setBulkDeleteOpen] = useState(false);
    const [handoverOpen, setHandoverOpen] = useState(false);
    const [exportOpen, setExportOpen] = useState(false);
    const [selectedRecord, setSelectedRecord] = useState<OrderResult | null>(null);

    const productMap = useProductMap();
    const { data: items = [], isLoading: itemsLoading } = useOrderItems(selectedRecord?.id ?? null);
    const itemColumns = useMemo(() => getLineItemPanelColumns<OrderItemResult>(productMap, { showTax: true }), [productMap]);


    /** Chạy một hành động trên Đơn hàng, báo lỗi qua alert nếu bước chuyển không hợp lệ.
     *  Xuất hóa đơn thành công → điều hướng sang Hóa đơn (hóa đơn vừa tạo). */
    const runAction = (id: number, action: OrderAction) =>
        workflowFn({ id, action }, {
            onSuccess: () => {
                if (action === 'createInvoice') {
                    showAlert('Đã xuất hóa đơn từ đơn hàng');
                    navigate('/hoa-don');
                }
            },
            onError: (err: unknown) => {
                const msg = (err as { response?: { data?: { message?: string } } })?.response?.data?.message
                    ?? 'Không thực hiện được hành động';
                showAlert(msg);
            },
        });

    const columns = useMemo<ColumnDef<OrderResult>[]>(() => getOrderColumns(), []);

    /** Thao tác của một đơn hàng — hiện trong menu chuột phải. */
    const rowActions = (o: OrderResult): RowAction[] => [
        { key: 'detail', label: 'Xem chi tiết', onClick: () => navigate(`/don-hang/${o.id}`) },
        ...(o.status === 'draft' && can('order', 'process')
            ? [{ key: 'confirm', label: 'Xác nhận đơn', onClick: () => runAction(o.id, 'confirm') }]
            : []),
        ...(o.status === 'confirmed' && can('order', 'process')
            ? [{ key: 'process', label: 'Bắt đầu xử lý', onClick: () => runAction(o.id, 'process') }]
            : []),
        ...((o.status === 'confirmed' || o.status === 'processing') && can('order', 'create_invoice')
            ? [{ key: 'createInvoice', label: 'Xuất hóa đơn', onClick: () => runAction(o.id, 'createInvoice') }]
            : []),
        ...(o.status === 'processing' && can('order', 'process')
            ? [{ key: 'complete', label: 'Hoàn tất đơn', onClick: () => runAction(o.id, 'complete') }]
            : []),
        ...((o.status === 'draft' || o.status === 'confirmed' || o.status === 'processing') && can('order', 'process')
            ? [{ key: 'cancel', label: 'Hủy đơn', onClick: () => runAction(o.id, 'cancel') }]
            : []),
        ...(!o.isLocked && can('order', 'edit')
            ? [{ key: 'edit', label: 'Chỉnh sửa', onClick: () => setEditTarget(o) }]
            : []),
        ...(can('order', 'delete')
            ? [{ key: 'delete', label: 'Xóa', danger: true, onClick: () => setDeleteTarget(o.id) }]
            : []),
    ];

    return (
        <div className="p-6 bg-bg-main">
            <PageHeaderSlot>
                <h1 className="text-lg font-semibold text-text-main truncate">Đơn hàng</h1>
                <div className="flex items-center gap-1.5">
                    {can('order', 'import') && (
                        <ActionButton variant="secondary" icon={FiUpload} onClick={() => navigate('/don-hang/nhap-file')}>
                            Nhập
                        </ActionButton>
                    )}
                    {can('order', 'export') && (
                        <ActionButton variant="secondary" icon={FiDownload} onClick={() => setExportOpen(true)}>
                            Xuất{selectedRows.length > 0 ? ` (${selectedRows.length})` : ''}
                        </ActionButton>
                    )}
                    {can('order', 'create') && <CreateButton onClick={goCreate} />}
                    {selectedRows.length > 0 && (
                        <>
                            {can('order', 'handover') && (
                                <ActionButton variant="info" icon={FiShare2} onClick={() => setHandoverOpen(true)}>
                                    Bàn giao ({selectedRows.length})
                                </ActionButton>
                            )}
                            {can('order', 'delete') && (
                                <ActionButton variant="danger" icon={FiTrash2} onClick={() => setBulkDeleteOpen(true)}>
                                    Xóa ({selectedRows.length})
                                </ActionButton>
                            )}
                        </>
                    )}
                </div>
            </PageHeaderSlot>
            <div className="bg-white rounded-card p-4 shadow-sm">
                <DataTable
                    data={data}
                    columns={columns}
                    isLoading={isLoading}
                    emptyText="Chưa có Đơn hàng nào"
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
                    onRowDoubleClick={(o) => navigate(`/don-hang/${o.id}`)}
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
                    message="Bạn có chắc muốn xóa Đơn hàng này?"
                    confirmLabel="Xóa"
                    confirmDanger
                    isLoading={isDeleting}
                    onConfirm={() => deleteFn(deleteTarget, { onSuccess: () => setDeleteTarget(null) })}
                    onCancel={() => setDeleteTarget(null)}
                />
            )}

            {bulkDeleteOpen && (
                <ConfirmModal
                    message={`Xóa ${selectedRows.length} Đơn hàng đã chọn?`}
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
                columns={orderExportColumns}
                rowCount={selectedRows.length > 0 ? selectedRows.length : total}
                onClose={() => setExportOpen(false)}
                onExport={async (keys, format) => {
                    // Không tick dòng → tải toàn bộ kết quả đang lọc từ server rồi xuất
                    const rows = selectedRows.length > 0
                        ? selectedRows
                        : (await orderService.getList({ page: 0, size: 10000, sortBy: 'createdAt', sortDir: 'desc', q: search || undefined, status: quickStatus || undefined })).data.data.items;
                    exportRows(rows, orderExportColumns, keys, format, 'don-hang');
                    setExportOpen(false);
                }}
            />

            <OrderEditModal item={editTarget} onClose={() => setEditTarget(null)} />

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

export default DonHangPage;
