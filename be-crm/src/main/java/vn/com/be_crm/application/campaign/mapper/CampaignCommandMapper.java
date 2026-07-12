package vn.com.be_crm.application.campaign.mapper;

import vn.com.be_crm.application.campaign.dto.*;
import vn.com.be_crm.domain.campaign.entity.Campaign;
import vn.com.be_crm.domain.campaign.enums.CampaignStatus;
import vn.com.be_crm.domain.campaign.enums.CampaignType;

/** Chuyển đổi Command ↔ Campaign ↔ CampaignResult. */
public class CampaignCommandMapper {

    /**
     * Tạo Campaign từ CreateCampaignCommand. Trạng thái mặc định draft.
     * @param cmd command tạo mới @return domain entity
     */
    public static Campaign toEntity(CreateCampaignCommand cmd) {
        return Campaign.builder()
                .code(cmd.getCode()).name(cmd.getName())
                .type(cmd.getType() != null ? cmd.getType() : CampaignType.other)
                .status(CampaignStatus.draft)
                .channel(cmd.getChannel())
                .startDate(cmd.getStartDate()).endDate(cmd.getEndDate())
                .budget(cmd.getBudget()).actualCost(cmd.getActualCost())
                .targetSize(cmd.getTargetSize()).expectedRevenue(cmd.getExpectedRevenue())
                .ownerId(cmd.getOwnerId()).description(cmd.getDescription())
                .build();
    }

    /**
     * Cập nhật Campaign từ UpdateCampaignCommand. Trạng thái giữ nguyên (đổi qua hành động).
     * @param cmd command cập nhật @param e entity hiện tại @return domain entity đã cập nhật
     */
    public static Campaign toEntity(UpdateCampaignCommand cmd, Campaign e) {
        return Campaign.builder()
                .id(e.getId()).code(e.getCode())
                .name(cmd.getName() != null ? cmd.getName() : e.getName())
                .type(cmd.getType() != null ? cmd.getType() : e.getType())
                .status(e.getStatus())
                .channel(cmd.getChannel() != null ? cmd.getChannel() : e.getChannel())
                .startDate(cmd.getStartDate() != null ? cmd.getStartDate() : e.getStartDate())
                .endDate(cmd.getEndDate() != null ? cmd.getEndDate() : e.getEndDate())
                .budget(cmd.getBudget() != null ? cmd.getBudget() : e.getBudget())
                .actualCost(cmd.getActualCost() != null ? cmd.getActualCost() : e.getActualCost())
                .targetSize(cmd.getTargetSize() != null ? cmd.getTargetSize() : e.getTargetSize())
                .expectedRevenue(cmd.getExpectedRevenue() != null ? cmd.getExpectedRevenue() : e.getExpectedRevenue())
                .ownerId(cmd.getOwnerId() != null ? cmd.getOwnerId() : e.getOwnerId())
                .description(cmd.getDescription() != null ? cmd.getDescription() : e.getDescription())
                .createdBy(e.getCreatedBy()).updatedBy(e.getUpdatedBy())
                .createdAt(e.getCreatedAt()).build();
    }

    /**
     * Chuyển Campaign sang CampaignResult.
     * @param e domain entity @return result DTO
     */
    public static CampaignResult toResult(Campaign e) {
        return CampaignResult.builder()
                .id(e.getId()).code(e.getCode()).name(e.getName()).type(e.getType()).status(e.getStatus())
                .channel(e.getChannel()).startDate(e.getStartDate()).endDate(e.getEndDate())
                .budget(e.getBudget()).actualCost(e.getActualCost())
                .targetSize(e.getTargetSize()).expectedRevenue(e.getExpectedRevenue())
                .ownerId(e.getOwnerId()).description(e.getDescription())
                .createdBy(e.getCreatedBy()).updatedBy(e.getUpdatedBy())
                .createdAt(e.getCreatedAt()).updatedAt(e.getUpdatedAt()).build();
    }

    private CampaignCommandMapper() {}
}
