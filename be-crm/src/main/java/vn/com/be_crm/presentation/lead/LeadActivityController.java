package vn.com.be_crm.presentation.lead;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.com.be_crm.application.lead.command.*;
import vn.com.be_crm.application.lead.dto.*;
import vn.com.be_crm.application.lead.query.ListLeadActivityUseCase;
import vn.com.be_crm.presentation.shared.ApiResponse;

import java.util.List;

/**
 * REST controller cho hoạt động tiềm năng.
 */
@RestController
@RequestMapping("/api/leads/{leadId}/activities")
public class LeadActivityController {
    private final CreateLeadActivityUseCase createUC;
    private final UpdateLeadActivityUseCase updateUC;
    private final DeleteLeadActivityUseCase deleteUC;
    private final ListLeadActivityUseCase listUC;

    /** @param createUC tạo mới @param updateUC cập nhật @param deleteUC xóa @param listUC danh sách */
    public LeadActivityController(CreateLeadActivityUseCase createUC, UpdateLeadActivityUseCase updateUC,
                                   DeleteLeadActivityUseCase deleteUC, ListLeadActivityUseCase listUC) {
        this.createUC = createUC; this.updateUC = updateUC; this.deleteUC = deleteUC; this.listUC = listUC;
    }

    /** Tạo mới hoạt động. @param leadId ID tiềm năng @param cmd body @return 201 */
    @PostMapping
    public ResponseEntity<ApiResponse<LeadActivityResult>> create(@PathVariable Long leadId,
                                                                   @Valid @RequestBody CreateLeadActivityCommand cmd) {
        return ResponseEntity.status(201).body(ApiResponse.created(createUC.execute(
                CreateLeadActivityCommand.builder().leadId(leadId).type(cmd.getType()).subject(cmd.getSubject())
                        .content(cmd.getContent()).dueAt(cmd.getDueAt()).completedAt(cmd.getCompletedAt())
                        .createdBy(cmd.getCreatedBy()).build())));
    }

    /** Lấy danh sách hoạt động. @param leadId ID tiềm năng @return 200 */
    @GetMapping
    public ResponseEntity<ApiResponse<List<LeadActivityResult>>> list(@PathVariable Long leadId) {
        return ResponseEntity.ok(ApiResponse.ok(listUC.execute(leadId)));
    }

    /** Cập nhật hoạt động. @param id ID @param cmd body @return 200 */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<LeadActivityResult>> update(@PathVariable Long leadId, @PathVariable Long id,
                                                                   @Valid @RequestBody UpdateLeadActivityCommand cmd) {
        return ResponseEntity.ok(ApiResponse.ok(updateUC.execute(
                UpdateLeadActivityCommand.builder().id(id).type(cmd.getType()).subject(cmd.getSubject())
                        .content(cmd.getContent()).dueAt(cmd.getDueAt()).completedAt(cmd.getCompletedAt()).build())));
    }

    /** Xóa hoạt động. @param id ID @return 204 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long leadId, @PathVariable Long id) {
        deleteUC.execute(id); return ResponseEntity.noContent().build();
    }
}
