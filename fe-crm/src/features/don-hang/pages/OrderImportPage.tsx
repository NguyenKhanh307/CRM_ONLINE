import { ImportWizard } from '@/shared/components/import/ImportWizard';
import type { ImportField } from '@/shared/components/import/importTypes';
import { useImportOrderBulk } from '../hooks/useImportOrderBulk';

const FIELDS: ImportField[] = [
    { key: 'code',           label: 'Mã Đơn hàng (để cập nhật)',    type: 'text' },
    { key: 'quotationId',    label: 'ID báo giá',                    type: 'number' },
    { key: 'orderDate',      label: 'Ngày đơn hàng',                 type: 'date' },
    { key: 'deliveryDate',   label: 'Ngày giao dự kiến',             type: 'date' },
    { key: 'status',         label: 'Trạng thái',                     type: 'enum', enumValues: ['draft', 'confirmed', 'processing', 'completed', 'cancelled'], enumLabels: { draft: 'Nháp', confirmed: 'Đã xác nhận', processing: 'Đang xử lý', completed: 'Hoàn tất', cancelled: 'Đã hủy' } },
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
