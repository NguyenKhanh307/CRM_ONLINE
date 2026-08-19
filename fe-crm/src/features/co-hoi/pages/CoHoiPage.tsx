import { useState, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import { FiTrash2, FiUpload, FiShare2, FiDownload, FiSliders, FiColumns } from 'react-icons/fi';
import type { ColumnDef } from '@tanstack/react-table';
import { PageHeaderSlot } from '@/shared/components/layout/PageHeaderSlot';
import { ActionButton } from '@/shared/components/ActionButton';
import { CreateButton } from '@/shared/components/CreateButton';
import { usePageShortcuts } from '@/shared/keyboard/PageShortcutsProvider';
import { usePermission } from '@/core/permissions/usePermission';
import { DataTable } from '@/shared/components/table/DataTable';
import { RecordItemsPanel } from '@/shared/components/table/RecordItemsPanel';
import { getLineItemPanelColumns } from '@/shared/components/table/lineItemPanelColumns';
import { useProductMap } from '@/features/san-pham/hooks/useProductMap';
import { ConfirmModal } from '@/shared/components/ConfirmModal';
import { HandoverModal } from '@/shared/components/HandoverModal';
import { ExportModal } from '@/shared/components/export/ExportModal';
import { exportRows } from '@/shared/components/export/exportFile';
import { usePagedOpportunityList } from '../hooks/usePagedOpportunityList';
import { opportunityService } from '../services/opportunityService';
import { useOpportunityItems } from '../hooks/useOpportunityItems';
import { useDeleteOpportunity } from '../hooks/useDeleteOpportunity';
import { useHandoverBulkOpportunity } from '../hooks/useHandoverBulkOpportunity';
import { getOpportunityColumns } from '../config/opportunityColumns';
import { opportunityExportColumns } from '../config/opportunityExportColumns';
import { OpportunityEditModal } from '../components/OpportunityEditModal';
import type { OpportunityItemResult, OpportunityResult } from '../types/opportunityTypes';

// tag lọc nhanh — hằng ngoài component để giữ ref ổn định giữa các lần render
const QUICK_FILTERS = [
    { id: 'open', label: 'Đang mở',  field: 'status', value: 'open' },
    { id: 'won',  label: 'Đã thắng', field: 'status', value: 'won' },
    { id: 'lost', label: 'Đã thua',  field: 'status', value: 'lost' },
];

const CoHoiPage = () => {
    const navigate = useNavigate();
    const { can } = usePermission();
    const goCreate = () => navigate('/co-hoi/them-moi');
    usePageShortcuts({ onCreate: can('opportunity', 'create') ? goCreate : undefined });
    // server-side pagination + search + tag lọc nhanh
    const [page, setPage] = useState(0);
    const [pageSize, setPageSize] = useState(10);
    const [search, setSearch] = useState('');
    const [quickStatus, setQuickStatus] = useState<string | null>(null);
    const { data: pageData, isLoading } = usePagedOpportunityList({
        page, size: pageSize, sortBy: 'createdAt', sortDir: 'desc',
        q: search || undefined, status: quickStatus || undefined,
    });
    const data = pageData?.items ?? [];
    const total = pageData?.total ?? 0;
    const { mutate: deleteFn, isPending: isDeleting } = useDeleteOpportunity();
    const { mutate: handoverFn, isPending: isHandovering } = useHandoverBulkOpportunity();

    const [editTarget, setEditTarget] = useState<OpportunityResult | null>(null);
    const [deleteTarget, setDeleteTarget] = useState<number | null>(null);
    const [selectedRows, setSelectedRows] = useState<OpportunityResult[]>([]);
    const [bulkDeleteOpen, setBulkDeleteOpen] = useState(false);
    const [handoverOpen, setHandoverOpen] = useState(false);
    const [exportOpen, setExportOpen] = useState(false);
    const [selectedRecord, setSelectedRecord] = useState<OpportunityResult | null>(null);

    const productMap = useProductMap();
    const { data: items = [], isLoading: itemsLoading } = useOpportunityItems(selectedRecord?.id ?? null);
    const itemColumns = useMemo(() => getLineItemPanelColumns<OpportunityItemResult>(productMap), [productMap]);


    const columns = useMemo<ColumnDef<OpportunityResult>[]>(() => getOpportunityColumns(), []);

    return (
        <div className="p-6 bg-bg-main">
            <PageHeaderSlot>
                <h1 className="text-lg font-semibold text-text-main truncate">Cơ hội</h1>
                <div className="flex items-center gap-1.5">
                    <ActionButton variant="secondary" icon={FiColumns} onClick={() => navigate('/co-hoi/kanban')}>
                        Bảng
                    </ActionButton>
                    <ActionButton variant="secondary" icon={FiSliders} onClick={() => navigate('/co-hoi/pipeline')}>
                        Giai đoạn
                    </ActionButton>
                    {can('opportunity', 'import') && (
                        <ActionButton variant="secondary" icon={FiUpload} onClick={() => navigate('/co-hoi/nhap-file')}>
                            Nhập
                        </ActionButton>
                    )}
                    {can('opportunity', 'export') && (
                        <ActionButton variant="secondary" icon={FiDownload} onClick={() => setExportOpen(true)}>
                            Xuất{selectedRows.length > 0 ? ` (${selectedRows.length})` : ''}
                        </ActionButton>
                    )}
                    {can('opportunity', 'create') && <CreateButton onClick={goCreate} />}
                    {selectedRows.length > 0 && (
                        <>
                            {can('opportunity', 'handover') && (
                                <ActionButton variant="info" icon={FiShare2} onClick={() => setHandoverOpen(true)}>
                                    Bàn giao ({selectedRows.length})
                                </ActionButton>
                            )}
                            {can('opportunity', 'delete') && (
                                <ActionButton variant="danger" icon={FiTrash2} onClick={() => setBulkDeleteOpen(true)}>
                                    Xóa ({selectedRows.length})
                                </ActionButton>
                            )}
                        </>
                    )}
                </div>
            </PageHeaderSlot>
            <div className="bg-white rounded-card p-4 shadow-sm">
                <DataTable
                    data={data}
                    columns={columns}
                    isLoading={isLoading}
                    emptyText="Chưa có cơ hội nào"
                    onSelectionChange={setSelectedRows}
                    onRowSelect={setSelectedRecord}
                    visibleRows={7}
                    autoSelectFirstRow
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
                    onRowDoubleClick={(o) => navigate(`/co-hoi/${o.id}`)}
                    rowActions={(o) => [
                        { key: 'detail', label: 'Xem chi tiết', onClick: () => navigate(`/co-hoi/${o.id}`) },
                        ...(can('quotation', 'create')
                            ? [{ key: 'quote', label: 'Tạo báo giá từ cơ hội', onClick: () => navigate(`/bao-gia/them-moi?fromOpportunity=${o.id}`) }]
                            : []),
                        ...(can('opportunity', 'edit')
                            ? [{ key: 'edit', label: 'Chỉnh sửa', onClick: () => setEditTarget(o) }]
                            : []),
                        ...(can('opportunity', 'delete')
                            ? [{ key: 'delete', label: 'Xóa', danger: true, onClick: () => setDeleteTarget(o.id) }]
                            : []),
                    ]}
                    quickFilters={QUICK_FILTERS}
                />
            </div>

            <div className="bg-white rounded-card p-4 shadow-sm mt-4">
                <RecordItemsPanel
                    title={selectedRecord ? `${selectedRecord.code} — ${selectedRecord.name}` : undefined}
                    columns={itemColumns}
                    rows={items}
                    isLoading={itemsLoading}
                />
            </div>

            {deleteTarget !== null && (
                <ConfirmModal
                    message="Bạn có chắc muốn xóa cơ hội này?"
                    confirmLabel="Xóa"
                    confirmDanger
                    isLoading={isDeleting}
                    onConfirm={() => deleteFn(deleteTarget, { onSuccess: () => setDeleteTarget(null) })}
                    onCancel={() => setDeleteTarget(null)}
                />
            )}

            {bulkDeleteOpen && (
                <ConfirmModal
                    message={`Xóa ${selectedRows.length} cơ hội đã chọn?`}
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
                columns={opportunityExportColumns}
                rowCount={selectedRows.length > 0 ? selectedRows.length : total}
                onClose={() => setExportOpen(false)}
                onExport={async (keys) => {
                    // không tick dòng -> tải toàn bộ kết quả đang lọc từ server rồi xuất
                    const rows = selectedRows.length > 0
                        ? selectedRows
                        : (await opportunityService.getList({ page: 0, size: 10000, sortBy: 'createdAt', sortDir: 'desc', q: search || undefined, status: quickStatus || undefined })).data.data.items;
                    exportRows(rows, opportunityExportColumns, keys, 'co-hoi');
                    setExportOpen(false);
                }}
            />

            <OpportunityEditModal item={editTarget} onClose={() => setEditTarget(null)} />

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

export default CoHoiPage;
