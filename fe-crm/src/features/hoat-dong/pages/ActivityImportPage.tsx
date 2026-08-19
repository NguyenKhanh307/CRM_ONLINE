import { ImportWizard } from '@/shared/components/import/ImportWizard';
import type { ImportField } from '@/shared/components/import/importTypes';
import { useImportActivityBulk } from '../hooks/useImportActivityBulk';

const FIELDS: ImportField[] = [
    { key: 'type',           label: 'Loại hoạt động', required: true, type: 'enum', enumValues: ['call', 'email', 'meeting', 'task', 'note'], enumLabels: { call: 'Cuộc gọi', email: 'Email', meeting: 'Cuộc họp', task: 'Nhiệm vụ', note: 'Ghi chú' } },
    { key: 'subject',        label: 'Tiêu đề',        required: true, type: 'text' },
    { key: 'content',        label: 'Nội dung',                        type: 'text' },
    { key: 'priority',       label: 'Độ ưu tiên',                      type: 'enum', enumValues: ['low', 'medium', 'high'], enumLabels: { low: 'Thấp', medium: 'Trung bình', high: 'Cao' } },
    { key: 'targetType',     label: 'Loại đối tượng chính',            type: 'text' },
    { key: 'targetId',       label: 'ID đối tượng chính',              type: 'number' },
    { key: 'location',       label: 'Địa điểm',                        type: 'text' },
    { key: 'callDirection',  label: 'Chiều cuộc gọi',                  type: 'enum', enumValues: ['in', 'out'], enumLabels: { in: 'Gọi đến', out: 'Gọi đi' } },
    { key: 'callResult',     label: 'Kết quả cuộc gọi',                type: 'text' },
    { key: 'callDuration',   label: 'Thời lượng gọi (phút)',           type: 'number' },
    { key: 'status',         label: 'Trạng thái',                      type: 'enum', enumValues: ['planned', 'in_progress', 'done', 'cancelled'], enumLabels: { planned: 'Đã lên kế hoạch', in_progress: 'Đang thực hiện', done: 'Hoàn thành', cancelled: 'Đã hủy' } },
    { key: 'dueAt',          label: 'Ngày đến hạn',                    type: 'date' },
];

const ActivityImportPage = () => {
    const importBulk = useImportActivityBulk();
    return (
    <ImportWizard
        title="Hoạt động"
        fields={FIELDS}
        onImport={importBulk}
        backPath="/hoat-dong"
    />
    );
};

export default ActivityImportPage;
