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
import { ConfirmModal } from '@/shared/components/ConfirmModal';
import { HandoverModal } from '@/shared/components/HandoverModal';
import { ExportModal } from '@/shared/components/export/ExportModal';
import { exportRows } from '@/shared/components/export/exportFile';
import { useAlert } from '@/shared/alert/useAlert';
import { usePagedCustomerList } from '../hooks/usePagedCustomerList';
import { customerService } from '../services/customerService';
import { useDeleteCustomer } from '../hooks/useDeleteCustomer';
import { useHandoverBulkCustomer } from '../hooks/useHandoverBulkCustomer';
import { useCustomerWorkflow, type CustomerAction } from '../hooks/useCustomerWorkflow';
import { getCustomerColumns } from '../config/customerColumns';
import { customerExportColumns } from '../config/customerExportColumns';
import { CustomerEditModal } from '../components/CustomerEditModal';
import type { CustomerResult } from '../types/customerTypes';

/** Tag lọc nhanh — hằng ngoài component để giữ ref ổn định giữa các lần render. */
const QUICK_FILTERS = [
    { id: 'active',   label: 'Hoạt động',       field: 'status', value: 'active' },
    { id: 'inactive', label: 'Không hoạt động',  field: 'status', value: 'inactive' },
];

const KhachHangPage = () => {
    const navigate = useNavigate();
    const goCreate = () => navigate('/khach-hang/them-moi');
    usePageShortcuts({ onCreate: goCreate });
    const { showAlert } = useAlert();
    // Server-side pagination + search + tag lọc nhanh
    const [page, setPage] = useState(0);
    const [pageSize, setPageSize] = useState(10);
    const [search, setSearch] = useState('');
    const [quickStatus, setQuickStatus] = useState<string | null>(null);
    const { data: pageData, isLoading } = usePagedCustomerList({
        page, size: pageSize, sortBy: 'createdAt', sortDir: 'desc',
        q: search || undefined, status: quickStatus || undefined,
    });
    const data = pageData?.items ?? [];
    const total = pageData?.total ?? 0;
    const { mutate: deleteFn, isPending: isDeleting } = useDeleteCustomer();
    const { mutate: handoverFn, isPending: isHandovering } = useHandoverBulkCustomer();
    const { mutate: workflowFn } = useCustomerWorkflow();

    const [editTarget, setEditTarget] = useState<CustomerResult | null>(null);
    const [deleteTarget, setDeleteTarget] = useState<number | null>(null);
    const [selectedRows, setSelectedRows] = useState<CustomerResult[]>([]);
    const [bulkDeleteOpen, setBulkDeleteOpen] = useState(false);
    const [handoverOpen, setHandoverOpen] = useState(false);
    const [exportOpen, setExportOpen] = useState(false);


    /** Chạy hành động kích hoạt/ngừng khách hàng, báo lỗi qua alert nếu không hợp lệ. */
    const runAction = (id: number, action: CustomerAction) =>
        workflowFn({ id, action }, {
            onError: (err: unknown) => {
                const msg = (err as { response?: { data?: { message?: string } } })?.response?.data?.message
                    ?? 'Không thực hiện được hành động';
                showAlert(msg);
            },
        });

    const columns = useMemo<ColumnDef<CustomerResult>[]>(() => getCustomerColumns(), []);

    /** Thao tác của một khách hàng — hiện trong menu chuột phải. */
    const rowActions = (c: CustomerResult): RowAction[] => [
        { key: 'detail', label: 'Xem chi tiết', onClick: () => navigate(`/khach-hang/${c.id}`) },
        ...(c.status !== 'active'
            ? [{ key: 'activate', label: 'Kích hoạt', onClick: () => runAction(c.id, 'activate') }]
            : [{ key: 'deactivate', label: 'Ngừng hoạt động', onClick: () => runAction(c.id, 'deactivate') }]),
        { key: 'edit', label: 'Chỉnh sửa', onClick: () => setEditTarget(c) },
        { key: 'delete', label: 'Xóa', danger: true, onClick: () => setDeleteTarget(c.id) },
    ];

    return (
        <div className="p-6 bg-bg-main">
            <PageHeaderSlot>
                <h1 className="text-lg font-semibold text-text-main truncate">Khách hàng</h1>
                <div className="flex items-center gap-1.5">
                    <ActionButton variant="secondary" icon={FiUpload} onClick={() => navigate('/khach-hang/nhap-file')}>
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
                    emptyText="Chưa có khách hàng nào"
                    onSelectionChange={setSelectedRows}
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
                    onRowDoubleClick={(c) => navigate(`/khach-hang/${c.id}`)}
                    quickFilters={QUICK_FILTERS}
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
                rowCount={selectedRows.length > 0 ? selectedRows.length : total}
                onClose={() => setExportOpen(false)}
                onExport={async (keys, format) => {
                    // Không tick dòng → tải toàn bộ kết quả đang lọc từ server rồi xuất
                    const rows = selectedRows.length > 0
                        ? selectedRows
                        : (await customerService.getList({ page: 0, size: 10000, sortBy: 'createdAt', sortDir: 'desc', q: search || undefined, status: quickStatus || undefined })).data.data.items;
                    exportRows(rows, customerExportColumns, keys, format, 'khach-hang');
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
