package vn.com.be_crm.domain.invoice.enums;

import java.math.BigDecimal;

/** Trạng thái thanh toán hóa đơn (suy ra từ tổng tiền đã trả so với tổng hóa đơn). */
public enum PaymentStatus {
    unpaid, partial, paid;

    /**
     * Suy ra trạng thái thanh toán từ số tiền đã trả và tổng hóa đơn.
     * @param paid tổng tiền đã trả @param total tổng hóa đơn
     * @return paid nếu trả đủ, partial nếu trả một phần, unpaid nếu chưa trả
     */
    public static PaymentStatus fromAmounts(BigDecimal paidAmount, BigDecimal total) {
        BigDecimal p = paidAmount != null ? paidAmount : BigDecimal.ZERO;
        BigDecimal t = total != null ? total : BigDecimal.ZERO;
        if (p.compareTo(BigDecimal.ZERO) <= 0) return unpaid;
        if (p.compareTo(t) >= 0 && t.compareTo(BigDecimal.ZERO) > 0) return paid;
        return partial;
    }
}
