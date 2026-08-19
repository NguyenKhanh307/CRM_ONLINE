import { ImportWizard } from '@/shared/components/import/ImportWizard';
import type { ImportField } from '@/shared/components/import/importTypes';
import { useImportTicketBulk } from '../hooks/useImportTicketBulk';

const FIELDS: ImportField[] = [
    { key: 'code',           label: 'Mã phiếu (để cập nhật)',        type: 'text' },
    { key: 'type',           label: 'Loại yêu cầu',                   type: 'enum', enumValues: ['support', 'return', 'exchange', 'complaint'], enumLabels: { support: 'Hỗ trợ', return: 'Trả hàng', exchange: 'Đổi hàng', complaint: 'Khiếu nại' } },
    { key: 'subject',        label: 'Tiêu đề',        required: true, type: 'text' },
    { key: 'description',    label: 'Mô tả',                          type: 'text' },
    { key: 'orderId',        label: 'ID đơn hàng',                    type: 'number' },
    { key: 'channel',        label: 'Kênh tiếp nhận',                 type: 'enum', enumValues: ['phone', 'email', 'web', 'zalo', 'other'], enumLabels: { phone: 'Điện thoại', email: 'Email', web: 'Web', zalo: 'Zalo', other: 'Khác' } },
    { key: 'priority',       label: 'Độ ưu tiên',                     type: 'enum', enumValues: ['low', 'medium', 'high', 'urgent'], enumLabels: { low: 'Thấp', medium: 'Thường', high: 'Cao', urgent: 'Khẩn' } },
    { key: 'reason',         label: 'Lý do trả/đổi/khiếu nại',        type: 'enum', enumValues: ['defective', 'wrong_item', 'not_as_described', 'changed_mind', 'late_delivery', 'other'], enumLabels: { defective: 'Lỗi kỹ thuật', wrong_item: 'Giao sai hàng', not_as_described: 'Không như mô tả', changed_mind: 'Đổi ý', late_delivery: 'Giao trễ', other: 'Khác' } },
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
