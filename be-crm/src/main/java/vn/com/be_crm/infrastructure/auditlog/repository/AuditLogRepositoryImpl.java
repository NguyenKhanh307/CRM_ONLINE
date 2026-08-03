package vn.com.be_crm.infrastructure.auditlog.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.application.auditlog.dto.AuditLogEntry;
import vn.com.be_crm.core.page.PageResult;
import vn.com.be_crm.domain.auditlog.repository.IAuditLogRepository;
import vn.com.be_crm.core.tx.impl.TxSupport;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Hibernate implementation của IAuditLogRepository — KHÔNG có bảng riêng cho nhật ký.
 * Gộp 5 nguồn dữ liệu đã có trong DB bằng UNION ALL native SQL, tất cả trong MỘT session
 * (TxSupport.read, theo mẫu {@code RelatedRepositoryImpl}):
 * <ol>
 *   <li>{@code quotation_approvals} — duyệt/từ chối báo giá</li>
 *   <li>{@code lead_transfers} — bàn giao tiềm năng</li>
 *   <li>{@code ticket_comments} (type='system') — nhật ký tự động của phiếu chăm sóc</li>
 *   <li>{@code notifications} — thông báo hệ thống đã phát</li>
 *   <li>{@code created_by}/{@code updated_by}/{@code deleted_by} của 6 bảng nghiệp vụ chính
 *       (leads/customers/opportunities/quotations/orders/invoices) — "ai tạo/sửa/xóa gần nhất"</li>
 * </ol>
 */
@Repository
public class AuditLogRepositoryImpl implements IAuditLogRepository {

    private final SessionFactory sf;

    /** @param sf Hibernate SessionFactory */
    public AuditLogRepositoryImpl(SessionFactory sf) {
        this.sf = sf;
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("unchecked")
    public PageResult<AuditLogEntry> list(String source, String q, int page, int size) {
        String union = unionSql();
        String sourceFilter = (source != null && !source.isBlank()) ? " AND x.source = :source" : "";
        String searchFilter = (q != null && !q.isBlank())
                ? " AND (x.target_label LIKE :q OR x.note LIKE :q OR x.actor_name LIKE :q)" : "";
        String where = " WHERE 1=1" + sourceFilter + searchFilter;

        String listSql = "SELECT x.source, x.actor_name, x.action, x.target_label, x.note, x.occurred_at " +
                "FROM (" + union + ") x" + where + " ORDER BY x.occurred_at DESC LIMIT :size OFFSET :offset";
        String countSql = "SELECT COUNT(*) FROM (" + union + ") x" + where;

        return TxSupport.read(sf, s -> {
            var lq = s.createNativeQuery(listSql, Object[].class);
            bindParams(lq, source, q);
            lq.setParameter("size", size).setParameter("offset", page * size);
            List<AuditLogEntry> items = ((List<Object[]>) lq.list()).stream().map(this::toEntry).toList();

            var cq = s.createNativeQuery(countSql, Object.class);
            bindParams(cq, source, q);
            Object c = cq.uniqueResult();

            return PageResult.<AuditLogEntry>builder()
                    .items(items).total(c == null ? 0 : ((Number) c).longValue())
                    .page(page).size(size).build();
        });
    }

    /** Gán tham số dùng chung cho cả câu SELECT lẫn COUNT. */
    private void bindParams(org.hibernate.query.Query<?> query, String source, String q) {
        if (source != null && !source.isBlank()) query.setParameter("source", source);
        if (q != null && !q.isBlank()) query.setParameter("q", "%" + q.trim() + "%");
    }

    /** Map một dòng 6 cột thành AuditLogEntry. */
    private AuditLogEntry toEntry(Object[] r) {
        return AuditLogEntry.builder()
                .source(str(r[0])).actorName(str(r[1])).action(str(r[2]))
                .targetLabel(str(r[3])).note(str(r[4])).occurredAt(toDateTime(r[5]))
                .build();
    }

    private String str(Object v) {
        return v == null ? null : v.toString();
    }

    /** Đổi giá trị cột DATE/DATETIME của MySQL/TiDB sang LocalDateTime. */
    private LocalDateTime toDateTime(Object v) {
        if (v == null) return null;
        if (v instanceof Timestamp ts) return ts.toLocalDateTime();
        return (LocalDateTime) v;
    }

    /** UNION ALL của 5 nguồn — mỗi nhánh trả đúng 6 cột: source, actor_name, action, target_label, note, occurred_at. */
    private String unionSql() {
        return String.join(" UNION ALL ",
                quotationApprovalSql(),
                leadTransferSql(),
                ticketCommentSql(),
                notificationSql(),
                recordChangeSql("leads", "Tiềm năng"),
                recordChangeSql("customers", "Khách hàng"),
                recordChangeSql("opportunities", "Cơ hội"),
                recordChangeSql("quotations", "Báo giá"),
                recordChangeSql("orders", "Đơn hàng"),
                recordChangeSql("invoices", "Hóa đơn"));
    }

    private String quotationApprovalSql() {
        return "SELECT 'quotation_approval' AS source, u.full_name AS actor_name, " +
                "CONCAT('Duyệt báo giá - ', qa.status) AS action, CONCAT('Báo giá ', q.code) AS target_label, " +
                "qa.comment AS note, COALESCE(qa.approved_at, qa.created_at) AS occurred_at " +
                "FROM quotation_approvals qa " +
                "LEFT JOIN users u ON u.id = qa.approver_id " +
                "LEFT JOIN quotations q ON q.id = qa.quotation_id";
    }

    private String leadTransferSql() {
        return "SELECT 'lead_transfer' AS source, uf.full_name AS actor_name, " +
                "CONCAT('Bàn giao tiềm năng cho ', COALESCE(ut.full_name, CONCAT('#', lt.to_user_id))) AS action, " +
                "CONCAT('Tiềm năng ', l.code) AS target_label, lt.reason AS note, lt.transferred_at AS occurred_at " +
                "FROM lead_transfers lt " +
                "LEFT JOIN users uf ON uf.id = lt.from_user_id " +
                "LEFT JOIN users ut ON ut.id = lt.to_user_id " +
                "LEFT JOIN leads l ON l.id = lt.lead_id";
    }

    private String ticketCommentSql() {
        return "SELECT 'ticket_comment' AS source, u.full_name AS actor_name, " +
                "'Nhật ký phiếu chăm sóc' AS action, CONCAT('Phiếu ', t.code) AS target_label, " +
                "tc.content AS note, tc.created_at AS occurred_at " +
                "FROM ticket_comments tc " +
                "LEFT JOIN users u ON u.id = tc.author_id " +
                "LEFT JOIN support_tickets t ON t.id = tc.ticket_id " +
                "WHERE tc.type = 'system'";
    }

    private String notificationSql() {
        return "SELECT 'notification' AS source, NULL AS actor_name, n.type AS action, " +
                "n.title AS target_label, n.content AS note, n.created_at AS occurred_at " +
                "FROM notifications n";
    }

    /**
     * Sự kiện tạo/sửa/xóa gần nhất trên một bảng nghiệp vụ chính (created_by/updated_by/deleted_by
     * đã có sẵn — không thêm cột/bảng nào). Người thực hiện ưu tiên updated_by, sau đó created_by.
     *
     * @param table bảng nguồn (đã có cột code/created_at/updated_at/deleted_at/created_by/updated_by)
     * @param label nhãn hiển thị tiếng Việt của phân hệ
     */
    private String recordChangeSql(String table, String label) {
        return "SELECT 'record_change' AS source, u.full_name AS actor_name, " +
                "(CASE WHEN t.deleted_at IS NOT NULL THEN 'Xóa bản ghi' " +
                "WHEN t.updated_at <> t.created_at THEN 'Sửa bản ghi' ELSE 'Tạo bản ghi' END) AS action, " +
                "CONCAT('" + label + " ', t.code) AS target_label, NULL AS note, " +
                "COALESCE(t.deleted_at, t.updated_at, t.created_at) AS occurred_at " +
                "FROM " + table + " t " +
                "LEFT JOIN users u ON u.id = COALESCE(t.updated_by, t.created_by)";
    }
}
