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
import { usePagedCampaignList } from '../hooks/usePagedCampaignList';
import { campaignService } from '../services/campaignService';
import { useDeleteCampaign } from '../hooks/useDeleteCampaign';
import { useHandoverBulkCampaign } from '../hooks/useHandoverBulkCampaign';
import { useCampaignWorkflow, type CampaignAction } from '../hooks/useCampaignWorkflow';
import { getCampaignColumns } from '../config/campaignColumns';
import { campaignExportColumns } from '../config/campaignExportColumns';
import { CampaignEditModal } from '../components/CampaignEditModal';
import type { CampaignResult } from '../types/campaignTypes';

/** Tag lọc nhanh — hằng ngoài component để giữ ref ổn định giữa các lần render. */
const QUICK_FILTERS = [
    { id: 'draft',     label: 'Nháp',        field: 'status', value: 'draft' },
    { id: 'running',   label: 'Đang chạy',   field: 'status', value: 'running' },
    { id: 'paused',    label: 'Tạm dừng',    field: 'status', value: 'paused' },
    { id: 'completed', label: 'Hoàn tất',    field: 'status', value: 'completed' },
];

const ChienDichPage = () => {
    const navigate = useNavigate();
    const goCreate = () => navigate('/chien-dich/them-moi');
    usePageShortcuts({ onCreate: goCreate });
    const { showAlert } = useAlert();
    // Server-side pagination + search + tag lọc nhanh
    const [page, setPage] = useState(0);
    const [pageSize, setPageSize] = useState(10);
    const [search, setSearch] = useState('');
    const [quickStatus, setQuickStatus] = useState<string | null>(null);
    const { data: pageData, isLoading } = usePagedCampaignList({
        page, size: pageSize, sortBy: 'createdAt', sortDir: 'desc',
        q: search || undefined, status: quickStatus || undefined,
    });
    const data = pageData?.items ?? [];
    const total = pageData?.total ?? 0;
    const { mutate: deleteFn, isPending: isDeleting } = useDeleteCampaign();
    const { mutate: handoverFn, isPending: isHandovering } = useHandoverBulkCampaign();
    const { mutate: workflowFn } = useCampaignWorkflow();

    const [editTarget, setEditTarget] = useState<CampaignResult | null>(null);
    const [deleteTarget, setDeleteTarget] = useState<number | null>(null);
    const [selectedRows, setSelectedRows] = useState<CampaignResult[]>([]);
    const [bulkDeleteOpen, setBulkDeleteOpen] = useState(false);
    const [handoverOpen, setHandoverOpen] = useState(false);
    const [exportOpen, setExportOpen] = useState(false);


    const runAction = (id: number, action: CampaignAction) =>
        workflowFn({ id, action }, {
            onError: (err: unknown) => {
                const msg = (err as { response?: { data?: { message?: string } } })?.response?.data?.message
                    ?? 'Không thực hiện được hành động';
                showAlert(msg);
            },
        });

    const columns = useMemo<ColumnDef<CampaignResult>[]>(() => getCampaignColumns(), []);

    /** Thao tác của một chiến dịch — hiện trong menu chuột phải. */
    const rowActions = (c: CampaignResult): RowAction[] => [
        { key: 'detail', label: 'Chi tiết', onClick: () => navigate(`/chien-dich/${c.id}`) },
        ...(c.status === 'draft' || c.status === 'scheduled' || c.status === 'paused'
            ? [{ key: 'start', label: 'Bắt đầu chạy', onClick: () => runAction(c.id, 'start') }]
            : []),
        ...(c.status === 'running'
            ? [{ key: 'pause', label: 'Tạm dừng', onClick: () => runAction(c.id, 'pause') }]
            : []),
        ...(c.status === 'running' || c.status === 'paused'
            ? [{ key: 'complete', label: 'Hoàn tất', onClick: () => runAction(c.id, 'complete') }]
            : []),
        ...(c.status !== 'completed' && c.status !== 'cancelled'
            ? [{ key: 'cancel', label: 'Hủy', onClick: () => runAction(c.id, 'cancel') }]
            : []),
        { key: 'edit', label: 'Chỉnh sửa', onClick: () => setEditTarget(c) },
        { key: 'delete', label: 'Xóa', danger: true, onClick: () => setDeleteTarget(c.id) },
    ];

    return (
        <div className="p-6 bg-bg-main">
            <PageHeaderSlot>
                <h1 className="text-lg font-semibold text-text-main truncate">Chiến dịch</h1>
                <div className="flex items-center gap-1.5">
                    <ActionButton variant="secondary" icon={FiUpload} onClick={() => navigate('/chien-dich/nhap-file')}>
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
                    emptyText="Chưa có Chiến dịch nào"
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
                    onRowDoubleClick={(c) => navigate(`/chien-dich/${c.id}`)}
                    quickFilters={QUICK_FILTERS}
                />
            </div>

            {deleteTarget !== null && (
                <ConfirmModal
                    message="Bạn có chắc muốn xóa Chiến dịch này?"
                    confirmLabel="Xóa" confirmDanger isLoading={isDeleting}
                    onConfirm={() => deleteFn(deleteTarget, { onSuccess: () => setDeleteTarget(null) })}
                    onCancel={() => setDeleteTarget(null)}
                />
            )}

            {bulkDeleteOpen && (
                <ConfirmModal
                    message={`Xóa ${selectedRows.length} Chiến dịch đã chọn?`}
                    confirmLabel="Xóa tất cả" confirmDanger isLoading={isDeleting}
                    onConfirm={() => { Promise.all(selectedRows.map(r => deleteFn(r.id))); setBulkDeleteOpen(false); }}
                    onCancel={() => setBulkDeleteOpen(false)}
                />
            )}

            <ExportModal
                open={exportOpen}
                columns={campaignExportColumns}
                rowCount={selectedRows.length > 0 ? selectedRows.length : total}
                onClose={() => setExportOpen(false)}
                onExport={async (keys, format) => {
                    // Không tick dòng → tải toàn bộ kết quả đang lọc từ server rồi xuất
                    const rows = selectedRows.length > 0
                        ? selectedRows
                        : (await campaignService.getList({ page: 0, size: 10000, sortBy: 'createdAt', sortDir: 'desc', q: search || undefined, status: quickStatus || undefined })).data.data.items;
                    exportRows(rows, campaignExportColumns, keys, format, 'chien-dich');
                    setExportOpen(false);
                }}
            />

            <CampaignEditModal item={editTarget} onClose={() => setEditTarget(null)} />

            <HandoverModal
                open={handoverOpen} count={selectedRows.length} isLoading={isHandovering}
                onClose={() => setHandoverOpen(false)}
                onConfirm={(toUserId, reason) =>
                    handoverFn({ ids: selectedRows.map(r => r.id), toUserId, reason },
                        { onSuccess: () => { setHandoverOpen(false); setSelectedRows([]); } })}
            />
        </div>
    );
};

export default ChienDichPage;
