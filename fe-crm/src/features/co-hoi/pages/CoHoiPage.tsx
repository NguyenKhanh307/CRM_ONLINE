import { useState, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import { FiTrash2, FiUpload, FiShare2, FiDownload, FiSliders } from 'react-icons/fi';
import type { ColumnDef } from '@tanstack/react-table';
import { PageHeaderSlot } from '@/shared/components/layout/PageHeaderSlot';
import { ActionButton } from '@/shared/components/ActionButton';
import { CreateButton } from '@/shared/components/CreateButton';
import { usePageShortcuts } from '@/shared/keyboard/PageShortcutsProvider';
import { DataTable } from '@/shared/components/table/DataTable';
import { RecordItemsPanel } from '@/shared/components/table/RecordItemsPanel';
import { getLineItemPanelColumns } from '@/shared/components/table/lineItemPanelColumns';
import { useProductMap } from '@/features/san-pham/hooks/useProductMap';
import { ConfirmModal } from '@/shared/components/ConfirmModal';
import { HandoverModal } from '@/shared/components/HandoverModal';
import { ExportModal } from '@/shared/components/export/ExportModal';
import { exportRows } from '@/shared/components/export/exportFile';
import { useAlert } from '@/shared/alert/useAlert';
import { useOpportunityList } from '../hooks/useOpportunityList';
import { useOpportunityItems } from '../hooks/useOpportunityItems';
import { useDeleteOpportunity } from '../hooks/useDeleteOpportunity';
import { useHandoverBulkOpportunity } from '../hooks/useHandoverBulkOpportunity';
import { useCreateQuotationFromOpportunity } from '../hooks/useCreateQuotationFromOpportunity';
import { getOpportunityColumns } from '../config/opportunityColumns';
import { opportunityExportColumns } from '../config/opportunityExportColumns';
import { OpportunityEditModal } from '../components/OpportunityEditModal';
import type { OpportunityItemResult, OpportunityResult } from '../types/opportunityTypes';

/** Tag lọc nhanh — hằng ngoài component để giữ ref ổn định giữa các lần render. */
const QUICK_FILTERS = [
    { id: 'open', label: 'Đang mở',  field: 'status', value: 'open' },
    { id: 'won',  label: 'Đã thắng', field: 'status', value: 'won' },
    { id: 'lost', label: 'Đã thua',  field: 'status', value: 'lost' },
];

const CoHoiPage = () => {
    const navigate = useNavigate();
    const goCreate = () => navigate('/co-hoi/them-moi');
    usePageShortcuts({ onCreate: goCreate });
    const { showAlert } = useAlert();
    const { data = [], isLoading } = useOpportunityList();
    const { mutate: deleteFn, isPending: isDeleting } = useDeleteOpportunity();
    const { mutate: handoverFn, isPending: isHandovering } = useHandoverBulkOpportunity();
    const { mutateAsync: createQuoteFn } = useCreateQuotationFromOpportunity();

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

    const rowsToExport = selectedRows.length > 0 ? selectedRows : data;

    /** Tạo báo giá từ cơ hội (clone) rồi điều hướng sang danh sách báo giá. */
    const createQuote = async (opportunityId: number) => {
        try {
            await createQuoteFn(opportunityId);
            showAlert('Đã tạo báo giá từ cơ hội');
            navigate('/bao-gia');
        } catch (err) {
            const msg = (err as { response?: { data?: { message?: string } } })?.response?.data?.message
                ?? 'Không tạo được báo giá từ cơ hội';
            showAlert(msg);
        }
    };

    const columns = useMemo<ColumnDef<OpportunityResult>[]>(() => getOpportunityColumns(), []);

    return (
        <div className="p-6 bg-bg-main">
            <PageHeaderSlot>
                <h1 className="text-lg font-semibold text-text-main truncate">Cơ hội</h1>
                <div className="flex items-center gap-1.5">
                    <ActionButton variant="secondary" icon={FiSliders} onClick={() => navigate('/co-hoi/pipeline')}>
                        Giai đoạn
                    </ActionButton>
                    <ActionButton variant="secondary" icon={FiUpload} onClick={() => navigate('/co-hoi/nhap-file')}>
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
                    emptyText="Chưa có cơ hội nào"
                    onSelectionChange={setSelectedRows}
                    onRowSelect={setSelectedRecord}
                    visibleRows={7}
                    autoSelectFirstRow
                    onRowDoubleClick={(o) => setEditTarget(o)}
                    rowActions={(o) => [
                        { key: 'quote', label: 'Tạo báo giá từ cơ hội', onClick: () => createQuote(o.id) },
                        { key: 'edit', label: 'Chỉnh sửa', onClick: () => setEditTarget(o) },
                        { key: 'delete', label: 'Xóa', danger: true, onClick: () => setDeleteTarget(o.id) },
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
                rowCount={rowsToExport.length}
                onClose={() => setExportOpen(false)}
                onExport={(keys, format) => {
                    exportRows(rowsToExport, opportunityExportColumns, keys, format, 'co-hoi');
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
