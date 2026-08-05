import type { LeadFormState } from '../types/trackingTypes';

// khóa lưu mã tiềm năng web (dạng TNW...) trên máy khách — thay cho cookie tracking thật
export const STORAGE_KEY = 'tnw_lead_code';

// ngưỡng điểm để backend báo cho người phụ trách tiếp cận (khớp `AddLeadScoreUseCase.QUALIFY_THRESHOLD`)
// — không còn tự đổi trạng thái, chỉ còn thông báo `lead_hot`
export const QUALIFY_THRESHOLD = 50;

// điểm cộng khi khách nộp form liên hệ (endpoint /submit cũ, không còn dùng ở trang demo)
export const SUBMIT_POINTS = 30;

// điểm cộng khi khách yêu cầu báo giá các sản phẩm đã chọn
export const REQUEST_QUOTE_POINTS = 30;

// tên tham số UTM mang mã chiến dịch trên URL landing page
export const UTM_PARAM = 'utm_campaign';

// nội dung phần đầu trang — giải thích đây là trang demo, không phải nội dung marketing thật
export const HERO_CONTENT = {
    title: 'Landing page',
    subtitle: 'Đây là trang demo cho một landing page của doanh nghiệp — dùng để mô phỏng khi khách hàng '
        + 'tiềm năng tiếp cận trang giới thiệu và thể hiện sự quan tâm đến mặt hàng của doanh nghiệp.',
};

// form liên hệ rỗng
export const EMPTY_FORM: LeadFormState = { name: '', companyName: '', email: '', phone: '', note: '' };
