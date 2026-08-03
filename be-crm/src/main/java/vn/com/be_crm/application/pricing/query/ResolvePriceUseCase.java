package vn.com.be_crm.application.pricing.query;

import vn.com.be_crm.application.pricing.dto.ResolvePriceResult;
import vn.com.be_crm.domain.pricing.entity.PricePolicyProduct;
import vn.com.be_crm.domain.pricing.enums.DiscountType;
import vn.com.be_crm.domain.pricing.repository.IPricePolicyCustomerRepository;
import vn.com.be_crm.domain.pricing.repository.IPricePolicyEmployeeRepository;
import vn.com.be_crm.domain.pricing.repository.IPricePolicyProductRepository;
import vn.com.be_crm.core.error.frontend.ForbiddenException;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Use case tra cứu đơn giá & chiết khấu của một sản phẩm theo chính sách giá (pricebook entry).
 * Đối chiếu số lượng tối thiểu; quy đổi chiết khấu % hoặc số tiền về chiết khấu trên một đơn vị.
 * Trước khi tra giá, kiểm tra người gọi (trừ ADMIN/SALES_MANAGER) và khách hàng (nếu có) có thuộc
 * danh sách "nhân viên/khách hàng áp dụng" của chính sách hay không — danh sách rỗng = không giới hạn.
 */
public class ResolvePriceUseCase {
    private final IPricePolicyProductRepository repo;
    private final IPricePolicyEmployeeRepository employeeRepo;
    private final IPricePolicyCustomerRepository customerRepo;

    /** @param repo port lưu trữ dòng chính sách giá @param employeeRepo port danh sách nhân viên áp dụng @param customerRepo port danh sách khách hàng áp dụng */
    public ResolvePriceUseCase(IPricePolicyProductRepository repo, IPricePolicyEmployeeRepository employeeRepo,
                                IPricePolicyCustomerRepository customerRepo) {
        this.repo = repo; this.employeeRepo = employeeRepo; this.customerRepo = customerRepo;
    }

    /**
     * Tra cứu giá cho (chính sách giá, sản phẩm, số lượng), có kiểm tra quyền sử dụng chính sách.
     *
     * <p>Ngưỡng {@code min_qty} được xét <b>sau</b> khi đã tìm ra dòng chính sách, không lọc ngay trong
     * stream — nhờ vậy phân biệt được "sản phẩm ngoài chính sách" (minQty null) với "chưa đủ số lượng"
     * (minQty có giá trị), để frontend giải thích được cho người dùng.
     *
     * @param pricePolicyId ID chính sách giá @param productId ID sản phẩm @param quantity số lượng
     * @param callerUserId ID người gọi @param callerPrivileged true nếu ADMIN/SALES_MANAGER (bỏ qua kiểm tra nhân viên)
     * @param customerId ID khách hàng đang chọn trên form (null = không kiểm tra)
     * @return kết quả giá (found=false nếu ngoài chính sách hoặc chưa đạt số lượng tối thiểu)
     */
    public ResolvePriceResult execute(Long pricePolicyId, Long productId, BigDecimal quantity,
                                       Long callerUserId, boolean callerPrivileged, Long customerId) {
        if (!callerPrivileged && !employeeRepo.isEligibleForUser(pricePolicyId, callerUserId)) {
            throw new ForbiddenException("Bạn không được phép sử dụng chính sách giá này");
        }
        if (customerId != null && !customerRepo.isEligibleForCustomer(pricePolicyId, customerId)) {
            throw new ForbiddenException("Khách hàng này không thuộc chính sách giá đã chọn");
        }
        BigDecimal qty = quantity != null ? quantity : BigDecimal.ONE;
        // Bảng có UNIQUE (price_policy_id, product_id) nên tối đa một dòng cho mỗi sản phẩm
        PricePolicyProduct entry = repo.findAllByPricePolicyId(pricePolicyId).stream()
                .filter(p -> p.getProductId() != null && p.getProductId().equals(productId))
                .findFirst()
                .orElse(null);
        if (entry == null) return new ResolvePriceResult(productId, null, BigDecimal.ZERO, false, null);

        // Chưa đủ số lượng tối thiểu → không được hưởng giá ưu đãi, nhưng vẫn trả ngưỡng để giải thích
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
