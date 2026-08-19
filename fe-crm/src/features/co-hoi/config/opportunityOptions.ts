// loại cơ hội — dùng chung cho OpportunityAddPage và OpportunityEditModal
// (không có enum backend, giá trị khớp dữ liệu seed hiện có trong diagrams/data1.sql)
export const OPPORTUNITY_TYPE_OPTIONS = [
    { value: 'new_business', label: 'Khách mới' },
    { value: 'existing_business', label: 'Khách cũ' },
];
