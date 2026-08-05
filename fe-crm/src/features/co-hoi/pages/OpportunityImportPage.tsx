import { ImportWizard } from '@/shared/components/import/ImportWizard';
import type { ImportField } from '@/shared/components/import/importTypes';
import { useImportOpportunityBulk } from '../hooks/useImportOpportunityBulk';

const FIELDS: ImportField[] = [
    { key: 'code',              label: 'Mã cơ hội (để cập nhật)',          type: 'text' },
    { key: 'name',              label: 'Tên cơ hội',      required: true, type: 'text' },
    { key: 'opportunityType',   label: 'Loại (KH mới/cũ)',                type: 'text' },
    { key: 'customerId',        label: 'ID khách hàng',                   type: 'number' },
    { key: 'contactId',         label: 'ID liên hệ',                      type: 'number' },
    { key: 'stageId',           label: 'ID giai đoạn',                    type: 'number' },
    { key: 'pricePolicyId',     label: 'ID chính sách giá',               type: 'number' },
    { key: 'amount',            label: 'Giá trị',                         type: 'number' },
    { key: 'source',            label: 'Nguồn',                            type: 'text' },
    { key: 'campaignId',        label: 'ID chiến dịch nguồn',              type: 'number' },
    { key: 'winLossReason',     label: 'Lý do thắng/thua',                type: 'text' },
    { key: 'description',       label: 'Mô tả',                            type: 'text' },
    { key: 'status',            label: 'Trạng thái',                       type: 'enum', enumValues: ['open', 'won', 'lost'] },
];

const OpportunityImportPage = () => {
    const importBulk = useImportOpportunityBulk();
    return (
    <ImportWizard
        title="Cơ hội"
        fields={FIELDS}
        onImport={importBulk}
        backPath="/co-hoi"
    />
    );
};

export default OpportunityImportPage;
