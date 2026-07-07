package vn.com.be_crm.application.dashboard.query;

import vn.com.be_crm.domain.dashboard.model.DateRange;

import java.time.LocalDate;

/**
 * Helper tính khoảng thời gian kỳ hiện tại / kỳ trước và mốc chuỗi 12 tháng từ mã kỳ.
 */
public final class PeriodRanges {

    private PeriodRanges() {
    }

    /**
     * Tính kỳ hiện tại từ mã kỳ ("month" | "quarter" | "year", mặc định year).
     *
     * @param period mã kỳ
     * @return khoảng [đầu kỳ, đầu kỳ kế tiếp)
     */
    public static DateRange current(String period) {
        LocalDate today = LocalDate.now();
        return switch (period == null ? "year" : period.toLowerCase()) {
            case "month" -> {
                LocalDate from = today.withDayOfMonth(1);
                yield new DateRange(from, from.plusMonths(1));
            }
            case "quarter" -> {
                int q = (today.getMonthValue() - 1) / 3;
                LocalDate from = LocalDate.of(today.getYear(), q * 3 + 1, 1);
                yield new DateRange(from, from.plusMonths(3));
            }
            default -> {
                LocalDate from = today.withDayOfYear(1);
                yield new DateRange(from, from.plusYears(1));
            }
        };
    }

    /**
     * Tính kỳ liền trước có cùng độ dài với kỳ hiện tại.
     *
     * @param period mã kỳ
     * @return khoảng kỳ trước
     */
    public static DateRange previous(String period) {
        DateRange cur = current(period);
        return switch (period == null ? "year" : period.toLowerCase()) {
            case "month" -> new DateRange(cur.from().minusMonths(1), cur.from());
            case "quarter" -> new DateRange(cur.from().minusMonths(3), cur.from());
            default -> new DateRange(cur.from().minusYears(1), cur.from());
        };
    }

    /**
     * Mốc bắt đầu chuỗi 12 tháng cho biểu đồ theo tháng (ngày 1 của tháng cách nay 11 tháng).
     *
     * @return LocalDate đầu chuỗi
     */
    public static LocalDate seriesFrom() {
        return LocalDate.now().withDayOfMonth(1).minusMonths(11);
    }
}
