package vn.com.be_crm.application.dashboard.dto;

import java.math.BigDecimal;

/**
 * Chỉ số KPI kèm so sánh kỳ trước để tính % tăng trưởng.
 *
 * @param current   giá trị kỳ hiện tại
 * @param previous  giá trị kỳ liền trước
 * @param growthPct % tăng trưởng so với kỳ trước ((current-previous)/previous*100); null khi kỳ trước = 0
 */
public record KpiMetric(BigDecimal current, BigDecimal previous, Double growthPct) {

    /**
     * Tạo KpiMetric và tự tính % tăng trưởng từ 2 giá trị.
     *
     * @param current  giá trị kỳ hiện tại
     * @param previous giá trị kỳ liền trước
     * @return KpiMetric đã tính growthPct
     */
    public static KpiMetric of(BigDecimal current, BigDecimal previous) {
        BigDecimal cur = current != null ? current : BigDecimal.ZERO;
        BigDecimal prev = previous != null ? previous : BigDecimal.ZERO;
        Double growth = null;
        if (prev.signum() != 0) {
            growth = cur.subtract(prev)
                    .divide(prev.abs(), 4, java.math.RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();
        }
        return new KpiMetric(cur, prev, growth);
    }
}
