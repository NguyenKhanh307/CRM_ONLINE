import { ImportWizard } from '@/shared/components/import/ImportWizard';
import type { ImportField } from '@/shared/components/import/importTypes';
import { useImportQuotationBulk } from '../hooks/useImportQuotationBulk';

const FIELDS: ImportField[] = [
    { key: 'code',           label: 'Mã báo giá (để cập nhật)',       type: 'text' },
    { key: 'customerId',     label: 'ID khách hàng',                  type: 'number' },
    { key: 'contactId',      label: 'ID liên hệ',                     type: 'number' },
    { key: 'opportunityId',  label: 'ID cơ hội',                      type: 'number' },
    { key: 'campaignId',     label: 'ID chiến dịch nguồn',            type: 'number' },
    { key: 'pricePolicyId',  label: 'ID chính sách giá',              type: 'number' },
    { key: 'quoteDate',      label: 'Ngày báo giá',  required: true, type: 'date' },
    { key: 'validUntil',     label: 'Hiệu lực đến',                  type: 'date' },
    { key: 'currency',       label: 'Đơn vị tiền tệ',                 type: 'text' },
    { key: 'exchangeRate',   label: 'Tỷ giá',                         type: 'number' },
    { key: 'status',         label: 'Trạng thái',                     type: 'enum', enumValues: ['draft', 'pending', 'approved', 'rejected', 'sent', 'accepted', 'expired'] },
    { key: 'subtotal',       label: 'Tạm tính',                       type: 'number' },
    { key: 'discount',       label: 'Chiết khấu',                     type: 'number' },
    { key: 'tax',            label: 'Thuế',                           type: 'number' },
    { key: 'total',          label: 'Tổng cộng',                      type: 'number' },
    { key: 'note',           label: 'Ghi chú',                        type: 'text' },
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
