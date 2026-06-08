package vn.com.be_crm.presentation.opportunity;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.com.be_crm.application.opportunity.command.*;
import vn.com.be_crm.application.opportunity.dto.*;
import vn.com.be_crm.application.opportunity.query.*;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.presentation.shared.ApiResponse;
import vn.com.be_crm.presentation.shared.PageResponse;

/**
 * REST controller cho nghiệp vụ quản lý cơ hội bán hàng.
 */
@RestController
@RequestMapping("/api/opportunities")
public class OpportunityController {
    private final CreateOpportunityUseCase createUC;
    private final UpdateOpportunityUseCase updateUC;
    private final DeleteOpportunityUseCase deleteUC;
    private final GetOpportunityUseCase getUC;
    private final ListOpportunityUseCase listUC;

    /** @param createUC tạo mới @param updateUC cập nhật @param deleteUC xóa @param getUC lấy @param listUC danh sách */
    public OpportunityController(CreateOpportunityUseCase createUC, UpdateOpportunityUseCase updateUC,
                                  DeleteOpportunityUseCase deleteUC, GetOpportunityUseCase getUC,
                                  ListOpportunityUseCase listUC) {
        this.createUC = createUC; this.updateUC = updateUC; this.deleteUC = deleteUC;
        this.getUC = getUC; this.listUC = listUC;
    }

    /** Tạo mới cơ hội. @param cmd JSON body @return 201 */
    @PostMapping
    public ResponseEntity<ApiResponse<OpportunityResult>> create(@Valid @RequestBody CreateOpportunityCommand cmd) {
        return ResponseEntity.status(201).body(ApiResponse.created(createUC.execute(cmd)));
    }

    /** Lấy danh sách cơ hội. @return 200 */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<OpportunityResult>>> list(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "asc") String sortDir) {
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(listUC.execute(
                PageRequest.builder().page(page).size(size).sortBy(sortBy).sortDir(sortDir).build()))));
    }

    /** Lấy cơ hội theo ID. @param id ID @return 200 */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OpportunityResult>> get(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(getUC.execute(id)));
    }

    /** Cập nhật cơ hội. @param id ID @param cmd body @return 200 */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<OpportunityResult>> update(@PathVariable Long id,
                                                                  @Valid @RequestBody UpdateOpportunityCommand cmd) {
        return ResponseEntity.ok(ApiResponse.ok(updateUC.execute(
                UpdateOpportunityCommand.builder().id(id).name(cmd.getName()).customerId(cmd.getCustomerId())
                        .contactId(cmd.getContactId()).ownerId(cmd.getOwnerId()).stageId(cmd.getStageId())
                        .amount(cmd.getAmount()).probability(cmd.getProbability())
                        .expectedCloseDate(cmd.getExpectedCloseDate()).status(cmd.getStatus()).build())));
    }

    /** Xóa mềm cơ hội. @param id ID @return 204 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteUC.execute(id); return ResponseEntity.noContent().build();
    }
}
