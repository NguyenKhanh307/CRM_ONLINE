import { ImportWizard } from '@/shared/components/import/ImportWizard';
import type { ImportField } from '@/shared/components/import/importTypes';
import { useImportContactBulk } from '../hooks/useImportContactBulk';

const FIELDS: ImportField[] = [
    { key: 'fullName',     label: 'Họ và tên',   required: true, type: 'text' },
    { key: 'position',     label: 'Chức vụ',                     type: 'text' },
    { key: 'email',        label: 'Email',                        type: 'text' },
    { key: 'gender',       label: 'Giới tính',                    type: 'enum', enumValues: ['male', 'female', 'other'] },
    { key: 'dateOfBirth',  label: 'Ngày sinh',                    type: 'date' },
    { key: 'address',      label: 'Địa chỉ',                      type: 'text' },
];

const ContactImportPage = () => {
    const importBulk = useImportContactBulk();
    return (
    <ImportWizard
        title="Liên hệ"
        fields={FIELDS}
        onImport={importBulk}
        backPath="/lien-he"
    />
    );
};

export default ContactImportPage;
