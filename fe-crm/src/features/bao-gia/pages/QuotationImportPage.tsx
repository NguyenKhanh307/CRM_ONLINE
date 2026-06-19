import { ImportWizard } from '@/shared/components/import/ImportWizard';
import type { ImportField } from '@/shared/components/import/importTypes';
import { useImportQuotationBulk } from '../hooks/useImportQuotationBulk';

const FIELDS: ImportField[] = [
    { key: 'code',        label: 'Mã báo giá (để cập nhật)',       type: 'text' },
    { key: 'quoteDate',   label: 'Ngày báo giá',  required: true, type: 'date' },
    { key: 'validUntil',  label: 'Hiệu lực đến',                  type: 'date' },
    { key: 'status',      label: 'Trạng thái',                     type: 'enum', enumValues: ['draft', 'sent', 'approved', 'rejected'] },
    { key: 'subtotal',    label: 'Tạm tính',                       type: 'number' },
    { key: 'discount',    label: 'Chiết khấu',                     type: 'number' },
    { key: 'tax',         label: 'Thuế',                           type: 'number' },
    { key: 'total',       label: 'Tổng cộng',                      type: 'number' },
    { key: 'note',        label: 'Ghi chú',                        type: 'text' },
];

const QuotationImportPage = () => {
    const importBulk = useImportQuotationBulk();
    return (
    <ImportWizard
        title="Báo giá"
        fields={FIELDS}
        onImport={importBulk}
        backPath="/bao-gia"
    />
    );
};

export default QuotationImportPage;
