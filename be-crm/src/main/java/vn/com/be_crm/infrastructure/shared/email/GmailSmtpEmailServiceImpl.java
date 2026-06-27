package vn.com.be_crm.infrastructure.shared.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import vn.com.be_crm.application.shared.email.IEmailService;

/**
 * Gửi email qua Gmail SMTP bằng JavaMailSender.
 * Cần cấu hình spring.mail.* trong application.properties với App Password Gmail.
 */
@Component
public class GmailSmtpEmailServiceImpl implements IEmailService {

    private final JavaMailSender mailSender;

    /** @param mailSender Spring JavaMailSender được inject từ auto-configuration */
    public GmailSmtpEmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Gửi email kích hoạt tài khoản với link hết hạn sau 1 ngày.
     *
     * @param toEmail        địa chỉ email nhận
     * @param recipientName  tên hiển thị người nhận
     * @param activationLink URL kích hoạt đầy đủ
     */
    @Override
    public void sendActivationEmail(String toEmail, String recipientName, String activationLink) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(toEmail);
            helper.setSubject("Kích hoạt tài khoản CRM của bạn");
            helper.setText(buildHtmlBody(recipientName, activationLink), true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Không thể gửi email kích hoạt: " + e.getMessage(), e);
        }
    }

    /**
     * Gửi email báo giá tới khách hàng/liên hệ.
     *
     * @param toEmail       địa chỉ email nhận
     * @param recipientName tên hiển thị người nhận
     * @param quotationCode mã báo giá
     * @param total         tổng tiền đã format
     * @param note          ghi chú (có thể null)
     */
    @Override
    public void sendQuotationEmail(String toEmail, String recipientName, String quotationCode, String total, String note) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(toEmail);
            helper.setSubject("Báo giá " + quotationCode);
            helper.setText(buildQuotationBody(recipientName, quotationCode, total, note), true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Không thể gửi email báo giá: " + e.getMessage(), e);
        }
    }

    private String buildQuotationBody(String name, String code, String total, String note) {
        String noteHtml = (note == null || note.isBlank()) ? ""
                : "<p style=\"color:#374151\">Ghi chú: " + note + "</p>";
        return """
                <div style="font-family:Arial,sans-serif;max-width:600px;margin:0 auto">
                  <h2 style="color:#2563eb">Báo giá %s</h2>
                  <p>Kính gửi <b>%s</b>,</p>
                  <p>Chúng tôi xin gửi tới Quý khách báo giá <b>%s</b> với tổng giá trị <b>%s</b>.</p>
                  %s
                  <p style="color:#6b7280;font-size:14px">Trân trọng cảm ơn.</p>
                </div>
                """.formatted(code, name, code, total, noteHtml);
    }

    private String buildHtmlBody(String name, String link) {
        return """
                <div style="font-family:Arial,sans-serif;max-width:600px;margin:0 auto">
                  <h2 style="color:#2563eb">Kích hoạt tài khoản CRM</h2>
                  <p>Xin chào <b>%s</b>,</p>
                  <p>Admin đã tạo tài khoản CRM cho bạn. Nhấn vào nút bên dưới để đặt mật khẩu và kích hoạt tài khoản.</p>
                  <p style="text-align:center;margin:32px 0">
                    <a href="%s"
                       style="background:#2563eb;color:#fff;padding:12px 24px;border-radius:6px;text-decoration:none;font-weight:bold">
                      Kích hoạt tài khoản
                    </a>
                  </p>
                  <p style="color:#6b7280;font-size:14px">Link có hiệu lực trong <b>24 giờ</b>.</p>
                  <p style="color:#6b7280;font-size:14px">Nếu bạn không yêu cầu, hãy bỏ qua email này.</p>
                </div>
                """.formatted(name, link);
    }
}
