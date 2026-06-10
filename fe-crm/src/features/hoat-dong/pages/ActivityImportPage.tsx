import { ImportWizard } from '@/shared/components/import/ImportWizard';
import type { ImportField } from '@/shared/components/import/importTypes';
import { activityService } from '../services/activityService';

const FIELDS: ImportField[] = [
    { key: 'type',    label: 'Loại hoạt động', required: true, type: 'enum', enumValues: ['call', 'email', 'meeting', 'task', 'note'] },
    { key: 'subject', label: 'Tiêu đề',        required: true, type: 'text' },
    { key: 'content', label: 'Nội dung',                        type: 'text' },
    { key: 'status',  label: 'Trạng thái',                      type: 'enum', enumValues: ['planned', 'in_progress', 'completed', 'cancelled'] },
    { key: 'dueAt',   label: 'Ngày đến hạn',                    type: 'date' },
];

const ActivityImportPage = () => (
    <ImportWizard
        title="Hoạt động"
        fields={FIELDS}
        onImport={(rows, opts) => activityService.importBulk(rows, opts).then(r => r.data.data)}
        backPath="/hoat-dong"
    />
);

export default ActivityImportPage;
