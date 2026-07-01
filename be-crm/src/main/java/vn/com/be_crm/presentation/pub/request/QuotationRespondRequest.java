package vn.com.be_crm.presentation.pub.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Body cho phản hồi báo giá của khách hàng từ trang công khai.
 */
@Getter
@Setter
@NoArgsConstructor
public class QuotationRespondRequest {
    /** Hành động phản hồi: accept | adjust | reject. */
    private String action;
    /** Nội dung điều chỉnh / lý do (tùy chọn). */
    private String note;
}
