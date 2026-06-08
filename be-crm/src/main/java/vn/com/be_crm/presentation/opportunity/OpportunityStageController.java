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
 * REST controller cho nghiệp vụ quản lý giai đoạn pipeline cơ hội.
 */
@RestController
@RequestMapping("/api/opportunity-stages")
public class OpportunityStageController {
    private final CreateOpportunityStageUseCase createUC;
    private final UpdateOpportunityStageUseCase updateUC;
    private final DeleteOpportunityStageUseCase deleteUC;
    private final GetOpportunityStageUseCase getUC;
    private final ListOpportunityStageUseCase listUC;

    /**
     * @param createUC use case tạo mới @param updateUC use case cập nhật @param deleteUC use case xóa
     * @param getUC    use case lấy theo ID @param listUC use case lấy danh sách
     */
    public OpportunityStageController(CreateOpportunityStageUseCase createUC, UpdateOpportunityStageUseCase updateUC,
                                       DeleteOpportunityStageUseCase deleteUC, GetOpportunityStageUseCase getUC,
                                       ListOpportunityStageUseCase listUC) {
        this.createUC = createUC; this.updateUC = updateUC; this.deleteUC = deleteUC;
        this.getUC = getUC; this.listUC = listUC;
    }

    /** Tạo mới giai đoạn pipeline. @param cmd JSON @return 201 */
    @PostMapping
    public ResponseEntity<ApiResponse<OpportunityStageResult>> create(@Valid @RequestBody CreateOpportunityStageCommand cmd) {
        return ResponseEntity.status(201).body(ApiResponse.created(createUC.execute(cmd)));
    }

    /** Lấy danh sách có phân trang. @return 200 */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<OpportunityStageResult>>> list(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "sortOrder") String sortBy, @RequestParam(defaultValue = "asc") String sortDir) {
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(listUC.execute(
                PageRequest.builder().page(page).size(size).sortBy(sortBy).sortDir(sortDir).build()))));
    }

    /** Lấy theo ID. @param id ID @return 200 */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OpportunityStageResult>> get(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(getUC.execute(id)));
    }

    /** Cập nhật. @param id ID @param cmd JSON @return 200 */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<OpportunityStageResult>> update(@PathVariable Long id,
                                                                       @Valid @RequestBody UpdateOpportunityStageCommand cmd) {
        return ResponseEntity.ok(ApiResponse.ok(updateUC.execute(
                UpdateOpportunityStageCommand.builder().id(id).name(cmd.getName()).sortOrder(cmd.getSortOrder())
                        .probability(cmd.getProbability()).isWon(cmd.getIsWon()).isLost(cmd.getIsLost()).build())));
    }

    /** Xóa. @param id ID @return 204 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteUC.execute(id); return ResponseEntity.noContent().build();
    }
}
