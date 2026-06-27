package vn.com.be_crm.application.pricing.dto;

import java.math.BigDecimal;

/**
 * Kết quả tra cứu giá theo chính sách giá cho một sản phẩm + số lượng.
 * @param productId ID sản phẩm
 * @param unitPrice đơn giá niêm yết theo chính sách (null nếu không tìm thấy)
 * @param discount  chiết khấu trên một đơn vị (quy đổi từ % hoặc số tiền)
 * @param found     true nếu sản phẩm có trong chính sách giá
 */
public record ResolvePriceResult(
        Long productId,
        BigDecimal unitPrice,
        BigDecimal discount,
        boolean found
) {}
