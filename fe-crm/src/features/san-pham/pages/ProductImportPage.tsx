import { ImportWizard } from '@/shared/components/import/ImportWizard';
import type { ImportField } from '@/shared/components/import/importTypes';
import { useImportProductBulk } from '../hooks/useImportProductBulk';

const FIELDS: ImportField[] = [
    { key: 'name',        label: 'Tên sản phẩm', required: true, type: 'text' },
    { key: 'sku',         label: 'Mã SKU',                        type: 'text' },
    { key: 'type',        label: 'Loại',                          type: 'enum', enumValues: ['product', 'service', 'combo'] },
    { key: 'unit',        label: 'Đơn vị tính',                   type: 'text' },
    { key: 'basePrice',   label: 'Giá bán',                       type: 'number' },
    { key: 'costPrice',   label: 'Giá vốn',                       type: 'number' },
    { key: 'vatRate',     label: 'Thuế VAT (%)',                   type: 'number' },
    { key: 'barcode',     label: 'Barcode',                        type: 'text' },
    { key: 'description', label: 'Mô tả',                         type: 'text' },
];

const ProductImportPage = () => {
    const importBulk = useImportProductBulk();
    return (
    <ImportWizard
        title="Sản phẩm"
        fields={FIELDS}
        onImport={importBulk}
        backPath="/san-pham"
    />
    );
};

export default ProductImportPage;
