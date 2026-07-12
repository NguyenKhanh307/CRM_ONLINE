package vn.com.be_crm.infrastructure.related.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.application.related.dto.CustomerRelatedResult;
import vn.com.be_crm.application.related.dto.OpportunityRelatedResult;
import vn.com.be_crm.application.related.dto.RelatedGroup;
import vn.com.be_crm.application.related.dto.RelatedRecord;
import vn.com.be_crm.domain.related.repository.IRelatedRepository;
import vn.com.be_crm.infrastructure.shared.tx.TxSupport;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Hibernate implementation của IRelatedRepository — gom bản ghi liên quan bằng native SQL,
 * tất cả trong MỘT session (TxSupport.read) để tránh N+1 và nhiều round-trip.
 * Tên người phụ trách resolve ngay trong SQL bằng LEFT JOIN users → không cần INameResolver.
 */
@Repository
public class RelatedRepositoryImpl implements IRelatedRepository {

    /** Số dòng tối đa nạp cho mỗi nhóm (tổng số thật vẫn trả qua COUNT). */
    private static final int LIMIT = 50;

    private final SessionFactory sf;

    /** @param sf Hibernate SessionFactory */
    public RelatedRepositoryImpl(SessionFactory sf) {
        this.sf = sf;
    }

    /** {@inheritDoc} */
    @Override
    public CustomerRelatedResult getCustomerRelated(Long customerId) {
        Map<String, Object> p = Map.of("id", customerId);
        return TxSupport.read(sf, s -> new CustomerRelatedResult(
                group(s, "contact", CONTACTS_SQL, "contacts c", "c.deleted_at IS NULL AND c.customer_id = :id", p),
                group(s, "opportunity", OPPORTUNITIES_SQL, "opportunities o", "o.deleted_at IS NULL AND o.customer_id = :id", p),
                group(s, "quotation", quotationsSql("q.customer_id = :id"), "quotations q", "q.deleted_at IS NULL AND q.customer_id = :id", p),
                group(s, "order", ordersSql("o.customer_id = :id"), "orders o", "o.deleted_at IS NULL AND o.customer_id = :id", p),
                group(s, "invoice", invoicesSql("i.customer_id = :id"), "invoices i", "i.deleted_at IS NULL AND i.customer_id = :id", p),
                group(s, "ticket", TICKETS_SQL, "support_tickets t", "t.deleted_at IS NULL AND t.customer_id = :id", p),
                group(s, "activity", activitiesSql("customer"), "activities a",
                        "(a.target_type = 'customer' AND a.target_id = :id) OR (a.related_type = 'customer' AND a.related_id = :id)", p)));
    }

    /** {@inheritDoc} */
    @Override
    public OpportunityRelatedResult getOpportunityRelated(Long opportunityId) {
        Map<String, Object> p = Map.of("id", opportunityId);
        return TxSupport.read(sf, s -> new OpportunityRelatedResult(
                group(s, "quotation", quotationsSql("q.opportunity_id = :id"), "quotations q", "q.deleted_at IS NULL AND q.opportunity_id = :id", p),
                group(s, "order", ordersSql("o.opportunity_id = :id"), "orders o", "o.deleted_at IS NULL AND o.opportunity_id = :id", p),
                group(s, "invoice", invoicesSql("i.opportunity_id = :id"), "invoices i", "i.deleted_at IS NULL AND i.opportunity_id = :id", p),
                group(s, "activity", activitiesSql("opportunity"), "activities a",
                        "(a.target_type = 'opportunity' AND a.target_id = :id) OR (a.related_type = 'opportunity' AND a.related_id = :id)", p)));
    }

    // ==================== SQL từng phân hệ ====================
    // Mọi câu SELECT trả đúng 7 cột theo thứ tự: id, code, name, status, date, amount, owner_name

    private static final String CONTACTS_SQL =
            "SELECT c.id, c.email, c.full_name, c.title, c.created_at, NULL, u.full_name " +
                    "FROM contacts c LEFT JOIN users u ON u.id = c.assigned_user_id " +
                    "WHERE c.deleted_at IS NULL AND c.customer_id = :id ORDER BY c.is_primary DESC, c.id DESC LIMIT " + LIMIT;

    private static final String OPPORTUNITIES_SQL =
            "SELECT o.id, o.code, o.name, o.status, o.expected_close_date, o.amount, u.full_name " +
                    "FROM opportunities o LEFT JOIN users u ON u.id = o.owner_id " +
                    "WHERE o.deleted_at IS NULL AND o.customer_id = :id ORDER BY o.id DESC LIMIT " + LIMIT;

    private static final String TICKETS_SQL =
            "SELECT t.id, t.code, t.subject, t.status, t.created_at, NULL, u.full_name " +
                    "FROM support_tickets t LEFT JOIN users u ON u.id = t.assigned_user_id " +
                    "WHERE t.deleted_at IS NULL AND t.customer_id = :id ORDER BY t.id DESC LIMIT " + LIMIT;

    /** Báo giá — {@code cond} là điều kiện liên kết (theo khách hàng hoặc theo cơ hội). */
    private String quotationsSql(String cond) {
        return "SELECT q.id, q.code, q.code, q.status, q.quote_date, q.total, u.full_name " +
                "FROM quotations q LEFT JOIN users u ON u.id = q.owner_id " +
                "WHERE q.deleted_at IS NULL AND " + cond + " ORDER BY q.id DESC LIMIT " + LIMIT;
    }

    /** Đơn hàng — {@code cond} là điều kiện liên kết. */
    private String ordersSql(String cond) {
        return "SELECT o.id, o.code, o.code, o.status, o.order_date, o.total, u.full_name " +
                "FROM orders o LEFT JOIN users u ON u.id = o.owner_id " +
                "WHERE o.deleted_at IS NULL AND " + cond + " ORDER BY o.id DESC LIMIT " + LIMIT;
    }

    /** Hóa đơn — {@code cond} là điều kiện liên kết. */
    private String invoicesSql(String cond) {
        return "SELECT i.id, i.code, i.code, i.status, i.invoice_date, i.total, u.full_name " +
                "FROM invoices i LEFT JOIN users u ON u.id = i.owner_id " +
                "WHERE i.deleted_at IS NULL AND " + cond + " ORDER BY i.id DESC LIMIT " + LIMIT;
    }

    /**
     * Hoạt động gắn với đối tượng đa hình — khớp cả target_* lẫn related_*.
     * Activities KHÔNG có soft delete nên không lọc deleted_at.
     *
     * @param type giá trị target_type/related_type ("customer" hoặc "opportunity")
     */
    private String activitiesSql(String type) {
        return "SELECT a.id, a.type, a.subject, a.status, COALESCE(a.due_at, a.created_at), NULL, u.full_name " +
                "FROM activities a LEFT JOIN users u ON u.id = a.assigned_user_id " +
                "WHERE (a.target_type = '" + type + "' AND a.target_id = :id) " +
                "OR (a.related_type = '" + type + "' AND a.related_id = :id) " +
                "ORDER BY a.id DESC LIMIT " + LIMIT;
    }

    // ==================== Helpers ====================

    /**
     * Chạy câu SELECT danh sách + câu COUNT tương ứng, trả về một nhóm bản ghi liên quan.
     *
     * @param s         session Hibernate
     * @param module    khóa phân hệ gắn vào từng dòng (FE dùng để deep-link)
     * @param listSql   câu SELECT 7 cột (đã kèm ORDER BY + LIMIT)
     * @param countFrom mệnh đề FROM của câu đếm (vd "quotations q")
     * @param countWhere mệnh đề WHERE của câu đếm
     * @param params    tham số bind
     * @return nhóm gồm danh sách rút gọn + tổng số thật
     */
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

    /** Map một dòng 7 cột thành RelatedRecord. */
    private RelatedRecord toRecord(String module, Object[] r) {
        return new RelatedRecord(module,
                r[0] == null ? null : ((Number) r[0]).longValue(),
                str(r[1]), str(r[2]), str(r[3]), toDateTime(r[4]), toBig(r[5]), str(r[6]));
    }

    /** Đổi giá trị cột DATE/DATETIME của MySQL/TiDB sang LocalDateTime. */
    private LocalDateTime toDateTime(Object v) {
        if (v == null) return null;
        if (v instanceof Timestamp ts) return ts.toLocalDateTime();
        if (v instanceof java.sql.Date d) return d.toLocalDate().atStartOfDay();
        if (v instanceof LocalDateTime dt) return dt;
        if (v instanceof LocalDate d) return d.atStartOfDay();
        return null;
    }

    /** Đổi giá trị cột số sang BigDecimal (null-safe). */
    private BigDecimal toBig(Object v) {
        if (v == null) return null;
        if (v instanceof BigDecimal b) return b;
        return BigDecimal.valueOf(((Number) v).doubleValue());
    }

    /** Đổi giá trị cột chuỗi sang String (null-safe). */
    private String str(Object v) {
        return v == null ? null : String.valueOf(v);
    }
}
