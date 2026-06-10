import { useState } from 'react';
import { FiRefreshCw, FiRotateCcw, FiTrash2 } from 'react-icons/fi';
import { DataTable } from '@/shared/components/table/DataTable';
import { ConfirmModal } from '@/shared/components/ConfirmModal';
import { trashColumns } from '@/features/thung-rac/config/trashColumns';
import { useDeletedItems, useRestore, usePurge } from '@/features/thung-rac/hooks/useTrash';
import { TRASH_MODULE_LABELS } from '@/features/thung-rac/types/thungRacTypes';
import type { TrashModule, DeletedItemRow } from '@/features/thung-rac/types/thungRacTypes';
import type { ColumnDef } from '@tanstack/react-table';

type ConfirmState = { type: 'restore' | 'purge'; id: number } | null;

/** Nội dung tab — load data theo module đang chọn. */
const TrashTabContent = ({
    module,
    onAction,
}: {
    module: TrashModule;
    onAction: (type: 'restore' | 'purge', id: number) => void;
}) => {
    const { data, isLoading } = useDeletedItems(module);
    const items = data?.items ?? [];

    const actionColumn: ColumnDef<DeletedItemRow> = {
        id: 'actions',
        header: 'Thao tác',
        size: 160,
        cell: ({ row }) => (
            <div className="flex items-center gap-2">
                <button
                    onClick={() => onAction('restore', row.original.id)}
                    className="flex items-center gap-1 px-2 py-1 text-sm bg-green-50 text-green-700 border border-green-200 rounded-btn hover:bg-green-100 transition-colors"
                >
                    <FiRotateCcw size={12} />
                    Khôi phục
                </button>
                <button
                    onClick={() => onAction('purge', row.original.id)}
                    className="flex items-center gap-1 px-2 py-1 text-sm bg-red-50 text-danger border border-red-200 rounded-btn hover:bg-red-100 transition-colors"
                >
                    <FiTrash2 size={12} />
                    Xóa
                </button>
            </div>
        ),
    };

    if (isLoading) {
        return (
            <div className="flex justify-center items-center py-16 text-gray-400 text-md">
                Đang tải...
            </div>
        );
    }

    return (
        <DataTable
            data={items}
            columns={[...trashColumns, actionColumn]}
            emptyText="Không có bản ghi nào trong thùng rác"
        />
    );
};

/** Trang thùng rác — hiển thị bản ghi đã xóa theo từng phân hệ, hỗ trợ khôi phục và xóa vĩnh viễn. */
const ThungRacPage = () => {
    const [activeModule, setActiveModule] = useState<TrashModule>('tiem-nang');
    const [confirm, setConfirm] = useState<ConfirmState>(null);
    const [refreshKey, setRefreshKey] = useState(0);

    const restoreMutation = useRestore(activeModule);
    const purgeMutation = usePurge(activeModule);

    const handleAction = (type: 'restore' | 'purge', id: number) => {
        setConfirm({ type, id });
    };

    const handleConfirm = () => {
        if (!confirm) return;
        if (confirm.type === 'restore') {
            restoreMutation.mutate(confirm.id, { onSuccess: () => setConfirm(null) });
        } else {
            purgeMutation.mutate(confirm.id, { onSuccess: () => setConfirm(null) });
        }
    };

    const isSubmitting = restoreMutation.isPending || purgeMutation.isPending;

    return (
        <div className="p-6 bg-bg-main min-h-screen">
            {/* Tabs */}
            <div className="flex items-center gap-1 mb-4 border-b border-gray-200">
                {TRASH_MODULE_LABELS.map(m => (
                    <button
                        key={m.id}
                        onClick={() => setActiveModule(m.id)}
                        className={`px-4 py-2 text-md font-medium transition-colors border-b-2 -mb-px ${
                            activeModule === m.id
                                ? 'border-primary text-primary'
                                : 'border-transparent text-gray-500 hover:text-gray-700'
                        }`}
                    >
                        {m.label}
                    </button>
                ))}
                <button
                    className="ml-auto flex items-center justify-center w-8 h-8 bg-white border border-gray-300 rounded-btn text-gray-500 hover:border-primary hover:text-primary transition-colors mb-1"
                    onClick={() => setRefreshKey(k => k + 1)}
                    title="Làm mới"
                >
                    <FiRefreshCw size={14} />
                </button>
            </div>

            {/* Table */}
            <div className="bg-white rounded-card shadow-sm" key={`${activeModule}-${refreshKey}`}>
                <TrashTabContent module={activeModule} onAction={handleAction} />
            </div>

            {/* Confirm Modal */}
            {confirm && (
                <ConfirmModal
                    message={
                        confirm.type === 'restore'
                            ? 'Bạn có chắc muốn khôi phục bản ghi này?'
                            : 'Bạn có chắc muốn xóa vĩnh viễn bản ghi này? Hành động này không thể hoàn tác.'
                    }
                    confirmLabel={confirm.type === 'restore' ? 'Khôi phục' : 'Xóa vĩnh viễn'}
                    confirmDanger={confirm.type === 'purge'}
                    onConfirm={handleConfirm}
                    onCancel={() => setConfirm(null)}
                    isLoading={isSubmitting}
                />
            )}
        </div>
    );
};

export default ThungRacPage;
