package vn.com.be_crm.presentation.lead;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.com.be_crm.application.lead.command.*;
import vn.com.be_crm.application.lead.dto.*;
import vn.com.be_crm.application.lead.query.ListLeadTransferUseCase;
import vn.com.be_crm.core.response.ApiResponse;

import java.util.List;

// REST controller cho chuyển giao tiềm năng
@RestController
@RequestMapping("/api/leads/{leadId}/transfers")
public class LeadTransferController {
    private final CreateLeadTransferUseCase createUC;
    private final UpdateLeadTransferUseCase updateUC;
    private final DeleteLeadTransferUseCase deleteUC;
    private final ListLeadTransferUseCase listUC;

    public LeadTransferController(CreateLeadTransferUseCase createUC, UpdateLeadTransferUseCase updateUC,
                                   DeleteLeadTransferUseCase deleteUC, ListLeadTransferUseCase listUC) {
        this.createUC = createUC; this.updateUC = updateUC; this.deleteUC = deleteUC; this.listUC = listUC;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<LeadTransferResult>> create(@PathVariable Long leadId,
                                                                   @Valid @RequestBody CreateLeadTransferCommand cmd) {
        return ResponseEntity.status(201).body(ApiResponse.created(createUC.execute(
                CreateLeadTransferCommand.builder().leadId(leadId).fromUserId(cmd.getFromUserId())
                        .toUserId(cmd.getToUserId()).reason(cmd.getReason()).build())));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<LeadTransferResult>>> list(@PathVariable Long leadId) {
        return ResponseEntity.ok(ApiResponse.ok(listUC.execute(leadId)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<LeadTransferResult>> update(@PathVariable Long leadId, @PathVariable Long id,
                                                                   @Valid @RequestBody UpdateLeadTransferCommand cmd) {
        return ResponseEntity.ok(ApiResponse.ok(updateUC.execute(
                UpdateLeadTransferCommand.builder().id(id).fromUserId(cmd.getFromUserId())
                        .toUserId(cmd.getToUserId()).reason(cmd.getReason()).build())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long leadId, @PathVariable Long id) {
        deleteUC.execute(id); return ResponseEntity.noContent().build();
    }
}
