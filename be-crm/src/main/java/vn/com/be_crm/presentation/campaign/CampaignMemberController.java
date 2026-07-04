package vn.com.be_crm.presentation.campaign;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.com.be_crm.application.campaign.command.*;
import vn.com.be_crm.application.campaign.dto.*;
import vn.com.be_crm.application.campaign.query.ListCampaignMemberUseCase;
import vn.com.be_crm.presentation.shared.ApiResponse;

import java.util.List;

/**
 * REST controller cho thành viên (người nhận) trong chiến dịch marketing.
 */
@RestController
@RequestMapping("/api/campaigns/{campaignId}/members")
public class CampaignMemberController {
    private final CreateCampaignMemberUseCase createUC;
    private final UpdateCampaignMemberUseCase updateUC;
    private final DeleteCampaignMemberUseCase deleteUC;
    private final ListCampaignMemberUseCase listUC;

    /** @param createUC tạo @param updateUC sửa @param deleteUC xóa @param listUC danh sách */
    public CampaignMemberController(CreateCampaignMemberUseCase createUC, UpdateCampaignMemberUseCase updateUC,
                                    DeleteCampaignMemberUseCase deleteUC, ListCampaignMemberUseCase listUC) {
        this.createUC = createUC; this.updateUC = updateUC; this.deleteUC = deleteUC; this.listUC = listUC;
    }

    /** Tạo mới thành viên chiến dịch. @param campaignId ID chiến dịch @param cmd body @return 201 */
    @PostMapping
    public ResponseEntity<ApiResponse<CampaignMemberResult>> create(@PathVariable Long campaignId,
                                                                    @Valid @RequestBody CreateCampaignMemberCommand cmd) {
        return ResponseEntity.status(201).body(ApiResponse.created(createUC.execute(
                CreateCampaignMemberCommand.builder().campaignId(campaignId)
                        .leadId(cmd.getLeadId()).contactId(cmd.getContactId())
                        .name(cmd.getName()).email(cmd.getEmail()).phone(cmd.getPhone()).build())));
    }

    /** Lấy danh sách thành viên chiến dịch. @param campaignId ID chiến dịch @return 200 */
    @GetMapping
    public ResponseEntity<ApiResponse<List<CampaignMemberResult>>> list(@PathVariable Long campaignId) {
        return ResponseEntity.ok(ApiResponse.ok(listUC.execute(campaignId)));
    }

    /** Cập nhật thành viên chiến dịch. @param id ID @param cmd body @return 200 */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CampaignMemberResult>> update(@PathVariable Long campaignId, @PathVariable Long id,
                                                                    @Valid @RequestBody UpdateCampaignMemberCommand cmd) {
        return ResponseEntity.ok(ApiResponse.ok(updateUC.execute(
                UpdateCampaignMemberCommand.builder().id(id).leadId(cmd.getLeadId()).contactId(cmd.getContactId())
                        .name(cmd.getName()).email(cmd.getEmail()).phone(cmd.getPhone()).status(cmd.getStatus()).build())));
    }

    /** Xóa thành viên chiến dịch. @param id ID @return 204 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long campaignId, @PathVariable Long id) {
        deleteUC.execute(id); return ResponseEntity.noContent().build();
    }
}
