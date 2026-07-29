package vn.com.be_crm.presentation.pub;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.com.be_crm.application.service.command.SubmitCsatByCodeUseCase;
import vn.com.be_crm.application.service.dto.PublicTicketView;
import vn.com.be_crm.application.service.query.GetTicketByCodePublicUseCase;
import vn.com.be_crm.presentation.pub.request.CsatRequest;
import vn.com.be_crm.presentation.shared.ApiResponse;

/**
 * REST controller công khai (không cần JWT) cho khách xem trạng thái phiếu chăm sóc + tự
 * đánh giá hài lòng theo mã phiếu (`/support-page/{code}`).
 */
@RestController
@RequestMapping("/api/public/tickets")
public class PublicTicketController {
    private final GetTicketByCodePublicUseCase getUC;
    private final SubmitCsatByCodeUseCase csatUC;

    /** @param getUC lấy phiếu theo mã @param csatUC ghi nhận đánh giá của khách */
    public PublicTicketController(GetTicketByCodePublicUseCase getUC, SubmitCsatByCodeUseCase csatUC) {
        this.getUC = getUC;
        this.csatUC = csatUC;
    }

    /** Xem phiếu công khai theo mã. @param code mã phiếu @return 200 */
    @GetMapping("/{code}")
    public ResponseEntity<ApiResponse<PublicTicketView>> get(@PathVariable String code) {
        return ResponseEntity.ok(ApiResponse.ok(getUC.execute(code)));
    }

    /** Khách gửi đánh giá hài lòng. @param code mã phiếu @param body điểm + nhận xét @return 200 */
    @PostMapping("/{code}/csat")
    public ResponseEntity<ApiResponse<PublicTicketView>> submitCsat(@PathVariable String code,
            @RequestBody CsatRequest body) {
        return ResponseEntity.ok(ApiResponse.ok(csatUC.execute(code, body.getScore(), body.getComment())));
    }
}
