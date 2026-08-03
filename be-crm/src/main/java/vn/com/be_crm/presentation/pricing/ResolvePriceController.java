package vn.com.be_crm.presentation.pricing;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.com.be_crm.application.pricing.dto.ResolvePriceResult;
import vn.com.be_crm.application.pricing.query.ResolvePriceUseCase;
import vn.com.be_crm.core.util.SecurityUtils;
import vn.com.be_crm.core.response.ApiResponse;

import java.math.BigDecimal;

/**
 * REST controller tra cứu giá theo chính sách giá (pricebook) cho form Cơ hội / Báo giá.
 */
@RestController
@RequestMapping("/api/pricing")
public class ResolvePriceController {
    private final ResolvePriceUseCase resolveUC;

    /** @param resolveUC use case tra cứu giá */
    public ResolvePriceController(ResolvePriceUseCase resolveUC) { this.resolveUC = resolveUC; }

    /**
     * Tra cứu đơn giá & chiết khấu theo chính sách giá — kiểm tra người gọi/khách hàng có được phép
     * dùng chính sách này không (ném 403 nếu không hợp lệ).
     * @param pricePolicyId ID chính sách giá @param productId ID sản phẩm @param quantity số lượng (mặc định 1)
     * @param customerId ID khách hàng đang chọn trên form (tùy chọn)
     * @return 200 kèm kết quả giá
     */
    @GetMapping("/resolve")
    public ResponseEntity<ApiResponse<ResolvePriceResult>> resolve(
            @RequestParam Long pricePolicyId,
            @RequestParam Long productId,
            @RequestParam(required = false, defaultValue = "1") BigDecimal quantity,
            @RequestParam(required = false) Long customerId,
            HttpServletRequest req) {
        Long userId = (Long) req.getAttribute("userId");
        boolean privileged = SecurityUtils.isAdminOrManager(SecurityContextHolder.getContext().getAuthentication());
        return ResponseEntity.ok(ApiResponse.ok(resolveUC.execute(pricePolicyId, productId, quantity, userId, privileged, customerId)));
    }
}
