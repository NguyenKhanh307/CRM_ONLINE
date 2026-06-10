import { ImportWizard } from '@/shared/components/import/ImportWizard';
import type { ImportField } from '@/shared/components/import/importTypes';
import { warehouseService } from '../services/warehouseService';

const FIELDS: ImportField[] = [
    { key: 'name',    label: 'Tên kho',  required: true, type: 'text' },
    { key: 'code',    label: 'Mã kho',                    type: 'text' },
    { key: 'address', label: 'Địa chỉ',                   type: 'text' },
];

const WarehouseImportPage = () => (
    <ImportWizard
        title="Kho hàng"
        fields={FIELDS}
        onImport={(rows, opts) => warehouseService.importBulk(rows, opts).then(r => r.data.data)}
        backPath="/kho-hang"
    />
);

export default WarehouseImportPage;
