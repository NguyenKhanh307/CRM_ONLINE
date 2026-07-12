package vn.com.be_crm.infrastructure.opportunity.repository;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.application.opportunity.dto.BoardCardResult;
import vn.com.be_crm.application.opportunity.dto.BoardColumnResult;
import vn.com.be_crm.domain.opportunity.repository.IOpportunityBoardRepository;
import vn.com.be_crm.infrastructure.shared.tx.TxSupport;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Hibernate implementation của IOpportunityBoardRepository — nạp cả bảng Kanban bằng 3 native query
 * trong một session: (1) giai đoạn, (2) đếm + tổng tiền mỗi giai đoạn, (3) top 50 thẻ mỗi giai đoạn
 * (window function ROW_NUMBER — TiDB tương thích MySQL 8).
 */
@Repository
public class OpportunityBoardRepositoryImpl implements IOpportunityBoardRepository {

    /** Số thẻ tối đa nạp cho mỗi cột; phần dư FE mời sang trang danh sách. */
    private static final int CARDS_PER_COLUMN = 50;

    private final SessionFactory sf;

    /** @param sf Hibernate SessionFactory */
    public OpportunityBoardRepositoryImpl(SessionFactory sf) {
        this.sf = sf;
    }

    /** {@inheritDoc} */
    @Override
    public List<BoardColumnResult> getBoard(Long ownerId, Integer dataAccessFromYear, String q) {
        boolean hasQ = q != null && !q.isBlank();
        String filter = ""
                + (ownerId != null ? " AND o.owner_id = :owner" : "")
                + (dataAccessFromYear != null ? " AND YEAR(o.created_at) >= :fromYear" : "")
                + (hasQ ? " AND (o.code LIKE :q OR o.name LIKE :q)" : "");

        return TxSupport.read(sf, s -> {
            List<Object[]> stages = s.createNativeQuery(
                    "SELECT id, name, sort_order, is_won, is_lost FROM opportunity_stages ORDER BY sort_order, id",
                    Object[].class).list();

            var statsQuery = s.createNativeQuery(
                    "SELECT o.stage_id, COUNT(*), COALESCE(SUM(o.amount), 0) FROM opportunities o " +
                            "WHERE o.deleted_at IS NULL" + filter + " GROUP BY o.stage_id", Object[].class);

            var cardsQuery = s.createNativeQuery(
                    "SELECT t.id, t.code, t.name, t.cust, t.owner, t.amount, t.expected_close_date, t.probability, t.stage_id FROM (" +
                            "SELECT o.id, o.code, o.name, c.name AS cust, u.full_name AS owner, o.amount, " +
                            "o.expected_close_date, o.probability, o.stage_id, " +
                            "ROW_NUMBER() OVER (PARTITION BY o.stage_id ORDER BY o.updated_at DESC, o.id DESC) AS rn " +
                            "FROM opportunities o " +
                            "LEFT JOIN customers c ON c.id = o.customer_id " +
                            "LEFT JOIN users u ON u.id = o.owner_id " +
                            "WHERE o.deleted_at IS NULL" + filter +
                            ") t WHERE t.rn <= :limit", Object[].class)
                    .setParameter("limit", CARDS_PER_COLUMN);

            for (var query : List.of(statsQuery, cardsQuery)) {
                if (ownerId != null) query.setParameter("owner", ownerId);
                if (dataAccessFromYear != null) query.setParameter("fromYear", dataAccessFromYear);
                if (hasQ) query.setParameter("q", "%" + q.trim() + "%");
            }

            Map<Long, long[]> counts = new HashMap<>();          // stageId → [total]
            Map<Long, BigDecimal> sums = new HashMap<>();
            for (Object[] r : statsQuery.list()) {
                Long stageId = num(r[0]);
                counts.put(stageId, new long[]{((Number) r[1]).longValue()});
                sums.put(stageId, toBig(r[2]));
            }

            Map<Long, List<BoardCardResult>> cards = new LinkedHashMap<>();
            for (Object[] r : cardsQuery.list()) {
                Long stageId = num(r[8]);
                cards.computeIfAbsent(stageId, k -> new ArrayList<>()).add(new BoardCardResult(
                        num(r[0]), str(r[1]), str(r[2]), str(r[3]), str(r[4]),
                        toBig(r[5]), toDate(r[6]), toBig(r[7]), stageId));
            }

            List<BoardColumnResult> out = new ArrayList<>();
            for (Object[] st : stages) {
                Long stageId = num(st[0]);
                long total = counts.containsKey(stageId) ? counts.get(stageId)[0] : 0;
                out.add(new BoardColumnResult(
                        stageId, str(st[1]), st[2] == null ? 0 : ((Number) st[2]).intValue(),
                        bool(st[3]), bool(st[4]),
                        total, sums.getOrDefault(stageId, BigDecimal.ZERO),
                        cards.getOrDefault(stageId, List.of())));
            }
            return out;
        });
    }

    /** Đổi giá trị cột số nguyên sang Long (null-safe). */
    private Long num(Object v) {
        return v == null ? null : ((Number) v).longValue();
    }

    /** Đổi cột TINYINT(1) sang boolean — driver MySQL trả Boolean, TiDB có thể trả Number. */
    private boolean bool(Object v) {
        if (v instanceof Boolean b) return b;
        return v instanceof Number n && n.intValue() != 0;
    }

    /** Đổi cột DATE/DATETIME sang LocalDate (null-safe). */
    private LocalDate toDate(Object v) {
        if (v == null) return null;
        if (v instanceof java.sql.Date d) return d.toLocalDate();
        if (v instanceof Timestamp ts) return ts.toLocalDateTime().toLocalDate();
        if (v instanceof LocalDate d) return d;
        return null;
    }

    /** Đổi cột số sang BigDecimal (null → 0). */
    private BigDecimal toBig(Object v) {
        if (v == null) return BigDecimal.ZERO;
        if (v instanceof BigDecimal b) return b;
        return BigDecimal.valueOf(((Number) v).doubleValue());
    }

    /** Đổi cột chuỗi sang String (null-safe). */
    private String str(Object v) {
        return v == null ? null : String.valueOf(v);
    }
}
