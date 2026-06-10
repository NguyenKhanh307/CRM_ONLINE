import { ImportWizard } from '@/shared/components/import/ImportWizard';
import type { ImportField } from '@/shared/components/import/importTypes';
import { customerService } from '../services/customerService';

const FIELDS: ImportField[] = [
    { key: 'name',    label: 'Tên khách hàng', required: true, type: 'text' },
    { key: 'type',    label: 'Loại',                           type: 'enum', enumValues: ['individual', 'company'] },
    { key: 'taxCode', label: 'Mã số thuế',                     type: 'text' },
    { key: 'phone',   label: 'Điện thoại',                     type: 'text' },
    { key: 'email',   label: 'Email',                          type: 'text' },
    { key: 'address', label: 'Địa chỉ',                        type: 'text' },
    { key: 'source',  label: 'Nguồn',                          type: 'text' },
    { key: 'status',  label: 'Trạng thái',                     type: 'enum', enumValues: ['active', 'inactive', 'potential'] },
];

const CustomerImportPage = () => (
    <ImportWizard
        title="Khách hàng"
        fields={FIELDS}
        onImport={(rows, opts) => customerService.importBulk(rows, opts).then(r => r.data.data)}
        backPath="/khach-hang"
    />
);

export default CustomerImportPage;
