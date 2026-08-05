package vn.com.be_crm.infrastructure.related.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.application.related.dto.CampaignRelatedResult;
import vn.com.be_crm.application.related.dto.ContactRelatedResult;
import vn.com.be_crm.application.related.dto.CustomerRelatedResult;
import vn.com.be_crm.application.related.dto.InvoiceRelatedResult;
import vn.com.be_crm.application.related.dto.LeadRelatedResult;
import vn.com.be_crm.application.related.dto.OpportunityRelatedResult;
import vn.com.be_crm.application.related.dto.OrderRelatedResult;
import vn.com.be_crm.application.related.dto.QuotationRelatedResult;
import vn.com.be_crm.application.related.dto.RelatedGroup;
import vn.com.be_crm.application.related.dto.RelatedRecord;
import vn.com.be_crm.domain.related.repository.IRelatedRepository;
import vn.com.be_crm.core.tx.impl.TxSupport;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

// Hibernate implementation của IRelatedRepository — gom bản ghi liên quan bằng native SQL,
// tất cả trong MỘT session (TxSupport.read) để tránh N+1 và nhiều round-trip.
// Tên người phụ trách resolve ngay trong SQL bằng LEFT JOIN users → không cần INameResolver.
// orders/invoices/support_tickets không còn customer_id/contact_id/opportunity_id/campaign_id trực
// tiếp (chỉ còn quotationId/orderId để tra ngược) — ordersSql/invoicesSql/ticketsSql LUÔN kèm sẵn
// LEFT JOIN qua chuỗi Order -> Quotation -> Opportunity để mọi điều kiện lọc theo khách hàng/liên hệ/
// cơ hội/chiến dịch vẫn dùng được (xem alias q2/opp2 trong từng hàm dựng SQL).
@Repository
public class RelatedRepositoryImpl implements IRelatedRepository {

    // số dòng tối đa nạp cho mỗi nhóm (tổng số thật vẫn trả qua COUNT)
    private static final int LIMIT = 50;
    // công thức thành tiền một dòng — khớp LineItemTotals.lineAmount(), dùng chung cho quotation/order/invoice
    private static final String QUOTATION_LINE_AMOUNT =
            "(SELECT COALESCE(SUM((qi.quantity*qi.unit_price - qi.discount) * (1 + qi.tax_rate/100)),0) FROM quotation_items qi WHERE qi.quotation_id = q.id)";
    private static final String ORDER_LINE_AMOUNT =
            "(SELECT COALESCE(SUM((oi.quantity*oi.unit_price - oi.discount) * (1 + oi.tax_rate/100)),0) FROM order_items oi WHERE oi.order_id = o.id)";
    private static final String INVOICE_LINE_AMOUNT =
            "(SELECT COALESCE(SUM((ii.quantity*ii.unit_price - ii.discount) * (1 + ii.tax_rate/100)),0) FROM invoice_items ii WHERE ii.invoice_id = i.id)";

    private final SessionFactory sf;

    public RelatedRepositoryImpl(SessionFactory sf) {
        this.sf = sf;
    }

    @Override
    public CustomerRelatedResult getCustomerRelated(Long customerId) {
        Map<String, Object> p = Map.of("id", customerId);
        return TxSupport.read(sf, s -> new CustomerRelatedResult(
                group(s, "contact", CONTACTS_SQL, "contacts c", "c.deleted_at IS NULL AND c.customer_id = :id", p),
                group(s, "opportunity", opportunitiesSql("o.customer_id = :id"), "opportunities o", "o.deleted_at IS NULL AND o.customer_id = :id", p),
                group(s, "quotation", quotationsSql("q.customer_id = :id"), "quotations q", "q.deleted_at IS NULL AND q.customer_id = :id", p),
                group(s, "order", ordersSql("q2.customer_id = :id"), "orders o LEFT JOIN quotations q2 ON q2.id = o.quotation_id", "o.deleted_at IS NULL AND q2.customer_id = :id", p),
                group(s, "invoice", invoicesSql("q2.customer_id = :id"), "invoices i LEFT JOIN orders o2 ON o2.id = i.order_id LEFT JOIN quotations q2 ON q2.id = o2.quotation_id", "i.deleted_at IS NULL AND q2.customer_id = :id", p),
                group(s, "ticket", ticketsSql("q2.customer_id = :id"), "support_tickets t LEFT JOIN orders o2 ON o2.id = t.order_id LEFT JOIN quotations q2 ON q2.id = o2.quotation_id", "t.deleted_at IS NULL AND q2.customer_id = :id", p),
                group(s, "activity", activitiesSql("customer"), "activities a",
                        "a.target_type = 'customer' AND a.target_id = :id", p)));
    }

    @Override
    public OpportunityRelatedResult getOpportunityRelated(Long opportunityId) {
        Map<String, Object> p = Map.of("id", opportunityId);
        return TxSupport.read(sf, s -> new OpportunityRelatedResult(
                group(s, "quotation", quotationsSql("q.opportunity_id = :id"), "quotations q", "q.deleted_at IS NULL AND q.opportunity_id = :id", p),
                group(s, "order", ordersSql("q2.opportunity_id = :id"), "orders o LEFT JOIN quotations q2 ON q2.id = o.quotation_id", "o.deleted_at IS NULL AND q2.opportunity_id = :id", p),
                group(s, "invoice", invoicesSql("q2.opportunity_id = :id"), "invoices i LEFT JOIN orders o2 ON o2.id = i.order_id LEFT JOIN quotations q2 ON q2.id = o2.quotation_id", "i.deleted_at IS NULL AND q2.opportunity_id = :id", p),
                group(s, "activity", activitiesSql("opportunity"), "activities a",
                        "a.target_type = 'opportunity' AND a.target_id = :id", p)));
    }

    @Override
    public LeadRelatedResult getLeadRelated(Long leadId) {
        Map<String, Object> p = Map.of("id", leadId);
        String oppCond = "o.id IN (SELECT l.converted_opportunity_id FROM leads l WHERE l.id = :id AND l.converted_opportunity_id IS NOT NULL)";
        return TxSupport.read(sf, s -> new LeadRelatedResult(
                group(s, "opportunity", opportunitiesSql(oppCond), "opportunities o", "o.deleted_at IS NULL AND " + oppCond, p),
                group(s, "activity", activitiesSql("lead"), "activities a",
                        "a.target_type = 'lead' AND a.target_id = :id", p)));
    }

    @Override
    public ContactRelatedResult getContactRelated(Long contactId) {
        Map<String, Object> p = Map.of("id", contactId);
        return TxSupport.read(sf, s -> new ContactRelatedResult(
                group(s, "opportunity", opportunitiesSql("o.contact_id = :id"), "opportunities o", "o.deleted_at IS NULL AND o.contact_id = :id", p),
                group(s, "quotation", quotationsSql("q.contact_id = :id"), "quotations q", "q.deleted_at IS NULL AND q.contact_id = :id", p),
                group(s, "order", ordersSql("q2.contact_id = :id"), "orders o LEFT JOIN quotations q2 ON q2.id = o.quotation_id", "o.deleted_at IS NULL AND q2.contact_id = :id", p),
                group(s, "invoice", invoicesSql("q2.contact_id = :id"), "invoices i LEFT JOIN orders o2 ON o2.id = i.order_id LEFT JOIN quotations q2 ON q2.id = o2.quotation_id", "i.deleted_at IS NULL AND q2.contact_id = :id", p),
                group(s, "ticket", ticketsSql("q2.contact_id = :id"), "support_tickets t LEFT JOIN orders o2 ON o2.id = t.order_id LEFT JOIN quotations q2 ON q2.id = o2.quotation_id", "t.deleted_at IS NULL AND q2.contact_id = :id", p),
                group(s, "activity", activitiesSql("contact"), "activities a",
                        "a.target_type = 'contact' AND a.target_id = :id", p)));
    }

    @Override
    public QuotationRelatedResult getQuotationRelated(Long quotationId) {
        Map<String, Object> p = Map.of("id", quotationId);
        return TxSupport.read(sf, s -> new QuotationRelatedResult(
                group(s, "order", ordersSql("o.quotation_id = :id"), "orders o", "o.deleted_at IS NULL AND o.quotation_id = :id", p),
                group(s, "invoice", invoicesSql("o2.quotation_id = :id"), "invoices i LEFT JOIN orders o2 ON o2.id = i.order_id", "i.deleted_at IS NULL AND o2.quotation_id = :id", p),
                group(s, "activity", activitiesSql("quotation"), "activities a",
                        "a.target_type = 'quotation' AND a.target_id = :id", p)));
    }

    @Override
    public OrderRelatedResult getOrderRelated(Long orderId) {
        Map<String, Object> p = Map.of("id", orderId);
        return TxSupport.read(sf, s -> new OrderRelatedResult(
                group(s, "invoice", invoicesSql("i.order_id = :id"), "invoices i", "i.deleted_at IS NULL AND i.order_id = :id", p),
                group(s, "activity", activitiesSql("order"), "activities a",
                        "a.target_type = 'order' AND a.target_id = :id", p)));
    }

    @Override
    public InvoiceRelatedResult getInvoiceRelated(Long invoiceId) {
        Map<String, Object> p = Map.of("id", invoiceId);
        // support_tickets không còn invoice_id (chỉ còn order_id) — tra qua đơn hàng nguồn của hóa đơn
        String ticketCond = "t.order_id = (SELECT i2.order_id FROM invoices i2 WHERE i2.id = :id)";
        return TxSupport.read(sf, s -> new InvoiceRelatedResult(
                group(s, "ticket", ticketsSql(ticketCond), "support_tickets t", "t.deleted_at IS NULL AND " + ticketCond, p),
                group(s, "activity", activitiesSql("invoice"), "activities a",
                        "a.target_type = 'invoice' AND a.target_id = :id", p)));
    }

    @Override
    public CampaignRelatedResult getCampaignRelated(Long campaignId) {
        Map<String, Object> p = Map.of("id", campaignId);
        return TxSupport.read(sf, s -> new CampaignRelatedResult(
                group(s, "lead", leadsSql("l.campaign_id = :id"), "leads l", "l.deleted_at IS NULL AND l.campaign_id = :id", p),
                group(s, "opportunity", opportunitiesSql("o.campaign_id = :id"), "opportunities o", "o.deleted_at IS NULL AND o.campaign_id = :id", p),
                group(s, "order", ordersSql("opp2.campaign_id = :id"),
                        "orders o LEFT JOIN quotations q2 ON q2.id = o.quotation_id LEFT JOIN opportunities opp2 ON opp2.id = q2.opportunity_id",
                        "o.deleted_at IS NULL AND opp2.campaign_id = :id", p),
                group(s, "invoice", invoicesSql("opp2.campaign_id = :id"),
                        "invoices i LEFT JOIN orders o2 ON o2.id = i.order_id LEFT JOIN quotations q2 ON q2.id = o2.quotation_id LEFT JOIN opportunities opp2 ON opp2.id = q2.opportunity_id",
                        "i.deleted_at IS NULL AND opp2.campaign_id = :id", p)));
    }

    // ==================== SQL từng phân hệ ====================
    // Mọi câu SELECT trả đúng 7 cột theo thứ tự: id, code, name, status, date, amount, owner_name

    private static final String CONTACTS_SQL =
            "SELECT c.id, c.email, c.full_name, c.title, c.created_at, NULL, u.full_name " +
                    "FROM contacts c LEFT JOIN users u ON u.id = c.assigned_user_id " +
                    "WHERE c.deleted_at IS NULL AND c.customer_id = :id ORDER BY c.is_primary DESC, c.id DESC LIMIT " + LIMIT;

    // tiềm năng — cond là điều kiện liên kết (theo chiến dịch). estimated_value đã bỏ khỏi schema
    // mới nên cột "amount" trả NULL
    private String leadsSql(String cond) {
        return "SELECT l.id, l.code, l.name, l.status, l.created_at, NULL, u.full_name " +
                "FROM leads l LEFT JOIN users u ON u.id = l.owner_id " +
                "WHERE l.deleted_at IS NULL AND " + cond + " ORDER BY l.id DESC LIMIT " + LIMIT;
    }

    // cơ hội — cond là điều kiện liên kết (theo khách hàng / liên hệ / cơ hội convert / chiến dịch).
    // expected_close_date đã bỏ khỏi schema mới nên cột "date" dùng created_at
    private String opportunitiesSql(String cond) {
        return "SELECT o.id, o.code, o.name, o.status, o.created_at, o.amount, u.full_name " +
                "FROM opportunities o LEFT JOIN users u ON u.id = o.owner_id " +
                "WHERE o.deleted_at IS NULL AND " + cond + " ORDER BY o.id DESC LIMIT " + LIMIT;
    }

    // phiếu chăm sóc — cond là điều kiện liên kết. support_tickets không còn customer_id/contact_id/
    // invoice_id (chỉ còn order_id) nên LUÔN kèm sẵn join qua Order -> Quotation để cond có thể lọc
    // theo q2.customer_id / q2.contact_id
    private String ticketsSql(String cond) {
        return "SELECT t.id, t.code, t.subject, t.status, t.created_at, NULL, u.full_name " +
                "FROM support_tickets t LEFT JOIN users u ON u.id = t.assigned_user_id " +
                "LEFT JOIN orders o2 ON o2.id = t.order_id LEFT JOIN quotations q2 ON q2.id = o2.quotation_id " +
                "WHERE t.deleted_at IS NULL AND " + cond + " ORDER BY t.id DESC LIMIT " + LIMIT;
    }

    // báo giá — cond là điều kiện liên kết (theo khách hàng hoặc theo cơ hội). Không còn cột total
    // lưu sẵn — tính on-read từ quotation_items
    private String quotationsSql(String cond) {
        return "SELECT q.id, q.code, q.code, q.status, q.quote_date, " + QUOTATION_LINE_AMOUNT + ", u.full_name " +
                "FROM quotations q LEFT JOIN users u ON u.id = q.owner_id " +
                "WHERE q.deleted_at IS NULL AND " + cond + " ORDER BY q.id DESC LIMIT " + LIMIT;
    }

    // đơn hàng — cond là điều kiện liên kết. orders không còn customer_id/contact_id/opportunity_id/
    // campaign_id (chỉ còn quotation_id) nên LUÔN kèm sẵn join qua Quotation (-> Opportunity) để cond
    // có thể lọc theo q2.customer_id / q2.contact_id / q2.opportunity_id / opp2.campaign_id. Không còn
    // cột total lưu sẵn — tính on-read từ order_items
    private String ordersSql(String cond) {
        return "SELECT o.id, o.code, o.code, o.status, o.order_date, " + ORDER_LINE_AMOUNT + ", u.full_name " +
                "FROM orders o LEFT JOIN users u ON u.id = o.owner_id " +
                "LEFT JOIN quotations q2 ON q2.id = o.quotation_id LEFT JOIN opportunities opp2 ON opp2.id = q2.opportunity_id " +
                "WHERE o.deleted_at IS NULL AND " + cond + " ORDER BY o.id DESC LIMIT " + LIMIT;
    }

    // hóa đơn — cond là điều kiện liên kết. invoices không còn customer_id/contact_id/quotation_id/
    // opportunity_id/campaign_id (chỉ còn order_id) nên LUÔN kèm sẵn join qua Order -> Quotation ->
    // Opportunity để cond có thể lọc theo q2.customer_id / q2.contact_id / o2.quotation_id /
    // q2.opportunity_id / opp2.campaign_id. Không còn cột total lưu sẵn — tính on-read từ invoice_items
    private String invoicesSql(String cond) {
        return "SELECT i.id, i.code, i.code, i.status, i.invoice_date, " + INVOICE_LINE_AMOUNT + ", u.full_name " +
                "FROM invoices i LEFT JOIN users u ON u.id = i.owner_id " +
                "LEFT JOIN orders o2 ON o2.id = i.order_id LEFT JOIN quotations q2 ON q2.id = o2.quotation_id " +
                "LEFT JOIN opportunities opp2 ON opp2.id = q2.opportunity_id " +
                "WHERE i.deleted_at IS NULL AND " + cond + " ORDER BY i.id DESC LIMIT " + LIMIT;
    }

    // hoạt động gắn với đối tượng đa hình — khớp target_*. Activities KHÔNG có
    // soft delete nên không lọc deleted_at. type = giá trị target_type (vd "customer")
    private String activitiesSql(String type) {
        return "SELECT a.id, a.type, a.subject, a.status, COALESCE(a.due_at, a.created_at), NULL, u.full_name " +
                "FROM activities a LEFT JOIN users u ON u.id = a.assigned_user_id " +
                "WHERE a.target_type = '" + type + "' AND a.target_id = :id " +
                "ORDER BY a.id DESC LIMIT " + LIMIT;
    }

    // ==================== Helpers ====================

    // chạy câu SELECT danh sách + câu COUNT tương ứng, trả về một nhóm bản ghi liên quan.
    // countFrom = mệnh đề FROM của câu đếm (kèm join nếu cond cần), countWhere = mệnh đề WHERE
    @SuppressWarnings("unchecked")
    private RelatedGroup group(Session s, String module, String listSql, String countFrom, String countWhere,
                               Map<String, Object> params) {
        var lq = s.createNativeQuery(listSql, Object[].class);
        params.forEach(lq::setParameter);
        List<RelatedRecord> items = ((List<Object[]>) lq.list()).stream()
                .map(r -> toRecord(module, r)).toList();

        var cq = s.createNativeQuery("SELECT COUNT(*) FROM " + countFrom + " WHERE " + countWhere, Object.class);
        params.forEach(cq::setParameter);
        Object c = cq.uniqueResult();
        return new RelatedGroup(items, c == null ? 0 : ((Number) c).longValue());
    }

    // map một dòng 7 cột thành RelatedRecord
    private RelatedRecord toRecord(String module, Object[] r) {
        return new RelatedRecord(module,
                r[0] == null ? null : ((Number) r[0]).longValue(),
                str(r[1]), str(r[2]), str(r[3]), toDateTime(r[4]), toBig(r[5]), str(r[6]));
    }

    // đổi giá trị cột DATE/DATETIME của MySQL/TiDB sang LocalDateTime
    private LocalDateTime toDateTime(Object v) {
        if (v == null) return null;
        if (v instanceof Timestamp ts) return ts.toLocalDateTime();
        if (v instanceof java.sql.Date d) return d.toLocalDate().atStartOfDay();
        if (v instanceof LocalDateTime dt) return dt;
        if (v instanceof LocalDate d) return d.atStartOfDay();
        return null;
    }

    // đổi giá trị cột số sang BigDecimal (null-safe)
    private BigDecimal toBig(Object v) {
        if (v == null) return null;
        if (v instanceof BigDecimal b) return b;
        return BigDecimal.valueOf(((Number) v).doubleValue());
    }

    // đổi giá trị cột chuỗi sang String (null-safe)
    private String str(Object v) {
        return v == null ? null : String.valueOf(v);
    }
}
