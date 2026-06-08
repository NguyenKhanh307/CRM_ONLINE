package vn.com.be_crm.presentation.lead;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.com.be_crm.application.lead.command.*;
import vn.com.be_crm.application.lead.dto.*;
import vn.com.be_crm.application.lead.query.*;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.presentation.shared.ApiResponse;
import vn.com.be_crm.presentation.shared.PageResponse;

/**
 * REST controller cho nghiệp vụ quản lý tiềm năng bán hàng.
 */
@RestController
@RequestMapping("/api/leads")
public class LeadController {
    private final CreateLeadUseCase createUC;
    private final UpdateLeadUseCase updateUC;
    private final DeleteLeadUseCase deleteUC;
    private final GetLeadUseCase getUC;
    private final ListLeadUseCase listUC;

    /** @param createUC tạo mới @param updateUC cập nhật @param deleteUC xóa @param getUC lấy @param listUC danh sách */
    public LeadController(CreateLeadUseCase createUC, UpdateLeadUseCase updateUC, DeleteLeadUseCase deleteUC,
                           GetLeadUseCase getUC, ListLeadUseCase listUC) {
        this.createUC = createUC; this.updateUC = updateUC; this.deleteUC = deleteUC;
        this.getUC = getUC; this.listUC = listUC;
    }

    /** Tạo mới tiềm năng. @param cmd JSON body @return 201 */
    @PostMapping
    public ResponseEntity<ApiResponse<LeadResult>> create(@Valid @RequestBody CreateLeadCommand cmd) {
        return ResponseEntity.status(201).body(ApiResponse.created(createUC.execute(cmd)));
    }

    /** Lấy danh sách tiềm năng. @return 200 */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<LeadResult>>> list(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "asc") String sortDir) {
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(listUC.execute(
                PageRequest.builder().page(page).size(size).sortBy(sortBy).sortDir(sortDir).build()))));
    }

    /** Lấy tiềm năng theo ID. @param id ID @return 200 */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LeadResult>> get(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(getUC.execute(id)));
    }

    /** Cập nhật tiềm năng. @param id ID @param cmd body @return 200 */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<LeadResult>> update(@PathVariable Long id, @Valid @RequestBody UpdateLeadCommand cmd) {
        return ResponseEntity.ok(ApiResponse.ok(updateUC.execute(
                UpdateLeadCommand.builder().id(id).name(cmd.getName()).ownerId(cmd.getOwnerId())
                        .customerId(cmd.getCustomerId()).contactId(cmd.getContactId()).source(cmd.getSource())
                        .status(cmd.getStatus()).estimatedValue(cmd.getEstimatedValue()).phone(cmd.getPhone())
                        .email(cmd.getEmail()).note(cmd.getNote()).build())));
    }

    /** Xóa mềm tiềm năng. @param id ID @return 204 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteUC.execute(id); return ResponseEntity.noContent().build();
    }
}
