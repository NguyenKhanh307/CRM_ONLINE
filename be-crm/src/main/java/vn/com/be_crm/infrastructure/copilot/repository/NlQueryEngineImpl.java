package vn.com.be_crm.infrastructure.copilot.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import vn.com.be_crm.application.copilot.dto.CopilotChartData;
import vn.com.be_crm.application.copilot.dto.CopilotChartSegment;
import vn.com.be_crm.application.copilot.dto.CopilotQuerySpec;
import vn.com.be_crm.application.copilot.dto.NlQueryResult;
import vn.com.be_crm.application.copilot.intent.CopilotRangeParser;
import vn.com.be_crm.application.copilot.intent.CopilotRangeParser.Range;
import vn.com.be_crm.application.dashboard.query.PeriodRanges;
import vn.com.be_crm.domain.copilot.repository.INlQueryEngine;
import vn.com.be_crm.domain.dashboard.model.DateRange;
import vn.com.be_crm.core.tx.impl.TxSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static vn.com.be_crm.infrastructure.copilot.repository.CopilotSqlSupport.*;
import static vn.com.be_crm.infrastructure.copilot.repository.NlQueryRegistry.ModuleConfig;

// Engine NL2SQL có kiểm soát: nhận CopilotQuerySpec đã do LLM chọn (chỉ chứa TÊN THAM SỐ, không
// chứa SQL), đối chiếu whitelist NlQueryRegistry rồi tự ghép SQL — mọi tên bảng/cột luôn lấy từ
// hằng số Java trong registry, mọi giá trị động (ngày, id, status) luôn bind qua :param.
// Chi tiết điều kiện LIST (mục 3b kế hoạch) tách sang NlQueryListConditions để giữ file dưới 400 dòng.
@Repository
public class NlQueryEngineImpl implements INlQueryEngine {

    private final SessionFactory sf;
    private final CopilotRangeParser rangeParser = new CopilotRangeParser();

    public NlQueryEngineImpl(SessionFactory sf) {
        this.sf = sf;
    }

    @Override
    public NlQueryResult run(String queryType, CopilotQuerySpec spec, String question, Long ownerId, boolean isPrivileged) {
        if (spec == null || spec.module() == null) return NlQueryResult.invalid();
        ModuleConfig cfg = NlQueryRegistry.MODULES.get(spec.module());
        if (cfg == null) return NlQueryResult.invalid();

        return TxSupport.read(sf, s -> {
            List<Long> scope = resolveScope(s, spec.employeeNames(), ownerId, isPrivileged);
            // nhân viên hỏi tên đồng nghiệp (privileged) mà không khớp ai -> báo rõ, không lặng lẽ
            // bỏ qua bộ lọc (bỏ qua sẽ vô tình lộ số liệu TOÀN CÔNG TY thay vì đúng người được hỏi)
            if (isPrivileged && spec.employeeNames() != null && !spec.employeeNames().isEmpty() && scope.isEmpty()) {
                return new NlQueryResult(true, "Không tìm thấy nhân viên nào khớp tên bạn nêu trong hệ thống.", null);
            }
            if ("LIST".equals(queryType)) return runList(s, spec, cfg, scope);
            return runAggregate(s, spec, cfg, scope, question);
        });
    }

    // ==================== AGGREGATE (COUNT/SUM_AMOUNT/RATE_ACCEPTED, có thể nhóm theo) ====================

    private NlQueryResult runAggregate(Session s, CopilotQuerySpec spec, ModuleConfig cfg, List<Long> scope, String question) {
        String metric = spec.metric() == null ? "COUNT" : spec.metric();
        if (!NlQueryRegistry.METRICS.contains(metric)) return NlQueryResult.invalid();
        if ("SUM_AMOUNT".equals(metric) && !NlQueryRegistry.SUM_AMOUNT_MODULES.contains(spec.module())) return NlQueryResult.invalid();
        if ("RATE_ACCEPTED".equals(metric) && !NlQueryRegistry.RATE_MODULES.contains(spec.module())) return NlQueryResult.invalid();

        String groupBy = spec.groupBy() == null ? "NONE" : spec.groupBy();
        if (!NlQueryRegistry.GROUP_BYS.contains(groupBy)) return NlQueryResult.invalid();
        if ("OWNER".equals(groupBy) && cfg.ownerColumn() == null) return NlQueryResult.invalid();
        if ("STATUS".equals(groupBy) && cfg.statusColumn() == null) return NlQueryResult.invalid();

        String from = "FROM " + cfg.table() + " x";
        if ("invoice".equals(spec.module()) && "SUM_AMOUNT".equals(metric)) {
            from += " JOIN invoice_items ii ON ii.invoice_id = x.id";
        }
        String metricExpr = switch (metric) {
            case "SUM_AMOUNT" -> "opportunity".equals(spec.module())
                    ? "COALESCE(SUM(x.amount),0)" : "COALESCE(SUM(" + INVOICE_LINE_AMOUNT + "),0)";
            case "RATE_ACCEPTED" -> "ROUND(SUM(CASE WHEN x.status = 'accepted' THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 1)";
            default -> "COUNT(*)";
        };

        Map<String, Object> params = new HashMap<>();
        StringBuilder where = new StringBuilder("WHERE 1=1");
        if (cfg.softDelete()) where.append(" AND x.deleted_at IS NULL");
        DateRange range = resolveRange(question);
        if (range != null) {
            where.append(" AND x.created_at >= :f AND x.created_at < :t");
            params.put("f", range.from()); params.put("t", range.toExclusive());
        }
        if (spec.status() != null && !spec.status().isBlank() && cfg.statusColumn() != null) {
            where.append(" AND x.").append(cfg.statusColumn()).append(" = :st");
            params.put("st", spec.status());
        }
        if (!scope.isEmpty() && cfg.ownerColumn() != null) {
            where.append(" AND x.").append(cfg.ownerColumn()).append(" IN (:ids)");
            params.put("ids", scope);
        }

        String select; String tail = "";
        switch (groupBy) {
            case "OWNER" -> {
                from += " JOIN users u ON u.id = x." + cfg.ownerColumn() + " AND u.deleted_at IS NULL";
                select = "u.full_name AS label, " + metricExpr + " AS val";
                tail = " GROUP BY u.id, u.full_name ORDER BY val DESC LIMIT 8";
            }
            case "MONTH" -> {
                select = "DATE_FORMAT(x.created_at, '%Y-%m') AS label, " + metricExpr + " AS val";
                tail = " GROUP BY label ORDER BY label";
            }
            case "STATUS" -> {
                select = "x." + cfg.statusColumn() + " AS label, " + metricExpr + " AS val";
                tail = " GROUP BY label ORDER BY val DESC";
            }
            default -> select = metricExpr + " AS val";
        }

        String sql = "SELECT " + select + " " + from + " " + where + tail;
        String moduleLabel = NlQueryRegistry.MODULE_LABELS.get(spec.module());
        String metricLabel = switch (metric) {
            case "SUM_AMOUNT" -> "tổng tiền";
            case "RATE_ACCEPTED" -> "tỉ lệ chốt đơn (%)";
            default -> "số lượng";
        };

        if ("NONE".equals(groupBy)) {
            // truy vấn 1 cột 1 dòng -> dùng sum() (Object.class + uniqueResult), KHÔNG dùng rows()
            // (Object[].class) để tránh rủi ro ép kiểu khi SQL chỉ có đúng 1 cột
            java.math.BigDecimal v = sum(s, sql, params);
            String answer = "Số liệu " + metricLabel + " " + moduleLabel + ": " + money(v)
                    + ("SUM_AMOUNT".equals(metric) ? " đ" : "");
            return new NlQueryResult(true, answer, null);
        }

        List<Object[]> rs = rows(s, sql, params);
        if (rs.isEmpty()) return new NlQueryResult(true, "Không có dữ liệu " + metricLabel + " " + moduleLabel + " khớp yêu cầu.", null);
        List<CopilotChartSegment> segs = new ArrayList<>();
        List<String> parts = new ArrayList<>();
        for (Object[] r : rs) {
            String label = str(r[0]);
            segs.add(new CopilotChartSegment(label, toBig(r[1])));
            parts.add(label + " = " + money(toBig(r[1])));
        }
        String answer = metricLabel.substring(0, 1).toUpperCase() + metricLabel.substring(1) + " " + moduleLabel
                + " theo " + groupByLabel(groupBy) + ": " + String.join("; ", parts) + ".";
        return new NlQueryResult(true, answer, new CopilotChartData(moduleLabel + " theo " + groupByLabel(groupBy), segs));
    }

    // ==================== LIST (liệt kê bản ghi khớp điều kiện đã đăng ký sẵn) ====================

    private NlQueryResult runList(Session s, CopilotQuerySpec spec, ModuleConfig cfg, List<Long> scope) {
        var allowed = NlQueryRegistry.LIST_CONDITIONS.get(spec.module());
        if (allowed == null || spec.condition() == null || !allowed.contains(spec.condition())) return NlQueryResult.invalid();

        NlQueryListConditions.ListQuery cq = NlQueryListConditions.get(spec.module(), spec.condition());
        if (cq == null) return NlQueryResult.invalid();

        String ownerFilter = "";
        Map<String, Object> params = new HashMap<>(cq.params());
        if (!scope.isEmpty() && cfg.ownerColumn() != null) {
            ownerFilter = " AND x." + cfg.ownerColumn() + " IN (:ids)";
            params.put("ids", scope);
        }

        String moduleLabel = NlQueryRegistry.MODULE_LABELS.get(spec.module());
        String conditionLabel = NlQueryListConditions.label(spec.condition());
        long total = count(s, "SELECT COUNT(*) " + cq.from() + " WHERE " + cq.where() + ownerFilter, params);
        if (total == 0) {
            return new NlQueryResult(true, "Không có " + moduleLabel + " nào " + conditionLabel + ".", null);
        }
        List<Object[]> rs = rows(s, "SELECT " + cq.selectCols() + " " + cq.from() +
                " WHERE " + cq.where() + ownerFilter + " ORDER BY x.created_at DESC LIMIT 15", params);
        List<String> codes = rs.stream().map(r -> str(r[1])).toList();
        String more = total > codes.size() ? " (và " + (total - codes.size()) + " bản ghi khác)" : "";
        String answer = "Có " + total + " " + moduleLabel + " " + conditionLabel + ": " + String.join(", ", codes) + more + ".";
        return new NlQueryResult(true, answer, null);
    }

    // ==================== Helpers ====================

    // tra tên/email nhân viên -> user_id; nhân viên thường luôn bị ép về chính mình (không cho lộ
    // số liệu đồng nghiệp dù LLM có nêu tên ai đi nữa)
    private List<Long> resolveScope(Session s, List<String> employeeNames, Long ownerId, boolean isPrivileged) {
        if (!isPrivileged) return ownerId != null ? List.of(ownerId) : List.of();
        if (employeeNames == null || employeeNames.isEmpty()) return List.of();
        List<Long> ids = new ArrayList<>();
        for (String raw : employeeNames) {
            String term = raw == null ? "" : raw.trim().toLowerCase();
            if (term.isBlank()) continue;
            List<Object[]> rs = rows(s, "SELECT id FROM users WHERE deleted_at IS NULL AND " +
                    "(LOWER(full_name) LIKE :t OR LOWER(email) LIKE :t) ORDER BY id LIMIT 1",
                    Map.of("t", "%" + term + "%"));
            if (!rs.isEmpty()) ids.add(((Number) rs.get(0)[0]).longValue());
        }
        return ids;
    }

    // suy khoảng thời gian từ câu hỏi: ưu tiên khoảng tường minh (CopilotRangeParser), sau đó từ
    // khóa kỳ (tháng/quý/năm); không nhắc gì tới thời gian -> null (không lọc, tính toàn thời gian)
    private DateRange resolveRange(String question) {
        List<Range> ranges = rangeParser.parse(question);
        if (!ranges.isEmpty()) {
            Range r = ranges.get(0);
            return new DateRange(r.from(), r.toExclusive());
        }
        String low = question == null ? "" : question.toLowerCase();
        if (low.contains("tháng") || low.contains("thang")) return PeriodRanges.current("month");
        if (low.contains("quý") || low.contains("quy")) return PeriodRanges.current("quarter");
        if (low.contains("năm") || low.contains("nam")) return PeriodRanges.current("year");
        return null;
    }

    private String groupByLabel(String groupBy) {
        return switch (groupBy) {
            case "OWNER" -> "nhân viên";
            case "MONTH" -> "tháng";
            case "STATUS" -> "trạng thái";
            default -> "";
        };
    }
}
