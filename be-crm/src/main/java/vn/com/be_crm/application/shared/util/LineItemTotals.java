package vn.com.be_crm.application.shared.util;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.function.Function;

/**
 * Tiện ích cộng dồn (roll-up) giá trị dòng hàng — dùng chung cho Cơ hội / Báo giá / Hóa đơn.
 */
public final class LineItemTotals {

    private LineItemTotals() {}

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
