package vn.com.be_crm.presentation.lead;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.com.be_crm.application.lead.command.*;
import vn.com.be_crm.application.lead.dto.*;
import vn.com.be_crm.application.lead.query.ListLeadAttachmentUseCase;
import vn.com.be_crm.presentation.shared.ApiResponse;

import java.util.List;

/**
 * REST controller cho tệp đính kèm tiềm năng.
 */
@RestController
@RequestMapping("/api/leads/{leadId}/attachments")
public class LeadAttachmentController {
    private final CreateLeadAttachmentUseCase createUC;
    private final UpdateLeadAttachmentUseCase updateUC;
    private final DeleteLeadAttachmentUseCase deleteUC;
    private final ListLeadAttachmentUseCase listUC;

    /** @param createUC tạo mới @param updateUC cập nhật @param deleteUC xóa @param listUC danh sách */
    public LeadAttachmentController(CreateLeadAttachmentUseCase createUC, UpdateLeadAttachmentUseCase updateUC,
                                     DeleteLeadAttachmentUseCase deleteUC, ListLeadAttachmentUseCase listUC) {
        this.createUC = createUC; this.updateUC = updateUC; this.deleteUC = deleteUC; this.listUC = listUC;
    }

    /** Tạo mới tệp đính kèm. @param leadId ID tiềm năng @param cmd body @return 201 */
    @PostMapping
    public ResponseEntity<ApiResponse<LeadAttachmentResult>> create(@PathVariable Long leadId,
                                                                     @Valid @RequestBody CreateLeadAttachmentCommand cmd) {
        return ResponseEntity.status(201).body(ApiResponse.created(createUC.execute(
                CreateLeadAttachmentCommand.builder().leadId(leadId).fileName(cmd.getFileName())
                        .fileUrl(cmd.getFileUrl()).fileSize(cmd.getFileSize()).mimeType(cmd.getMimeType())
                        .uploadedBy(cmd.getUploadedBy()).build())));
    }

    /** Lấy danh sách tệp đính kèm. @param leadId ID tiềm năng @return 200 */
    @GetMapping
    public ResponseEntity<ApiResponse<List<LeadAttachmentResult>>> list(@PathVariable Long leadId) {
        return ResponseEntity.ok(ApiResponse.ok(listUC.execute(leadId)));
    }

    /** Cập nhật tệp đính kèm. @param id ID @param cmd body @return 200 */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<LeadAttachmentResult>> update(@PathVariable Long leadId, @PathVariable Long id,
                                                                     @Valid @RequestBody UpdateLeadAttachmentCommand cmd) {
        return ResponseEntity.ok(ApiResponse.ok(updateUC.execute(
                UpdateLeadAttachmentCommand.builder().id(id).fileName(cmd.getFileName())
                        .fileUrl(cmd.getFileUrl()).fileSize(cmd.getFileSize()).mimeType(cmd.getMimeType()).build())));
    }

    /** Xóa tệp đính kèm. @param id ID @return 204 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long leadId, @PathVariable Long id) {
        deleteUC.execute(id); return ResponseEntity.noContent().build();
    }
}
