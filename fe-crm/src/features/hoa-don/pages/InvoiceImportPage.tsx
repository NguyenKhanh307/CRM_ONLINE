import { ImportWizard } from '@/shared/components/import/ImportWizard';
import type { ImportField } from '@/shared/components/import/importTypes';
import { useImportInvoiceBulk } from '../hooks/useImportInvoiceBulk';

const FIELDS: ImportField[] = [
    { key: 'code',            label: 'Mã Hóa đơn (để cập nhật)',     type: 'text' },
    { key: 'orderId',         label: 'ID đơn hàng',                   type: 'number' },
    { key: 'invoiceDate',     label: 'Ngày hóa đơn',                  type: 'date' },
    { key: 'dueDate',         label: 'Hạn thanh toán',                type: 'date' },
    { key: 'status',          label: 'Trạng thái',                     type: 'enum', enumValues: ['draft', 'sent', 'partially_paid', 'paid', 'cancelled'], enumLabels: { draft: 'Nháp', sent: 'Đã gửi', partially_paid: 'Thanh toán một phần', paid: 'Đã thanh toán', cancelled: 'Đã hủy' } },
    { key: 'paymentStatus',   label: 'Trạng thái thanh toán',          type: 'enum', enumValues: ['unpaid', 'partial', 'paid'], enumLabels: { unpaid: 'Chưa thanh toán', partial: 'Thanh toán một phần', paid: 'Đã thanh toán' } },
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
