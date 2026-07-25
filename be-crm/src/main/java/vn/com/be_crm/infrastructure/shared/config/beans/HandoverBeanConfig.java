package vn.com.be_crm.infrastructure.shared.config.beans;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.com.be_crm.application.handover.HandoverAllUseCase;
import vn.com.be_crm.application.shared.notify.NotifyAssignmentUseCase;
import vn.com.be_crm.domain.customer.repository.ICustomerRepository;
import vn.com.be_crm.domain.lead.repository.ILeadRepository;
import vn.com.be_crm.domain.opportunity.repository.IOpportunityRepository;
import vn.com.be_crm.domain.invoice.repository.IInvoiceRepository;
import vn.com.be_crm.domain.quotation.repository.IQuotationRepository;

/**
 * Wire UseCase bàn giao toàn bộ (cross-module: lead, customer, opportunity, quotation, invoice) qua @Bean.
 */
@Configuration
public class HandoverBeanConfig {

    /** @return HandoverAllUseCase được inject 5 repository nghiệp vụ + use case thông báo */
    @Bean
    public HandoverAllUseCase handoverAllUseCase(ILeadRepository leadRepo,
                                                 ICustomerRepository customerRepo,
                                                 IOpportunityRepository opportunityRepo,
                                                 IQuotationRepository quotationRepo,
                                                 IInvoiceRepository invoiceRepo,
                                                 NotifyAssignmentUseCase notifyUC) {
        return new HandoverAllUseCase(leadRepo, customerRepo, opportunityRepo, quotationRepo, invoiceRepo, notifyUC);
    }
}
