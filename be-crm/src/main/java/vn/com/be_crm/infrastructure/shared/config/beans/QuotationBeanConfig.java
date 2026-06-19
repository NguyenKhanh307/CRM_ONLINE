package vn.com.be_crm.infrastructure.shared.config.beans;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.com.be_crm.application.quotation.command.*;
import vn.com.be_crm.application.quotation.query.*;
import vn.com.be_crm.domain.quotation.repository.IQuotationApprovalRepository;
import vn.com.be_crm.domain.quotation.repository.IQuotationItemRepository;
import vn.com.be_crm.domain.quotation.repository.IQuotationRepository;

/**
 * Wire các UseCase của module Quotation (quotation, item, approval, trash, handover, import) qua @Bean.
 */
@Configuration
public class QuotationBeanConfig {

    // ===== Quotation =====

    /** @return CreateQuotationUseCase */
    @Bean public CreateQuotationUseCase createQuotationUseCase(IQuotationRepository r) { return new CreateQuotationUseCase(r); }
    /** @return UpdateQuotationUseCase */
    @Bean public UpdateQuotationUseCase updateQuotationUseCase(IQuotationRepository r) { return new UpdateQuotationUseCase(r); }
    /** @return DeleteQuotationUseCase */
    @Bean public DeleteQuotationUseCase deleteQuotationUseCase(IQuotationRepository r) { return new DeleteQuotationUseCase(r); }
    /** @return GetQuotationUseCase */
    @Bean public GetQuotationUseCase getQuotationUseCase(IQuotationRepository r) { return new GetQuotationUseCase(r); }
    /** @return ListQuotationUseCase */
    @Bean public ListQuotationUseCase listQuotationUseCase(IQuotationRepository r) { return new ListQuotationUseCase(r); }

    // ===== Quotation Item =====

    /** @return CreateQuotationItemUseCase */
    @Bean public CreateQuotationItemUseCase createQuotationItemUseCase(IQuotationItemRepository r) { return new CreateQuotationItemUseCase(r); }
    /** @return UpdateQuotationItemUseCase */
    @Bean public UpdateQuotationItemUseCase updateQuotationItemUseCase(IQuotationItemRepository r) { return new UpdateQuotationItemUseCase(r); }
    /** @return DeleteQuotationItemUseCase */
    @Bean public DeleteQuotationItemUseCase deleteQuotationItemUseCase(IQuotationItemRepository r) { return new DeleteQuotationItemUseCase(r); }
    /** @return ListQuotationItemUseCase */
    @Bean public ListQuotationItemUseCase listQuotationItemUseCase(IQuotationItemRepository r) { return new ListQuotationItemUseCase(r); }

    // ===== Quotation Approval =====

    /** @return CreateQuotationApprovalUseCase */
    @Bean public CreateQuotationApprovalUseCase createQuotationApprovalUseCase(IQuotationApprovalRepository r) { return new CreateQuotationApprovalUseCase(r); }
    /** @return UpdateQuotationApprovalUseCase */
    @Bean public UpdateQuotationApprovalUseCase updateQuotationApprovalUseCase(IQuotationApprovalRepository r) { return new UpdateQuotationApprovalUseCase(r); }
    /** @return DeleteQuotationApprovalUseCase */
    @Bean public DeleteQuotationApprovalUseCase deleteQuotationApprovalUseCase(IQuotationApprovalRepository r) { return new DeleteQuotationApprovalUseCase(r); }
    /** @return ListQuotationApprovalUseCase */
    @Bean public ListQuotationApprovalUseCase listQuotationApprovalUseCase(IQuotationApprovalRepository r) { return new ListQuotationApprovalUseCase(r); }

    // ===== Trash =====

    /** @return ListDeletedQuotationsUseCase */
    @Bean public ListDeletedQuotationsUseCase listDeletedQuotationsUseCase(IQuotationRepository r) { return new ListDeletedQuotationsUseCase(r); }
    /** @return RestoreQuotationUseCase */
    @Bean public RestoreQuotationUseCase restoreQuotationUseCase(IQuotationRepository r) { return new RestoreQuotationUseCase(r); }
    /** @return PurgeQuotationUseCase */
    @Bean public PurgeQuotationUseCase purgeQuotationUseCase(IQuotationRepository r) { return new PurgeQuotationUseCase(r); }

    // ===== Handover & Import =====

    /** @return HandoverBulkQuotationUseCase */
    @Bean public HandoverBulkQuotationUseCase handoverBulkQuotationUseCase(IQuotationRepository r) { return new HandoverBulkQuotationUseCase(r); }
    /** @return ImportBulkQuotationUseCase */
    @Bean public ImportBulkQuotationUseCase importBulkQuotationUseCase(IQuotationRepository r) { return new ImportBulkQuotationUseCase(r); }
}
