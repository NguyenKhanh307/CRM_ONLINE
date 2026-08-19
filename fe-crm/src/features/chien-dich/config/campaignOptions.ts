// kênh chiến dịch — dùng chung cho CampaignAddPage và CampaignEditModal
// (không có enum backend, giá trị khớp dữ liệu seed hiện có trong diagrams/data1.sql —
// value = label vì cột channel là VARCHAR tự do, không có mã kỹ thuật riêng)
export const CHANNEL_OPTIONS = [
    { value: 'Sự kiện offline', label: 'Sự kiện offline' },
    { value: 'Email marketing', label: 'Email marketing' },
    { value: 'Facebook/Zalo', label: 'Facebook/Zalo' },
    { value: 'SEO website', label: 'SEO website' },
    { value: 'Quảng cáo Google Ads', label: 'Quảng cáo Google Ads' },
    { value: 'Webinar online', label: 'Webinar online' },
    { value: 'Kênh khác', label: 'Kênh khác' },
];
