import { ImportWizard } from '@/shared/components/import/ImportWizard';
import type { ImportField } from '@/shared/components/import/importTypes';
import { useImportLeadBulk } from '../hooks/useImportLeadBulk';

const FIELDS: ImportField[] = [
    { key: 'name',           label: 'Họ và tên',        required: true, type: 'text' },
    { key: 'companyName',    label: 'Tên tổ chức',                       type: 'text' },
    { key: 'leadType',       label: 'Loại tiềm năng',                    type: 'text' },
    { key: 'taxCode',        label: 'Mã số thuế',                        type: 'text' },
    { key: 'website',        label: 'Website',                           type: 'text' },
    { key: 'industry',       label: 'Ngành nghề',                        type: 'text' },
    { key: 'phone',          label: 'Điện thoại',                        type: 'text' },
    { key: 'email',          label: 'Email',                             type: 'text' },
    { key: 'source',         label: 'Nguồn',                             type: 'text' },
    { key: 'campaignId',     label: 'ID chiến dịch nguồn',                type: 'number' },
    { key: 'status',         label: 'Trạng thái',                        type: 'enum', enumValues: ['new', 'contacting', 'converted'] },
    { key: 'note',           label: 'Ghi chú',                           type: 'text' },
];

const LeadImportPage = () => {
    const importBulk = useImportLeadBulk();
    return (
    <ImportWizard
        title="Tiềm năng"
        fields={FIELDS}
        onImport={importBulk}
        backPath="/tiem-nang"
    />
    );
};

export default LeadImportPage;
