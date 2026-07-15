package vn.com.be_crm.presentation.related;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.com.be_crm.application.related.dto.ContactRelatedResult;
import vn.com.be_crm.application.related.dto.CustomerRelatedResult;
import vn.com.be_crm.application.related.dto.InvoiceRelatedResult;
import vn.com.be_crm.application.related.dto.LeadRelatedResult;
import vn.com.be_crm.application.related.dto.OpportunityRelatedResult;
import vn.com.be_crm.application.related.dto.OrderRelatedResult;
import vn.com.be_crm.application.related.dto.QuotationRelatedResult;
import vn.com.be_crm.application.related.query.GetContactRelatedUseCase;
import vn.com.be_crm.application.related.query.GetCustomerRelatedUseCase;
import vn.com.be_crm.application.related.query.GetInvoiceRelatedUseCase;
import vn.com.be_crm.application.related.query.GetLeadRelatedUseCase;
import vn.com.be_crm.application.related.query.GetOpportunityRelatedUseCase;
import vn.com.be_crm.application.related.query.GetOrderRelatedUseCase;
import vn.com.be_crm.application.related.query.GetQuotationRelatedUseCase;
import vn.com.be_crm.infrastructure.shared.util.SecurityUtils;
import vn.com.be_crm.presentation.shared.ApiResponse;

/**
 * REST controller cho trang chi tiết 360° — trả bản ghi liên quan của một bản ghi cha.
 */
@RestController
@RequestMapping("/api")
public class RelatedController {

    private final GetCustomerRelatedUseCase customerUC;
    private final GetOpportunityRelatedUseCase opportunityUC;
    private final GetLeadRelatedUseCase leadUC;
    private final GetContactRelatedUseCase contactUC;
    private final GetQuotationRelatedUseCase quotationUC;
    private final GetOrderRelatedUseCase orderUC;
    private final GetInvoiceRelatedUseCase invoiceUC;

    /**
     * @param customerUC    use case 360° khách hàng
     * @param opportunityUC use case 360° cơ hội
     * @param leadUC        use case 360° tiềm năng
     * @param contactUC     use case 360° liên hệ
     * @param quotationUC   use case 360° báo giá
     * @param orderUC       use case 360° đơn hàng
     * @param invoiceUC     use case 360° hóa đơn
     */
    public RelatedController(GetCustomerRelatedUseCase customerUC, GetOpportunityRelatedUseCase opportunityUC,
                             GetLeadRelatedUseCase leadUC, GetContactRelatedUseCase contactUC,
                             GetQuotationRelatedUseCase quotationUC, GetOrderRelatedUseCase orderUC,
                             GetInvoiceRelatedUseCase invoiceUC) {
        this.customerUC = customerUC;
        this.opportunityUC = opportunityUC;
        this.leadUC = leadUC;
        this.contactUC = contactUC;
        this.quotationUC = quotationUC;
        this.orderUC = orderUC;
        this.invoiceUC = invoiceUC;
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

    /**
     * Bản ghi liên quan của một tiềm năng (cơ hội đã convert + hoạt động).
     *
     * @param id  ID tiềm năng
     * @param req HTTP request (lấy userId từ JWT)
     * @return 200 kèm dữ liệu; 403 nếu không phụ trách tiềm năng này
     */
    @GetMapping("/leads/{id}/related")
    public ResponseEntity<ApiResponse<LeadRelatedResult>> leadRelated(@PathVariable Long id, HttpServletRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(leadUC.execute(id, userId(req), privileged())));
    }

    /**
     * Bản ghi liên quan của một liên hệ (cơ hội, báo giá, đơn, hóa đơn, phiếu CS, hoạt động).
     *
     * @param id  ID liên hệ
     * @param req HTTP request (lấy userId từ JWT)
     * @return 200 kèm dữ liệu; 403 nếu không phụ trách liên hệ này
     */
    @GetMapping("/contacts/{id}/related")
    public ResponseEntity<ApiResponse<ContactRelatedResult>> contactRelated(@PathVariable Long id, HttpServletRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(contactUC.execute(id, userId(req), privileged())));
    }

    /**
     * Bản ghi liên quan của một báo giá (đơn hàng, hóa đơn, hoạt động).
     *
     * @param id  ID báo giá
     * @param req HTTP request (lấy userId từ JWT)
     * @return 200 kèm dữ liệu; 403 nếu không phụ trách báo giá này
     */
    @GetMapping("/quotations/{id}/related")
    public ResponseEntity<ApiResponse<QuotationRelatedResult>> quotationRelated(@PathVariable Long id, HttpServletRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(quotationUC.execute(id, userId(req), privileged())));
    }

    /**
     * Bản ghi liên quan của một đơn hàng (hóa đơn, hoạt động).
     *
     * @param id  ID đơn hàng
     * @param req HTTP request (lấy userId từ JWT)
     * @return 200 kèm dữ liệu; 403 nếu không phụ trách đơn hàng này
     */
    @GetMapping("/orders/{id}/related")
    public ResponseEntity<ApiResponse<OrderRelatedResult>> orderRelated(@PathVariable Long id, HttpServletRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(orderUC.execute(id, userId(req), privileged())));
    }

    /**
     * Bản ghi liên quan của một hóa đơn (phiếu chăm sóc, hoạt động).
     *
     * @param id  ID hóa đơn
     * @param req HTTP request (lấy userId từ JWT)
     * @return 200 kèm dữ liệu; 403 nếu không phụ trách hóa đơn này
     */
    @GetMapping("/invoices/{id}/related")
    public ResponseEntity<ApiResponse<InvoiceRelatedResult>> invoiceRelated(@PathVariable Long id, HttpServletRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(invoiceUC.execute(id, userId(req), privileged())));
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
