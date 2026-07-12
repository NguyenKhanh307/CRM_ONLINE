package vn.com.be_crm.presentation.quotation.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Body cho hành động gửi email báo giá — người nhận, CC/BCC, tiêu đề và nội dung do người dùng soạn
 * (đều tùy chọn). Bỏ trống trường nào thì backend dùng giá trị mặc định cho trường đó.
 */
@Getter
@Setter
@NoArgsConstructor
public class SendQuotationRequest {
    /** Người nhận chính (bỏ trống → email của liên hệ/khách hàng trên báo giá). */
    private String to;
    /** Danh sách CC, phân tách bởi dấu phẩy hoặc chấm phẩy. */
    private String cc;
    /** Danh sách BCC, phân tách bởi dấu phẩy hoặc chấm phẩy. */
    private String bcc;
    /** Tiêu đề email (bỏ trống → dùng mặc định "Báo giá {mã}"). */
    private String subject;
    /** Nội dung message HTML (bỏ trống → dùng nội dung mặc định). */
    private String body;
}
