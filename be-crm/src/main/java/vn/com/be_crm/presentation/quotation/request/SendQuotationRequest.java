package vn.com.be_crm.presentation.quotation.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Body cho hành động gửi email báo giá — tiêu đề + nội dung do người dùng soạn (đều tùy chọn).
 * Bỏ trống trường nào thì backend dùng nội dung mặc định cho trường đó.
 */
@Getter
@Setter
@NoArgsConstructor
public class SendQuotationRequest {
    /** Tiêu đề email (bỏ trống → dùng mặc định "Báo giá {mã}"). */
    private String subject;
    /** Nội dung message HTML (bỏ trống → dùng nội dung mặc định). */
    private String body;
}
