import { ImportWizard } from '@/shared/components/import/ImportWizard';
import type { ImportField } from '@/shared/components/import/importTypes';
import { useImportInvoiceBulk } from '../hooks/useImportInvoiceBulk';

const FIELDS: ImportField[] = [
    { key: 'code',            label: 'Mã Hóa đơn (để cập nhật)',     type: 'text' },
    { key: 'customerId',      label: 'ID khách hàng',                 type: 'number' },
    { key: 'contactId',       label: 'ID liên hệ',                    type: 'number' },
    { key: 'quotationId',     label: 'ID báo giá',                    type: 'number' },
    { key: 'opportunityId',   label: 'ID cơ hội',                     type: 'number' },
    { key: 'orderId',         label: 'ID đơn hàng',                   type: 'number' },
    { key: 'campaignId',      label: 'ID chiến dịch nguồn',           type: 'number' },
    { key: 'invoiceDate',     label: 'Ngày hóa đơn',                  type: 'date' },
    { key: 'dueDate',         label: 'Hạn thanh toán',                type: 'date' },
    { key: 'currency',        label: 'Đơn vị tiền tệ',                type: 'text' },
    { key: 'exchangeRate',    label: 'Tỷ giá',                        type: 'number' },
    { key: 'status',          label: 'Trạng thái',                     type: 'enum', enumValues: ['draft', 'sent', 'partially_paid', 'paid', 'cancelled'] },
    { key: 'paymentStatus',   label: 'Trạng thái thanh toán',          type: 'enum', enumValues: ['unpaid', 'partial', 'paid'] },
    { key: 'billingAddress',  label: 'Địa chỉ xuất hóa đơn',          type: 'text' },
    { key: 'taxCode',         label: 'Mã số thuế',                    type: 'text' },
    { key: 'subtotal',        label: 'Tạm tính',                       type: 'number' },
    { key: 'discount',        label: 'Chiết khấu',                     type: 'number' },
    { key: 'tax',             label: 'Thuế',                           type: 'number' },
    { key: 'total',           label: 'Tổng cộng',                      type: 'number' },
    { key: 'note',            label: 'Ghi chú',                        type: 'text' },
];

const InvoiceImportPage = () => {
    const importBulk = useImportInvoiceBulk();
    return (
    <ImportWizard
        title="Hóa đơn"
        fields={FIELDS}
        onImport={importBulk}
        backPath="/hoa-don"
    />
    );
};

export default InvoiceImportPage;
