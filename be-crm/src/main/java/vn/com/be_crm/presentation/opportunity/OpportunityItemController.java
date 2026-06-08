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

    /** @param createUC tạo mới @param updateUC cập nhật @param deleteUC xóa @param listUC danh sách */
    public OpportunityItemController(CreateOpportunityItemUseCase createUC, UpdateOpportunityItemUseCase updateUC,
                                      DeleteOpportunityItemUseCase deleteUC, ListOpportunityItemUseCase listUC) {
        this.createUC = createUC; this.updateUC = updateUC; this.deleteUC = deleteUC; this.listUC = listUC;
    }

    /** Tạo mới dòng sản phẩm. @param opportunityId ID cơ hội @param cmd body @return 201 */
    @PostMapping
    public ResponseEntity<ApiResponse<OpportunityItemResult>> create(@PathVariable Long opportunityId,
                                                                      @Valid @RequestBody CreateOpportunityItemCommand cmd) {
        return ResponseEntity.status(201).body(ApiResponse.created(createUC.execute(
                CreateOpportunityItemCommand.builder().opportunityId(opportunityId).productId(cmd.getProductId())
                        .quantity(cmd.getQuantity()).unitPrice(cmd.getUnitPrice()).discount(cmd.getDiscount())
                        .amount(cmd.getAmount()).note(cmd.getNote()).build())));
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
        return ResponseEntity.ok(ApiResponse.ok(updateUC.execute(
                UpdateOpportunityItemCommand.builder().id(id).productId(cmd.getProductId())
                        .quantity(cmd.getQuantity()).unitPrice(cmd.getUnitPrice()).discount(cmd.getDiscount())
                        .amount(cmd.getAmount()).note(cmd.getNote()).build())));
    }

    /** Xóa dòng sản phẩm. @param id ID @return 204 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long opportunityId, @PathVariable Long id) {
        deleteUC.execute(id); return ResponseEntity.noContent().build();
    }
}
