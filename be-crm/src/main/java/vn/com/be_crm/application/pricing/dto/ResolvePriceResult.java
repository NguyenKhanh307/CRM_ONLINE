package vn.com.be_crm.application.pricing.dto;

import java.math.BigDecimal;

/**
 * Kết quả tra cứu giá theo chính sách giá cho một sản phẩm + số lượng.
 * @param productId ID sản phẩm
 * @param unitPrice đơn giá niêm yết theo chính sách (null nếu chưa áp dụng được)
 * @param discount  chiết khấu trên một đơn vị (quy đổi từ % hoặc số tiền)
 * @param found     true nếu sản phẩm có trong chính sách <b>và</b> số lượng đã đạt ngưỡng
 * @param minQty    số lượng tối thiểu của dòng chính sách; null khi sản phẩm không có trong chính sách.
 *                  Dùng để giải thích cho người dùng vì sao giá ưu đãi chưa được áp
 */
public record ResolvePriceResult(
        Long productId,
        BigDecimal unitPrice,
        BigDecimal discount,
        boolean found,
        BigDecimal minQty
) {}
