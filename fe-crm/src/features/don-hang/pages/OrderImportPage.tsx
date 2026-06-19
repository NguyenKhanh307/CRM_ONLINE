import { ImportWizard } from '@/shared/components/import/ImportWizard';
import type { ImportField } from '@/shared/components/import/importTypes';
import { useImportOrderBulk } from '../hooks/useImportOrderBulk';

const FIELDS: ImportField[] = [
    { key: 'code',          label: 'Mã đơn hàng (để cập nhật)',     type: 'text' },
    { key: 'orderType',     label: 'Loại đơn hàng', required: true, type: 'enum', enumValues: ['sale', 'purchase', 'return'] },
    { key: 'orderDate',     label: 'Ngày đặt hàng',                 type: 'date' },
    { key: 'status',        label: 'Trạng thái',                     type: 'enum', enumValues: ['draft', 'confirmed', 'delivering', 'completed', 'cancelled'] },
    { key: 'paymentStatus', label: 'Trạng thái thanh toán',          type: 'enum', enumValues: ['unpaid', 'partial', 'paid'] },
    { key: 'subtotal',      label: 'Tạm tính',                       type: 'number' },
    { key: 'discount',      label: 'Chiết khấu',                     type: 'number' },
    { key: 'tax',           label: 'Thuế',                           type: 'number' },
    { key: 'total',         label: 'Tổng cộng',                      type: 'number' },
    { key: 'note',          label: 'Ghi chú',                        type: 'text' },
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
