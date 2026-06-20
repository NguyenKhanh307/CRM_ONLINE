import { useState, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import { FiEdit2, FiTrash2, FiUpload, FiPlus, FiDownload } from 'react-icons/fi';
import type { ColumnDef } from '@tanstack/react-table';
import { DataTable } from '@/shared/components/table/DataTable';
import { ConfirmModal } from '@/shared/components/ConfirmModal';
import { ExportModal } from '@/shared/components/export/ExportModal';
import { exportRows } from '@/shared/components/export/exportFile';
import { toIdNameMap } from '@/shared/utils/lookup';
import { useProductList } from '../hooks/useProductList';
import { useProductCategories } from '../hooks/useProductCategories';
import { useDeleteProduct } from '../hooks/useDeleteProduct';
import { getProductColumns } from '../config/productColumns';
import { productExportColumns } from '../config/productExportColumns';
import { ProductEditModal } from '../components/ProductEditModal';
import type { ProductResult } from '../types/productTypes';

const SanPhamPage = () => {
    const navigate = useNavigate();
    const { data = [], isLoading } = useProductList();
    const { mutate: deleteFn, isPending: isDeleting } = useDeleteProduct();
    const { data: categories } = useProductCategories();

    const [editTarget, setEditTarget] = useState<ProductResult | null>(null);
    const [deleteTarget, setDeleteTarget] = useState<number | null>(null);
    const [selectedRows, setSelectedRows] = useState<ProductResult[]>([]);
    const [bulkDeleteOpen, setBulkDeleteOpen] = useState(false);
    const [exportOpen, setExportOpen] = useState(false);

    const rowsToExport = selectedRows.length > 0 ? selectedRows : data;

    const columns = useMemo<ColumnDef<ProductResult>[]>(() => [
        ...getProductColumns({
            categories: toIdNameMap(categories, 'id', 'name'),
        }),
        {
            id: 'actions',
            header: '',
            enableSorting: false,
            size: 80,
            cell: ({ row }) => (
                <div className="flex gap-1 justify-end" onClick={(e) => e.stopPropagation()}>
                    <button
                        className="p-1.5 rounded hover:bg-gray-100 text-gray-400 hover:text-primary"
                        title="Chỉnh sửa"
                        onClick={() => setEditTarget(row.original)}
                    >
                        <FiEdit2 size={14} />
                    </button>
                    <button
                        className="p-1.5 rounded hover:bg-red-50 text-gray-400 hover:text-danger"
                        title="Xóa"
                        onClick={() => setDeleteTarget(row.original.id)}
                    >
                        <FiTrash2 size={14} />
                    </button>
                </div>
            ),
        },
    ], [categories]);

    return (
        <div className="p-6 bg-bg-main min-h-screen">
            <div className="flex items-center justify-between mb-4">
                <h1 className="text-xl font-semibold text-text-main">Sản phẩm</h1>
                <div className="flex items-center gap-2">
                    <button
                        onClick={() => navigate('/san-pham/nhap-file')}
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
                        onClick={() => navigate('/san-pham/them-moi')}
                        className="flex items-center gap-1.5 px-3 py-1.5 rounded-btn bg-primary text-white text-md hover:opacity-90"
                    >
                        <FiPlus size={14} />
                        Thêm mới
                    </button>
                    {selectedRows.length > 0 && (
                        <button
                            onClick={() => setBulkDeleteOpen(true)}
                            className="flex items-center gap-2 px-3 py-1.5 rounded-btn bg-red-50 text-danger text-md font-medium hover:bg-red-100"
                        >
                            <FiTrash2 size={14} />
                            Xóa đã chọn ({selectedRows.length})
                        </button>
                    )}
                </div>
            </div>
            <div className="bg-white rounded-card p-4 shadow-sm">
                <DataTable
                    data={data}
                    columns={columns}
                    isLoading={isLoading}
                    emptyText="Chưa có sản phẩm nào"
                    onSelectionChange={setSelectedRows}
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
