package vn.com.be_crm.presentation.pricing;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.com.be_crm.application.pricing.dto.ResolvePriceResult;
import vn.com.be_crm.application.pricing.query.ResolvePriceUseCase;
import vn.com.be_crm.core.response.ApiResponse;

import java.math.BigDecimal;

// REST controller tra cứu giá theo chính sách giá (pricebook) cho form Cơ hội / Báo giá
@RestController
@RequestMapping("/api/pricing")
public class ResolvePriceController {
    private final ResolvePriceUseCase resolveUC;

    public ResolvePriceController(ResolvePriceUseCase resolveUC) { this.resolveUC = resolveUC; }

    // tra cứu đơn giá & chiết khấu theo chính sách giá — kiểm tra khách hàng có được phép dùng
    // chính sách này không (ném 403 nếu không hợp lệ)
    @GetMapping("/resolve")
    public ResponseEntity<ApiResponse<ResolvePriceResult>> resolve(
            @RequestParam Long pricePolicyId,
            @RequestParam Long productId,
            @RequestParam(required = false, defaultValue = "1") BigDecimal quantity,
            @RequestParam(required = false) Long customerId) {
        return ResponseEntity.ok(ApiResponse.ok(resolveUC.execute(pricePolicyId, productId, quantity, customerId)));
    }
}
