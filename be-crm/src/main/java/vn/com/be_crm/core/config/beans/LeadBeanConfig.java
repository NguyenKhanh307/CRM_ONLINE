package vn.com.be_crm.core.config.beans;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.com.be_crm.core.notify.NotifyAssignmentUseCase;
import vn.com.be_crm.application.lead.command.*;
import vn.com.be_crm.application.lead.query.*;
import vn.com.be_crm.application.notification.command.CreateNotificationUseCase;
import vn.com.be_crm.domain.lead.repository.ILeadItemRepository;
import vn.com.be_crm.domain.lead.repository.ILeadRepository;
import vn.com.be_crm.domain.lead.repository.ILeadTrackingEventRepository;
import vn.com.be_crm.domain.product.repository.IProductRepository;
import vn.com.be_crm.core.tx.port.ITransactionRunner;

// wire các UseCase của module Lead (lead, sản phẩm quan tâm, tracking, trash, handover, import)
@Configuration
public class LeadBeanConfig {

    // ===== Lead =====

    @Bean public CreateLeadUseCase createLeadUseCase(ILeadRepository r) { return new CreateLeadUseCase(r); }
    @Bean public UpdateLeadUseCase updateLeadUseCase(ILeadRepository r, NotifyAssignmentUseCase n) { return new UpdateLeadUseCase(r, n); }
    @Bean public DeleteLeadUseCase deleteLeadUseCase(ILeadRepository r) { return new DeleteLeadUseCase(r); }
    @Bean public GetLeadUseCase getLeadUseCase(ILeadRepository r, vn.com.be_crm.core.lookup.port.INameResolver n) { return new GetLeadUseCase(r, n); }
    @Bean public ListLeadUseCase listLeadUseCase(ILeadRepository r, vn.com.be_crm.core.lookup.port.INameResolver n) { return new ListLeadUseCase(r, n); }
    // claim — không còn "convert" tự động tách KH+LH+CH, cũng không còn qualify/lose (đã bỏ)
    @Bean public LeadWorkflowUseCase leadWorkflowUseCase(ILeadRepository r, ITransactionRunner tx) {
        return new LeadWorkflowUseCase(r, tx);
    }
    // báo cân nhắc chuyển đổi khi tiềm năng có đơn hàng đầu tiên — gọi từ ConvertQuotationToOrderUseCase
    // (quotation->order) và CreateOrderUseCase (tạo đơn tay có gắn báo giá)
    @Bean public NotifyLeadFirstOrderUseCase notifyLeadFirstOrderUseCase(ILeadRepository r, CreateNotificationUseCase n,
                                                                          vn.com.be_crm.core.notify.port.IManagerResolver mr) {
        return new NotifyLeadFirstOrderUseCase(r, n, mr);
    }

    // ===== Sản phẩm quan tâm (lead_items) =====

    @Bean public CreateLeadItemUseCase createLeadItemUseCase(ILeadItemRepository r) { return new CreateLeadItemUseCase(r); }
    @Bean public ListLeadItemUseCase listLeadItemUseCase(ILeadItemRepository r) { return new ListLeadItemUseCase(r); }
    // yêu cầu báo giá từ landing page công khai (gắn sản phẩm quan tâm vào tiềm năng + báo sale/quản lý)
    @Bean public RequestProductQuoteUseCase requestProductQuoteUseCase(ILeadRepository r, ILeadTrackingEventRepository e,
                                                                       ILeadItemRepository li, AddLeadScoreUseCase a,
                                                                       vn.com.be_crm.core.lookup.port.INameResolver n,
                                                                       vn.com.be_crm.core.notify.port.IManagerResolver mr,
                                                                       CreateNotificationUseCase cn, ITransactionRunner tx) {
        return new RequestProductQuoteUseCase(r, e, li, a, n, mr, cn, tx);
    }

    // ===== Lead Scoring & Web Tracking =====

    // cộng điểm dùng chung (tracking + activity)
    @Bean public AddLeadScoreUseCase addLeadScoreUseCase(ILeadRepository r, CreateNotificationUseCase n,
                                                         vn.com.be_crm.core.notify.port.IManagerResolver mr,
                                                         ITransactionRunner tx) {
        return new AddLeadScoreUseCase(r, n, mr, tx);
    }
    @Bean public TrackVisitUseCase trackVisitUseCase(ILeadRepository r) { return new TrackVisitUseCase(r); }
    @Bean public RecordTrackingEventUseCase recordTrackingEventUseCase(ILeadRepository r, ILeadTrackingEventRepository e, AddLeadScoreUseCase a,
                                                                       ITransactionRunner tx) {
        return new RecordTrackingEventUseCase(r, e, a, tx);
    }
    @Bean public SubmitTrackingFormUseCase submitTrackingFormUseCase(ILeadRepository r, ILeadTrackingEventRepository e, AddLeadScoreUseCase a,
                                                                     ITransactionRunner tx) {
        return new SubmitTrackingFormUseCase(r, e, a, tx);
    }
    // xem chi tiết sản phẩm trên landing page công khai (ghi lead_items interestType=viewed)
    @Bean public TrackProductViewUseCase trackProductViewUseCase(ILeadRepository r, ILeadItemRepository li,
                                                                  IProductRepository p, ILeadTrackingEventRepository e,
                                                                  AddLeadScoreUseCase a, ITransactionRunner tx) {
        return new TrackProductViewUseCase(r, li, p, e, a, tx);
    }

    // ===== Trash =====

    @Bean public ListDeletedLeadsUseCase listDeletedLeadsUseCase(ILeadRepository r) { return new ListDeletedLeadsUseCase(r); }
    @Bean public RestoreLeadUseCase restoreLeadUseCase(ILeadRepository r) { return new RestoreLeadUseCase(r); }
    @Bean public PurgeLeadUseCase purgeLeadUseCase(ILeadRepository r) { return new PurgeLeadUseCase(r); }

    // ===== Handover & Import =====

    @Bean public HandoverBulkLeadUseCase handoverBulkLeadUseCase(ILeadRepository r, NotifyAssignmentUseCase n) { return new HandoverBulkLeadUseCase(r, n); }
    @Bean public ImportBulkLeadUseCase importBulkLeadUseCase(ILeadRepository r) { return new ImportBulkLeadUseCase(r); }
}
