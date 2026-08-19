import { ImportWizard } from '@/shared/components/import/ImportWizard';
import type { ImportField } from '@/shared/components/import/importTypes';
import { useImportCampaignBulk } from '../hooks/useImportCampaignBulk';

const FIELDS: ImportField[] = [
    { key: 'code',            label: 'Mã chiến dịch (để cập nhật)', type: 'text' },
    { key: 'name',            label: 'Tên chiến dịch',              type: 'text' },
    { key: 'type',            label: 'Loại',                         type: 'enum', enumValues: ['email', 'event', 'ads', 'social', 'seo', 'webinar', 'other'], enumLabels: { email: 'Email', event: 'Sự kiện', ads: 'Quảng cáo', social: 'Mạng xã hội', seo: 'SEO', webinar: 'Webinar', other: 'Khác' } },
    { key: 'status',          label: 'Trạng thái',                   type: 'enum', enumValues: ['draft', 'scheduled', 'running', 'paused', 'completed', 'cancelled'], enumLabels: { draft: 'Nháp', scheduled: 'Đã lên lịch', running: 'Đang chạy', paused: 'Tạm dừng', completed: 'Hoàn tất', cancelled: 'Đã hủy' } },
    { key: 'channel',         label: 'Kênh',                         type: 'text' },
    { key: 'startDate',       label: 'Ngày bắt đầu',                 type: 'date' },
    { key: 'endDate',         label: 'Ngày kết thúc',                type: 'date' },
    { key: 'budget',          label: 'Ngân sách',                    type: 'number' },
    { key: 'actualCost',      label: 'Chi phí thực tế',              type: 'number' },
    { key: 'targetSize',      label: 'Quy mô đối tượng mục tiêu',    type: 'number' },
    { key: 'expectedRevenue', label: 'Doanh thu kỳ vọng',            type: 'number' },
    { key: 'note',            label: 'Mô tả',                        type: 'text' },
];

const CampaignImportPage = () => {
    const importBulk = useImportCampaignBulk();
    return (
        <ImportWizard title="Chiến dịch" fields={FIELDS} onImport={importBulk} backPath="/chien-dich" />
    );
};

export default CampaignImportPage;
