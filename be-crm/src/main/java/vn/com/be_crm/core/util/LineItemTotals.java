package vn.com.be_crm.core.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.function.Function;

/**
 * Tiện ích tính tiền dòng hàng — dùng chung cho Cơ hội / Báo giá / Đơn hàng / Hóa đơn.
 * Server LUÔN tự tính lại tiền từ (quantity, unitPrice, discount, taxRate), KHÔNG tin số tổng client gửi.
 * Công thức (khớp frontend {@code productLineItem.ts}):
 * <pre>
 *   net_i   = quantity × unitPrice − discount   (discount là số tiền tuyệt đối)
 *   tax_i   = net_i × taxRate / 100             (thuế tính SAU chiết khấu)
 *   total_i = net_i + tax_i
 * </pre>
 */
public final class LineItemTotals {

    /** Số chữ số thập phân khi làm tròn tiền — khớp cột DB (precision 18, scale 2). */
    private static final int SCALE = 2;

    private LineItemTotals() {}

    /** Giá trị an toàn: null → 0. */
    private static BigDecimal nz(BigDecimal v) {
        return v != null ? v : BigDecimal.ZERO;
    }

    /** Làm tròn tiền HALF_UP về 2 chữ số thập phân. */
    private static BigDecimal round(BigDecimal v) {
        return v.setScale(SCALE, RoundingMode.HALF_UP);
    }

    /**
     * Thành tiền trước thuế của một dòng: {@code quantity × unitPrice − discount}.
     *
     * @param quantity  số lượng @param unitPrice đơn giá @param discount chiết khấu (số tiền tuyệt đối)
     * @return thành tiền sau chiết khấu, trước thuế
     */
    public static BigDecimal lineNet(BigDecimal quantity, BigDecimal unitPrice, BigDecimal discount) {
        return round(nz(quantity).multiply(nz(unitPrice)).subtract(nz(discount)));
    }

    /**
     * Tiền thuế của một dòng: {@code net × taxRate / 100} (thuế tính sau chiết khấu).
     *
     * @param quantity số lượng @param unitPrice đơn giá @param discount chiết khấu @param taxRate thuế suất (%)
     * @return tiền thuế của dòng
     */
    public static BigDecimal lineTax(BigDecimal quantity, BigDecimal unitPrice, BigDecimal discount, BigDecimal taxRate) {
        BigDecimal net = lineNet(quantity, unitPrice, discount);
        return round(net.multiply(nz(taxRate)).divide(BigDecimal.valueOf(100), SCALE + 4, RoundingMode.HALF_UP));
    }

    /**
     * Thành tiền của một dòng (đã gồm thuế): {@code net + tax}.
     *
     * @param quantity số lượng @param unitPrice đơn giá @param discount chiết khấu @param taxRate thuế suất (%)
     * @return thành tiền dòng hàng
     */
    public static BigDecimal lineAmount(BigDecimal quantity, BigDecimal unitPrice, BigDecimal discount, BigDecimal taxRate) {
        return round(lineNet(quantity, unitPrice, discount).add(lineTax(quantity, unitPrice, discount, taxRate)));
    }

    /**
     * Cộng dồn một thành phần tiền của tất cả dòng hàng.
     *
     * @param items danh sách dòng hàng @param fn hàm lấy giá trị cần cộng của một dòng
     * @param <T>   kiểu dòng hàng @return tổng (0 nếu rỗng/null)
     */
    public static <T> BigDecimal sum(Collection<T> items, Function<T, BigDecimal> fn) {
        if (items == null || items.isEmpty()) return BigDecimal.ZERO;
        return round(items.stream().map(fn).filter(v -> v != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    /** Bộ tổng tiền của chứng từ (báo giá / đơn hàng / hóa đơn). */
    public record Totals(BigDecimal subtotal, BigDecimal discount, BigDecimal tax, BigDecimal total) {}

    /**
     * Tính toàn bộ tổng tiền của chứng từ TỪ dòng hàng — nguồn chân lý duy nhất, bỏ qua số client gửi.
     * Dùng cho Báo giá / Đơn hàng / Hóa đơn (các entity dòng hàng có cùng bộ trường nhưng không chung interface).
     *
     * @param items   danh sách dòng hàng
     * @param qty     hàm lấy số lượng @param price hàm lấy đơn giá
     * @param disc    hàm lấy chiết khấu (tiền) @param taxRate hàm lấy thuế suất (%)
     * @param <T>     kiểu dòng hàng
     * @return tổng tạm tính / chiết khấu / thuế / thành tiền
     */
    public static <T> Totals compute(Collection<T> items,
                                     Function<T, BigDecimal> qty, Function<T, BigDecimal> price,
                                     Function<T, BigDecimal> disc, Function<T, BigDecimal> taxRate) {
        if (items == null || items.isEmpty()) {
            return new Totals(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        }
        BigDecimal subtotal = sum(items, i -> lineNet(qty.apply(i), price.apply(i), disc.apply(i)));
        BigDecimal discount = sum(items, disc);
        BigDecimal tax = sum(items, i -> lineTax(qty.apply(i), price.apply(i), disc.apply(i), taxRate.apply(i)));
        BigDecimal total = sum(items, i -> lineAmount(qty.apply(i), price.apply(i), disc.apply(i), taxRate.apply(i)));
        return new Totals(subtotal, discount, tax, total);
    }

    /**
     * Cộng dồn tổng thành tiền của các dòng hàng.
     * @param items danh sách dòng hàng (có thể null) @param amountFn hàm lấy thành tiền của một dòng
     * @param <T> kiểu dòng hàng @return tổng thành tiền (0 nếu rỗng/null)
     */
    public static <T> BigDecimal sumAmount(Collection<T> items, Function<T, BigDecimal> amountFn) {
        if (items == null || items.isEmpty()) return BigDecimal.ZERO;
        return items.stream()
                .map(amountFn)
                .filter(a -> a != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
