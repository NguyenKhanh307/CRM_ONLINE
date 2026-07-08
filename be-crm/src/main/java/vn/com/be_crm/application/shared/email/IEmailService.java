package vn.com.be_crm.application.shared.email;

/**
 * Port gửi email — application layer không biết chi tiết SMTP hay provider.
 */
public interface IEmailService {

    /**
     * Gửi email kích hoạt tài khoản tới nhân viên mới.
     *
     * @param toEmail        địa chỉ email nhận
     * @param recipientName  tên hiển thị người nhận
     * @param activationLink URL kích hoạt đầy đủ (bao gồm token)
     */
    void sendActivationEmail(String toEmail, String recipientName, String activationLink);

    /**
     * Gửi email báo giá tới khách hàng/liên hệ — đính kèm PDF + 3 nút phản hồi.
     * Tiêu đề và phần nội dung message do người dùng soạn (hoặc lấy mặc định) truyền vào;
     * khối 3 nút phản hồi (kèm token) được tầng gửi tự chèn vào cuối để link luôn đúng.
     *
     * @param toEmail      địa chỉ email nhận
     * @param subject      tiêu đề email
     * @param bodyHtml     nội dung message (HTML) — không gồm 3 nút phản hồi
     * @param responseLink URL trang phản hồi công khai (kèm token), email gắn ?action=agree|adjust|reject
     * @param pdf          nội dung file PDF bảng báo giá (có thể null nếu không đính kèm)
     * @param pdfFileName  tên file PDF đính kèm
     */
    void sendQuotationEmail(String toEmail, String subject, String bodyHtml,
                            String responseLink, byte[] pdf, String pdfFileName);

    /**
     * Gửi email chiến dịch marketing tới một thành viên (người nhận).
     *
     * @param toEmail       địa chỉ email nhận
     * @param recipientName tên hiển thị người nhận (có thể null)
     * @param subject       tiêu đề email chiến dịch
     * @param body          nội dung email (HTML hoặc text)
     */
    void sendCampaignEmail(String toEmail, String recipientName, String subject, String body);
}
