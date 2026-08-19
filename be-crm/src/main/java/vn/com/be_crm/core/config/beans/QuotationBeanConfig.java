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
    @Bean public QuotationExpiryUseCase quotationExpiryUseCase(IQuotationRepository r, CreateNotificationUseCase n) { return new QuotationExpiryUseCase(r, n); }
    @Bean public GetQuotationUseCase getQuotationUseCase(IQuotationRepository r, IQuotationItemRepository ir, vn.com.be_crm.core.lookup.port.INameResolver n, QuotationExpiryUseCase eu) { return new GetQuotationUseCase(r, ir, n, eu); }
    @Bean public ListQuotationUseCase listQuotationUseCase(IQuotationRepository r, IQuotationItemRepository ir, vn.com.be_crm.core.lookup.port.INameResolver n, QuotationExpiryUseCase eu) { return new ListQuotationUseCase(r, ir, n, eu); }

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
            ITransactionRunner tx) {
        return new QuotationWorkflowUseCase(qr, ar, nuc, mr, es, cor, pdf, pdfDataBuilder, composer, frontendBaseUrl, tx);
    }

    @Bean public PreviewQuotationPdfUseCase previewQuotationPdfUseCase(IQuotationRepository qr, IQuotationPdfService pdf,
            vn.com.be_crm.application.quotation.pdf.QuotationPdfDataBuilder pdfDataBuilder,
            vn.com.be_crm.application.quotation.email.QuotationEmailComposer composer) {
        return new PreviewQuotationPdfUseCase(qr, pdf, pdfDataBuilder, composer);
    }

    // khách phản hồi báo giá (đồng ý/không đồng ý — đồng ý tự sinh đơn hàng + tự đặt primary)
    @Bean public RespondToQuotationUseCase respondToQuotationUseCase(IQuotationRepository qr, CreateNotificationUseCase nuc,
            ConvertQuotationToOrderUseCase convertToOrderUC, SetPrimaryQuotationUseCase setPrimaryUC, ITransactionRunner tx) {
        return new RespondToQuotationUseCase(qr, nuc, convertToOrderUC, setPrimaryUC, tx);
    }

    // khách "Chỉnh sửa" báo giá — chỉ lưu đề xuất (không đụng quotation_items)
    @Bean public ProposeQuotationAdjustmentUseCase proposeQuotationAdjustmentUseCase(IQuotationRepository qr,
            IQuotationItemRepository qir, CreateNotificationUseCase nuc, ITransactionRunner tx) {
        return new ProposeQuotationAdjustmentUseCase(qr, qir, nuc, tx);
    }

    // nhân viên bấm "Tạo báo giá mới theo yêu cầu khách" — sinh báo giá mới từ đề xuất, khóa báo giá cũ
    @Bean public CreateQuotationFromAdjustmentUseCase createQuotationFromAdjustmentUseCase(IQuotationRepository qr,
            IQuotationItemRepository qir, ITransactionRunner tx) {
        return new CreateQuotationFromAdjustmentUseCase(qr, qir, tx);
    }

    // xem báo giá công khai theo mã (code)
    @Bean public GetQuotationByCodeUseCase getQuotationByCodeUseCase(IQuotationRepository qr,
            IQuotationItemRepository qir, IProductRepository pr, ICustomerRepository cr, IContactRepository cor) {
        return new GetQuotationByCodeUseCase(qr, qir, pr, cr, cor);
    }

    // ===== Quotation <-> Opportunity <-> Order (primary / sync / convert) =====

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
    // dùng NỘI BỘ bởi RespondToQuotationUseCase (khách tự đồng ý qua trang công khai, không có ai
    // ngồi điền AddPage nên vẫn cần tự sinh cả đơn hàng) — không còn lộ ra REST endpoint riêng
    @Bean public ConvertQuotationToOrderUseCase convertQuotationToOrderUseCase(
            IQuotationRepository qr, IQuotationItemRepository qir,
            vn.com.be_crm.domain.order.repository.IOrderRepository ordr, IOpportunityRepository or,
            vn.com.be_crm.application.lead.command.NotifyLeadFirstOrderUseCase notifyLeadFirstOrderUC,
            ITransactionRunner tx) {
        return new ConvertQuotationToOrderUseCase(qr, qir, ordr, or, notifyLeadFirstOrderUC, tx);
    }
    // nhân viên tự tay chuyển báo giá thành đơn hàng qua AddPage — chỉ khóa báo giá + cơ hội won
    @Bean public MarkQuotationConvertedUseCase markQuotationConvertedUseCase(
            IQuotationRepository qr, IOpportunityRepository or, ITransactionRunner tx) {
        return new MarkQuotationConvertedUseCase(qr, or, tx);
    }

    // ===== Trash =====

    @Bean public ListDeletedQuotationsUseCase listDeletedQuotationsUseCase(IQuotationRepository r) { return new ListDeletedQuotationsUseCase(r); }
    @Bean public RestoreQuotationUseCase restoreQuotationUseCase(IQuotationRepository r) { return new RestoreQuotationUseCase(r); }
    @Bean public PurgeQuotationUseCase purgeQuotationUseCase(IQuotationRepository r) { return new PurgeQuotationUseCase(r); }

    // ===== Handover & Import =====

    @Bean public HandoverBulkQuotationUseCase handoverBulkQuotationUseCase(IQuotationRepository r, NotifyAssignmentUseCase n) { return new HandoverBulkQuotationUseCase(r, n); }
    @Bean public ImportBulkQuotationUseCase importBulkQuotationUseCase(IQuotationRepository r, ITransactionRunner tx) { return new ImportBulkQuotationUseCase(r, tx); }
}
