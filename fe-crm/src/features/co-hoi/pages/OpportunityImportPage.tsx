import { ImportWizard } from '@/shared/components/import/ImportWizard';
import type { ImportField } from '@/shared/components/import/importTypes';
import { opportunityService } from '../services/opportunityService';

const FIELDS: ImportField[] = [
    { key: 'name',              label: 'Tên cơ hội',      required: true, type: 'text' },
    { key: 'amount',            label: 'Giá trị',                         type: 'number' },
    { key: 'probability',       label: 'Xác suất (%)',                     type: 'number' },
    { key: 'expectedCloseDate', label: 'Ngày chốt dự kiến',               type: 'date' },
    { key: 'status',            label: 'Trạng thái',                       type: 'enum', enumValues: ['open', 'won', 'lost'] },
];

const OpportunityImportPage = () => (
    <ImportWizard
        title="Cơ hội"
        fields={FIELDS}
        onImport={(rows, opts) => opportunityService.importBulk(rows, opts).then(r => r.data.data)}
        backPath="/co-hoi"
    />
);

export default OpportunityImportPage;
