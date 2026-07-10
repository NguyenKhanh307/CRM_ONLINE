import { useState, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import { FiTrash2, FiUpload, FiDownload } from 'react-icons/fi';
import type { ColumnDef } from '@tanstack/react-table';
import { PageHeaderSlot } from '@/shared/components/layout/PageHeaderSlot';
import { ActionButton } from '@/shared/components/ActionButton';
import { CreateButton } from '@/shared/components/CreateButton';
import { usePageShortcuts } from '@/shared/keyboard/PageShortcutsProvider';
import { DataTable } from '@/shared/components/table/DataTable';
import { ConfirmModal } from '@/shared/components/ConfirmModal';
import { ExportModal } from '@/shared/components/export/ExportModal';
import { exportRows } from '@/shared/components/export/exportFile';
import { useProductList } from '../hooks/useProductList';
import { useDeleteProduct } from '../hooks/useDeleteProduct';
import { getProductColumns } from '../config/productColumns';
import { productExportColumns } from '../config/productExportColumns';
import { ProductEditModal } from '../components/ProductEditModal';
import type { ProductResult } from '../types/productTypes';

const SanPhamPage = () => {
    const navigate = useNavigate();
    const goCreate = () => navigate('/san-pham/them-moi');
    usePageShortcuts({ onCreate: goCreate });
    const { data = [], isLoading } = useProductList();
    const { mutate: deleteFn, isPending: isDeleting } = useDeleteProduct();

    const [editTarget, setEditTarget] = useState<ProductResult | null>(null);
    const [deleteTarget, setDeleteTarget] = useState<number | null>(null);
    const [selectedRows, setSelectedRows] = useState<ProductResult[]>([]);
    const [bulkDeleteOpen, setBulkDeleteOpen] = useState(false);
    const [exportOpen, setExportOpen] = useState(false);

    const rowsToExport = selectedRows.length > 0 ? selectedRows : data;

    const columns = useMemo<ColumnDef<ProductResult>[]>(() => getProductColumns(), []);

    return (
        <div className="p-6 bg-bg-main">
            <PageHeaderSlot>
                <h1 className="text-lg font-semibold text-text-main truncate">Sản phẩm</h1>
                <div className="flex items-center gap-1.5">
                    <ActionButton variant="secondary" icon={FiUpload} onClick={() => navigate('/san-pham/nhap-file')}>
                        Nhập
                    </ActionButton>
                    <ActionButton variant="secondary" icon={FiDownload} onClick={() => setExportOpen(true)}>
                        Xuất{selectedRows.length > 0 ? ` (${selectedRows.length})` : ''}
                    </ActionButton>
                    <CreateButton onClick={goCreate} />
                    {selectedRows.length > 0 && (
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
                    onRowDoubleClick={(p) => setEditTarget(p)}
                    rowActions={(p) => [
                        { key: 'edit', label: 'Chỉnh sửa', onClick: () => setEditTarget(p) },
                        { key: 'delete', label: 'Xóa', danger: true, onClick: () => setDeleteTarget(p.id) },
                    ]}
                    quickFilters={[
                        { id: 'active',       label: 'Đang bán',  field: 'isActive', value: 'true' },
                        { id: 'discontinued', label: 'Ngừng bán', field: 'isActive', value: 'false' },
                    ]}
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
                rowCount={rowsToExport.length}
                onClose={() => setExportOpen(false)}
                onExport={(keys, format) => {
                    exportRows(rowsToExport, productExportColumns, keys, format, 'san-pham');
                    setExportOpen(false);
                }}
            />

            <ProductEditModal item={editTarget} onClose={() => setEditTarget(null)} />
        </div>
    );
};

export default SanPhamPage;
