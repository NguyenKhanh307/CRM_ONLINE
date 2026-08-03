import type { LeadFormState } from '../types/trackingTypes';

// khóa lưu mã tiềm năng web (dạng TNW...) trên máy khách — thay cho cookie tracking thật
export const STORAGE_KEY = 'tnw_lead_code';

// ngưỡng điểm để backend tự chuyển tiềm năng sang `qualified` (khớp `AddLeadScoreUseCase.QUALIFY_THRESHOLD`)
export const QUALIFY_THRESHOLD = 50;

// điểm cộng khi khách nộp form liên hệ
export const SUBMIT_POINTS = 30;

// tên tham số UTM mang mã chiến dịch trên URL landing page
export const UTM_PARAM = 'utm_campaign';

// địa chỉ landing page giả lập — chỉ để minh họa đường link quảng cáo thật trông thế nào
export const LANDING_BASE_URL = 'https://landing.congty-demo.vn/giai-phap-crm';

// một hành vi mô phỏng trên website: bấm nút -> ghi `lead_tracking_events` + cộng điểm
export interface TrackingAction {
    action: string;
    label: string;
    points: number;
    hint: string;
}

// các hành vi mô phỏng, xếp theo mức độ thể hiện ý định mua tăng dần
// tổng điểm của cả 5 nút là 80 — vượt ngưỡng 50 nên demo luôn tới được trạng thái `qualified`
export const TRACKING_ACTIONS: TrackingAction[] = [
    { action: 'watch_demo', label: 'Xem video demo', points: 10, hint: 'Mới tìm hiểu' },
    { action: 'subscribe', label: 'Đăng ký nhận tin', points: 10, hint: 'Quan tâm nhẹ' },
    { action: 'view_pricing', label: 'Xem bảng giá', points: 15, hint: 'Bắt đầu cân nhắc' },
    { action: 'download_brochure', label: 'Tải brochure', points: 20, hint: 'Đang so sánh' },
    { action: 'request_quote', label: 'Yêu cầu báo giá', points: 25, hint: 'Ý định mua rõ' },
];

// form liên hệ rỗng
export const EMPTY_FORM: LeadFormState = { name: '', companyName: '', email: '', phone: '', note: '' };

// dựng url quảng cáo minh họa cho một mã chiến dịch — chính là đường link mà web tracking thật
// đọc `utm_campaign` để quy nguồn tiềm năng; campaignCode rỗng thì trả url chưa gắn nguồn
export const buildLandingUrl = (campaignCode: string | null): string =>
    campaignCode
        ? `${LANDING_BASE_URL}?utm_source=google&utm_medium=cpc&${UTM_PARAM}=${campaignCode}`
        : LANDING_BASE_URL;
