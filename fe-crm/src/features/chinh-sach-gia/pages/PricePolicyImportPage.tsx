import { ImportWizard } from '@/shared/components/import/ImportWizard';
import type { ImportField } from '@/shared/components/import/importTypes';
import { useImportPricePolicyBulk } from '../hooks/useImportPricePolicyBulk';

const FIELDS: ImportField[] = [
    { key: 'code',      label: 'Mã chính sách (để cập nhật)', type: 'text' },
    { key: 'name',      label: 'Tên chính sách', required: true, type: 'text' },
    { key: 'type',      label: 'Loại',                         type: 'enum', enumValues: ['standard', 'promotional', 'special'], enumLabels: { standard: 'Tiêu chuẩn', promotional: 'Khuyến mãi', special: 'Đặc biệt' } },
    { key: 'priority',  label: 'Độ ưu tiên',                   type: 'number' },
    { key: 'startDate', label: 'Ngày bắt đầu',                 type: 'date' },
    { key: 'endDate',   label: 'Ngày kết thúc',                type: 'date' },
    { key: 'status',    label: 'Trạng thái',                   type: 'enum', enumValues: ['active', 'inactive', 'expired'], enumLabels: { active: 'Đang áp dụng', inactive: 'Ngừng áp dụng', expired: 'Hết hạn' } },
];

const PricePolicyImportPage = () => {
    const importBulk = useImportPricePolicyBulk();
    return (
    <ImportWizard
        title="Chính sách giá"
        fields={FIELDS}
        onImport={importBulk}
        backPath="/chinh-sach-gia"
    />
    );
};

export default PricePolicyImportPage;
