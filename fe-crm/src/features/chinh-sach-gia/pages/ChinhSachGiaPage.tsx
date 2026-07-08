import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { FiTrash2, FiPlus } from 'react-icons/fi';
import type { RowAction } from '@/shared/types/table';
import { DataTable } from '@/shared/components/table/DataTable';
import { ConfirmModal } from '@/shared/components/ConfirmModal';
import { usePricePolicyList } from '../hooks/usePricePolicyList';
import { useDeletePricePolicy } from '../hooks/useDeletePricePolicy';
import { pricingColumns } from '../config/pricingColumns';
import { PricePolicyFormModal } from '../components/PricePolicyFormModal';
import type { PricePolicyResult } from '../types/pricingTypes';

const ChinhSachGiaPage = () => {
    const navigate = useNavigate();
    const { data = [], isLoading } = usePricePolicyList();
    const { mutate: deleteFn, isPending: isDeleting } = useDeletePricePolicy();

    const [formOpen, setFormOpen] = useState(false);
    const [editTarget, setEditTarget] = useState<PricePolicyResult | null>(null);
    const [deleteTarget, setDeleteTarget] = useState<number | null>(null);
    const [selectedRows, setSelectedRows] = useState<PricePolicyResult[]>([]);
    const [bulkDeleteOpen, setBulkDeleteOpen] = useState(false);

    const openCreate = () => { setEditTarget(null); setFormOpen(true); };
    const openEdit = (item: PricePolicyResult) => { setEditTarget(item); setFormOpen(true); };

    /** Thao tác của một chính sách giá — hiện trong menu chuột phải. */
    const rowActions = (p: PricePolicyResult): RowAction[] => [
        { key: 'detail', label: 'Xem chi tiết', onClick: () => navigate(`/chinh-sach-gia/${p.id}`) },
        { key: 'edit', label: 'Chỉnh sửa', onClick: () => openEdit(p) },
        { key: 'delete', label: 'Xóa', danger: true, onClick: () => setDeleteTarget(p.id) },
    ];

    return (
        <div className="p-6 bg-bg-main min-h-screen">
            <div className="flex items-center justify-between mb-4">
                <h1 className="text-xl font-semibold text-text-main">Chính sách giá</h1>
                <div className="flex items-center gap-2">
                    {selectedRows.length > 0 && (
                        <button
                            onClick={() => setBulkDeleteOpen(true)}
                            className="flex items-center gap-2 px-3 py-1.5 rounded-btn bg-red-50 text-danger text-md font-medium hover:bg-red-100"
                        >
                            <FiTrash2 size={14} />
                            Xóa đã chọn ({selectedRows.length})
                        </button>
                    )}
                    <button
                        onClick={openCreate}
                        className="flex items-center gap-1.5 px-3 py-1.5 rounded-btn bg-primary text-white text-md hover:opacity-90"
                    >
                        <FiPlus size={14} />
                        Tạo mới
                    </button>
                </div>
            </div>

            <div className="bg-white rounded-card p-4 shadow-sm">
                <DataTable
                    data={data}
                    columns={pricingColumns}
                    isLoading={isLoading}
                    emptyText="Chưa có chính sách giá nào"
                    onSelectionChange={setSelectedRows}
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
        </div>
    );
};

export default ChinhSachGiaPage;
