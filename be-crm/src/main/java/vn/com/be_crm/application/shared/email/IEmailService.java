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
     * Gửi email báo giá tới khách hàng/liên hệ.
     *
     * @param toEmail        địa chỉ email nhận
     * @param recipientName  tên hiển thị người nhận
     * @param quotationCode  mã báo giá
     * @param total          tổng tiền báo giá (đã format sẵn để hiển thị)
     * @param note           ghi chú kèm theo (có thể null)
     */
    void sendQuotationEmail(String toEmail, String recipientName, String quotationCode, String total, String note);
}
