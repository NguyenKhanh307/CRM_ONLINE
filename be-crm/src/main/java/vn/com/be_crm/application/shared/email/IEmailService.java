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
     *
     * @param toEmail        địa chỉ email nhận
     * @param recipientName  tên hiển thị người nhận
     * @param quotationCode  mã báo giá
     * @param total          tổng tiền báo giá (đã format sẵn để hiển thị)
     * @param note           ghi chú kèm theo (có thể null)
     * @param responseLink   URL trang phản hồi công khai (kèm token), email gắn ?action=agree|adjust|reject
     * @param pdf            nội dung file PDF bảng báo giá (có thể null nếu không đính kèm)
     * @param pdfFileName    tên file PDF đính kèm
     */
    void sendQuotationEmail(String toEmail, String recipientName, String quotationCode, String total, String note,
                            String responseLink, byte[] pdf, String pdfFileName);
}
