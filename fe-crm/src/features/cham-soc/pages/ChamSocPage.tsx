import { useState, useMemo } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { FiTrash2, FiShare2, FiDownload, FiPlus } from 'react-icons/fi';
import type { ColumnDef } from '@tanstack/react-table';
import { PageHeaderSlot } from '@/shared/components/layout/PageHeaderSlot';
import { DataTable } from '@/shared/components/table/DataTable';
import { ConfirmModal } from '@/shared/components/ConfirmModal';
import { HandoverModal } from '@/shared/components/HandoverModal';
import { ExportModal } from '@/shared/components/export/ExportModal';
import { exportRows } from '@/shared/components/export/exportFile';
import { useTicketList } from '../hooks/useTicketList';
import { useDeleteTicket } from '../hooks/useDeleteTicket';
import { useHandoverBulkTicket } from '../hooks/useHandoverBulkTicket';
import { getTicketColumns } from '../config/ticketColumns';
import { ticketExportColumns } from '../config/ticketExportColumns';
import type { TicketResult } from '../types/ticketTypes';

const ChamSocPage = () => {
    const navigate = useNavigate();
    const [searchParams] = useSearchParams();
    const focusId = searchParams.get('focus');
    const { data = [], isLoading } = useTicketList();
    const { mutate: deleteFn, isPending: isDeleting } = useDeleteTicket();
    const { mutate: handoverFn, isPending: isHandovering } = useHandoverBulkTicket();

    const [deleteTarget, setDeleteTarget] = useState<number | null>(null);
    const [selectedRows, setSelectedRows] = useState<TicketResult[]>([]);
    const [bulkDeleteOpen, setBulkDeleteOpen] = useState(false);
    const [handoverOpen, setHandoverOpen] = useState(false);
    const [exportOpen, setExportOpen] = useState(false);

    const rowsToExport = selectedRows.length > 0 ? selectedRows : data;

    const columns = useMemo<ColumnDef<TicketResult>[]>(() => getTicketColumns(), []);

    return (
        <div className="p-6 bg-bg-main">
            <PageHeaderSlot>
                <h1 className="text-lg font-semibold text-text-main truncate">Chăm sóc sau bán</h1>
                <div className="flex items-center gap-2">
                    <button onClick={() => navigate('/cham-soc/them-moi')}
                        className="flex items-center gap-1.5 px-3 py-1.5 rounded-btn bg-primary text-white text-md font-medium hover:opacity-90">
                        <FiPlus size={14} /> Thêm mới
                    </button>
                    <button onClick={() => setExportOpen(true)}
                        className="flex items-center gap-1.5 px-3 py-1.5 rounded-btn border border-gray-300 text-md text-gray-600 hover:bg-gray-50">
                        <FiDownload size={14} /> Xuất file{selectedRows.length > 0 ? ` (${selectedRows.length})` : ''}
                    </button>
                    {selectedRows.length > 0 && (
                        <>
                            <button onClick={() => setHandoverOpen(true)}
                                className="flex items-center gap-2 px-3 py-1.5 rounded-btn bg-blue-50 text-primary text-md font-medium hover:bg-blue-100">
                                <FiShare2 size={14} /> Bàn giao ({selectedRows.length})
                            </button>
                            <button onClick={() => setBulkDeleteOpen(true)}
                                className="flex items-center gap-2 px-3 py-1.5 rounded-btn bg-red-50 text-danger text-md font-medium hover:bg-red-100">
                                <FiTrash2 size={14} /> Xóa đã chọn ({selectedRows.length})
                            </button>
                        </>
                    )}
                </div>
            </PageHeaderSlot>
            <div className="bg-white rounded-card p-4 shadow-sm">
                <DataTable
                    data={data}
                    columns={columns}
                    isLoading={isLoading}
                    emptyText="Chưa có phiếu hỗ trợ nào"
                    onSelectionChange={setSelectedRows}
                    focusId={focusId}
                    onRowDoubleClick={(t) => navigate(`/cham-soc/${t.id}`)}
                    rowActions={(t) => [
                        { key: 'view', label: 'Xem / xử lý', onClick: () => navigate(`/cham-soc/${t.id}`) },
                        { key: 'delete', label: 'Xóa', danger: true, onClick: () => setDeleteTarget(t.id) },
                    ]}
                    quickFilters={[
                        { id: 'support', label: 'Hỗ trợ', field: 'type', value: 'support' },
                        { id: 'return', label: 'Trả hàng', field: 'type', value: 'return' },
                        { id: 'exchange', label: 'Đổi hàng', field: 'type', value: 'exchange' },
                        { id: 'complaint', label: 'Khiếu nại', field: 'type', value: 'complaint' },
                    ]}
                />
            </div>

            {deleteTarget !== null && (
                <ConfirmModal
                    message="Bạn có chắc muốn xóa phiếu này?"
                    confirmLabel="Xóa" confirmDanger isLoading={isDeleting}
                    onConfirm={() => deleteFn(deleteTarget, { onSuccess: () => setDeleteTarget(null) })}
                    onCancel={() => setDeleteTarget(null)}
                />
            )}

            {bulkDeleteOpen && (
                <ConfirmModal
                    message={`Xóa ${selectedRows.length} phiếu đã chọn?`}
                    confirmLabel="Xóa tất cả" confirmDanger isLoading={isDeleting}
                    onConfirm={() => { Promise.all(selectedRows.map(r => deleteFn(r.id))); setBulkDeleteOpen(false); }}
                    onCancel={() => setBulkDeleteOpen(false)}
                />
            )}

            <ExportModal
                open={exportOpen}
                columns={ticketExportColumns}
                rowCount={rowsToExport.length}
                onClose={() => setExportOpen(false)}
                onExport={(keys, format) => { exportRows(rowsToExport, ticketExportColumns, keys, format, 'cham-soc'); setExportOpen(false); }}
            />

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

export default ChamSocPage;
