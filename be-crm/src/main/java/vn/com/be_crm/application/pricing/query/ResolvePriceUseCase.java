package vn.com.be_crm.application.pricing.query;

import vn.com.be_crm.application.pricing.dto.ResolvePriceResult;
import vn.com.be_crm.domain.pricing.entity.PricePolicyProduct;
import vn.com.be_crm.domain.pricing.enums.DiscountType;
import vn.com.be_crm.domain.pricing.repository.IPricePolicyProductRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Use case tra cứu đơn giá & chiết khấu của một sản phẩm theo chính sách giá (pricebook entry).
 * Đối chiếu số lượng tối thiểu; quy đổi chiết khấu % hoặc số tiền về chiết khấu trên một đơn vị.
 */
public class ResolvePriceUseCase {
    private final IPricePolicyProductRepository repo;

    /** @param repo port lưu trữ dòng chính sách giá */
    public ResolvePriceUseCase(IPricePolicyProductRepository repo) { this.repo = repo; }

    /**
     * Tra cứu giá cho (chính sách giá, sản phẩm, số lượng).
     * @param pricePolicyId ID chính sách giá @param productId ID sản phẩm @param quantity số lượng
     * @return kết quả giá (found=false nếu không có trong chính sách)
     */
    public ResolvePriceResult execute(Long pricePolicyId, Long productId, BigDecimal quantity) {
        BigDecimal qty = quantity != null ? quantity : BigDecimal.ONE;
        PricePolicyProduct entry = repo.findAllByPricePolicyId(pricePolicyId).stream()
                .filter(p -> p.getProductId() != null && p.getProductId().equals(productId))
                .filter(p -> p.getMinQty() == null || qty.compareTo(p.getMinQty()) >= 0)
                .findFirst()
                .orElse(null);
        if (entry == null) return new ResolvePriceResult(productId, null, BigDecimal.ZERO, false);

        BigDecimal unitPrice = entry.getPrice() != null ? entry.getPrice() : BigDecimal.ZERO;
        BigDecimal discount = BigDecimal.ZERO;
        if (entry.getDiscountValue() != null) {
            if (entry.getDiscountType() == DiscountType.percent) {
                discount = unitPrice.multiply(entry.getDiscountValue())
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            } else if (entry.getDiscountType() == DiscountType.amount) {
                discount = entry.getDiscountValue();
            }
        }
        return new ResolvePriceResult(productId, unitPrice, discount, true);
    }
}
