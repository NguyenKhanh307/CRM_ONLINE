import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { FiTrash2, FiUpload, FiDownload } from 'react-icons/fi';
import type { RowAction } from '@/shared/types/table';
import { PageHeaderSlot } from '@/shared/components/layout/PageHeaderSlot';
import { ActionButton } from '@/shared/components/ActionButton';
import { CreateButton } from '@/shared/components/CreateButton';
import { usePageShortcuts } from '@/shared/keyboard/PageShortcutsProvider';
import { DataTable } from '@/shared/components/table/DataTable';
import { ConfirmModal } from '@/shared/components/ConfirmModal';
import { ExportModal } from '@/shared/components/export/ExportModal';
import { exportRows } from '@/shared/components/export/exportFile';
import { usePermission } from '@/core/permissions/usePermission';
import { usePagedPricePolicyList } from '../hooks/usePagedPricePolicyList';
import { useDeletePricePolicy } from '../hooks/useDeletePricePolicy';
import { pricingColumns } from '../config/pricingColumns';
import { pricingExportColumns } from '../config/pricingExportColumns';
import { pricingService } from '../services/pricingService';
import { PricePolicyFormModal } from '../components/PricePolicyFormModal';
import type { PricePolicyResult } from '../types/pricingTypes';

const ChinhSachGiaPage = () => {
    const navigate = useNavigate();
    // Master data: gate nút theo permission (khớp guard BE pricing.create/edit/delete/import)
    const { can } = usePermission();
    const canCreate = can('pricing', 'create');
    const canEdit = can('pricing', 'edit');
    const canDelete = can('pricing', 'delete');
    const canImport = can('pricing', 'import');
    const canExport = can('pricing', 'export');

    // Server-side pagination + search
    const [page, setPage] = useState(0);
    const [pageSize, setPageSize] = useState(10);
    const [search, setSearch] = useState('');
    const { data: pageData, isLoading } = usePagedPricePolicyList({
        page, size: pageSize, sortBy: 'createdAt', sortDir: 'desc',
        q: search || undefined,
    });
    const data = pageData?.items ?? [];
    const total = pageData?.total ?? 0;
    const { mutate: deleteFn, isPending: isDeleting } = useDeletePricePolicy();

    const [formOpen, setFormOpen] = useState(false);
    const [editTarget, setEditTarget] = useState<PricePolicyResult | null>(null);
    const [deleteTarget, setDeleteTarget] = useState<number | null>(null);
    const [selectedRows, setSelectedRows] = useState<PricePolicyResult[]>([]);
    const [bulkDeleteOpen, setBulkDeleteOpen] = useState(false);
    const [exportOpen, setExportOpen] = useState(false);

    const openCreate = () => { setEditTarget(null); setFormOpen(true); };
    const openEdit = (item: PricePolicyResult) => { setEditTarget(item); setFormOpen(true); };

    usePageShortcuts({ onCreate: canCreate ? openCreate : undefined });

    /** Thao tác của một chính sách giá — hiện trong menu chuột phải (theo permission). */
    const rowActions = (p: PricePolicyResult): RowAction[] => [
        { key: 'detail', label: 'Xem chi tiết', onClick: () => navigate(`/chinh-sach-gia/${p.id}`) },
        ...(canEdit ? [{ key: 'edit', label: 'Chỉnh sửa', onClick: () => openEdit(p) }] : []),
        ...(canDelete ? [{ key: 'delete', label: 'Xóa', danger: true, onClick: () => setDeleteTarget(p.id) }] : []),
    ];

    return (
        <div className="p-6 bg-bg-main">
            <PageHeaderSlot>
                <h1 className="text-lg font-semibold text-text-main truncate">Chính sách giá</h1>
                <div className="flex items-center gap-1.5">
                    {canImport && (
                        <ActionButton variant="secondary" icon={FiUpload} onClick={() => navigate('/chinh-sach-gia/nhap-file')}>
                            Nhập
                        </ActionButton>
                    )}
                    {canExport && (
                        <ActionButton variant="secondary" icon={FiDownload} onClick={() => setExportOpen(true)}>
                            Xuất{selectedRows.length > 0 ? ` (${selectedRows.length})` : ''}
                        </ActionButton>
                    )}
                    {canCreate && <CreateButton onClick={openCreate} />}
                    {canDelete && selectedRows.length > 0 && (
                        <ActionButton variant="danger" icon={FiTrash2} onClick={() => setBulkDeleteOpen(true)}>
                            Xóa ({selectedRows.length})
                        </ActionButton>
                    )}
                </div>
            </PageHeaderSlot>

            <div className="bg-white rounded-card p-4 shadow-sm">
                <DataTable
                    data={data}
                    columns={pricingColumns}
                    isLoading={isLoading}
                    emptyText="Chưa có chính sách giá nào"
                    onSelectionChange={setSelectedRows}
                    server={{
                        totalRows: total,
                        pageIndex: page,
                        pageSize,
                        onPageChange: setPage,
                        onPageSizeChange: setPageSize,
                        searchValue: search,
                        onSearchChange: (v) => { setSearch(v); setPage(0); },
                    }}
                    rowActions={rowActions}
                    onRowDoubleClick={(p) => navigate(`/chinh-sach-gia/${p.id}`)}
                />
            </div>

            <PricePolicyFormModal item={editTarget} open={formOpen} onClose={() => setFormOpen(false)} />

            {deleteTarget !== null && (
                <ConfirmModal
                    message="Bạn có chắc muốn xóa chính sách giá này?"
                    confirmLabel="Xóa"
                    confirmDanger
                    isLoading={isDeleting}
                    onConfirm={() => deleteFn(deleteTarget, { onSuccess: () => setDeleteTarget(null) })}
                    onCancel={() => setDeleteTarget(null)}
                />
            )}

            {bulkDeleteOpen && (
                <ConfirmModal
                    message={`Xóa ${selectedRows.length} chính sách giá đã chọn?`}
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
                columns={pricingExportColumns}
                rowCount={selectedRows.length > 0 ? selectedRows.length : total}
                onClose={() => setExportOpen(false)}
                onExport={async (keys, format) => {
                    // Không tick dòng → tải toàn bộ kết quả đang lọc từ server rồi xuất
                    const rows = selectedRows.length > 0
                        ? selectedRows
                        : (await pricingService.getList({ page: 0, size: 10000, sortBy: 'createdAt', sortDir: 'desc', q: search || undefined })).data.data.items;
                    exportRows(rows, pricingExportColumns, keys, format, 'chinh-sach-gia');
                    setExportOpen(false);
                }}
            />
        </div>
    );
};

export default ChinhSachGiaPage;
