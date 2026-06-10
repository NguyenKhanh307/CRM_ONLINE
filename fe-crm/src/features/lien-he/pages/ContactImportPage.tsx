import { ImportWizard } from '@/shared/components/import/ImportWizard';
import type { ImportField } from '@/shared/components/import/importTypes';
import { contactService } from '../services/contactService';

const FIELDS: ImportField[] = [
    { key: 'fullName',     label: 'Họ và tên',   required: true, type: 'text' },
    { key: 'position',     label: 'Chức vụ',                     type: 'text' },
    { key: 'email',        label: 'Email',                        type: 'text' },
    { key: 'gender',       label: 'Giới tính',                    type: 'enum', enumValues: ['male', 'female', 'other'] },
    { key: 'dateOfBirth',  label: 'Ngày sinh',                    type: 'date' },
    { key: 'address',      label: 'Địa chỉ',                      type: 'text' },
];

const ContactImportPage = () => (
    <ImportWizard
        title="Liên hệ"
        fields={FIELDS}
        onImport={(rows, opts) => contactService.importBulk(rows, opts).then(r => r.data.data)}
        backPath="/lien-he"
    />
);

export default ContactImportPage;
