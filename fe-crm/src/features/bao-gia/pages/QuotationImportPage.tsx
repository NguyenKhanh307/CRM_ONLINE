import { ImportWizard } from '@/shared/components/import/ImportWizard';
import type { ImportField } from '@/shared/components/import/importTypes';
import { quotationService } from '../services/quotationService';

const FIELDS: ImportField[] = [
    { key: 'quoteDate',   label: 'Ngày báo giá',  required: true, type: 'date' },
    { key: 'validUntil',  label: 'Hiệu lực đến',                  type: 'date' },
    { key: 'status',      label: 'Trạng thái',                     type: 'enum', enumValues: ['draft', 'sent', 'approved', 'rejected'] },
    { key: 'subtotal',    label: 'Tạm tính',                       type: 'number' },
    { key: 'discount',    label: 'Chiết khấu',                     type: 'number' },
    { key: 'tax',         label: 'Thuế',                           type: 'number' },
    { key: 'total',       label: 'Tổng cộng',                      type: 'number' },
    { key: 'note',        label: 'Ghi chú',                        type: 'text' },
];

const QuotationImportPage = () => (
    <ImportWizard
        title="Báo giá"
        fields={FIELDS}
        onImport={(rows, opts) => quotationService.importBulk(rows, opts).then(r => r.data.data)}
        backPath="/bao-gia"
    />
);

export default QuotationImportPage;
