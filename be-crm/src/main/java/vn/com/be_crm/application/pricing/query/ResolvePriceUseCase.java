package vn.com.be_crm.application.pricing.query;

import vn.com.be_crm.application.pricing.dto.ResolvePriceResult;
import vn.com.be_crm.domain.pricing.entity.PricePolicyProduct;
import vn.com.be_crm.domain.pricing.enums.DiscountType;
import vn.com.be_crm.domain.pricing.repository.IPricePolicyCustomerRepository;
import vn.com.be_crm.domain.pricing.repository.IPricePolicyProductRepository;
import vn.com.be_crm.core.error.frontend.ForbiddenException;

import java.math.BigDecimal;
import java.math.RoundingMode;

// tra cứu đơn giá & chiết khấu của một sản phẩm theo chính sách giá (pricebook entry). Đối
// chiếu số lượng tối thiểu; quy đổi chiết khấu % hoặc số tiền về chiết khấu trên một đơn vị.
// Không còn giới hạn theo nhân viên (bảng price_policy_employees đã bỏ) — chỉ còn kiểm khách
// hàng (nếu có) có thuộc danh sách "khách hàng áp dụng" của chính sách hay không.
public class ResolvePriceUseCase {
    private final IPricePolicyProductRepository repo;
    private final IPricePolicyCustomerRepository customerRepo;

    public ResolvePriceUseCase(IPricePolicyProductRepository repo, IPricePolicyCustomerRepository customerRepo) {
        this.repo = repo; this.customerRepo = customerRepo;
    }

    // ngưỡng min_qty được xét SAU khi đã tìm ra dòng chính sách, không lọc ngay trong stream —
    // nhờ vậy phân biệt được "sản phẩm ngoài chính sách" (minQty null) với "chưa đủ số lượng"
    // (minQty có giá trị), để frontend giải thích được cho người dùng
    public ResolvePriceResult execute(Long pricePolicyId, Long productId, BigDecimal quantity, Long customerId) {
        if (customerId != null && !customerRepo.isEligibleForCustomer(pricePolicyId, customerId)) {
            throw new ForbiddenException("Khách hàng này không thuộc chính sách giá đã chọn");
        }
        BigDecimal qty = quantity != null ? quantity : BigDecimal.ONE;
        // bảng có UNIQUE (price_policy_id, product_id) nên tối đa một dòng cho mỗi sản phẩm
        PricePolicyProduct entry = repo.findAllByPricePolicyId(pricePolicyId).stream()
                .filter(p -> p.getProductId() != null && p.getProductId().equals(productId))
                .findFirst()
                .orElse(null);
        if (entry == null) return new ResolvePriceResult(productId, null, BigDecimal.ZERO, false, null);

        // chưa đủ số lượng tối thiểu -> không được hưởng giá ưu đãi, nhưng vẫn trả ngưỡng để giải thích
        BigDecimal minQty = entry.getMinQty();
        if (minQty != null && qty.compareTo(minQty) < 0) {
            return new ResolvePriceResult(productId, null, BigDecimal.ZERO, false, minQty);
        }

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
        return new ResolvePriceResult(productId, unitPrice, discount, true, minQty);
    }
}
