import { ImportWizard } from '@/shared/components/import/ImportWizard';
import type { ImportField } from '@/shared/components/import/importTypes';
import { useImportContactBulk } from '../hooks/useImportContactBulk';

const FIELDS: ImportField[] = [
    { key: 'customerId',    label: 'ID khách hàng',                type: 'number' },
    { key: 'salutation',    label: 'Xưng hô',                      type: 'text' },
    { key: 'fullName',      label: 'Họ và tên',   required: true,  type: 'text' },
    { key: 'title',         label: 'Chức danh',                    type: 'text' },
    { key: 'department',    label: 'Phòng ban',                    type: 'text' },
    { key: 'email',         label: 'Email',                        type: 'text' },
    { key: 'phone',         label: 'SĐT chính',                    type: 'text' },
    { key: 'zalo',          label: 'Zalo',                         type: 'text' },
    { key: 'source',        label: 'Nguồn gốc',                    type: 'text' },
    { key: 'gender',        label: 'Giới tính',                    type: 'enum', enumValues: ['male', 'female', 'other'], enumLabels: { male: 'Nam', female: 'Nữ', other: 'Khác' } },
    { key: 'dateOfBirth',   label: 'Ngày sinh',                    type: 'date' },
    { key: 'isPrimary',     label: 'Liên hệ chính',                type: 'enum', enumValues: ['true', 'false'], enumLabels: { true: 'Chính', false: 'Phụ' } },
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
