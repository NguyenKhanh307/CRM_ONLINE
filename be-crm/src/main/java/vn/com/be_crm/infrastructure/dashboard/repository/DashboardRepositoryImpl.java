package vn.com.be_crm.infrastructure.dashboard.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.application.dashboard.dto.*;
import vn.com.be_crm.domain.dashboard.model.DateRange;
import vn.com.be_crm.domain.dashboard.repository.IDashboardRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import vn.com.be_crm.core.tx.impl.TxSupport;

/**
 * Hibernate implementation của IDashboardRepository — thống kê tổng hợp bằng native COUNT/SUM.
 */
@Repository
public class DashboardRepositoryImpl implements IDashboardRepository {

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
    private static final DateTimeFormatter YM = DateTimeFormatter.ofPattern("yyyy-MM");

    private final SessionFactory sf;

    /** @param sf Hibernate SessionFactory */
    public DashboardRepositoryImpl(SessionFactory sf) {
        this.sf = sf;
    }

    /** {@inheritDoc} */
    @Override
    public AdminDashboardResult getAdmin(DateRange cur, DateRange prev, LocalDate seriesFrom) {
        return TxSupport.read(sf, s -> {
            long totalNow = count(s, "SELECT COUNT(*) FROM users WHERE deleted_at IS NULL", Map.of());
            long newCur = count(s, "SELECT COUNT(*) FROM users WHERE deleted_at IS NULL AND created_at >= :f AND created_at < :t",
                    Map.of("f", cur.from(), "t", cur.toExclusive()));
            long newPrev = count(s, "SELECT COUNT(*) FROM users WHERE deleted_at IS NULL AND created_at >= :f AND created_at < :t",
                    Map.of("f", prev.from(), "t", prev.toExclusive()));

            List<DonutSegment> byStatus = donut(rows(s,
                    "SELECT status, COUNT(*) FROM users WHERE deleted_at IS NULL GROUP BY status", Map.of()));
            long roleCount = count(s, "SELECT COUNT(*) FROM roles", Map.of());
            long permCount = count(s, "SELECT COUNT(*) FROM permissions", Map.of());
            List<DonutSegment> byRole = donut(rows(s,
                    "SELECT r.name, COUNT(ur.id) FROM user_roles ur JOIN roles r ON r.id = ur.role_id " +
                            "JOIN users u ON u.id = ur.user_id WHERE u.deleted_at IS NULL GROUP BY r.id, r.name ORDER BY r.name", Map.of()));
            List<TimeSeriesPoint> usersByMonth = fillMonths(seriesFrom, rows(s,
                    "SELECT DATE_FORMAT(created_at, '%Y-%m') p, COUNT(*) v FROM users " +
                            "WHERE deleted_at IS NULL AND created_at >= :f GROUP BY p ORDER BY p", Map.of("f", seriesFrom)));

            List<DonutSegment> recordTotals = new ArrayList<>();
            recordTotals.add(recordTotal(s, "Tiềm năng", "leads"));
            recordTotals.add(recordTotal(s, "Khách hàng", "customers"));
            recordTotals.add(recordTotal(s, "Cơ hội", "opportunities"));
            recordTotals.add(recordTotal(s, "Đơn hàng", "orders"));
            recordTotals.add(recordTotal(s, "Hóa đơn", "invoices"));
            recordTotals.add(recordTotal(s, "Chăm sóc", "support_tickets"));
            recordTotals = withPct(recordTotals);

            return new AdminDashboardResult(
                    KpiMetric.of(BigDecimal.valueOf(totalNow), BigDecimal.valueOf(totalNow - newCur)),
                    byStatus,
                    KpiMetric.of(BigDecimal.valueOf(newCur), BigDecimal.valueOf(newPrev)),
                    usersByMonth, roleCount, permCount, byRole, recordTotals);
        });
    }

    /** {@inheritDoc} */
    @Override
    public SalesDashboardResult getSales(Long ownerId, boolean includeTeam, DateRange cur, DateRange prev, LocalDate seriesFrom) {
        return TxSupport.read(sf, s -> {
            String of = ownerId == null ? "" : " AND owner_id = :o";           // lọc theo owner (bảng có owner_id)
            String ofI = ownerId == null ? "" : " AND i.owner_id = :o";        // alias i cho invoices
            String ofTicket = ownerId == null ? "" : " AND assigned_user_id = :o";

            // ----- Tài chính -----
            BigDecimal revCur = sum(s, "SELECT COALESCE(SUM(total),0) FROM invoices WHERE status <> 'cancelled' AND deleted_at IS NULL " +
                    "AND invoice_date >= :f AND invoice_date < :t" + of, dateOwner(cur, ownerId));
            BigDecimal revPrev = sum(s, "SELECT COALESCE(SUM(total),0) FROM invoices WHERE status <> 'cancelled' AND deleted_at IS NULL " +
                    "AND invoice_date >= :f AND invoice_date < :t" + of, dateOwner(prev, ownerId));
            BigDecimal costCur = sum(s, cogsSql(ofI), dateOwner(cur, ownerId));
            BigDecimal costPrev = sum(s, cogsSql(ofI), dateOwner(prev, ownerId));

            List<TimeSeriesPoint> revByMonth = fillMonths(seriesFrom, rows(s,
                    "SELECT DATE_FORMAT(invoice_date, '%Y-%m') p, COALESCE(SUM(total),0) v FROM invoices " +
                            "WHERE status <> 'cancelled' AND deleted_at IS NULL AND invoice_date >= :f" + of + " GROUP BY p ORDER BY p",
                    seriesOwner(seriesFrom, ownerId)));
            List<TimeSeriesPoint> costByMonth = fillMonths(seriesFrom, rows(s,
                    "SELECT DATE_FORMAT(i.invoice_date, '%Y-%m') p, COALESCE(SUM(ii.quantity * COALESCE(pr.cost_price,0)),0) v " +
                            "FROM invoices i JOIN invoice_items ii ON ii.invoice_id = i.id LEFT JOIN products pr ON pr.id = ii.product_id " +
                            "WHERE i.status <> 'cancelled' AND i.deleted_at IS NULL AND i.invoice_date >= :f" + ofI + " GROUP BY p ORDER BY p",
                    seriesOwner(seriesFrom, ownerId)));
            List<TimeSeriesPoint> profitByMonth = subtractSeries(revByMonth, costByMonth);
            List<TimeSeriesPoint> expectedByMonth = fillMonths(seriesFrom, rows(s,
                    "SELECT DATE_FORMAT(expected_close_date, '%Y-%m') p, COALESCE(SUM(COALESCE(expected_revenue, amount)),0) v " +
                            "FROM opportunities WHERE status = 'open' AND deleted_at IS NULL AND expected_close_date IS NOT NULL " +
                            "AND expected_close_date >= :f" + of + " GROUP BY p ORDER BY p", seriesOwner(seriesFrom, ownerId)));

            // ----- KPI cơ hội -----
            KpiMetric oppTotal = oppKpi(s, "", of, cur, prev, ownerId);
            KpiMetric oppOpen = oppKpi(s, " AND status = 'open'", of, cur, prev, ownerId);
            KpiMetric oppWon = oppKpi(s, " AND status = 'won'", of, cur, prev, ownerId);
            KpiMetric oppLost = oppKpi(s, " AND status = 'lost'", of, cur, prev, ownerId);
            KpiMetric winRate = KpiMetric.of(winRatePct(oppWon.current(), oppLost.current()),
                    winRatePct(oppWon.previous(), oppLost.previous()));

            // ----- Phễu chuyển đổi (không lọc theo kỳ) -----
            List<FunnelStage> funnel = funnel(s, ownerId);

            // ----- Cơ hội giá trị lớn -----
            List<RankedItem> topOpp = rows(s,
                    "SELECT id, name, amount FROM opportunities WHERE deleted_at IS NULL AND status = 'open'" + of +
                            " ORDER BY amount DESC LIMIT 8", ownerId == null ? Map.of() : Map.of("o", ownerId))
                    .stream().map(r -> new RankedItem(((Number) r[0]).longValue(), String.valueOf(r[1]), toBig(r[2]))).toList();

            // ----- Trạng thái -----
            List<DonutSegment> leadsByStatus = donut(rows(s, "SELECT status, COUNT(*) FROM leads WHERE deleted_at IS NULL" + of + " GROUP BY status", owner(ownerId)));
            List<DonutSegment> oppByStatus = donut(rows(s, "SELECT status, COUNT(*) FROM opportunities WHERE deleted_at IS NULL" + of + " GROUP BY status", owner(ownerId)));
            List<DonutSegment> ordersByStatus = donut(rows(s, "SELECT status, COUNT(*) FROM orders WHERE deleted_at IS NULL" + of + " GROUP BY status", owner(ownerId)));
            List<DonutSegment> invByStatus = donut(rows(s, "SELECT status, COUNT(*) FROM invoices WHERE deleted_at IS NULL" + of + " GROUP BY status", owner(ownerId)));
            List<DonutSegment> ticketsByStatus = donut(rows(s, "SELECT status, COUNT(*) FROM support_tickets WHERE deleted_at IS NULL" + ofTicket + " GROUP BY status", owner(ownerId)));

            // ----- Việc gấp -----
            List<UrgentItem> urgent = urgentItems(s, of, ofTicket, ownerId);

            // ----- Riêng manager -----
            List<GroupedStatusRow> teamByOwner = includeTeam ? teamByOwner(s) : null;
            List<RankedItem> revenueByOwner = includeTeam ? revenueByOwner(s, cur) : null;

            return new SalesDashboardResult(
                    KpiMetric.of(revCur, revPrev),
                    KpiMetric.of(costCur, costPrev),
                    KpiMetric.of(revCur.subtract(costCur), revPrev.subtract(costPrev)),
                    revByMonth, costByMonth, profitByMonth,
                    oppTotal, oppOpen, oppWon, oppLost, winRate,
                    expectedByMonth, funnel, topOpp,
                    leadsByStatus, oppByStatus, ordersByStatus, invByStatus, ticketsByStatus,
                    urgent, teamByOwner, revenueByOwner);
        });
    }

    // ==================== Helpers truy vấn ====================

    /** SQL tính giá vốn (COGS) theo kỳ; {@code ofI} = mệnh đề lọc owner trên alias i. */
    private String cogsSql(String ofI) {
        return "SELECT COALESCE(SUM(ii.quantity * COALESCE(pr.cost_price,0)),0) FROM invoices i " +
                "JOIN invoice_items ii ON ii.invoice_id = i.id LEFT JOIN products pr ON pr.id = ii.product_id " +
                "WHERE i.status <> 'cancelled' AND i.deleted_at IS NULL AND i.invoice_date >= :f AND i.invoice_date < :t" + ofI;
    }

    /** Tính KpiMetric số cơ hội theo bộ lọc trạng thái, so kỳ hiện tại/kỳ trước. */
    private KpiMetric oppKpi(Session s, String statusFilter, String of, DateRange cur, DateRange prev, Long ownerId) {
        String sql = "SELECT COUNT(*) FROM opportunities WHERE deleted_at IS NULL AND created_at >= :f AND created_at < :t" + statusFilter + of;
        long c = count(s, sql, dateOwner(cur, ownerId));
        long p = count(s, sql, dateOwner(prev, ownerId));
        return KpiMetric.of(BigDecimal.valueOf(c), BigDecimal.valueOf(p));
    }

    /** Tỉ lệ thắng = won / (won + lost) * 100. */
    private BigDecimal winRatePct(BigDecimal won, BigDecimal lost) {
        BigDecimal denom = won.add(lost);
        if (denom.signum() == 0) return BigDecimal.ZERO;
        return won.multiply(HUNDRED).divide(denom, 1, RoundingMode.HALF_UP);
    }

    /** Phễu chuyển đổi theo giai đoạn pipeline; owner tùy chọn. */
    private List<FunnelStage> funnel(Session s, Long ownerId) {
        String join = ownerId == null ? "" : " AND o.owner_id = :o";
        List<Object[]> rs = rows(s,
                "SELECT st.name, COUNT(o.id) FROM opportunity_stages st " +
                        "LEFT JOIN opportunities o ON o.stage_id = st.id AND o.deleted_at IS NULL" + join +
                        " GROUP BY st.id, st.name, st.sort_order ORDER BY st.sort_order",
                owner(ownerId));
        long first = rs.isEmpty() ? 0 : ((Number) rs.get(0)[1]).longValue();
        List<FunnelStage> out = new ArrayList<>();
        for (Object[] r : rs) {
            long c = ((Number) r[1]).longValue();
            BigDecimal pct = first == 0 ? BigDecimal.ZERO
                    : BigDecimal.valueOf(c).multiply(HUNDRED).divide(BigDecimal.valueOf(first), 1, RoundingMode.HALF_UP);
            out.add(new FunnelStage(String.valueOf(r[0]), c, pct));
        }
        return out;
    }

    /** Danh sách việc gấp: phiếu quá hạn SLA + báo giá sắp hết hạn + hóa đơn quá hạn. */
    private List<UrgentItem> urgentItems(Session s, String of, String ofTicket, Long ownerId) {
        Map<String, Object> po = owner(ownerId);
        List<UrgentItem> out = new ArrayList<>();
        for (Object[] r : rows(s, "SELECT id, code, subject, sla_due_at FROM support_tickets WHERE deleted_at IS NULL " +
                "AND sla_due_at IS NOT NULL AND sla_due_at < NOW() AND status NOT IN ('resolved','closed','rejected')" + ofTicket +
                " ORDER BY sla_due_at ASC LIMIT 6", po)) {
            out.add(new UrgentItem("ticket", ((Number) r[0]).longValue(), str(r[1]), str(r[2]), "Phiếu quá hạn SLA"));
        }
        for (Object[] r : rows(s, "SELECT id, code, valid_until FROM quotations WHERE deleted_at IS NULL AND status = 'sent' " +
                "AND valid_until IS NOT NULL AND valid_until >= CURDATE() AND valid_until <= DATE_ADD(CURDATE(), INTERVAL 7 DAY)" + of +
                " ORDER BY valid_until ASC LIMIT 6", po)) {
            out.add(new UrgentItem("quotation", ((Number) r[0]).longValue(), str(r[1]), "Báo giá sắp hết hạn", "Hết hạn " + str(r[2])));
        }
        for (Object[] r : rows(s, "SELECT id, code, due_date FROM invoices WHERE deleted_at IS NULL AND due_date IS NOT NULL " +
                "AND due_date < CURDATE() AND payment_status <> 'paid' AND status <> 'cancelled'" + of +
                " ORDER BY due_date ASC LIMIT 6", po)) {
            out.add(new UrgentItem("invoice", ((Number) r[0]).longValue(), str(r[1]), "Hóa đơn quá hạn", "Đến hạn " + str(r[2])));
        }
        return out;
    }

    /** Thống kê cơ hội theo trạng thái, gom theo từng nhân viên (tối đa 8 người). */
    private List<GroupedStatusRow> teamByOwner(Session s) {
        List<Object[]> rs = rows(s,
                "SELECT u.full_name, o.status, COUNT(*) FROM opportunities o JOIN users u ON u.id = o.owner_id " +
                        "WHERE o.deleted_at IS NULL GROUP BY u.id, u.full_name, o.status ORDER BY u.full_name", Map.of());
        Map<String, List<DonutSegment>> grouped = new LinkedHashMap<>();
        for (Object[] r : rs) {
            grouped.computeIfAbsent(str(r[0]), k -> new ArrayList<>())
                    .add(new DonutSegment(str(r[1]), ((Number) r[2]).longValue(), BigDecimal.ZERO));
        }
        return grouped.entrySet().stream().limit(8)
                .map(e -> new GroupedStatusRow(e.getKey(), e.getValue())).toList();
    }

    /** {@inheritDoc} */
    @Override
    public List<RankedItem> revenueByCampaign(Long ownerId, DateRange cur) {
        String of = ownerId == null ? "" : " AND i.owner_id = :o";
        return TxSupport.read(sf, s -> rows(s,
                "SELECT c.id, c.name, COALESCE(SUM(i.total),0) v FROM invoices i JOIN campaigns c ON c.id = i.campaign_id " +
                        "WHERE i.status <> 'cancelled' AND i.deleted_at IS NULL AND i.invoice_date >= :f AND i.invoice_date < :t" + of +
                        " GROUP BY c.id, c.name ORDER BY v DESC LIMIT 8", dateOwner(cur, ownerId))
                .stream().map(r -> new RankedItem(((Number) r[0]).longValue(), str(r[1]), toBig(r[2]))).toList());
    }

    /** Doanh thu theo nhân viên trong kỳ (top 8). */
    private List<RankedItem> revenueByOwner(Session s, DateRange cur) {
        return rows(s, "SELECT u.id, u.full_name, COALESCE(SUM(i.total),0) v FROM invoices i JOIN users u ON u.id = i.owner_id " +
                "WHERE i.status <> 'cancelled' AND i.deleted_at IS NULL AND i.invoice_date >= :f AND i.invoice_date < :t " +
                "GROUP BY u.id, u.full_name ORDER BY v DESC LIMIT 8", Map.of("f", cur.from(), "t", cur.toExclusive()))
                .stream().map(r -> new RankedItem(((Number) r[0]).longValue(), str(r[1]), toBig(r[2]))).toList();
    }

    /** Đếm tổng bản ghi 1 bảng nghiệp vụ (chưa xóa mềm) → DonutSegment (pct điền sau). */
    private DonutSegment recordTotal(Session s, String label, String table) {
        long c = count(s, "SELECT COUNT(*) FROM " + table + " WHERE deleted_at IS NULL", Map.of());
        return new DonutSegment(label, c, BigDecimal.ZERO);
    }

    // ==================== Helpers dựng dữ liệu ====================

    /** Dựng danh sách donut kèm % từ các dòng [label, count]. */
    private List<DonutSegment> donut(List<Object[]> rs) {
        long total = rs.stream().mapToLong(r -> ((Number) r[1]).longValue()).sum();
        List<DonutSegment> out = new ArrayList<>();
        for (Object[] r : rs) {
            long c = ((Number) r[1]).longValue();
            out.add(new DonutSegment(str(r[0]), c, pct(c, total)));
        }
        return out;
    }

    /** Điền lại % cho danh sách DonutSegment đã có count. */
    private List<DonutSegment> withPct(List<DonutSegment> in) {
        long total = in.stream().mapToLong(DonutSegment::count).sum();
        return in.stream().map(d -> new DonutSegment(d.label(), d.count(), pct(d.count(), total))).toList();
    }

    /** Đổ các dòng [period, value] vào chuỗi 12 tháng liên tục từ {@code from}. */
    private List<TimeSeriesPoint> fillMonths(LocalDate from, List<Object[]> rs) {
        Map<String, BigDecimal> map = new HashMap<>();
        for (Object[] r : rs) map.put(str(r[0]), toBig(r[1]));
        List<TimeSeriesPoint> out = new ArrayList<>();
        LocalDate m = from.withDayOfMonth(1);
        for (int i = 0; i < 12; i++) {
            String key = m.format(YM);
            out.add(new TimeSeriesPoint(key, map.getOrDefault(key, BigDecimal.ZERO)));
            m = m.plusMonths(1);
        }
        return out;
    }

    /** Hiệu hai chuỗi cùng độ dài (doanh thu − chi phí). */
    private List<TimeSeriesPoint> subtractSeries(List<TimeSeriesPoint> a, List<TimeSeriesPoint> b) {
        List<TimeSeriesPoint> out = new ArrayList<>();
        for (int i = 0; i < a.size(); i++) {
            out.add(new TimeSeriesPoint(a.get(i).period(), a.get(i).value().subtract(b.get(i).value())));
        }
        return out;
    }

    /** % của count trên total, làm tròn 1 chữ số. */
    private BigDecimal pct(long count, long total) {
        if (total == 0) return BigDecimal.ZERO;
        return BigDecimal.valueOf(count).multiply(HUNDRED).divide(BigDecimal.valueOf(total), 1, RoundingMode.HALF_UP);
    }

    // ==================== Helpers native query ====================

    /** Chạy native query trả về 1 giá trị đếm. */
    private long count(Session s, String sql, Map<String, Object> params) {
        var q = s.createNativeQuery(sql, Object.class);
        params.forEach(q::setParameter);
        Object r = q.uniqueResult();
        return r == null ? 0 : ((Number) r).longValue();
    }

    /** Chạy native query trả về 1 giá trị SUM (BigDecimal). */
    private BigDecimal sum(Session s, String sql, Map<String, Object> params) {
        var q = s.createNativeQuery(sql, Object.class);
        params.forEach(q::setParameter);
        return toBig(q.uniqueResult());
    }

    /** Chạy native query trả về nhiều cột. */
    @SuppressWarnings("unchecked")
    private List<Object[]> rows(Session s, String sql, Map<String, Object> params) {
        var q = s.createNativeQuery(sql, Object[].class);
        params.forEach(q::setParameter);
        return q.list();
    }

    /** Map tham số {f,t} + owner tùy chọn. */
    private Map<String, Object> dateOwner(DateRange r, Long ownerId) {
        Map<String, Object> m = new HashMap<>();
        m.put("f", r.from());
        m.put("t", r.toExclusive());
        if (ownerId != null) m.put("o", ownerId);
        return m;
    }

    /** Map tham số {f = seriesFrom} + owner tùy chọn. */
    private Map<String, Object> seriesOwner(LocalDate seriesFrom, Long ownerId) {
        Map<String, Object> m = new HashMap<>();
        m.put("f", seriesFrom);
        if (ownerId != null) m.put("o", ownerId);
        return m;
    }

    /** Map chỉ chứa owner (rỗng nếu null). */
    private Map<String, Object> owner(Long ownerId) {
        return ownerId == null ? Map.of() : Map.of("o", ownerId);
    }

    /** Ép object kết quả về BigDecimal an toàn. */
    private BigDecimal toBig(Object o) {
        if (o == null) return BigDecimal.ZERO;
        if (o instanceof BigDecimal b) return b;
        if (o instanceof Number n) return new BigDecimal(n.toString());
        return new BigDecimal(o.toString());
    }

    /** Ép object về String an toàn. */
    private String str(Object o) {
        return o == null ? "" : o.toString();
    }
}
