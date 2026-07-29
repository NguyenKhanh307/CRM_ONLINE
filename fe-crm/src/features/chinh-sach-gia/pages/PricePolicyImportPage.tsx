import { ImportWizard } from '@/shared/components/import/ImportWizard';
import type { ImportField } from '@/shared/components/import/importTypes';
import { useImportPricePolicyBulk } from '../hooks/useImportPricePolicyBulk';

const FIELDS: ImportField[] = [
    { key: 'code',      label: 'Mã chính sách (để cập nhật)', type: 'text' },
    { key: 'name',      label: 'Tên chính sách', required: true, type: 'text' },
    { key: 'type',      label: 'Loại',                         type: 'text' },
    { key: 'priority',  label: 'Độ ưu tiên',                   type: 'number' },
    { key: 'startDate', label: 'Ngày bắt đầu',                 type: 'date' },
    { key: 'endDate',   label: 'Ngày kết thúc',                type: 'date' },
    { key: 'status',    label: 'Trạng thái',                   type: 'enum', enumValues: ['active', 'inactive', 'expired'] },
];

const PricePolicyImportPage = () => {
    const importBulk = useImportPricePolicyBulk();
    return (
    <ImportWizard
        title="Chính sách giá"
        fields={FIELDS}
        onImport={importBulk}
        backPath="/chinh-sach-gia"
    />
    );
};

export default PricePolicyImportPage;
