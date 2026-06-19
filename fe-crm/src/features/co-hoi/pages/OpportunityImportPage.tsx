import { ImportWizard } from '@/shared/components/import/ImportWizard';
import type { ImportField } from '@/shared/components/import/importTypes';
import { useImportOpportunityBulk } from '../hooks/useImportOpportunityBulk';

const FIELDS: ImportField[] = [
    { key: 'code',              label: 'Mã cơ hội (để cập nhật)',          type: 'text' },
    { key: 'name',              label: 'Tên cơ hội',      required: true, type: 'text' },
    { key: 'amount',            label: 'Giá trị',                         type: 'number' },
    { key: 'probability',       label: 'Xác suất (%)',                     type: 'number' },
    { key: 'expectedCloseDate', label: 'Ngày chốt dự kiến',               type: 'date' },
    { key: 'status',            label: 'Trạng thái',                       type: 'enum', enumValues: ['open', 'won', 'lost'] },
];

const OpportunityImportPage = () => {
    const importBulk = useImportOpportunityBulk();
    return (
    <ImportWizard
        title="Cơ hội"
        fields={FIELDS}
        onImport={importBulk}
        backPath="/co-hoi"
    />
    );
};

export default OpportunityImportPage;
