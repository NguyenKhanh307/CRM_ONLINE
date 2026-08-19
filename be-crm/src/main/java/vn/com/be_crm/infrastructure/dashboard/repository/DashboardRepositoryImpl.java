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

// Hibernate implementation của IDashboardRepository — thống kê tổng hợp bằng native COUNT/SUM.
// invoices/orders/quotations không còn cột subtotal/discount/tax/total lưu sẵn (tính on-read từ
// dòng hàng, xem LineItemTotals) — mọi SUM(total) ở đây được thay bằng công thức tương đương tính
// trực tiếp trong SQL: SUM((quantity*unit_price - discount) * (1 + tax_rate/100)) join qua
// invoice_items. Cộng dồn theo dòng cho kết quả TOÁN HỌC TƯƠNG ĐƯƠNG với cộng dồn theo hóa đơn
// (chỉ lệch sai số làm tròn rất nhỏ do làm tròn 2 lần thay vì 1 lần — chấp nhận được ở quy mô dashboard).
@Repository
public class DashboardRepositoryImpl implements IDashboardRepository {

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
    private static final DateTimeFormatter YM = DateTimeFormatter.ofPattern("yyyy-MM");
    // công thức thành tiền một dòng hóa đơn — khớp LineItemTotals.lineAmount()
    private static final String INVOICE_LINE_AMOUNT = "(ii.quantity * ii.unit_price - ii.discount) * (1 + ii.tax_rate / 100)";
    // subquery lần chăm sóc gần nhất của 1 cơ hội, dùng chung cho
    // stalledOpportunities/stalledByOwner
    private static final String ACTIVITY_LAST_TOUCH = "(SELECT target_id, MAX(created_at) last_touch FROM activities WHERE target_type = 'opportunity' GROUP BY target_id)";

    private final SessionFactory sf;

    public DashboardRepositoryImpl(SessionFactory sf) {
        this.sf = sf;
    }

    @Override
    public AdminDashboardResult getAdmin(DateRange cur, DateRange prev, LocalDate seriesFrom) {
        return TxSupport.read(sf, s -> {
            long totalNow = count(s, "SELECT COUNT(*) FROM users WHERE deleted_at IS NULL", Map.of());
            long newCur = count(s,
                    "SELECT COUNT(*) FROM users WHERE deleted_at IS NULL AND created_at >= :f AND created_at < :t",
                    Map.of("f", cur.from(), "t", cur.toExclusive()));
            long newPrev = count(s,
                    "SELECT COUNT(*) FROM users WHERE deleted_at IS NULL AND created_at >= :f AND created_at < :t",
                    Map.of("f", prev.from(), "t", prev.toExclusive()));

            List<DonutSegment> byStatus = donut(rows(s,
                    "SELECT status, COUNT(*) FROM users WHERE deleted_at IS NULL GROUP BY status", Map.of()));
            long roleCount = count(s, "SELECT COUNT(*) FROM roles", Map.of());
            long permCount = count(s, "SELECT COUNT(*) FROM permissions", Map.of());
            List<DonutSegment> byRole = donut(rows(s,
                    "SELECT r.name, COUNT(ur.id) FROM user_roles ur JOIN roles r ON r.id = ur.role_id " +
                            "JOIN users u ON u.id = ur.user_id WHERE u.deleted_at IS NULL GROUP BY r.id, r.name ORDER BY r.name",
                    Map.of()));
            List<TimeSeriesPoint> usersByMonth = fillMonths(seriesFrom, rows(s,
                    "SELECT DATE_FORMAT(created_at, '%Y-%m') p, COUNT(*) v FROM users " +
                            "WHERE deleted_at IS NULL AND created_at >= :f GROUP BY p ORDER BY p",
                    Map.of("f", seriesFrom)));

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

    @Override
    public SalesDashboardResult getSales(Long ownerId, boolean includeTeam, DateRange cur, DateRange prev,
            LocalDate seriesFrom) {
        return TxSupport.read(sf, s -> {

            String of = ownerId == null ? "" : " AND owner_id = :o"; // lọc theo owner (bảng có owner_id)
            String ofI = ownerId == null ? "" : " AND i.owner_id = :o"; // alias i cho invoices

            String ofTicket = ownerId == null ? "" : " AND assigned_user_id = :o";

            // ----- Tài chính (tổng tiền hóa đơn tính từ dòng hàng) -----
            // Tắt — totalRevenue/totalCost/totalProfit/*ByMonth đã cắt khỏi
            // SalesDashboardResult
            // (không FE nào đọc). Mở lại: bỏ comment khối này + cogsSql() bên dưới + 6
            // field
            // tương ứng trong SalesDashboardResult.java + lời gọi constructor cuối method
            // này.
            // BigDecimal revCur = sum(s, invoiceRevenueSql(ofI), dateOwner(cur, ownerId));
            // BigDecimal revPrev = sum(s, invoiceRevenueSql(ofI), dateOwner(prev,
            // ownerId));
            // BigDecimal costCur = sum(s, cogsSql(ofI), dateOwner(cur, ownerId));
            // BigDecimal costPrev = sum(s, cogsSql(ofI), dateOwner(prev, ownerId));
            //
            // List<TimeSeriesPoint> revByMonth = fillMonths(seriesFrom, rows(s,
            // "SELECT DATE_FORMAT(i.invoice_date, '%Y-%m') p, COALESCE(SUM(" +
            // INVOICE_LINE_AMOUNT + "),0) v " +
            // "FROM invoices i JOIN invoice_items ii ON ii.invoice_id = i.id " +
            // "WHERE i.status <> 'cancelled' AND i.deleted_at IS NULL AND i.invoice_date >=
            // :f" + ofI
            // + " GROUP BY p ORDER BY p",
            // seriesOwner(seriesFrom, ownerId)));
            // List<TimeSeriesPoint> costByMonth = fillMonths(seriesFrom, rows(s,
            // "SELECT DATE_FORMAT(i.invoice_date, '%Y-%m') p, COALESCE(SUM(ii.quantity *
            // COALESCE(pr.cost_price,0)),0) v "
            // +
            // "FROM invoices i JOIN invoice_items ii ON ii.invoice_id = i.id LEFT JOIN
            // products pr ON pr.id = ii.product_id "
            // +
            // "WHERE i.status <> 'cancelled' AND i.deleted_at IS NULL AND i.invoice_date >=
            // :f" + ofI
            // + " GROUP BY p ORDER BY p",
            // seriesOwner(seriesFrom, ownerId)));
            // List<TimeSeriesPoint> profitByMonth = subtractSeries(revByMonth,
            // costByMonth);

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
                            " ORDER BY amount DESC LIMIT 8",
                    ownerId == null ? Map.of() : Map.of("o", ownerId))
                    .stream().map(r -> new RankedItem(((Number) r[0]).longValue(), String.valueOf(r[1]), toBig(r[2])))
                    .toList();

            // ----- Trạng thái -----
            List<DonutSegment> oppByStatus = donut(rows(s,
                    "SELECT status, COUNT(*) FROM opportunities WHERE deleted_at IS NULL" + of + " GROUP BY status",
                    owner(ownerId)));
            List<DonutSegment> ordersByStatus = donut(
                    rows(s, "SELECT status, COUNT(*) FROM orders WHERE deleted_at IS NULL" + of + " GROUP BY status",
                            owner(ownerId)));
            List<DonutSegment> invByStatus = donut(
                    rows(s, "SELECT status, COUNT(*) FROM invoices WHERE deleted_at IS NULL" + of + " GROUP BY status",
                            owner(ownerId)));
            List<DonutSegment> ticketsByStatus = donut(
                    rows(s, "SELECT status, COUNT(*) FROM support_tickets WHERE deleted_at IS NULL" + ofTicket
                            + " GROUP BY status", owner(ownerId)));

            // ----- Việc gấp -----
            // Tắt — urgentItems đã cắt khỏi SalesDashboardResult (không FE nào đọc). Mở
            // lại: bỏ
            // comment dòng này + hàm urgentItems() bên dưới + field tương ứng.
            // List<UrgentItem> urgent = urgentItems(s, of, ofTicket, ownerId);

            // ----- Riêng manager -----
            // Tắt — teamByOwner/revenueByOwner đã cắt khỏi SalesDashboardResult (chỉ phục
            // vụ
            // /manager, endpoint đã tắt). Mở lại: bỏ comment 2 dòng này + 2 hàm helper bên
            // dưới +
            // field tương ứng.
            // List<GroupedStatusRow> teamByOwner = includeTeam ? teamByOwner(s) : null;
            // List<RankedItem> revenueByOwner = includeTeam ? revenueByOwner(s, cur) :
            // null;

            // Bản đầy đủ (9 tham số đã cắt:
            // totalRevenue/totalCost/totalProfit/revenueByMonth/
            // costByMonth/profitByMonth/urgentItems/teamByOwner/revenueByOwner) xem comment
            // ở
            // trên + SalesDashboardResult.java. Bản gọn dưới đây chỉ còn phần
            // StaffDashboardView
            // (FE) thực sự đọc.
            return new SalesDashboardResult(
                    oppTotal, oppOpen, oppWon, oppLost, winRate,
                    funnel, topOpp,
                    oppByStatus, ordersByStatus, invByStatus, ticketsByStatus);
        });
    }

    // ==================== Helpers truy vấn ====================

    // Tắt cùng khối "Tài chính" trong getSales() — chỉ dùng ở đó. Mở lại cùng lúc.
    // // SQL tính giá vốn (COGS) theo kỳ; ofI = mệnh đề lọc owner trên alias i
    // private String cogsSql(String ofI) {
    // return "SELECT COALESCE(SUM(ii.quantity * COALESCE(pr.cost_price,0)),0) FROM
    // invoices i " +
    // "JOIN invoice_items ii ON ii.invoice_id = i.id LEFT JOIN products pr ON pr.id
    // = ii.product_id " +
    // "WHERE i.status <> 'cancelled' AND i.deleted_at IS NULL AND i.invoice_date >=
    // :f AND i.invoice_date < :t"
    // + ofI;
    // }

    // SQL tính doanh thu (tổng tiền hóa đơn từ dòng hàng) theo kỳ; ofI = mệnh đề
    // lọc owner trên alias i
    private String invoiceRevenueSql(String ofI) {
        return "SELECT COALESCE(SUM(" + INVOICE_LINE_AMOUNT + "),0) FROM invoices i " +
                "JOIN invoice_items ii ON ii.invoice_id = i.id " +
                "WHERE i.status <> 'cancelled' AND i.deleted_at IS NULL AND i.invoice_date >= :f AND i.invoice_date < :t"
                + ofI;
    }

    // tính KpiMetric số cơ hội theo bộ lọc trạng thái, so kỳ hiện tại/kỳ trước
    private KpiMetric oppKpi(Session s, String statusFilter, String of, DateRange cur, DateRange prev, Long ownerId) {
        String sql = "SELECT COUNT(*) FROM opportunities WHERE deleted_at IS NULL AND created_at >= :f AND created_at < :t"
                + statusFilter + of;
        long c = count(s, sql, dateOwner(cur, ownerId));
        long p = count(s, sql, dateOwner(prev, ownerId));
        return KpiMetric.of(BigDecimal.valueOf(c), BigDecimal.valueOf(p));
    }

    // tỉ lệ thắng = won / (won + lost) * 100
    private BigDecimal winRatePct(BigDecimal won, BigDecimal lost) {
        BigDecimal denom = won.add(lost);
        if (denom.signum() == 0)
            return BigDecimal.ZERO;
        return won.multiply(HUNDRED).divide(denom, 1, RoundingMode.HALF_UP);
    }

    // phễu chuyển đổi theo giai đoạn pipeline; owner tùy chọn
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
                    : BigDecimal.valueOf(c).multiply(HUNDRED).divide(BigDecimal.valueOf(first), 1,
                            RoundingMode.HALF_UP);
            out.add(new FunnelStage(String.valueOf(r[0]), c, pct));
        }
        return out;
    }

    // Tắt cùng dòng "urgent = urgentItems(...)" trong getSales() — chỉ dùng ở đó.
    // Mở lại cùng lúc.
    // // danh sách việc gấp: phiếu quá hạn SLA + báo giá sắp hết hạn + hóa đơn quá
    // hạn
    // private List<UrgentItem> urgentItems(Session s, String of, String ofTicket,
    // Long ownerId) {
    // Map<String, Object> po = owner(ownerId);
    // List<UrgentItem> out = new ArrayList<>();
    // for (Object[] r : rows(s, "SELECT id, code, subject, sla_due_at FROM
    // support_tickets WHERE deleted_at IS NULL "
    // +
    // "AND sla_due_at IS NOT NULL AND sla_due_at < NOW() AND status NOT IN
    // ('resolved','closed','rejected')"
    // + ofTicket +
    // " ORDER BY sla_due_at ASC LIMIT 6", po)) {
    // out.add(new UrgentItem("ticket", ((Number) r[0]).longValue(), str(r[1]),
    // str(r[2]), "Phiếu quá hạn SLA"));
    // }
    // for (Object[] r : rows(s,
    // "SELECT id, code, valid_until FROM quotations WHERE deleted_at IS NULL AND
    // status = 'sent' " +
    // "AND valid_until IS NOT NULL AND valid_until >= CURDATE() AND valid_until <=
    // DATE_ADD(CURDATE(), INTERVAL 7 DAY)"
    // + of +
    // " ORDER BY valid_until ASC LIMIT 6",
    // po)) {
    // out.add(new UrgentItem("quotation", ((Number) r[0]).longValue(), str(r[1]),
    // "Báo giá sắp hết hạn",
    // "Hết hạn " + str(r[2])));
    // }
    // for (Object[] r : rows(s,
    // "SELECT id, code, due_date FROM invoices WHERE deleted_at IS NULL AND
    // due_date IS NOT NULL " +
    // "AND due_date < CURDATE() AND payment_status <> 'paid' AND status <>
    // 'cancelled'" + of +
    // " ORDER BY due_date ASC LIMIT 6",
    // po)) {
    // out.add(new UrgentItem("invoice", ((Number) r[0]).longValue(), str(r[1]),
    // "Hóa đơn quá hạn",
    // "Đến hạn " + str(r[2])));
    // }
    // return out;
    // }

    // Tắt cùng dòng "teamByOwner = includeTeam ? teamByOwner(s) : null" trong
    // getSales() — chỉ
    // dùng ở đó (phục vụ /manager, đã tắt). Mở lại cùng lúc.
    // // thống kê cơ hội theo trạng thái, gom theo từng nhân viên (tối đa 8 người)
    // private List<GroupedStatusRow> teamByOwner(Session s) {
    // List<Object[]> rs = rows(s,
    // "SELECT u.full_name, o.status, COUNT(*) FROM opportunities o JOIN users u ON
    // u.id = o.owner_id " +
    // "WHERE o.deleted_at IS NULL GROUP BY u.id, u.full_name, o.status ORDER BY
    // u.full_name",
    // Map.of());
    // Map<String, List<DonutSegment>> grouped = new LinkedHashMap<>();
    // for (Object[] r : rs) {
    // grouped.computeIfAbsent(str(r[0]), k -> new ArrayList<>())
    // .add(new DonutSegment(str(r[1]), ((Number) r[2]).longValue(),
    // BigDecimal.ZERO));
    // }
    // return grouped.entrySet().stream().limit(8)
    // .map(e -> new GroupedStatusRow(e.getKey(), e.getValue())).toList();
    // }

    // doanh thu theo chiến dịch (top 8) — invoices không còn campaign_id trực tiếp,
    // chiến dịch nay
    // suy ra qua chuỗi Invoice -> Order -> Quotation -> Opportunity -> campaign_id
    @Override
    public List<RankedItem> revenueByCampaign(Long ownerId, DateRange cur) {
        String of = ownerId == null ? "" : " AND i.owner_id = :o";
        return TxSupport.read(sf, s -> rows(s,
                "SELECT c.id, c.name, COALESCE(SUM(" + INVOICE_LINE_AMOUNT + "),0) v " +
                        "FROM invoices i " +
                        "JOIN invoice_items ii ON ii.invoice_id = i.id " +
                        "JOIN orders o ON o.id = i.order_id " +
                        "JOIN quotations q ON q.id = o.quotation_id " +
                        "JOIN opportunities opp ON opp.id = q.opportunity_id " +
                        "JOIN campaigns c ON c.id = opp.campaign_id " +
                        "WHERE i.status <> 'cancelled' AND i.deleted_at IS NULL AND i.invoice_date >= :f AND i.invoice_date < :t"
                        + of +
                        " GROUP BY c.id, c.name ORDER BY v DESC LIMIT 8",
                dateOwner(cur, ownerId))
                .stream().map(r -> new RankedItem(((Number) r[0]).longValue(), str(r[1]), toBig(r[2]))).toList());
    }

    // top chiến dịch theo ROI = (doanh thu - chi phí thực tế) / chi phí thực tế *
    // 100
    @Override
    public List<RankedItem> campaignRoi(DateRange cur) {
        return TxSupport.read(sf, s -> rows(s,
                "SELECT c.id, c.name, (COALESCE(rv.v,0) - c.actual_cost) / c.actual_cost * 100 roi " +
                        "FROM campaigns c LEFT JOIN (" + campaignRevenueSubquery() + ") rv ON rv.cid = c.id " +
                        "WHERE c.actual_cost > 0 ORDER BY roi DESC LIMIT 10",
                Map.of("f", cur.from(), "t", cur.toExclusive()))
                .stream().map(r -> new RankedItem(((Number) r[0]).longValue(), str(r[1]), toBig(r[2]))).toList());
    }

    // chi phí trên mỗi lead/cơ hội/đơn hàng theo từng chiến dịch trong kỳ
    @Override
    public List<CampaignCacRow> campaignCac(DateRange cur) {
        return TxSupport.read(sf, s -> {
            Map<String, Object> p = Map.of("f", cur.from(), "t", cur.toExclusive());
            List<Object[]> rs = rows(s,
                    "SELECT c.id, c.name, c.actual_cost, " +
                            "COUNT(DISTINCT l.id) leadCount, COUNT(DISTINCT opp.id) oppCount, COUNT(DISTINCT ord.id) orderCount "
                            +
                            "FROM campaigns c " +
                            "LEFT JOIN leads l ON l.campaign_id = c.id AND l.deleted_at IS NULL AND l.created_at >= :f AND l.created_at < :t "
                            +
                            "LEFT JOIN opportunities opp ON opp.campaign_id = c.id AND opp.deleted_at IS NULL AND opp.created_at >= :f AND opp.created_at < :t "
                            +
                            "LEFT JOIN quotations q2 ON q2.opportunity_id = opp.id " +
                            "LEFT JOIN orders ord ON ord.quotation_id = q2.id AND ord.deleted_at IS NULL AND ord.created_at >= :f AND ord.created_at < :t "
                            +
                            "GROUP BY c.id, c.name, c.actual_cost " +
                            "HAVING leadCount > 0 OR oppCount > 0 OR orderCount > 0",
                    p);
            List<CampaignCacRow> out = new ArrayList<>();
            for (Object[] r : rs) {
                BigDecimal cost = toBig(r[2]);
                long leadCount = ((Number) r[3]).longValue();
                long oppCount = ((Number) r[4]).longValue();
                long orderCount = ((Number) r[5]).longValue();
                out.add(new CampaignCacRow(((Number) r[0]).longValue(), str(r[1]), cost, leadCount, oppCount,
                        orderCount,
                        costPer(cost, leadCount), costPer(cost, oppCount), costPer(cost, orderCount)));
            }
            return out;
        });
    }

    // chia chi phí cho số lượng, null nếu số lượng = 0 (tránh chia 0 / kết quả vô
    // nghĩa)
    private BigDecimal costPer(BigDecimal cost, long count) {
        return count == 0 ? null : cost.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
    }

    // subquery doanh thu theo campaign (chuỗi
    // invoice->order->quotation->opportunity->campaign), dùng chung
    private String campaignRevenueSubquery() {
        return "SELECT opp.campaign_id cid, COALESCE(SUM(" + INVOICE_LINE_AMOUNT + "),0) v " +
                "FROM invoices i JOIN invoice_items ii ON ii.invoice_id = i.id " +
                "JOIN orders o ON o.id = i.order_id JOIN quotations q ON q.id = o.quotation_id " +
                "JOIN opportunities opp ON opp.id = q.opportunity_id " +
                "WHERE i.status <> 'cancelled' AND i.deleted_at IS NULL AND i.invoice_date >= :f AND i.invoice_date < :t "
                +
                "GROUP BY opp.campaign_id";
    }

    // doanh thu theo campaign trong 1 khoảng — trả Map<campaignId, revenue>, KHÔNG
    // giới hạn top 8 (dùng cho so sánh kỳ)
    private Map<Long, BigDecimal> campaignRevenueMap(Session s, DateRange r) {
        Map<Long, BigDecimal> m = new HashMap<>();
        for (Object[] row : rows(s, campaignRevenueSubquery(), Map.of("f", r.from(), "t", r.toExclusive()))) {
            if (row[0] != null)
                m.put(((Number) row[0]).longValue(), toBig(row[1]));
        }
        return m;
    }

    // tỉ lệ thắng-thua theo tháng, 12 tháng gần nhất
    @Override
    public List<TimeSeriesPoint> winRateTrend(Long ownerId, LocalDate seriesFrom) {
        return TxSupport.read(sf, s -> {
            String of = ownerId == null ? "" : " AND owner_id = :o";
            List<Object[]> rs = rows(s,
                    "SELECT DATE_FORMAT(updated_at, '%Y-%m') p, status, COUNT(*) c FROM opportunities " +
                            "WHERE deleted_at IS NULL AND status IN ('won','lost') AND updated_at >= :f" + of +
                            " GROUP BY p, status",
                    seriesOwner(seriesFrom, ownerId));
            Map<String, long[]> byMonth = new HashMap<>(); // [won, lost]
            for (Object[] r : rs) {
                String p = str(r[0]);
                long[] wl = byMonth.computeIfAbsent(p, k -> new long[2]);
                long c = ((Number) r[2]).longValue();
                if ("won".equals(str(r[1])))
                    wl[0] += c;
                else
                    wl[1] += c;
            }
            List<TimeSeriesPoint> out = new ArrayList<>();
            LocalDate m = seriesFrom.withDayOfMonth(1);
            for (int i = 0; i < 12; i++) {
                String key = m.format(YM);
                long[] wl = byMonth.getOrDefault(key, new long[2]);
                out.add(new TimeSeriesPoint(key, winRatePct(BigDecimal.valueOf(wl[0]), BigDecimal.valueOf(wl[1]))));
                m = m.plusMonths(1);
            }
            return out;
        });
    }

    // cơ hội "treo" — đang mở, không hoạt động chăm sóc trong N ngày
    @Override
    public CountedRankedList stalledOpportunities(Long ownerId, int days) {
        return TxSupport.read(sf, s -> {
            String of = ownerId == null ? "" : " AND o.owner_id = :o";
            String where = "FROM opportunities o LEFT JOIN " + ACTIVITY_LAST_TOUCH + " lt ON lt.target_id = o.id " +
                    "WHERE o.deleted_at IS NULL AND o.status = 'open' " +
                    "AND COALESCE(lt.last_touch, o.created_at) < DATE_SUB(NOW(), INTERVAL :days DAY)" + of;
            Map<String, Object> p = new HashMap<>();
            p.put("days", days);
            if (ownerId != null)
                p.put("o", ownerId);
            long total = count(s, "SELECT COUNT(*) " + where, p);
            List<RankedItem> items = rows(s,
                    "SELECT o.id, o.name, DATEDIFF(NOW(), COALESCE(lt.last_touch, o.created_at)) d " + where +
                            " ORDER BY d DESC LIMIT 10",
                    p)
                    .stream().map(r -> new RankedItem(((Number) r[0]).longValue(), str(r[1]), toBig(r[2]))).toList();
            return new CountedRankedList(total, items);
        });
    }

    // tỉ lệ cơ hội có nguồn từ chiến dịch vs tự phát sinh trong kỳ
    @Override
    public List<DonutSegment> opportunitySource(DateRange cur) {
        return TxSupport.read(sf, s -> donut(rows(s,
                "SELECT CASE WHEN campaign_id IS NOT NULL THEN 'Từ chiến dịch' ELSE 'Tự phát sinh' END lbl, COUNT(*) c "
                        +
                        "FROM opportunities WHERE deleted_at IS NULL AND created_at >= :f AND created_at < :t GROUP BY lbl",
                Map.of("f", cur.from(), "t", cur.toExclusive()))));
    }

    // lead đang ở pool chung (chưa ai nhận)
    @Override
    public CountedRankedList leadPool() {
        return TxSupport.read(sf, s -> {
            long total = count(s, "SELECT COUNT(*) FROM leads WHERE owner_id IS NULL AND deleted_at IS NULL", Map.of());
            List<RankedItem> items = rows(s,
                    "SELECT id, name, DATEDIFF(NOW(), created_at) d FROM leads WHERE owner_id IS NULL AND deleted_at IS NULL "
                            +
                            "ORDER BY created_at ASC LIMIT 10",
                    Map.of())
                    .stream().map(r -> new RankedItem(((Number) r[0]).longValue(), str(r[1]), toBig(r[2]))).toList();
            return new CountedRankedList(total, items);
        });
    }

    // % doanh thu quy kết được về marketing (có campaign_id) trên tổng doanh thu,
    // kỳ này vs kỳ trước
    @Override
    public KpiMetric campaignAttributedRevenuePct(DateRange cur, DateRange prev) {
        return TxSupport.read(sf, s -> {
            BigDecimal pctCur = attributedRevenuePct(s, cur);
            BigDecimal pctPrev = attributedRevenuePct(s, prev);
            return KpiMetric.of(pctCur, pctPrev);
        });
    }

    private BigDecimal attributedRevenuePct(Session s, DateRange r) {
        Map<String, Object> p = Map.of("f", r.from(), "t", r.toExclusive());
        BigDecimal total = sum(s, invoiceRevenueSql(""), p);
        BigDecimal attributed = sum(s, "SELECT COALESCE(SUM(" + INVOICE_LINE_AMOUNT + "),0) " +
                "FROM invoices i JOIN invoice_items ii ON ii.invoice_id = i.id " +
                "JOIN orders o ON o.id = i.order_id JOIN quotations q ON q.id = o.quotation_id " +
                "JOIN opportunities opp ON opp.id = q.opportunity_id " +
                "WHERE i.status <> 'cancelled' AND i.deleted_at IS NULL AND i.invoice_date >= :f AND i.invoice_date < :t "
                +
                "AND opp.campaign_id IS NOT NULL", p);
        if (total.signum() == 0)
            return BigDecimal.ZERO;
        return attributed.multiply(HUNDRED).divide(total, 1, RoundingMode.HALF_UP);
    }

    // so sánh doanh thu kỳ này/kỳ trước cho các chiến dịch đang chạy
    @Override
    public List<CampaignRevenueComparisonRow> campaignPeriodComparison(DateRange cur, DateRange prev) {
        return TxSupport.read(sf, s -> {
            List<Object[]> campaigns = rows(s, "SELECT id, name FROM campaigns WHERE status = 'running'", Map.of());
            Map<Long, BigDecimal> curMap = campaignRevenueMap(s, cur);
            Map<Long, BigDecimal> prevMap = campaignRevenueMap(s, prev);
            List<CampaignRevenueComparisonRow> out = new ArrayList<>();
            for (Object[] r : campaigns) {
                long id = ((Number) r[0]).longValue();
                BigDecimal c = curMap.getOrDefault(id, BigDecimal.ZERO);
                BigDecimal p = prevMap.getOrDefault(id, BigDecimal.ZERO);
                out.add(new CampaignRevenueComparisonRow(id, str(r[1]), KpiMetric.of(c, p)));
            }
            return out;
        });
    }

    // bảng xếp hạng nhân viên theo tỉ lệ thắng (kèm doanh thu) trong kỳ
    @Override
    public List<EmployeeWinRateRow> winRateLeaderboard(DateRange cur) {
        return TxSupport.read(sf, s -> {
            Map<String, Object> p = Map.of("f", cur.from(), "t", cur.toExclusive());
            List<Object[]> rs = rows(s,
                    "SELECT u.id, u.full_name, MAX(COALESCE(rv.v,0)) revenue, " +
                            "SUM(CASE WHEN o.status = 'won' THEN 1 ELSE 0 END) won, " +
                            "SUM(CASE WHEN o.status = 'lost' THEN 1 ELSE 0 END) lost " +
                            "FROM users u " +
                            "LEFT JOIN opportunities o ON o.owner_id = u.id AND o.deleted_at IS NULL " +
                            "AND o.status IN ('won','lost') AND o.updated_at >= :f AND o.updated_at < :t " +
                            "LEFT JOIN (SELECT i.owner_id oid, COALESCE(SUM(" + INVOICE_LINE_AMOUNT
                            + "),0) v FROM invoices i " +
                            "JOIN invoice_items ii ON ii.invoice_id = i.id WHERE i.status <> 'cancelled' AND i.deleted_at IS NULL "
                            +
                            "AND i.invoice_date >= :f AND i.invoice_date < :t GROUP BY i.owner_id) rv ON rv.oid = u.id "
                            +
                            "WHERE u.deleted_at IS NULL GROUP BY u.id, u.full_name " +
                            "HAVING won > 0 OR lost > 0 OR revenue > 0",
                    p);
            List<EmployeeWinRateRow> out = new ArrayList<>();
            for (Object[] r : rs) {
                long won = ((Number) r[3]).longValue();
                long lost = ((Number) r[4]).longValue();
                out.add(new EmployeeWinRateRow(((Number) r[0]).longValue(), str(r[1]), toBig(r[2]), won, lost,
                        winRatePct(BigDecimal.valueOf(won), BigDecimal.valueOf(lost))));
            }
            out.sort((a, b) -> b.winRatePct().compareTo(a.winRatePct()));
            return out;
        });
    }

    // lý do thua theo từng nhân viên trong kỳ
    @Override
    public List<GroupedStatusRow> lossReasonsByOwner(Long ownerId, DateRange cur) {
        return TxSupport.read(sf, s -> {
            String of = ownerId == null ? "" : " AND o.owner_id = :o";
            Map<String, Object> p = dateOwner(cur, ownerId);
            List<Object[]> rs = rows(s,
                    "SELECT u.full_name, o.win_loss_reason, COUNT(*) c FROM opportunities o JOIN users u ON u.id = o.owner_id "
                            +
                            "WHERE o.deleted_at IS NULL AND o.status = 'lost' AND o.win_loss_reason IS NOT NULL AND o.win_loss_reason <> '' "
                            +
                            "AND o.updated_at >= :f AND o.updated_at < :t" + of +
                            " GROUP BY u.id, u.full_name, o.win_loss_reason ORDER BY u.full_name",
                    p);
            Map<String, List<DonutSegment>> grouped = new LinkedHashMap<>();
            for (Object[] r : rs) {
                grouped.computeIfAbsent(str(r[0]), k -> new ArrayList<>())
                        .add(new DonutSegment(str(r[1]), ((Number) r[2]).longValue(), BigDecimal.ZERO));
            }
            return grouped.entrySet().stream().limit(8)
                    .map(e -> new GroupedStatusRow(e.getKey(), withPct(e.getValue()))).toList();
        });
    }

    // số cơ hội "treo" theo từng nhân viên (top 8)
    @Override
    public List<RankedItem> stalledByOwner(int days) {
        return TxSupport.read(sf, s -> rows(s,
                "SELECT u.id, u.full_name, COUNT(*) c FROM opportunities o JOIN users u ON u.id = o.owner_id " +
                        "LEFT JOIN " + ACTIVITY_LAST_TOUCH + " lt ON lt.target_id = o.id " +
                        "WHERE o.deleted_at IS NULL AND o.status = 'open' " +
                        "AND COALESCE(lt.last_touch, o.created_at) < DATE_SUB(NOW(), INTERVAL :days DAY) " +
                        "GROUP BY u.id, u.full_name ORDER BY c DESC LIMIT 8",
                Map.of("days", days))
                .stream().map(r -> new RankedItem(((Number) r[0]).longValue(), str(r[1]), toBig(r[2]))).toList());
    }

    // Tắt cùng dòng "revenueByOwner = includeTeam ? revenueByOwner(s, cur) : null"
    // trong
    // getSales() — chỉ dùng ở đó (phục vụ /manager, đã tắt). Mở lại cùng lúc.
    // // doanh thu theo nhân viên trong kỳ (top 8)
    // private List<RankedItem> revenueByOwner(Session s, DateRange cur) {
    // return rows(s, "SELECT u.id, u.full_name, COALESCE(SUM(" +
    // INVOICE_LINE_AMOUNT + "),0) v " +
    // "FROM invoices i JOIN invoice_items ii ON ii.invoice_id = i.id JOIN users u
    // ON u.id = i.owner_id " +
    // "WHERE i.status <> 'cancelled' AND i.deleted_at IS NULL AND i.invoice_date >=
    // :f AND i.invoice_date < :t "
    // +
    // "GROUP BY u.id, u.full_name ORDER BY v DESC LIMIT 8", Map.of("f", cur.from(),
    // "t", cur.toExclusive()))
    // .stream().map(r -> new RankedItem(((Number) r[0]).longValue(), str(r[1]),
    // toBig(r[2]))).toList();
    // }

    // đếm tổng bản ghi 1 bảng nghiệp vụ (chưa xóa mềm) → DonutSegment (pct điền
    // sau)
    private DonutSegment recordTotal(Session s, String label, String table) {
        long c = count(s, "SELECT COUNT(*) FROM " + table + " WHERE deleted_at IS NULL", Map.of());
        return new DonutSegment(label, c, BigDecimal.ZERO);
    }

    // ==================== Helpers dựng dữ liệu ====================

    // dựng danh sách donut kèm % từ các dòng [label, count]
    private List<DonutSegment> donut(List<Object[]> rs) {
        long total = rs.stream().mapToLong(r -> ((Number) r[1]).longValue()).sum();
        List<DonutSegment> out = new ArrayList<>();
        for (Object[] r : rs) {
            long c = ((Number) r[1]).longValue();
            out.add(new DonutSegment(str(r[0]), c, pct(c, total)));
        }
        return out;
    }

    // điền lại % cho danh sách DonutSegment đã có count
    private List<DonutSegment> withPct(List<DonutSegment> in) {
        long total = in.stream().mapToLong(DonutSegment::count).sum();
        return in.stream().map(d -> new DonutSegment(d.label(), d.count(), pct(d.count(), total))).toList();
    }

    // đổ các dòng [period, value] vào chuỗi 12 tháng liên tục từ "from"
    private List<TimeSeriesPoint> fillMonths(LocalDate from, List<Object[]> rs) {
        Map<String, BigDecimal> map = new HashMap<>();
        for (Object[] r : rs)
            map.put(str(r[0]), toBig(r[1]));
        List<TimeSeriesPoint> out = new ArrayList<>();
        LocalDate m = from.withDayOfMonth(1);
        for (int i = 0; i < 12; i++) {
            String key = m.format(YM);
            out.add(new TimeSeriesPoint(key, map.getOrDefault(key, BigDecimal.ZERO)));
            m = m.plusMonths(1);
        }
        return out;
    }

    // hiệu hai chuỗi cùng độ dài (doanh thu − chi phí)
    private List<TimeSeriesPoint> subtractSeries(List<TimeSeriesPoint> a, List<TimeSeriesPoint> b) {
        List<TimeSeriesPoint> out = new ArrayList<>();
        for (int i = 0; i < a.size(); i++) {
            out.add(new TimeSeriesPoint(a.get(i).period(), a.get(i).value().subtract(b.get(i).value())));
        }
        return out;
    }

    // % của count trên total, làm tròn 1 chữ số
    private BigDecimal pct(long count, long total) {
        if (total == 0)
            return BigDecimal.ZERO;
        return BigDecimal.valueOf(count).multiply(HUNDRED).divide(BigDecimal.valueOf(total), 1, RoundingMode.HALF_UP);
    }

    // ==================== Helpers native query ====================

    // chạy native query trả về 1 giá trị đếm
    private long count(Session s, String sql, Map<String, Object> params) {
        var q = s.createNativeQuery(sql, Object.class);
        params.forEach(q::setParameter);
        Object r = q.uniqueResult();
        return r == null ? 0 : ((Number) r).longValue();
    }

    // chạy native query trả về 1 giá trị SUM (BigDecimal)
    private BigDecimal sum(Session s, String sql, Map<String, Object> params) {
        var q = s.createNativeQuery(sql, Object.class);
        params.forEach(q::setParameter);
        return toBig(q.uniqueResult());
    }

    // chạy native query trả về nhiều cột
    @SuppressWarnings("unchecked")
    private List<Object[]> rows(Session s, String sql, Map<String, Object> params) {
        var q = s.createNativeQuery(sql, Object[].class);
        params.forEach(q::setParameter);
        return q.list();
    }

    // map tham số {f,t} + owner tùy chọn
    private Map<String, Object> dateOwner(DateRange r, Long ownerId) {
        Map<String, Object> m = new HashMap<>();
        m.put("f", r.from());
        m.put("t", r.toExclusive());
        if (ownerId != null)
            m.put("o", ownerId);
        return m;
    }

    // map tham số {f = seriesFrom} + owner tùy chọn
    private Map<String, Object> seriesOwner(LocalDate seriesFrom, Long ownerId) {
        Map<String, Object> m = new HashMap<>();
        m.put("f", seriesFrom);
        if (ownerId != null)
            m.put("o", ownerId);
        return m;
    }

    // map chỉ chứa owner (rỗng nếu null)
    private Map<String, Object> owner(Long ownerId) {
        return ownerId == null ? Map.of() : Map.of("o", ownerId);
    }

    // ép object kết quả về BigDecimal an toàn
    private BigDecimal toBig(Object o) {
        if (o == null)
            return BigDecimal.ZERO;
        if (o instanceof BigDecimal b)
            return b;
        if (o instanceof Number n)
            return new BigDecimal(n.toString());
        return new BigDecimal(o.toString());
    }

    // ép object về String an toàn
    private String str(Object o) {
        return o == null ? "" : o.toString();
    }
}
