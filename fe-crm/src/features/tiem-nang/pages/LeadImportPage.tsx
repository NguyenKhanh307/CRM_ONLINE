import { ImportWizard } from '@/shared/components/import/ImportWizard';
import type { ImportField } from '@/shared/components/import/importTypes';
import { leadService } from '../services/leadService';

const FIELDS: ImportField[] = [
    { key: 'name',           label: 'Họ và tên',        required: true, type: 'text' },
    { key: 'phone',          label: 'Điện thoại',                        type: 'text' },
    { key: 'email',          label: 'Email',                             type: 'text' },
    { key: 'source',         label: 'Nguồn',                             type: 'text' },
    { key: 'status',         label: 'Trạng thái',                        type: 'enum', enumValues: ['new', 'contacting', 'qualified', 'converted', 'lost'] },
    { key: 'estimatedValue', label: 'Giá trị ước tính',                  type: 'number' },
    { key: 'note',           label: 'Ghi chú',                           type: 'text' },
];

const LeadImportPage = () => (
    <ImportWizard
        title="Tiềm năng"
        fields={FIELDS}
        onImport={(rows, opts) => leadService.importBulk(rows, opts).then(r => r.data.data)}
        backPath="/tiem-nang"
    />
);

export default LeadImportPage;
