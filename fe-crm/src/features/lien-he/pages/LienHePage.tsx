import { useState, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import { FiTrash2, FiUpload, FiDownload } from 'react-icons/fi';
import type { ColumnDef } from '@tanstack/react-table';
import { PageHeaderSlot } from '@/shared/components/layout/PageHeaderSlot';
import { ActionButton } from '@/shared/components/ActionButton';
import { CreateButton } from '@/shared/components/CreateButton';
import { usePageShortcuts } from '@/shared/keyboard/PageShortcutsProvider';
import { DataTable } from '@/shared/components/table/DataTable';
import { ConfirmModal } from '@/shared/components/ConfirmModal';
import { ExportModal } from '@/shared/components/export/ExportModal';
import { exportRows } from '@/shared/components/export/exportFile';
import { useContactList } from '../hooks/useContactList';
import { useDeleteContact } from '../hooks/useDeleteContact';
import { getContactColumns } from '../config/contactColumns';
import { contactExportColumns } from '../config/contactExportColumns';
import { ContactEditModal } from '../components/ContactEditModal';
import type { ContactResult } from '../types/contactTypes';

const LienHePage = () => {
    const navigate = useNavigate();
    const goCreate = () => navigate('/lien-he/them-moi');
    usePageShortcuts({ onCreate: goCreate });
    const { data = [], isLoading } = useContactList();
    const { mutate: deleteFn, isPending: isDeleting } = useDeleteContact();

    const [editTarget, setEditTarget] = useState<ContactResult | null>(null);
    const [deleteTarget, setDeleteTarget] = useState<number | null>(null);
    const [selectedRows, setSelectedRows] = useState<ContactResult[]>([]);
    const [bulkDeleteOpen, setBulkDeleteOpen] = useState(false);
    const [exportOpen, setExportOpen] = useState(false);

    const rowsToExport = selectedRows.length > 0 ? selectedRows : data;

    const columns = useMemo<ColumnDef<ContactResult>[]>(() => getContactColumns(), []);

    return (
        <div className="p-6 bg-bg-main">
            <PageHeaderSlot>
                <h1 className="text-lg font-semibold text-text-main truncate">Liên hệ</h1>
                <div className="flex items-center gap-1.5">
                    <ActionButton variant="secondary" icon={FiUpload} onClick={() => navigate('/lien-he/nhap-file')}>
                        Nhập
                    </ActionButton>
                    <ActionButton variant="secondary" icon={FiDownload} onClick={() => setExportOpen(true)}>
                        Xuất{selectedRows.length > 0 ? ` (${selectedRows.length})` : ''}
                    </ActionButton>
                    <CreateButton onClick={goCreate} />
                    {selectedRows.length > 0 && (
                        <ActionButton variant="danger" icon={FiTrash2} onClick={() => setBulkDeleteOpen(true)}>
                            Xóa ({selectedRows.length})
                        </ActionButton>
                    )}
                </div>
            </PageHeaderSlot>
            <div className="bg-white rounded-card p-4 shadow-sm">
                <DataTable
                    data={data}
                    columns={columns}
                    isLoading={isLoading}
                    emptyText="Chưa có liên hệ nào"
                    onSelectionChange={setSelectedRows}
                    onRowDoubleClick={(c) => setEditTarget(c)}
                    rowActions={(c) => [
                        { key: 'edit', label: 'Chỉnh sửa', onClick: () => setEditTarget(c) },
                        { key: 'delete', label: 'Xóa', danger: true, onClick: () => setDeleteTarget(c.id) },
                    ]}
                    quickFilters={[
                        { id: 'primary', label: 'Liên hệ chính', field: 'isPrimary', value: 'true' },
                    ]}
                />
            </div>

            {deleteTarget !== null && (
                <ConfirmModal
                    message="Bạn có chắc muốn xóa liên hệ này?"
                    confirmLabel="Xóa"
                    confirmDanger
                    isLoading={isDeleting}
                    onConfirm={() => deleteFn(deleteTarget, { onSuccess: () => setDeleteTarget(null) })}
                    onCancel={() => setDeleteTarget(null)}
                />
            )}

            {bulkDeleteOpen && (
                <ConfirmModal
                    message={`Xóa ${selectedRows.length} liên hệ đã chọn?`}
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
                columns={contactExportColumns}
                rowCount={rowsToExport.length}
                onClose={() => setExportOpen(false)}
                onExport={(keys, format) => {
                    exportRows(rowsToExport, contactExportColumns, keys, format, 'lien-he');
                    setExportOpen(false);
                }}
            />

            <ContactEditModal item={editTarget} onClose={() => setEditTarget(null)} />
        </div>
    );
};

export default LienHePage;
