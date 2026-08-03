import { useState, useMemo, useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { FiTrash2, FiUpload, FiShare2, FiDownload } from 'react-icons/fi';
import type { ColumnDef } from '@tanstack/react-table';
import type { RowAction } from '@/shared/types/table';
import { PageHeaderSlot } from '@/shared/components/layout/PageHeaderSlot';
import { ActionButton } from '@/shared/components/ActionButton';
import { CreateButton } from '@/shared/components/CreateButton';
import { usePageShortcuts } from '@/shared/keyboard/PageShortcutsProvider';
import { usePermission } from '@/core/permissions/usePermission';
import { DataTable } from '@/shared/components/table/DataTable';
import { ConfirmModal } from '@/shared/components/ConfirmModal';
import { ReasonModal } from '@/shared/components/ReasonModal';
import { HandoverModal } from '@/shared/components/HandoverModal';
import { ExportModal } from '@/shared/components/export/ExportModal';
import { exportRows } from '@/shared/components/export/exportFile';
import { useAlert } from '@/shared/alert/useAlert';
import { usePagedLeadList } from '../hooks/usePagedLeadList';
import { leadService } from '../services/leadService';
import { useDeleteLead } from '../hooks/useDeleteLead';
import { useHandoverBulkLead } from '../hooks/useHandoverBulkLead';
import { useLeadWorkflow, type LeadAction } from '../hooks/useLeadWorkflow';
import { getLeadColumns } from '../config/leadColumns';
import { leadExportColumns } from '../config/leadExportColumns';
import { LeadEditModal } from '../components/LeadEditModal';
import { ConvertLeadModal } from '../components/ConvertLeadModal';
import type { LeadResult } from '../types/leadTypes';

// tag lọc nhanh — hằng ngoài component để giữ ref ổn định giữa các lần render
const QUICK_FILTERS = [
    { id: 'new',        label: 'Mới',         field: 'status', value: 'new' },
    { id: 'contacting', label: 'Đang liên hệ', field: 'status', value: 'contacting' },
    { id: 'qualified',  label: 'Đủ điều kiện', field: 'status', value: 'qualified' },
    { id: 'converted',  label: 'Đã chuyển đổi',field: 'status', value: 'converted' },
];

const TiemNangPage = () => {
    const navigate = useNavigate();
    const { can } = usePermission();
    const goCreate = () => navigate('/tiem-nang/them-moi');
    usePageShortcuts({ onCreate: can('lead', 'create') ? goCreate : undefined });
    const [searchParams] = useSearchParams();
    const focusId = searchParams.get('focus');
    const { showAlert } = useAlert();
    // state phân trang + tìm kiếm + lọc nhanh (server-side)
    const [page, setPage] = useState(0);
    const [pageSize, setPageSize] = useState(10);
    const [search, setSearch] = useState('');
    const [quickStatus, setQuickStatus] = useState<string | null>(null);
    const { data: pageData, isLoading } = usePagedLeadList({
        page, size: pageSize, sortBy: 'createdAt', sortDir: 'desc',
        q: search || undefined, status: quickStatus || undefined,
    });
    const data = pageData?.items ?? [];
    const total = pageData?.total ?? 0;
    const { mutate: deleteFn, isPending: isDeleting } = useDeleteLead();
    const { mutate: handoverFn, isPending: isHandovering } = useHandoverBulkLead();
    const { mutate: workflowFn } = useLeadWorkflow();

    const [editTarget, setEditTarget] = useState<LeadResult | null>(null);
    const [deleteTarget, setDeleteTarget] = useState<number | null>(null);
    const [loseTarget, setLoseTarget] = useState<number | null>(null);
    const [convertTarget, setConvertTarget] = useState<LeadResult | null>(null);
    const [selectedRows, setSelectedRows] = useState<LeadResult[]>([]);
    const [bulkDeleteOpen, setBulkDeleteOpen] = useState(false);
    const [handoverOpen, setHandoverOpen] = useState(false);
    const [exportOpen, setExportOpen] = useState(false);

    // bấm thông báo (?focus=) -> tra code bản ghi rồi search theo code để bản ghi hiện ra ở trang 1
    useEffect(() => {
        if (!focusId) return;
        leadService.getById(Number(focusId))
            .then(r => { const code = r.data.data.code; if (code) { setSearch(code); setPage(0); } })
            .catch(() => {});
    }, [focusId]);

    // hàm chạy hành động chuyển trạng thái tiềm năng — convert thành công thì điều hướng sang
    // Cơ hội (KH + LH + Cơ hội vừa được tạo)
    const runAction = (id: number, action: LeadAction, reason?: string, customerId?: number | null) =>
        workflowFn({ id, action, reason, customerId }, {
            onSuccess: () => {
                if (action === 'convert') {
                    setConvertTarget(null);
                    showAlert(customerId
                        ? 'Đã gắn Liên hệ và Cơ hội vào khách hàng đã có'
                        : 'Đã tạo Khách hàng, Liên hệ và Cơ hội từ tiềm năng');
                    navigate('/co-hoi');
                } else if (action === 'claim') {
                    showAlert('Bạn đã nhận chăm sóc tiềm năng này');
                }
            },
            onError: (err: unknown) => {
                const msg = (err as { response?: { data?: { message?: string } } })?.response?.data?.message
                    ?? 'Không thực hiện được hành động';
                showAlert(msg);
            },
        });

    const columns = useMemo<ColumnDef<LeadResult>[]>(() => getLeadColumns(), []);

    // hàm dựng menu thao tác chuột phải cho một dòng tiềm năng
    const rowActions = (l: LeadResult): RowAction[] => {
        const isOpen = l.status !== 'converted' && l.status !== 'lost';
        return [
            { key: 'detail', label: 'Xem chi tiết', onClick: () => navigate(`/tiem-nang/${l.id}`) },
            ...(l.ownerId == null && isOpen && can('lead', 'edit')
                ? [{ key: 'claim', label: 'Nhận chăm sóc', onClick: () => runAction(l.id, 'claim') }]
                : []),
            ...(l.status === 'new' || l.status === 'contacting'
                ? [{ key: 'qualify', label: 'Đủ điều kiện', onClick: () => runAction(l.id, 'qualify') }]
                : []),
            ...(l.status === 'qualified' && can('lead', 'convert')
                ? [{ key: 'convert', label: 'Chuyển đổi', onClick: () => setConvertTarget(l) }]
                : []),
            ...(isOpen
                ? [{ key: 'lose', label: 'Đánh mất', onClick: () => setLoseTarget(l.id) }]
                : []),
            ...(l.status !== 'converted' && can('lead', 'edit')
                ? [{ key: 'edit', label: 'Chỉnh sửa', onClick: () => setEditTarget(l) }]
                : []),
            ...(can('lead', 'delete')
                ? [{ key: 'delete', label: 'Xóa', danger: true, onClick: () => setDeleteTarget(l.id) }]
                : []),
        ];
    };

    return (
        <div className="p-6 bg-bg-main">
            <PageHeaderSlot>
                <h1 className="text-lg font-semibold text-text-main truncate">Tiềm năng</h1>
                <div className="flex items-center gap-1.5">
                    {can('lead', 'import') && (
                        <ActionButton variant="secondary" icon={FiUpload} onClick={() => navigate('/tiem-nang/nhap-file')}>
                            Nhập
                        </ActionButton>
                    )}
                    {can('lead', 'export') && (
                        <ActionButton variant="secondary" icon={FiDownload} onClick={() => setExportOpen(true)}>
                            Xuất{selectedRows.length > 0 ? ` (${selectedRows.length})` : ''}
                        </ActionButton>
                    )}
                    {can('lead', 'create') && <CreateButton onClick={goCreate} />}
                    {selectedRows.length > 0 && (
                        <>
                            {can('lead', 'handover') && (
                                <ActionButton variant="info" icon={FiShare2} onClick={() => setHandoverOpen(true)}>
                                    Bàn giao ({selectedRows.length})
                                </ActionButton>
                            )}
                            {can('lead', 'delete') && (
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
                    emptyText="Chưa có tiềm năng nào"
                    onSelectionChange={setSelectedRows}
                    focusId={focusId}
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
                    onRowDoubleClick={(l) => navigate(`/tiem-nang/${l.id}`)}
                    quickFilters={QUICK_FILTERS}
                />
            </div>

            {deleteTarget !== null && (
                <ConfirmModal
                    message="Bạn có chắc muốn xóa tiềm năng này?"
                    confirmLabel="Xóa"
                    confirmDanger
                    isLoading={isDeleting}
                    onConfirm={() => deleteFn(deleteTarget, { onSuccess: () => setDeleteTarget(null) })}
                    onCancel={() => setDeleteTarget(null)}
                />
            )}

            {bulkDeleteOpen && (
                <ConfirmModal
                    message={`Xóa ${selectedRows.length} tiềm năng đã chọn?`}
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
                columns={leadExportColumns}
                rowCount={selectedRows.length > 0 ? selectedRows.length : total}
                onClose={() => setExportOpen(false)}
                onExport={async (keys, format) => {
                    // không tick dòng nào -> tải toàn bộ kết quả đang lọc từ server rồi xuất
                    const rows = selectedRows.length > 0
                        ? selectedRows
                        : (await leadService.getList({ page: 0, size: 10000, sortBy: 'createdAt', sortDir: 'desc', q: search || undefined, status: quickStatus || undefined })).data.data.items;
                    exportRows(rows, leadExportColumns, keys, format, 'tiem-nang');
                    setExportOpen(false);
                }}
            />

            {convertTarget && (
                <ConvertLeadModal
                    lead={convertTarget}
                    onCancel={() => setConvertTarget(null)}
                    onConfirm={(customerId) => runAction(convertTarget.id, 'convert', undefined, customerId)}
                />
            )}

            {loseTarget !== null && (
                <ReasonModal
                    title="Đánh mất tiềm năng"
                    label="Lý do thất bại"
                    placeholder="Nhập lý do tiềm năng thất bại..."
                    confirmLabel="Đánh mất"
                    confirmDanger
                    onCancel={() => setLoseTarget(null)}
                    onConfirm={(reason) => {
                        runAction(loseTarget, 'lose', reason);
                        setLoseTarget(null);
                    }}
                />
            )}

            <LeadEditModal item={editTarget} onClose={() => setEditTarget(null)} />

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

export default TiemNangPage;
