package vn.com.be_crm.presentation.related;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.com.be_crm.application.related.dto.CustomerRelatedResult;
import vn.com.be_crm.application.related.dto.OpportunityRelatedResult;
import vn.com.be_crm.application.related.query.GetCustomerRelatedUseCase;
import vn.com.be_crm.application.related.query.GetOpportunityRelatedUseCase;
import vn.com.be_crm.infrastructure.shared.util.SecurityUtils;
import vn.com.be_crm.presentation.shared.ApiResponse;

/**
 * REST controller cho trang chi tiết 360° — trả bản ghi liên quan của một khách hàng / cơ hội.
 */
@RestController
@RequestMapping("/api")
public class RelatedController {

    private final GetCustomerRelatedUseCase customerUC;
    private final GetOpportunityRelatedUseCase opportunityUC;

    /** @param customerUC use case 360° khách hàng @param opportunityUC use case 360° cơ hội */
    public RelatedController(GetCustomerRelatedUseCase customerUC, GetOpportunityRelatedUseCase opportunityUC) {
        this.customerUC = customerUC;
        this.opportunityUC = opportunityUC;
    }

    /**
     * Bản ghi liên quan của một khách hàng (liên hệ, cơ hội, báo giá, đơn, hóa đơn, phiếu CS, hoạt động).
     *
     * @param id  ID khách hàng
     * @param req HTTP request (lấy userId từ JWT)
     * @return 200 kèm dữ liệu; 403 nếu không phụ trách khách hàng này
     */
    @GetMapping("/customers/{id}/related")
    public ResponseEntity<ApiResponse<CustomerRelatedResult>> customerRelated(@PathVariable Long id, HttpServletRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(customerUC.execute(id, userId(req), privileged())));
    }

    /**
     * Bản ghi liên quan của một cơ hội (báo giá, đơn, hóa đơn, hoạt động).
     *
     * @param id  ID cơ hội
     * @param req HTTP request (lấy userId từ JWT)
     * @return 200 kèm dữ liệu; 403 nếu không phụ trách cơ hội này
     */
    @GetMapping("/opportunities/{id}/related")
    public ResponseEntity<ApiResponse<OpportunityRelatedResult>> opportunityRelated(@PathVariable Long id, HttpServletRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(opportunityUC.execute(id, userId(req), privileged())));
    }

    /** ID người dùng hiện tại (JwtAuthFilter set vào request attribute). */
    private Long userId(HttpServletRequest req) {
        return (Long) req.getAttribute("userId");
    }

    /** true nếu ADMIN/SALES_MANAGER — xem được bản ghi của mọi nhân viên. */
    private boolean privileged() {
        return SecurityUtils.isAdminOrManager(SecurityContextHolder.getContext().getAuthentication());
    }
}
