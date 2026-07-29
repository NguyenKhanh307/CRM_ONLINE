import { useState, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import { FiTrash2, FiUpload, FiDownload, FiFolder } from 'react-icons/fi';
import type { ColumnDef } from '@tanstack/react-table';
import { PageHeaderSlot } from '@/shared/components/layout/PageHeaderSlot';
import { ActionButton } from '@/shared/components/ActionButton';
import { CreateButton } from '@/shared/components/CreateButton';
import { usePageShortcuts } from '@/shared/keyboard/PageShortcutsProvider';
import { DataTable } from '@/shared/components/table/DataTable';
import { ConfirmModal } from '@/shared/components/ConfirmModal';
import { ExportModal } from '@/shared/components/export/ExportModal';
import { exportRows } from '@/shared/components/export/exportFile';
import { usePermission } from '@/core/permissions/usePermission';
import { usePagedProductList } from '../hooks/usePagedProductList';
import { productService } from '../services/productService';
import { useDeleteProduct } from '../hooks/useDeleteProduct';
import { getProductColumns } from '../config/productColumns';
import { productExportColumns } from '../config/productExportColumns';
import { ProductEditModal } from '../components/ProductEditModal';
import type { ProductResult } from '../types/productTypes';

/** Tag lọc nhanh — hằng ngoài component để giữ ref ổn định giữa các lần render. */
const QUICK_FILTERS = [
    { id: 'active',       label: 'Đang bán',  field: 'isActive', value: 'true' },
    { id: 'discontinued', label: 'Ngừng bán', field: 'isActive', value: 'false' },
];

const SanPhamPage = () => {
    const navigate = useNavigate();
    // product.create/edit/delete/import/export (2026-07-28) — mã quyền riêng, không còn gate cứng theo role
    const { can } = usePermission();
    const goCreate = () => navigate('/san-pham/them-moi');
    usePageShortcuts({ onCreate: can('product', 'create') ? goCreate : undefined });

    // Server-side pagination + search + tag lọc nhanh (tag Sản phẩm lọc theo isActive — BE map param status → isActive)
    const [page, setPage] = useState(0);
    const [pageSize, setPageSize] = useState(10);
    const [search, setSearch] = useState('');
    const [quickStatus, setQuickStatus] = useState<string | null>(null);
    const { data: pageData, isLoading } = usePagedProductList({
        page, size: pageSize, sortBy: 'createdAt', sortDir: 'desc',
        q: search || undefined, status: quickStatus || undefined,
    });
    const data = pageData?.items ?? [];
    const total = pageData?.total ?? 0;
    const { mutate: deleteFn, isPending: isDeleting } = useDeleteProduct();

    const [editTarget, setEditTarget] = useState<ProductResult | null>(null);
    const [deleteTarget, setDeleteTarget] = useState<number | null>(null);
    const [selectedRows, setSelectedRows] = useState<ProductResult[]>([]);
    const [bulkDeleteOpen, setBulkDeleteOpen] = useState(false);
    const [exportOpen, setExportOpen] = useState(false);


    const columns = useMemo<ColumnDef<ProductResult>[]>(() => getProductColumns(), []);

    return (
        <div className="p-6 bg-bg-main">
            <PageHeaderSlot>
                <h1 className="text-lg font-semibold text-text-main truncate">Sản phẩm</h1>
                <div className="flex items-center gap-1.5">
                    <ActionButton variant="secondary" icon={FiFolder} onClick={() => navigate('/san-pham/danh-muc')}>
                        Danh mục
                    </ActionButton>
                    {can('product', 'import') && (
                        <ActionButton variant="secondary" icon={FiUpload} onClick={() => navigate('/san-pham/nhap-file')}>
                            Nhập
                        </ActionButton>
                    )}
                    {can('product', 'export') && (
                        <ActionButton variant="secondary" icon={FiDownload} onClick={() => setExportOpen(true)}>
                            Xuất{selectedRows.length > 0 ? ` (${selectedRows.length})` : ''}
                        </ActionButton>
                    )}
                    {can('product', 'create') && <CreateButton onClick={goCreate} />}
                    {can('product', 'delete') && selectedRows.length > 0 && (
                        <ActionButton variant="danger" icon={FiTrash2} onClick={() => setBulkDeleteOpen(true)}>
                            Xóa ({selectedRows.length})
                        </ActionButton>
                    )}
                </div>
            </PageHeaderSlot>
            <div className="bg-white rounded-card p-4 shadow-sm">
                <DataTable
                    data={data}
                    columns={columns}
                    isLoading={isLoading}
                    emptyText="Chưa có sản phẩm nào"
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
                    onRowDoubleClick={(p) => { if (can('product', 'edit')) setEditTarget(p); }}
                    rowActions={(p) => [
                        ...(can('product', 'edit')
                            ? [{ key: 'edit', label: 'Chỉnh sửa', onClick: () => setEditTarget(p) }]
                            : []),
                        ...(can('product', 'delete')
                            ? [{ key: 'delete', label: 'Xóa', danger: true, onClick: () => setDeleteTarget(p.id) }]
                            : []),
                    ]}
                    quickFilters={QUICK_FILTERS}
                />
            </div>

            {deleteTarget !== null && (
                <ConfirmModal
                    message="Bạn có chắc muốn xóa sản phẩm này?"
                    confirmLabel="Xóa"
                    confirmDanger
                    isLoading={isDeleting}
                    onConfirm={() => deleteFn(deleteTarget, { onSuccess: () => setDeleteTarget(null) })}
                    onCancel={() => setDeleteTarget(null)}
                />
            )}

            {bulkDeleteOpen && (
                <ConfirmModal
                    message={`Xóa ${selectedRows.length} sản phẩm đã chọn?`}
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
                columns={productExportColumns}
                rowCount={selectedRows.length > 0 ? selectedRows.length : total}
                onClose={() => setExportOpen(false)}
                onExport={async (keys, format) => {
                    // Không tick dòng → tải toàn bộ kết quả đang lọc từ server rồi xuất
                    const rows = selectedRows.length > 0
                        ? selectedRows
                        : (await productService.getList({ page: 0, size: 10000, sortBy: 'createdAt', sortDir: 'desc', q: search || undefined, status: quickStatus || undefined })).data.data.items;
                    exportRows(rows, productExportColumns, keys, format, 'san-pham');
                    setExportOpen(false);
                }}
            />

            <ProductEditModal item={editTarget} onClose={() => setEditTarget(null)} />
        </div>
    );
};

export default SanPhamPage;
