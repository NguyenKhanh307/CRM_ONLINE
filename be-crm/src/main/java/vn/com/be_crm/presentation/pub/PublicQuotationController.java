package vn.com.be_crm.presentation.pub;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.com.be_crm.application.quotation.command.ProposeQuotationAdjustmentUseCase;
import vn.com.be_crm.application.quotation.command.RespondToQuotationUseCase;
import vn.com.be_crm.application.quotation.dto.PublicQuotationView;
import vn.com.be_crm.application.quotation.dto.QuotationAdjustmentProposal;
import vn.com.be_crm.application.quotation.query.GetQuotationByCodeUseCase;
import vn.com.be_crm.presentation.pub.request.QuotationAdjustItemsRequest;
import vn.com.be_crm.presentation.pub.request.QuotationRespondRequest;
import vn.com.be_crm.core.response.ApiResponse;

import java.util.List;
import java.util.stream.Collectors;

// REST controller công khai (không cần JWT) cho khách hàng xem + phản hồi báo giá theo mã (code)
@RestController
@RequestMapping("/api/public/quotations")
public class PublicQuotationController {
    private final GetQuotationByCodeUseCase getUC;
    private final RespondToQuotationUseCase respondUC;
    private final ProposeQuotationAdjustmentUseCase proposeAdjustmentUC;

    public PublicQuotationController(GetQuotationByCodeUseCase getUC, RespondToQuotationUseCase respondUC,
            ProposeQuotationAdjustmentUseCase proposeAdjustmentUC) {
        this.getUC = getUC;
        this.respondUC = respondUC;
        this.proposeAdjustmentUC = proposeAdjustmentUC;
    }

    // xem báo giá công khai theo mã
    @GetMapping("/{code}")
    public ResponseEntity<ApiResponse<PublicQuotationView>> get(@PathVariable String code) {
        return ResponseEntity.ok(ApiResponse.ok(getUC.execute(code)));
    }

    // khách phản hồi báo giá (accept/reject) — "adjust" chuyển qua /propose-adjustment
    @PostMapping("/{code}/respond")
    public ResponseEntity<ApiResponse<Void>> respond(@PathVariable String code,
            @RequestBody QuotationRespondRequest body) {
        respondUC.execute(code, body.getAction(), body.getNote());
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    // khách "Chỉnh sửa" — chỉ lưu đề xuất (sửa số lượng/xóa dòng), không đụng dòng hàng thật
    @PostMapping("/{code}/propose-adjustment")
    public ResponseEntity<ApiResponse<Void>> proposeAdjustment(@PathVariable String code,
            @RequestBody QuotationAdjustItemsRequest body) {
        List<QuotationAdjustmentProposal.Item> items = (body.getItems() == null ? List.<QuotationAdjustItemsRequest.Item>of() : body.getItems())
                .stream().map(i -> new QuotationAdjustmentProposal.Item(i.getId(), i.getQuantity())).collect(Collectors.toList());
        proposeAdjustmentUC.execute(code, items, body.getNote());
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
