package vn.com.be_crm.application.quotation.email;

import vn.com.be_crm.application.quotation.dto.QuotationEmailDraft;
import vn.com.be_crm.core.util.LineItemTotals;
import vn.com.be_crm.domain.contact.entity.Contact;
import vn.com.be_crm.domain.contact.repository.IContactRepository;
import vn.com.be_crm.domain.customer.entity.Customer;
import vn.com.be_crm.domain.customer.repository.ICustomerRepository;
import vn.com.be_crm.domain.quotation.entity.Quotation;
import vn.com.be_crm.domain.quotation.entity.QuotationItem;
import vn.com.be_crm.domain.quotation.repository.IQuotationItemRepository;

import java.math.BigDecimal;
import java.util.List;

// dựng nội dung email báo giá mặc định (tiêu đề + phần message) và resolve người nhận. Dùng
// chung cho: endpoint xem-trước nội dung (FE hiển thị để sửa) và fallback khi gửi. Phần 3 nút
// phản hồi KHÔNG nằm ở đây — được tầng gửi email tự chèn vào cuối.
public class QuotationEmailComposer {

    private final ICustomerRepository customerRepo;
    private final IContactRepository contactRepo;
    private final IQuotationItemRepository itemRepo;

    public QuotationEmailComposer(ICustomerRepository customerRepo, IContactRepository contactRepo,
                                   IQuotationItemRepository itemRepo) {
        this.customerRepo = customerRepo;
        this.contactRepo = contactRepo;
        this.itemRepo = itemRepo;
    }

    // dựng nội dung email mặc định cho một báo giá (email người nhận + tên + tiêu đề + body HTML)
    public QuotationEmailDraft draft(Quotation q) {
        String[] recipient = resolveRecipient(q);
        String toEmail = recipient[0] != null ? recipient[0] : "";
        String name = recipient[1];
        String subject = "Báo giá " + q.getCode();
        List<QuotationItem> items = itemRepo.findAllByQuotationId(q.getId());
        BigDecimal total = LineItemTotals.compute(items,
                QuotationItem::getQuantity, QuotationItem::getUnitPrice, QuotationItem::getDiscount, QuotationItem::getTaxRate).total();
        String body = buildDefaultBody(name, q.getCode(), formatMoney(total), q.getNote());
        return new QuotationEmailDraft(toEmail, name, subject, body);
    }

    // trả về [email, tên hiển thị] ưu tiên liên hệ, sau đó khách hàng
    private String[] resolveRecipient(Quotation q) {
        if (q.getContactId() != null) {
            Contact c = contactRepo.findById(q.getContactId()).orElse(null);
            if (c != null) {
                String email = firstNonBlank(c.getEmail());
                if (email != null) return new String[]{email, c.getFullName()};
            }
        }
        if (q.getCustomerId() != null) {
            Customer c = customerRepo.findById(q.getCustomerId()).orElse(null);
            if (c != null && c.getEmail() != null && !c.getEmail().isBlank()) {
                return new String[]{c.getEmail(), c.getName()};
            }
        }
        return new String[]{null, "Quý khách"};
    }

    // trả về giá trị đầu tiên không rỗng, hoặc null
    private String firstNonBlank(String... values) {
        for (String v : values) {
            if (v != null && !v.isBlank()) return v;
        }
        return null;
    }

    // format tổng tiền dạng "1.234.567 đ" (dấu chấm phân cách nghìn)
    private String formatMoney(BigDecimal total) {
        if (total == null) return "0 đ";
        return String.format("%,d", total.longValue()).replace(',', '.') + " đ";
    }

    // dựng phần message HTML mặc định (không có outer container, không có nút phản hồi)
    private String buildDefaultBody(String name, String code, String total, String note) {
        String noteHtml = (note == null || note.isBlank()) ? ""
                : "<p style=\"color:#374151\">Ghi chú: " + note + "</p>";
        return """
                <h2 style="color:#2563eb">Báo giá %s</h2>
                <p>Kính gửi <b>%s</b>,</p>
                <p>Chúng tôi xin gửi tới Quý khách báo giá <b>%s</b> với tổng giá trị <b>%s</b> (xem chi tiết trong file PDF đính kèm).</p>
                %s
                <p style="color:#6b7280;font-size:14px">Trân trọng cảm ơn.</p>
                """.formatted(code, name, code, total, noteHtml);
    }
}
