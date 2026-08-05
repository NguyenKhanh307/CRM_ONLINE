package vn.com.be_crm.presentation.tracking;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.com.be_crm.application.campaign.dto.PublicCampaignResult;
import vn.com.be_crm.application.campaign.query.ListPublicCampaignsUseCase;
import vn.com.be_crm.application.lead.command.RecordTrackingEventUseCase;
import vn.com.be_crm.application.lead.command.RequestProductQuoteUseCase;
import vn.com.be_crm.application.lead.command.SubmitTrackingFormUseCase;
import vn.com.be_crm.application.lead.command.TrackProductViewUseCase;
import vn.com.be_crm.application.lead.command.TrackVisitUseCase;
import vn.com.be_crm.application.lead.dto.LeadResult;
import vn.com.be_crm.application.lead.dto.RequestProductQuoteCommand;
import vn.com.be_crm.application.lead.dto.TrackProductViewCommand;
import vn.com.be_crm.application.product.dto.PublicProductResult;
import vn.com.be_crm.application.product.query.ListPublicProductsUseCase;
import vn.com.be_crm.core.response.ApiResponse;

import java.util.List;

/**
 * REST controller công khai cho web tracking (không yêu cầu đăng nhập).
 * Phục vụ trang landing sản phẩm: tạo tiềm năng ẩn danh, cộng điểm, nộp form, yêu cầu báo giá.
 */
@RestController
@RequestMapping("/api/tracking")
public class TrackingController {
    private final TrackVisitUseCase visitUC;
    private final RecordTrackingEventUseCase scoreUC;
    private final SubmitTrackingFormUseCase submitUC;
    private final ListPublicCampaignsUseCase campaignsUC;
    private final ListPublicProductsUseCase productsUC;
    private final RequestProductQuoteUseCase requestQuoteUC;
    private final TrackProductViewUseCase trackViewUC;

    /**
     * @param visitUC        lượt truy cập
     * @param scoreUC        ghi điểm
     * @param submitUC       nộp form
     * @param campaignsUC    danh sách chiến dịch đang chạy (chọn nguồn cho landing page)
     * @param productsUC     danh sách sản phẩm công khai
     * @param requestQuoteUC yêu cầu báo giá các sản phẩm đã chọn
     * @param trackViewUC    ghi nhận lượt xem chi tiết một sản phẩm
     */
    public TrackingController(TrackVisitUseCase visitUC, RecordTrackingEventUseCase scoreUC,
                              SubmitTrackingFormUseCase submitUC, ListPublicCampaignsUseCase campaignsUC,
                              ListPublicProductsUseCase productsUC, RequestProductQuoteUseCase requestQuoteUC,
                              TrackProductViewUseCase trackViewUC) {
        this.visitUC = visitUC; this.scoreUC = scoreUC; this.submitUC = submitUC; this.campaignsUC = campaignsUC;
        this.productsUC = productsUC; this.requestQuoteUC = requestQuoteUC; this.trackViewUC = trackViewUC;
    }

    /**
     * Chiến dịch đang chạy để landing page gắn nguồn (`utm_campaign`).
     * Chỉ trả id/mã/tên — xem {@link ListPublicCampaignsUseCase}.
     *
     * @return 200 kèm danh sách rút gọn
     */
    @GetMapping("/campaigns")
    public ResponseEntity<ApiResponse<List<PublicCampaignResult>>> campaigns() {
        return ResponseEntity.ok(ApiResponse.ok(campaignsUC.execute()));
    }

    /** Lượt truy cập: tạo/trả về tiềm năng theo mã, gắn chiến dịch nguồn nếu có. @param req body @return 200 */
    @PostMapping("/visit")
    public ResponseEntity<ApiResponse<LeadResult>> visit(@RequestBody VisitRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(
                visitUC.execute(req != null ? req.code() : null, req != null ? req.campaignId() : null)));
    }

    /** Ghi nhận hành động + cộng điểm. @param req body @return 200 */
    @PostMapping("/score")
    public ResponseEntity<ApiResponse<LeadResult>> score(@RequestBody ScoreRequest req) {
        LeadResult result = scoreUC.execute(req.code(), req.action(), req.label(),
                req.points() != null ? req.points() : 0);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    /** Nộp form liên hệ + cộng điểm. @param req body @return 200 */
    @PostMapping("/submit")
    public ResponseEntity<ApiResponse<LeadResult>> submit(@RequestBody SubmitRequest req) {
        LeadResult result = submitUC.execute(req.code(), req.name(), req.companyName(),
                req.email(), req.phone(), req.note(), req.points() != null ? req.points() : 0);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    /** Danh sách sản phẩm đang hoạt động cho landing page. @return 200 */
    @GetMapping("/products")
    public ResponseEntity<ApiResponse<List<PublicProductResult>>> products() {
        return ResponseEntity.ok(ApiResponse.ok(productsUC.execute()));
    }

    /** Yêu cầu báo giá các sản phẩm đã chọn + cập nhật thông tin liên hệ. @param cmd body @return 200 */
    @PostMapping("/request-quote")
    public ResponseEntity<ApiResponse<LeadResult>> requestQuote(@RequestBody RequestProductQuoteCommand cmd) {
        return ResponseEntity.ok(ApiResponse.ok(requestQuoteUC.execute(cmd)));
    }

    /** Ghi nhận lượt xem chi tiết một sản phẩm. @param req body @return 200 */
    @PostMapping("/view-product")
    public ResponseEntity<ApiResponse<LeadResult>> viewProduct(@RequestBody ViewProductRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(trackViewUC.execute(
                TrackProductViewCommand.builder().code(req.code()).productId(req.productId()).build())));
    }

    /** Body cho /visit. */
    public record VisitRequest(String code, Long campaignId) {}

    /** Body cho /score. */
    public record ScoreRequest(String code, String action, String label, Integer points) {}

    /** Body cho /submit. */
    public record SubmitRequest(String code, String name, String companyName, String email,
                                String phone, String note, Integer points) {}

    /** Body cho /view-product. */
    public record ViewProductRequest(String code, Long productId) {}
}
