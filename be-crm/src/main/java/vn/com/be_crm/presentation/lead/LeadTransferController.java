package vn.com.be_crm.presentation.lead;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.com.be_crm.application.lead.command.*;
import vn.com.be_crm.application.lead.dto.*;
import vn.com.be_crm.application.lead.query.ListLeadTransferUseCase;
import vn.com.be_crm.presentation.shared.ApiResponse;

import java.util.List;

/**
 * REST controller cho chuyển giao tiềm năng.
 */
@RestController
@RequestMapping("/api/leads/{leadId}/transfers")
public class LeadTransferController {
    private final CreateLeadTransferUseCase createUC;
    private final UpdateLeadTransferUseCase updateUC;
    private final DeleteLeadTransferUseCase deleteUC;
    private final ListLeadTransferUseCase listUC;

    /** @param createUC tạo mới @param updateUC cập nhật @param deleteUC xóa @param listUC danh sách */
    public LeadTransferController(CreateLeadTransferUseCase createUC, UpdateLeadTransferUseCase updateUC,
                                   DeleteLeadTransferUseCase deleteUC, ListLeadTransferUseCase listUC) {
        this.createUC = createUC; this.updateUC = updateUC; this.deleteUC = deleteUC; this.listUC = listUC;
    }

    /** Tạo mới bản ghi chuyển giao. @param leadId ID tiềm năng @param cmd body @return 201 */
    @PostMapping
    public ResponseEntity<ApiResponse<LeadTransferResult>> create(@PathVariable Long leadId,
                                                                   @Valid @RequestBody CreateLeadTransferCommand cmd) {
        return ResponseEntity.status(201).body(ApiResponse.created(createUC.execute(
                CreateLeadTransferCommand.builder().leadId(leadId).fromUserId(cmd.getFromUserId())
                        .toUserId(cmd.getToUserId()).reason(cmd.getReason()).build())));
    }

    /** Lấy danh sách chuyển giao. @param leadId ID tiềm năng @return 200 */
    @GetMapping
    public ResponseEntity<ApiResponse<List<LeadTransferResult>>> list(@PathVariable Long leadId) {
        return ResponseEntity.ok(ApiResponse.ok(listUC.execute(leadId)));
    }

    /** Cập nhật bản ghi chuyển giao. @param id ID @param cmd body @return 200 */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<LeadTransferResult>> update(@PathVariable Long leadId, @PathVariable Long id,
                                                                   @Valid @RequestBody UpdateLeadTransferCommand cmd) {
        return ResponseEntity.ok(ApiResponse.ok(updateUC.execute(
                UpdateLeadTransferCommand.builder().id(id).fromUserId(cmd.getFromUserId())
                        .toUserId(cmd.getToUserId()).reason(cmd.getReason()).build())));
    }

    /** Xóa bản ghi chuyển giao. @param id ID @return 204 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long leadId, @PathVariable Long id) {
        deleteUC.execute(id); return ResponseEntity.noContent().build();
    }
}
