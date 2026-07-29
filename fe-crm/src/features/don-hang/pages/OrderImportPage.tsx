import { ImportWizard } from '@/shared/components/import/ImportWizard';
import type { ImportField } from '@/shared/components/import/importTypes';
import { useImportOrderBulk } from '../hooks/useImportOrderBulk';

const FIELDS: ImportField[] = [
    { key: 'code',           label: 'Mã Đơn hàng (để cập nhật)',    type: 'text' },
    { key: 'customerId',     label: 'ID khách hàng',                 type: 'number' },
    { key: 'contactId',      label: 'ID liên hệ',                    type: 'number' },
    { key: 'quotationId',    label: 'ID báo giá',                    type: 'number' },
    { key: 'opportunityId',  label: 'ID cơ hội',                     type: 'number' },
    { key: 'campaignId',     label: 'ID chiến dịch nguồn',           type: 'number' },
    { key: 'orderDate',      label: 'Ngày đơn hàng',                 type: 'date' },
    { key: 'deliveryDate',   label: 'Ngày giao dự kiến',             type: 'date' },
    { key: 'currency',       label: 'Đơn vị tiền tệ',                type: 'text' },
    { key: 'exchangeRate',   label: 'Tỷ giá',                        type: 'number' },
    { key: 'status',         label: 'Trạng thái',                     type: 'enum', enumValues: ['draft', 'confirmed', 'processing', 'completed', 'cancelled'] },
    { key: 'billingAddress', label: 'Địa chỉ xuất hóa đơn',          type: 'text' },
    { key: 'taxCode',        label: 'Mã số thuế',                    type: 'text' },
    { key: 'subtotal',       label: 'Tạm tính',                       type: 'number' },
    { key: 'discount',       label: 'Chiết khấu',                     type: 'number' },
    { key: 'tax',            label: 'Thuế',                           type: 'number' },
    { key: 'total',          label: 'Tổng cộng',                      type: 'number' },
    { key: 'note',           label: 'Ghi chú',                        type: 'text' },
];

const OrderImportPage = () => {
    const importBulk = useImportOrderBulk();
    return (
    <ImportWizard
        title="Đơn hàng"
        fields={FIELDS}
        onImport={importBulk}
        backPath="/don-hang"
    />
    );
};

export default OrderImportPage;
