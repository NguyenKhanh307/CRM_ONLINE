import { useState } from 'react';
import { FiRefreshCw } from 'react-icons/fi';
import { PageHeaderSlot } from '@/shared/components/layout/PageHeaderSlot';
import { DataTable } from '@/shared/components/table/DataTable';
import { ConfirmModal } from '@/shared/components/ConfirmModal';
import { trashColumns } from '@/features/thung-rac/config/trashColumns';
import { useDeletedItems, useRestore, usePurge } from '@/features/thung-rac/hooks/useTrash';
import { TRASH_MODULE_LABELS } from '@/features/thung-rac/types/thungRacTypes';
import type { TrashModule, DeletedItemRow } from '@/features/thung-rac/types/thungRacTypes';
import type { RowAction } from '@/shared/types/table';
import { usePermission } from '@/core/permissions/usePermission';

type ConfirmState = { type: 'restore' | 'purge'; id: number } | null;

/** Map slug tab thùng rác → key module permission (để gate Khôi phục/Xóa vĩnh viễn theo `<m>.delete`). */
const TRASH_TO_PERM: Record<TrashModule, string> = {
    'chien-dich': 'campaign',
    'tiem-nang': 'lead',
    'lien-he': 'contact',
    'khach-hang': 'customer',
    'co-hoi': 'opportunity',
    'bao-gia': 'quotation',
    'don-hang': 'order',
    'hoa-don': 'invoice',
    'san-pham': 'product',
};

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
    const { can } = usePermission();
    const canManage = can(TRASH_TO_PERM[module], 'delete');

    /** Thao tác của một bản ghi đã xóa — hiện trong menu chuột phải (theo quyền xóa của phân hệ). */
    const rowActions = (row: DeletedItemRow): RowAction[] => canManage
        ? [
            { key: 'restore', label: 'Khôi phục', onClick: () => onAction('restore', row.id) },
            { key: 'purge', label: 'Xóa vĩnh viễn', danger: true, onClick: () => onAction('purge', row.id) },
        ]
        : [];

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
            columns={trashColumns}
            emptyText="Không có bản ghi nào trong thùng rác"
            rowActions={rowActions}
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
        <div className="p-6 bg-bg-main">
            <PageHeaderSlot>
                <h1 className="text-lg font-semibold text-text-main truncate">Thùng rác</h1>
            </PageHeaderSlot>

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
