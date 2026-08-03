package vn.com.be_crm.presentation.quotation;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.com.be_crm.application.quotation.command.*;
import vn.com.be_crm.application.quotation.dto.*;
import vn.com.be_crm.application.quotation.query.ListQuotationApprovalUseCase;
import vn.com.be_crm.core.response.ApiResponse;

import java.util.List;

/**
 * REST controller cho bước phê duyệt báo giá.
 */
@RestController
@RequestMapping("/api/quotations/{quotationId}/approvals")
public class QuotationApprovalController {
    private final CreateQuotationApprovalUseCase createUC;
    private final UpdateQuotationApprovalUseCase updateUC;
    private final DeleteQuotationApprovalUseCase deleteUC;
    private final ListQuotationApprovalUseCase listUC;

    /** @param createUC tạo mới @param updateUC cập nhật @param deleteUC xóa @param listUC danh sách */
    public QuotationApprovalController(CreateQuotationApprovalUseCase createUC, UpdateQuotationApprovalUseCase updateUC,
                                        DeleteQuotationApprovalUseCase deleteUC, ListQuotationApprovalUseCase listUC) {
        this.createUC = createUC; this.updateUC = updateUC; this.deleteUC = deleteUC; this.listUC = listUC;
    }

    /** Tạo mới bước phê duyệt. @param quotationId ID báo giá @param cmd body @return 201 */
    @PostMapping
    public ResponseEntity<ApiResponse<QuotationApprovalResult>> create(@PathVariable Long quotationId,
                                                                        @Valid @RequestBody CreateQuotationApprovalCommand cmd) {
        return ResponseEntity.status(201).body(ApiResponse.created(createUC.execute(
                CreateQuotationApprovalCommand.builder().quotationId(quotationId).approverId(cmd.getApproverId())
                        .level(cmd.getLevel()).status(cmd.getStatus()).comment(cmd.getComment()).build())));
    }

    /** Lấy danh sách bước phê duyệt. @param quotationId ID báo giá @return 200 */
    @GetMapping
    public ResponseEntity<ApiResponse<List<QuotationApprovalResult>>> list(@PathVariable Long quotationId) {
        return ResponseEntity.ok(ApiResponse.ok(listUC.execute(quotationId)));
    }

    /** Cập nhật bước phê duyệt. @param id ID @param cmd body @return 200 */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<QuotationApprovalResult>> update(@PathVariable Long quotationId,
                                                                        @PathVariable Long id,
                                                                        @Valid @RequestBody UpdateQuotationApprovalCommand cmd) {
        return ResponseEntity.ok(ApiResponse.ok(updateUC.execute(
                UpdateQuotationApprovalCommand.builder().id(id).status(cmd.getStatus())
                        .comment(cmd.getComment()).approvedAt(cmd.getApprovedAt()).build())));
    }

    /** Xóa bước phê duyệt. @param id ID @return 204 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long quotationId, @PathVariable Long id) {
        deleteUC.execute(id); return ResponseEntity.noContent().build();
    }
}
