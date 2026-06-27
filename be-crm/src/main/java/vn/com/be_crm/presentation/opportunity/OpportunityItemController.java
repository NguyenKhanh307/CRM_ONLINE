package vn.com.be_crm.presentation.opportunity;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.com.be_crm.application.opportunity.command.*;
import vn.com.be_crm.application.opportunity.dto.*;
import vn.com.be_crm.application.opportunity.query.ListOpportunityItemUseCase;
import vn.com.be_crm.presentation.shared.ApiResponse;

import java.util.List;

/**
 * REST controller cho dòng sản phẩm trong cơ hội.
 */
@RestController
@RequestMapping("/api/opportunities/{opportunityId}/items")
public class OpportunityItemController {
    private final CreateOpportunityItemUseCase createUC;
    private final UpdateOpportunityItemUseCase updateUC;
    private final DeleteOpportunityItemUseCase deleteUC;
    private final ListOpportunityItemUseCase listUC;
    private final RecomputeOpportunityAmountUseCase recomputeUC;

    /** @param createUC tạo mới @param updateUC cập nhật @param deleteUC xóa @param listUC danh sách @param recomputeUC roll-up amount */
    public OpportunityItemController(CreateOpportunityItemUseCase createUC, UpdateOpportunityItemUseCase updateUC,
                                      DeleteOpportunityItemUseCase deleteUC, ListOpportunityItemUseCase listUC,
                                      RecomputeOpportunityAmountUseCase recomputeUC) {
        this.createUC = createUC; this.updateUC = updateUC; this.deleteUC = deleteUC; this.listUC = listUC;
        this.recomputeUC = recomputeUC;
    }

    /** Tạo mới dòng sản phẩm. @param opportunityId ID cơ hội @param cmd body @return 201 */
    @PostMapping
    public ResponseEntity<ApiResponse<OpportunityItemResult>> create(@PathVariable Long opportunityId,
                                                                      @Valid @RequestBody CreateOpportunityItemCommand cmd) {
        OpportunityItemResult result = createUC.execute(
                CreateOpportunityItemCommand.builder().opportunityId(opportunityId).productId(cmd.getProductId())
                        .quantity(cmd.getQuantity()).unitPrice(cmd.getUnitPrice()).discount(cmd.getDiscount())
                        .amount(cmd.getAmount()).note(cmd.getNote()).build());
        recomputeUC.execute(opportunityId);
        return ResponseEntity.status(201).body(ApiResponse.created(result));
    }

    /** Lấy danh sách dòng sản phẩm. @param opportunityId ID cơ hội @return 200 */
    @GetMapping
    public ResponseEntity<ApiResponse<List<OpportunityItemResult>>> list(@PathVariable Long opportunityId) {
        return ResponseEntity.ok(ApiResponse.ok(listUC.execute(opportunityId)));
    }

    /** Cập nhật dòng sản phẩm. @param id ID @param cmd body @return 200 */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<OpportunityItemResult>> update(@PathVariable Long opportunityId,
                                                                      @PathVariable Long id,
                                                                      @Valid @RequestBody UpdateOpportunityItemCommand cmd) {
        OpportunityItemResult result = updateUC.execute(
                UpdateOpportunityItemCommand.builder().id(id).productId(cmd.getProductId())
                        .quantity(cmd.getQuantity()).unitPrice(cmd.getUnitPrice()).discount(cmd.getDiscount())
                        .amount(cmd.getAmount()).note(cmd.getNote()).build());
        recomputeUC.execute(opportunityId);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    /** Xóa dòng sản phẩm. @param id ID @return 204 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long opportunityId, @PathVariable Long id) {
        deleteUC.execute(id);
        recomputeUC.execute(opportunityId);
        return ResponseEntity.noContent().build();
    }
}
