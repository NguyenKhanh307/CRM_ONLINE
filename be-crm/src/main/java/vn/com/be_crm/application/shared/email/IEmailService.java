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
}
