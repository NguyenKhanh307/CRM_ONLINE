import { useState, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import { FiTrash2, FiUpload, FiShare2, FiPlus, FiDownload } from 'react-icons/fi';
import type { ColumnDef } from '@tanstack/react-table';
import type { RowAction } from '@/shared/types/table';
import { PageHeaderSlot } from '@/shared/components/layout/PageHeaderSlot';
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
import { useOrderList } from '../hooks/useOrderList';
import { useOrderItems } from '../hooks/useOrderItems';
import { useDeleteOrder } from '../hooks/useDeleteOrder';
import { useHandoverBulkOrder } from '../hooks/useHandoverBulkOrder';
import { getOrderColumns } from '../config/orderColumns';
import { orderExportColumns } from '../config/orderExportColumns';
import { OrderEditModal } from '../components/OrderEditModal';
import type { OrderItemResult, OrderResult } from '../types/orderTypes';

const DonHangPage = () => {
    const navigate = useNavigate();
    const { showAlert } = useAlert();
    const { data = [], isLoading } = useOrderList();
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

    const rowsToExport = selectedRows.length > 0 ? selectedRows : data;

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
        ...(o.status === 'draft'
            ? [{ key: 'confirm', label: 'Xác nhận đơn', onClick: () => runAction(o.id, 'confirm') }]
            : []),
        ...(o.status === 'confirmed'
            ? [{ key: 'process', label: 'Bắt đầu xử lý', onClick: () => runAction(o.id, 'process') }]
            : []),
        ...(o.status === 'confirmed' || o.status === 'processing'
            ? [{ key: 'createInvoice', label: 'Xuất hóa đơn', onClick: () => runAction(o.id, 'createInvoice') }]
            : []),
        ...(o.status === 'processing'
            ? [{ key: 'complete', label: 'Hoàn tất đơn', onClick: () => runAction(o.id, 'complete') }]
            : []),
        ...(o.status === 'draft' || o.status === 'confirmed' || o.status === 'processing'
            ? [{ key: 'cancel', label: 'Hủy đơn', onClick: () => runAction(o.id, 'cancel') }]
            : []),
        ...(!o.isLocked
            ? [{ key: 'edit', label: 'Chỉnh sửa', onClick: () => setEditTarget(o) }]
            : []),
        { key: 'delete', label: 'Xóa', danger: true, onClick: () => setDeleteTarget(o.id) },
    ];

    return (
        <div className="p-6 bg-bg-main">
            <PageHeaderSlot>
                <h1 className="text-lg font-semibold text-text-main truncate">Đơn hàng</h1>
                <div className="flex items-center gap-2">
                    <button
                        onClick={() => navigate('/don-hang/nhap-file')}
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
                        onClick={() => navigate('/don-hang/them-moi')}
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
                    emptyText="Chưa có Đơn hàng nào"
                    onSelectionChange={setSelectedRows}
                    onRowSelect={setSelectedRecord}
                    visibleRows={7}
                    autoSelectFirstRow
                    rowActions={rowActions}
                    onRowDoubleClick={(o) => { if (!o.isLocked) setEditTarget(o); }}
                    quickFilters={[
                        { id: 'draft',      label: 'Nháp',        field: 'status', value: 'draft' },
                        { id: 'confirmed',  label: 'Đã xác nhận', field: 'status', value: 'confirmed' },
                        { id: 'processing', label: 'Đang xử lý',   field: 'status', value: 'processing' },
                        { id: 'completed',  label: 'Hoàn tất',     field: 'status', value: 'completed' },
                    ]}
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
                rowCount={rowsToExport.length}
                onClose={() => setExportOpen(false)}
                onExport={(keys, format) => {
                    exportRows(rowsToExport, orderExportColumns, keys, format, 'don-hang');
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
