package vn.com.be_crm.application.quotation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Lệnh gửi email báo giá cho khách hàng.
 * Người nhận/CC/BCC do người dùng soạn; bỏ trống `to` thì lấy email của liên hệ/khách hàng trên báo giá.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendQuotationCommand {

    /** ID báo giá — lấy từ path, KHÔNG đặt @NotNull. */
    private Long id;

    /** Người nhận chính (bỏ trống → mặc định từ liên hệ/khách hàng). */
    private String to;

    /** Danh sách CC, phân tách bởi dấu phẩy hoặc chấm phẩy. */
    private String cc;

    /** Danh sách BCC, phân tách bởi dấu phẩy hoặc chấm phẩy. */
    private String bcc;

    /** Tiêu đề (bỏ trống → mặc định). */
    private String subject;

    /** Nội dung HTML (bỏ trống → mặc định). */
    private String body;
}
