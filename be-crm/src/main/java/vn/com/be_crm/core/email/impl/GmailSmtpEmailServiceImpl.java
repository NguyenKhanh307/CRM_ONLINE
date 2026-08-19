package vn.com.be_crm.core.email.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import vn.com.be_crm.core.email.port.IEmailService;

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
     * Gửi email báo giá tới khách hàng/liên hệ với tiêu đề + nội dung do người dùng soạn.
     *
     * @param toEmail      địa chỉ email nhận
     * @param subject      tiêu đề email
     * @param bodyHtml     nội dung message (HTML) — nút "Xem chi tiết báo giá" được tự chèn vào cuối
     * @param responseLink URL trang chi tiết/phản hồi công khai
     */
    @Override
    public void sendQuotationEmail(String toEmail, String subject, String bodyHtml,
                                   String responseLink, byte[] pdf, String pdfFileName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(toEmail);
            helper.setSubject(subject != null && !subject.isBlank() ? subject : "Báo giá");
            helper.setText(wrapQuotationBody(bodyHtml, responseLink), true);
            if (pdf != null && pdf.length > 0) {
                helper.addAttachment(pdfFileName != null ? pdfFileName : "bao-gia.pdf", new ByteArrayResource(pdf));
            }
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Không thể gửi email báo giá: " + e.getMessage(), e);
        }
    }

    /**
     * Gửi email chiến dịch marketing tới một thành viên — tự chèn nút "Xem tại đây" ở cuối.
     *
     * @param toEmail       địa chỉ email nhận
     * @param recipientName tên hiển thị người nhận (có thể null)
     * @param subject       tiêu đề email
     * @param body          nội dung email (HTML hoặc text thuần)
     * @param trackingLink  URL landing page gắn mã chiến dịch (có thể null nếu không cần nút)
     */
    @Override
    public void sendCampaignEmail(String toEmail, String recipientName, String subject, String body, String trackingLink) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(toEmail);
            helper.setSubject(subject != null ? subject : "Thông tin từ chúng tôi");
            helper.setText(buildCampaignBody(recipientName, body, trackingLink), true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Không thể gửi email chiến dịch: " + e.getMessage(), e);
        }
    }

    private String buildCampaignBody(String name, String body, String trackingLink) {
        String greeting = (name == null || name.isBlank()) ? "Kính gửi Quý khách," : "Kính gửi <b>" + name + "</b>,";
        String buttonHtml = (trackingLink == null || trackingLink.isBlank()) ? "" : """
                <p style="text-align:center;margin:24px 0">
                  <a href="%1$s" style="background:#2563eb;color:#fff;padding:12px 24px;border-radius:6px;text-decoration:none;font-weight:bold">Xem tại đây</a>
                </p>
                """.formatted(trackingLink);
        return """
                <div style="font-family:Arial,sans-serif;max-width:600px;margin:0 auto">
                  <p>%s</p>
                  <div style="color:#374151">%s</div>
                  %s
                  <p style="color:#6b7280;font-size:14px">Trân trọng.</p>
                </div>
                """.formatted(greeting, body != null ? body : "", buttonHtml);
    }

    /** Bọc nội dung message (do người dùng soạn) trong khung email + tự chèn nút "Xem chi tiết báo giá" ở cuối. */
    private String wrapQuotationBody(String bodyHtml, String responseLink) {
        String buttonsHtml = (responseLink == null || responseLink.isBlank()) ? "" : """
                <p style="text-align:center;margin:24px 0">
                  <a href="%1$s" style="background:#2563eb;color:#fff;padding:12px 24px;border-radius:6px;text-decoration:none;font-weight:bold">Xem chi tiết báo giá</a>
                </p>
                """.formatted(responseLink);
        return """
                <div style="font-family:Arial,sans-serif;max-width:600px;margin:0 auto">
                  %s
                  %s
                </div>
                """.formatted(bodyHtml != null ? bodyHtml : "", buttonsHtml);
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
