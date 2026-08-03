package vn.com.be_crm.core.config.beans;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.com.be_crm.application.campaign.command.*;
import vn.com.be_crm.application.campaign.query.*;
import vn.com.be_crm.core.email.port.IEmailService;
import vn.com.be_crm.domain.campaign.repository.ICampaignMemberRepository;
import vn.com.be_crm.domain.campaign.repository.ICampaignRepository;

/**
 * Wire các UseCase của module Chiến dịch (campaign, member, trash, handover, import, workflow, gửi email, ROI) qua @Bean.
 */
@Configuration
public class CampaignBeanConfig {

    // ===== Campaign =====

    /** @return CreateCampaignUseCase */
    @Bean public CreateCampaignUseCase createCampaignUseCase(ICampaignRepository r) { return new CreateCampaignUseCase(r); }
    /** @return UpdateCampaignUseCase */
    @Bean public UpdateCampaignUseCase updateCampaignUseCase(ICampaignRepository r) { return new UpdateCampaignUseCase(r); }
    /** @return DeleteCampaignUseCase */
    @Bean public DeleteCampaignUseCase deleteCampaignUseCase(ICampaignRepository r) { return new DeleteCampaignUseCase(r); }
    /** @return GetCampaignUseCase */
    @Bean public GetCampaignUseCase getCampaignUseCase(ICampaignRepository r) { return new GetCampaignUseCase(r); }
    /** @return ListCampaignUseCase */
    @Bean public ListCampaignUseCase listCampaignUseCase(ICampaignRepository r, vn.com.be_crm.core.lookup.port.INameResolver n) { return new ListCampaignUseCase(r, n); }
    /** @return CampaignWorkflowUseCase — luồng trạng thái chiến dịch */
    @Bean public CampaignWorkflowUseCase campaignWorkflowUseCase(ICampaignRepository r) { return new CampaignWorkflowUseCase(r); }
    /** @return ListPublicCampaignsUseCase — chiến dịch đang chạy cho landing page công khai */
    @Bean public ListPublicCampaignsUseCase listPublicCampaignsUseCase(ICampaignRepository r) { return new ListPublicCampaignsUseCase(r); }
    /** @return GetCampaignStatsUseCase — thống kê ROI */
    @Bean public GetCampaignStatsUseCase getCampaignStatsUseCase(ICampaignRepository r) { return new GetCampaignStatsUseCase(r); }
    /** @return SendCampaignEmailUseCase — gửi email hàng loạt */
    @Bean public SendCampaignEmailUseCase sendCampaignEmailUseCase(ICampaignMemberRepository mr, IEmailService es) { return new SendCampaignEmailUseCase(mr, es); }

    // ===== Campaign Member =====

    /** @return CreateCampaignMemberUseCase */
    @Bean public CreateCampaignMemberUseCase createCampaignMemberUseCase(ICampaignMemberRepository r) { return new CreateCampaignMemberUseCase(r); }
    /** @return UpdateCampaignMemberUseCase */
    @Bean public UpdateCampaignMemberUseCase updateCampaignMemberUseCase(ICampaignMemberRepository r) { return new UpdateCampaignMemberUseCase(r); }
    /** @return DeleteCampaignMemberUseCase */
    @Bean public DeleteCampaignMemberUseCase deleteCampaignMemberUseCase(ICampaignMemberRepository r) { return new DeleteCampaignMemberUseCase(r); }
    /** @return ListCampaignMemberUseCase */
    @Bean public ListCampaignMemberUseCase listCampaignMemberUseCase(ICampaignMemberRepository r) { return new ListCampaignMemberUseCase(r); }

    // ===== Trash =====

    /** @return ListDeletedCampaignsUseCase */
    @Bean public ListDeletedCampaignsUseCase listDeletedCampaignsUseCase(ICampaignRepository r) { return new ListDeletedCampaignsUseCase(r); }
    /** @return RestoreCampaignUseCase */
    @Bean public RestoreCampaignUseCase restoreCampaignUseCase(ICampaignRepository r) { return new RestoreCampaignUseCase(r); }
    /** @return PurgeCampaignUseCase */
    @Bean public PurgeCampaignUseCase purgeCampaignUseCase(ICampaignRepository r) { return new PurgeCampaignUseCase(r); }

    // ===== Handover & Import =====

    /** @return HandoverBulkCampaignUseCase */
    @Bean public HandoverBulkCampaignUseCase handoverBulkCampaignUseCase(ICampaignRepository r) { return new HandoverBulkCampaignUseCase(r); }
    /** @return ImportBulkCampaignUseCase */
    @Bean public ImportBulkCampaignUseCase importBulkCampaignUseCase(ICampaignRepository r) { return new ImportBulkCampaignUseCase(r); }
}
