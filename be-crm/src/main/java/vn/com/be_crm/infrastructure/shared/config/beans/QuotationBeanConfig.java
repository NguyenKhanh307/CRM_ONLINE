package vn.com.be_crm.infrastructure.shared.config.beans;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.com.be_crm.application.notification.command.CreateNotificationUseCase;
import vn.com.be_crm.application.quotation.command.*;
import vn.com.be_crm.application.quotation.query.*;
import vn.com.be_crm.application.opportunity.command.RecomputeOpportunityAmountUseCase;
import vn.com.be_crm.application.shared.email.IEmailService;
import vn.com.be_crm.application.shared.pdf.IQuotationPdfService;
import vn.com.be_crm.domain.auth.repository.IUserRoleRepository;
import vn.com.be_crm.domain.contact.repository.IContactRepository;
import vn.com.be_crm.domain.customer.repository.ICustomerRepository;
import vn.com.be_crm.domain.opportunity.repository.IOpportunityItemRepository;
import vn.com.be_crm.domain.opportunity.repository.IOpportunityRepository;
import vn.com.be_crm.domain.product.repository.IProductRepository;
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

    // ===== Quotation Workflow (submit / approve / reject / send) =====

    /** @return QuotationWorkflowUseCase */
    @Bean public QuotationWorkflowUseCase quotationWorkflowUseCase(IQuotationRepository qr, IQuotationApprovalRepository ar,
            CreateNotificationUseCase nuc, IUserRoleRepository urr, IEmailService es,
            ICustomerRepository cr, IContactRepository cor, IQuotationItemRepository qir,
            IProductRepository pr, IQuotationPdfService pdf,
            @Value("${app.frontend.base-url}") String frontendBaseUrl) {
        return new QuotationWorkflowUseCase(qr, ar, nuc, urr, es, cr, cor, qir, pr, pdf, frontendBaseUrl);
    }

    /** @return RespondToQuotationUseCase — khách phản hồi báo giá (đồng ý/điều chỉnh/không đồng ý) */
    @Bean public RespondToQuotationUseCase respondToQuotationUseCase(IQuotationRepository qr, CreateNotificationUseCase nuc) {
        return new RespondToQuotationUseCase(qr, nuc);
    }

    /** @return GetQuotationByTokenUseCase — xem báo giá công khai theo token */
    @Bean public GetQuotationByTokenUseCase getQuotationByTokenUseCase(IQuotationRepository qr,
            IQuotationItemRepository qir, IProductRepository pr, ICustomerRepository cr, IContactRepository cor) {
        return new GetQuotationByTokenUseCase(qr, qir, pr, cr, cor);
    }

    // ===== Quotation ↔ Opportunity ↔ Invoice (clone / primary / sync / convert) =====

    /** @return CreateQuotationFromOpportunityUseCase — clone báo giá từ cơ hội */
    @Bean public CreateQuotationFromOpportunityUseCase createQuotationFromOpportunityUseCase(
            IQuotationRepository qr, IOpportunityRepository or, IOpportunityItemRepository oir) {
        return new CreateQuotationFromOpportunityUseCase(qr, or, oir);
    }
    /** @return RefreshQuotationItemsFromOpportunityUseCase — cập nhật lại dòng hàng báo giá từ cơ hội */
    @Bean public RefreshQuotationItemsFromOpportunityUseCase refreshQuotationItemsFromOpportunityUseCase(
            IQuotationRepository qr, IQuotationItemRepository qir, IOpportunityItemRepository oir) {
        return new RefreshQuotationItemsFromOpportunityUseCase(qr, qir, oir);
    }
    /** @return SetPrimaryQuotationUseCase — đặt báo giá đồng bộ */
    @Bean public SetPrimaryQuotationUseCase setPrimaryQuotationUseCase(IQuotationRepository qr) {
        return new SetPrimaryQuotationUseCase(qr);
    }
    /** @return SyncQuotationToOpportunityUseCase — đồng bộ dòng hàng báo giá primary về cơ hội */
    @Bean public SyncQuotationToOpportunityUseCase syncQuotationToOpportunityUseCase(
            IQuotationRepository qr, IQuotationItemRepository qir, IOpportunityItemRepository oir,
            RecomputeOpportunityAmountUseCase ruc) {
        return new SyncQuotationToOpportunityUseCase(qr, qir, oir, ruc);
    }
    /** @return ConvertQuotationToOrderUseCase — chuyển báo giá thành đơn hàng */
    @Bean public ConvertQuotationToOrderUseCase convertQuotationToOrderUseCase(
            IQuotationRepository qr, IQuotationItemRepository qir,
            vn.com.be_crm.domain.order.repository.IOrderRepository ordr, IOpportunityRepository or) {
        return new ConvertQuotationToOrderUseCase(qr, qir, ordr, or);
    }

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
