package vn.com.be_crm.presentation.pub;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.com.be_crm.application.quotation.command.RespondToQuotationUseCase;
import vn.com.be_crm.application.quotation.dto.PublicQuotationView;
import vn.com.be_crm.application.quotation.query.GetQuotationByTokenUseCase;
import vn.com.be_crm.presentation.pub.request.QuotationRespondRequest;
import vn.com.be_crm.core.response.ApiResponse;

/**
 * REST controller công khai (không cần JWT) cho khách hàng xem + phản hồi báo
 * giá theo token.
 */
@RestController
@RequestMapping("/api/public/quotations")
public class PublicQuotationController {
    private final GetQuotationByTokenUseCase getUC;
    private final RespondToQuotationUseCase respondUC;

    /**
     * @param getUC lấy báo giá theo token @param respondUC ghi nhận phản hồi của
     *              khách
     */
    public PublicQuotationController(GetQuotationByTokenUseCase getUC, RespondToQuotationUseCase respondUC) {
        this.getUC = getUC;
        this.respondUC = respondUC;
    }

    /** Xem báo giá công khai theo token. @param token token phản hồi @return 200 */
    @GetMapping("/{token}")
    public ResponseEntity<ApiResponse<PublicQuotationView>> get(@PathVariable String token) {
        return ResponseEntity.ok(ApiResponse.ok(getUC.execute(token)));
    }

    /**
     * Khách phản hồi báo giá (accept/adjust/reject). @param token token @param body
     * hành động + ghi chú @return 200
     */
    @PostMapping("/{token}/respond")
    public ResponseEntity<ApiResponse<Void>> respond(@PathVariable String token,
            @RequestBody QuotationRespondRequest body) {
        respondUC.execute(token, body.getAction(), body.getNote());
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
