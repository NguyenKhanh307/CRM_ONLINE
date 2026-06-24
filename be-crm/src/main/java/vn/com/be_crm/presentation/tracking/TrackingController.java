package vn.com.be_crm.presentation.tracking;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.com.be_crm.application.lead.command.RecordTrackingEventUseCase;
import vn.com.be_crm.application.lead.command.SubmitTrackingFormUseCase;
import vn.com.be_crm.application.lead.command.TrackVisitUseCase;
import vn.com.be_crm.application.lead.dto.LeadResult;
import vn.com.be_crm.presentation.shared.ApiResponse;

/**
 * REST controller công khai cho web tracking (không yêu cầu đăng nhập).
 * Phục vụ trang landing mô phỏng: tạo tiềm năng ẩn danh, cộng điểm, nộp form.
 */
@RestController
@RequestMapping("/api/tracking")
public class TrackingController {
    private final TrackVisitUseCase visitUC;
    private final RecordTrackingEventUseCase scoreUC;
    private final SubmitTrackingFormUseCase submitUC;

    /** @param visitUC lượt truy cập @param scoreUC ghi điểm @param submitUC nộp form */
    public TrackingController(TrackVisitUseCase visitUC, RecordTrackingEventUseCase scoreUC,
                              SubmitTrackingFormUseCase submitUC) {
        this.visitUC = visitUC; this.scoreUC = scoreUC; this.submitUC = submitUC;
    }

    /** Lượt truy cập: tạo/trả về tiềm năng theo mã. @param req body @return 200 */
    @PostMapping("/visit")
    public ResponseEntity<ApiResponse<LeadResult>> visit(@RequestBody VisitRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(visitUC.execute(req != null ? req.code() : null)));
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

    /** Body cho /visit. */
    public record VisitRequest(String code) {}

    /** Body cho /score. */
    public record ScoreRequest(String code, String action, String label, Integer points) {}

    /** Body cho /submit. */
    public record SubmitRequest(String code, String name, String companyName, String email,
                                String phone, String note, Integer points) {}
}
