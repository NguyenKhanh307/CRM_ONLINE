import { ImportWizard } from '@/shared/components/import/ImportWizard';
import type { ImportField } from '@/shared/components/import/importTypes';
import { useImportCustomerBulk } from '../hooks/useImportCustomerBulk';

const FIELDS: ImportField[] = [
    { key: 'name',           label: 'Tên khách hàng', required: true, type: 'text' },
    { key: 'shortName',      label: 'Tên viết tắt',                   type: 'text' },
    { key: 'type',           label: 'Loại',                           type: 'enum', enumValues: ['individual', 'company'], enumLabels: { individual: 'Cá nhân', company: 'Công ty' } },
    { key: 'taxCode',        label: 'Mã số thuế',                     type: 'text' },
    { key: 'phone',          label: 'Điện thoại',                     type: 'text' },
    { key: 'email',          label: 'Email',                          type: 'text' },
    { key: 'website',        label: 'Website',                        type: 'text' },
    { key: 'address',        label: 'Địa chỉ',                        type: 'text' },
    { key: 'industry',       label: 'Ngành nghề',                     type: 'text' },
    { key: 'source',         label: 'Nguồn',                          type: 'text' },
    { key: 'status',         label: 'Trạng thái',                     type: 'enum', enumValues: ['active', 'inactive', 'potential'], enumLabels: { active: 'Hoạt động', inactive: 'Không hoạt động', potential: 'Tiềm năng' } },
    { key: 'creditDays',     label: 'Số ngày được nợ',                type: 'number' },
    { key: 'creditLimit',    label: 'Hạn mức nợ tối đa',              type: 'number' },
    { key: 'rating',         label: 'Xếp hạng',                       type: 'enum', enumValues: ['VIP', 'A', 'B', 'C', 'D'] },
    { key: 'employeeSize',   label: 'Quy mô nhân sự',                 type: 'text' },
    { key: 'isDistributor',  label: 'Là nhà phân phối',               type: 'enum', enumValues: ['true', 'false'], enumLabels: { true: 'Có', false: 'Không' } },
];

const CustomerImportPage = () => {
    const importBulk = useImportCustomerBulk();
    return (
    <ImportWizard
        title="Khách hàng"
        fields={FIELDS}
        onImport={importBulk}
        backPath="/khach-hang"
    />
    );
};

export default CustomerImportPage;
