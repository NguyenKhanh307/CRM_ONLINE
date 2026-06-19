import { ImportWizard } from '@/shared/components/import/ImportWizard';
import type { ImportField } from '@/shared/components/import/importTypes';
import { useImportWarehouseBulk } from '../hooks/useImportWarehouseBulk';

const FIELDS: ImportField[] = [
    { key: 'name',    label: 'Tên kho',  required: true, type: 'text' },
    { key: 'code',    label: 'Mã kho',                    type: 'text' },
    { key: 'address', label: 'Địa chỉ',                   type: 'text' },
];

const WarehouseImportPage = () => {
    const importBulk = useImportWarehouseBulk();
    return (
    <ImportWizard
        title="Kho hàng"
        fields={FIELDS}
        onImport={importBulk}
        backPath="/kho-hang"
    />
    );
};

export default WarehouseImportPage;
