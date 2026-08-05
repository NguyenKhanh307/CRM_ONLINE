import { ImportWizard } from '@/shared/components/import/ImportWizard';
import type { ImportField } from '@/shared/components/import/importTypes';
import { useImportTicketBulk } from '../hooks/useImportTicketBulk';

const FIELDS: ImportField[] = [
    { key: 'code',           label: 'Mã phiếu (để cập nhật)',        type: 'text' },
    { key: 'type',           label: 'Loại yêu cầu',                   type: 'enum', enumValues: ['support', 'return', 'exchange', 'complaint'] },
    { key: 'subject',        label: 'Tiêu đề',        required: true, type: 'text' },
    { key: 'description',    label: 'Mô tả',                          type: 'text' },
    { key: 'orderId',        label: 'ID đơn hàng',                    type: 'number' },
    { key: 'channel',        label: 'Kênh tiếp nhận',                 type: 'enum', enumValues: ['phone', 'email', 'web', 'zalo', 'other'] },
    { key: 'priority',       label: 'Độ ưu tiên',                     type: 'enum', enumValues: ['low', 'medium', 'high', 'urgent'] },
    { key: 'reason',         label: 'Lý do trả/đổi/khiếu nại',        type: 'enum', enumValues: ['defective', 'wrong_item', 'not_as_described', 'changed_mind', 'late_delivery', 'other'] },
    { key: 'assignedUserId', label: 'ID nhân viên xử lý',             type: 'number' },
];

const TicketImportPage = () => {
    const importBulk = useImportTicketBulk();
    return (
    <ImportWizard
        title="Chăm sóc"
        fields={FIELDS}
        onImport={importBulk}
        backPath="/cham-soc"
    />
    );
};

export default TicketImportPage;
