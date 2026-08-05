package vn.com.be_crm.presentation.pub;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.com.be_crm.application.quotation.command.RespondToQuotationUseCase;
import vn.com.be_crm.application.quotation.dto.PublicQuotationView;
import vn.com.be_crm.application.quotation.query.GetQuotationByCodeUseCase;
import vn.com.be_crm.presentation.pub.request.QuotationRespondRequest;
import vn.com.be_crm.core.response.ApiResponse;

// REST controller công khai (không cần JWT) cho khách hàng xem + phản hồi báo giá theo mã (code)
@RestController
@RequestMapping("/api/public/quotations")
public class PublicQuotationController {
    private final GetQuotationByCodeUseCase getUC;
    private final RespondToQuotationUseCase respondUC;

    public PublicQuotationController(GetQuotationByCodeUseCase getUC, RespondToQuotationUseCase respondUC) {
        this.getUC = getUC;
        this.respondUC = respondUC;
    }

    // xem báo giá công khai theo mã
    @GetMapping("/{code}")
    public ResponseEntity<ApiResponse<PublicQuotationView>> get(@PathVariable String code) {
        return ResponseEntity.ok(ApiResponse.ok(getUC.execute(code)));
    }

    // khách phản hồi báo giá (accept/adjust/reject)
    @PostMapping("/{code}/respond")
    public ResponseEntity<ApiResponse<Void>> respond(@PathVariable String code,
            @RequestBody QuotationRespondRequest body) {
        respondUC.execute(code, body.getAction(), body.getNote());
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
