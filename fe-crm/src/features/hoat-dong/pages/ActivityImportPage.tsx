import { ImportWizard } from '@/shared/components/import/ImportWizard';
import type { ImportField } from '@/shared/components/import/importTypes';
import { useImportActivityBulk } from '../hooks/useImportActivityBulk';

const FIELDS: ImportField[] = [
    { key: 'type',    label: 'Loại hoạt động', required: true, type: 'enum', enumValues: ['call', 'email', 'meeting', 'task', 'note'] },
    { key: 'subject', label: 'Tiêu đề',        required: true, type: 'text' },
    { key: 'content', label: 'Nội dung',                        type: 'text' },
    { key: 'status',  label: 'Trạng thái',                      type: 'enum', enumValues: ['planned', 'in_progress', 'completed', 'cancelled'] },
    { key: 'dueAt',   label: 'Ngày đến hạn',                    type: 'date' },
];

const ActivityImportPage = () => {
    const importBulk = useImportActivityBulk();
    return (
    <ImportWizard
        title="Hoạt động"
        fields={FIELDS}
        onImport={importBulk}
        backPath="/hoat-dong"
    />
    );
};

export default ActivityImportPage;
