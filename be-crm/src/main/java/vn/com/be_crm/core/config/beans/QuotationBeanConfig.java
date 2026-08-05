package vn.com.be_crm.core.config.beans;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.com.be_crm.core.notify.NotifyAssignmentUseCase;
import vn.com.be_crm.application.notification.command.CreateNotificationUseCase;
import vn.com.be_crm.application.quotation.command.*;
import vn.com.be_crm.application.quotation.query.*;
import vn.com.be_crm.application.opportunity.command.RecomputeOpportunityAmountUseCase;
import vn.com.be_crm.core.email.port.IEmailService;
import vn.com.be_crm.core.pdf.port.IQuotationPdfService;
import vn.com.be_crm.domain.contact.repository.IContactRepository;
import vn.com.be_crm.domain.customer.repository.ICustomerRepository;
import vn.com.be_crm.domain.opportunity.repository.IOpportunityItemRepository;
import vn.com.be_crm.domain.opportunity.repository.IOpportunityRepository;
import vn.com.be_crm.domain.product.repository.IProductRepository;
import vn.com.be_crm.domain.quotation.repository.IQuotationApprovalRepository;
import vn.com.be_crm.domain.quotation.repository.IQuotationItemRepository;
import vn.com.be_crm.domain.quotation.repository.IQuotationRepository;
import vn.com.be_crm.core.tx.port.ITransactionRunner;

// wire các UseCase của module Quotation (quotation, item, approval, trash, handover, import)
@Configuration
public class QuotationBeanConfig {

    // ===== Quotation =====

    @Bean public CreateQuotationUseCase createQuotationUseCase(IQuotationRepository r) { return new CreateQuotationUseCase(r); }
    @Bean public UpdateQuotationUseCase updateQuotationUseCase(IQuotationRepository r, IQuotationItemRepository ir, NotifyAssignmentUseCase n) { return new UpdateQuotationUseCase(r, ir, n); }
    @Bean public DeleteQuotationUseCase deleteQuotationUseCase(IQuotationRepository r) { return new DeleteQuotationUseCase(r); }
    @Bean public GetQuotationUseCase getQuotationUseCase(IQuotationRepository r, IQuotationItemRepository ir, vn.com.be_crm.core.lookup.port.INameResolver n) { return new GetQuotationUseCase(r, ir, n); }
    @Bean public ListQuotationUseCase listQuotationUseCase(IQuotationRepository r, IQuotationItemRepository ir, vn.com.be_crm.core.lookup.port.INameResolver n) { return new ListQuotationUseCase(r, ir, n); }

    // ===== Quotation Item =====

    @Bean public CreateQuotationItemUseCase createQuotationItemUseCase(IQuotationItemRepository r) { return new CreateQuotationItemUseCase(r); }
    @Bean public UpdateQuotationItemUseCase updateQuotationItemUseCase(IQuotationItemRepository r) { return new UpdateQuotationItemUseCase(r); }
    @Bean public DeleteQuotationItemUseCase deleteQuotationItemUseCase(IQuotationItemRepository r) { return new DeleteQuotationItemUseCase(r); }
    @Bean public ListQuotationItemUseCase listQuotationItemUseCase(IQuotationItemRepository r) { return new ListQuotationItemUseCase(r); }

    // ===== Quotation Approval =====

    @Bean public CreateQuotationApprovalUseCase createQuotationApprovalUseCase(IQuotationApprovalRepository r) { return new CreateQuotationApprovalUseCase(r); }
    @Bean public UpdateQuotationApprovalUseCase updateQuotationApprovalUseCase(IQuotationApprovalRepository r) { return new UpdateQuotationApprovalUseCase(r); }
    @Bean public DeleteQuotationApprovalUseCase deleteQuotationApprovalUseCase(IQuotationApprovalRepository r) { return new DeleteQuotationApprovalUseCase(r); }
    @Bean public ListQuotationApprovalUseCase listQuotationApprovalUseCase(IQuotationApprovalRepository r) { return new ListQuotationApprovalUseCase(r); }

    // ===== Quotation Workflow (submit / approve / reject / send) =====

    @Bean public vn.com.be_crm.application.quotation.email.QuotationEmailComposer quotationEmailComposer(
            ICustomerRepository cr, IContactRepository cor, IQuotationItemRepository qir) {
        return new vn.com.be_crm.application.quotation.email.QuotationEmailComposer(cr, cor, qir);
    }

    @Bean public GetQuotationEmailDraftUseCase getQuotationEmailDraftUseCase(IQuotationRepository qr,
            vn.com.be_crm.application.quotation.email.QuotationEmailComposer composer) {
        return new GetQuotationEmailDraftUseCase(qr, composer);
    }

    @Bean public vn.com.be_crm.application.quotation.pdf.QuotationPdfDataBuilder quotationPdfDataBuilder(
            ICustomerRepository cr, IQuotationItemRepository qir, IProductRepository pr) {
        return new vn.com.be_crm.application.quotation.pdf.QuotationPdfDataBuilder(cr, qir, pr);
    }

    @Bean public QuotationWorkflowUseCase quotationWorkflowUseCase(IQuotationRepository qr, IQuotationApprovalRepository ar,
            CreateNotificationUseCase nuc, vn.com.be_crm.core.notify.port.IManagerResolver mr, IEmailService es,
            IContactRepository cor, IQuotationPdfService pdf, vn.com.be_crm.application.quotation.pdf.QuotationPdfDataBuilder pdfDataBuilder,
            vn.com.be_crm.application.quotation.email.QuotationEmailComposer composer,
            @Value("${app.frontend.base-url}") String frontendBaseUrl,
            ITransactionRunner tx, ConvertQuotationToOrderUseCase convertToOrderUC) {
        return new QuotationWorkflowUseCase(qr, ar, nuc, mr, es, cor, pdf, pdfDataBuilder, composer, frontendBaseUrl, tx, convertToOrderUC);
    }

    @Bean public PreviewQuotationPdfUseCase previewQuotationPdfUseCase(IQuotationRepository qr, IQuotationPdfService pdf,
            vn.com.be_crm.application.quotation.pdf.QuotationPdfDataBuilder pdfDataBuilder,
            vn.com.be_crm.application.quotation.email.QuotationEmailComposer composer) {
        return new PreviewQuotationPdfUseCase(qr, pdf, pdfDataBuilder, composer);
    }

    // khách phản hồi báo giá (đồng ý/điều chỉnh/không đồng ý, tự sinh đơn hàng khi đồng ý)
    @Bean public RespondToQuotationUseCase respondToQuotationUseCase(IQuotationRepository qr, CreateNotificationUseCase nuc,
            ConvertQuotationToOrderUseCase convertToOrderUC, ITransactionRunner tx) {
        return new RespondToQuotationUseCase(qr, nuc, convertToOrderUC, tx);
    }

    // xem báo giá công khai theo mã (code)
    @Bean public GetQuotationByCodeUseCase getQuotationByCodeUseCase(IQuotationRepository qr,
            IQuotationItemRepository qir, IProductRepository pr, ICustomerRepository cr, IContactRepository cor) {
        return new GetQuotationByCodeUseCase(qr, qir, pr, cr, cor);
    }

    // ===== Quotation <-> Opportunity <-> Order (clone / primary / sync / convert) =====

    @Bean public CreateQuotationFromOpportunityUseCase createQuotationFromOpportunityUseCase(
            IQuotationRepository qr, IOpportunityRepository or, IOpportunityItemRepository oir) {
        return new CreateQuotationFromOpportunityUseCase(qr, or, oir);
    }
    @Bean public RefreshQuotationItemsFromOpportunityUseCase refreshQuotationItemsFromOpportunityUseCase(
            IQuotationRepository qr, IQuotationItemRepository qir, IOpportunityItemRepository oir,
            ITransactionRunner tx) {
        return new RefreshQuotationItemsFromOpportunityUseCase(qr, qir, oir, tx);
    }
    @Bean public SetPrimaryQuotationUseCase setPrimaryQuotationUseCase(IQuotationRepository qr) {
        return new SetPrimaryQuotationUseCase(qr);
    }
    @Bean public SyncQuotationToOpportunityUseCase syncQuotationToOpportunityUseCase(
            IQuotationRepository qr, IQuotationItemRepository qir, IOpportunityItemRepository oir,
            RecomputeOpportunityAmountUseCase ruc, ITransactionRunner tx) {
        return new SyncQuotationToOpportunityUseCase(qr, qir, oir, ruc, tx);
    }
    @Bean public ConvertQuotationToOrderUseCase convertQuotationToOrderUseCase(
            IQuotationRepository qr, IQuotationItemRepository qir,
            vn.com.be_crm.domain.order.repository.IOrderRepository ordr, IOpportunityRepository or,
            vn.com.be_crm.application.lead.command.NotifyLeadFirstOrderUseCase notifyLeadFirstOrderUC,
            ITransactionRunner tx) {
        return new ConvertQuotationToOrderUseCase(qr, qir, ordr, or, notifyLeadFirstOrderUC, tx);
    }

    // ===== Trash =====

    @Bean public ListDeletedQuotationsUseCase listDeletedQuotationsUseCase(IQuotationRepository r) { return new ListDeletedQuotationsUseCase(r); }
    @Bean public RestoreQuotationUseCase restoreQuotationUseCase(IQuotationRepository r) { return new RestoreQuotationUseCase(r); }
    @Bean public PurgeQuotationUseCase purgeQuotationUseCase(IQuotationRepository r) { return new PurgeQuotationUseCase(r); }

    // ===== Handover & Import =====

    @Bean public HandoverBulkQuotationUseCase handoverBulkQuotationUseCase(IQuotationRepository r, NotifyAssignmentUseCase n) { return new HandoverBulkQuotationUseCase(r, n); }
    @Bean public ImportBulkQuotationUseCase importBulkQuotationUseCase(IQuotationRepository r) { return new ImportBulkQuotationUseCase(r); }
}
