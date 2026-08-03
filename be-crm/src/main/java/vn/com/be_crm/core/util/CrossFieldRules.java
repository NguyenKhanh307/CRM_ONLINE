package vn.com.be_crm.core.util;

import vn.com.be_crm.core.error.frontend.DomainException;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Ràng buộc chéo giữa nhiều trường — không biểu diễn được bằng annotation Bean Validation trên một field.
 * Gọi trong UseCase; vi phạm → {@link DomainException} (HTTP 400 với message nghiệp vụ).
 */
public final class CrossFieldRules {

    private CrossFieldRules() {}

    /**
     * Giá bán phải ≥ giá vốn (không bán lỗ do nhập nhầm). Bỏ qua nếu thiếu một trong hai.
     *
     * @param basePrice giá bán @param costPrice giá vốn
     */
    public static void requireSellPriceNotBelowCost(BigDecimal basePrice, BigDecimal costPrice) {
        if (basePrice == null || costPrice == null) return;
        if (basePrice.compareTo(costPrice) < 0) {
            throw new DomainException("Giá bán không được nhỏ hơn giá vốn");
        }
    }

    /**
     * Ngày kết thúc phải ≥ ngày bắt đầu (khoảng thời gian hợp lệ). Bỏ qua nếu thiếu một trong hai.
     *
     * @param start     ngày bắt đầu @param end ngày kết thúc
     * @param startName nhãn ngày bắt đầu @param endName nhãn ngày kết thúc
     */
    public static void requireDateRange(LocalDate start, LocalDate end, String startName, String endName) {
        if (start == null || end == null) return;
        if (end.isBefore(start)) {
            throw new DomainException(endName + " không được trước " + startName.toLowerCase());
        }
    }
}
